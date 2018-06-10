package vv.assignment.restful.ServiceTests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vv.assignment.restful.Customer.Address;
import vv.assignment.restful.Customer.Customer;
import vv.assignment.restful.MyExceptions.ServerNotTunedOnRequestException;
import vv.assignment.restful.Proxy.CustomerManagement;
import vv.assignment.restful.Proxy.LocalCallConstants;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Proxy.LocalCallConstants.deleteTestUser;

public class TestCustomerService {
    /**
     * The proxy provides the possibility to make requests to the "Customer-Endpoints" easily
     */
    CustomerManagement proxy = new CustomerManagement();

    /**
     * Customer data that can be used in the test cases
     */
    Customer gerhard = new Customer("Gerhard", "Schröder", LocalDate.of(1944, 4, 7),
            new Address("Hochschulstraße 1", "83022", "Rosenheim"));
    Customer anna = new Customer("Anna", "Schmidt", LocalDate.of(2018, 6, 3),
            new Address("Hochschulstraße 2", "83022", "Rosenheim"));
    Customer turing = new Customer("Alan", "Turing", LocalDate.of(1912, 6, 23),
            new Address("Hochschulstraße 3", "83022", "Rosenheim"));



    @BeforeAll
    public static void createTestUser() throws ServerNotTunedOnRequestException {
        LocalCallConstants.createTestUser();
    }



    @AfterAll
    public static void cleanUp(){
        deleteTestUser();
    }



    /**
     * Tests if Customers are created correctly
     */
    @Test
    public void createAndDeleteMultipleCustomers() {
        // Create Customers and check if Http-Status of responses is 201 (CREATED)
        ResponseEntity<Void> gerhardResponse = proxy.createEntity(gerhard);
        assertThat(gerhardResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> annaResponse = proxy.createEntity(anna);
        assertThat(annaResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> turingResponse = proxy.createEntity(turing);
        assertThat(turingResponse.getStatusCode(), equalTo(HttpStatus.CREATED));


        // Get location of recently created customers by looking it up in the response headers
        URI locationToGerhard = gerhardResponse.getHeaders().getLocation();
        URI locationToAnna = annaResponse.getHeaders().getLocation();
        URI locationToTuring = turingResponse.getHeaders().getLocation();

        // Get Customers from database, by performing a get-request to server with obtained locations
        ResponseEntity<Customer> customerGerhard = proxy.getEntity(locationToGerhard);
        // Check if the local customer's data is the same as the data from the resource we required through get request
        // Could be done with equal method, but to be on the save side, will be tested manually, this time
        assertThat(customerGerhard.getBody().getFirstname(), equalTo("Gerhard"));
        assertThat(customerGerhard.getBody().getLastname(), equalTo("Schröder"));
        assertThat(customerGerhard.getBody().getDateOfBirth(), equalTo(LocalDate.of(1944, 4, 7)));
        assertThat(customerGerhard.getBody().getAddress(),
                equalTo(new Address("Hochschulstraße 1", "83022", "Rosenheim")));

        // Get customer from database and test if equal with equal method
        ResponseEntity<Customer> customerAnna = proxy.getEntity(locationToAnna);
        assert(customerAnna.equals(anna));

        ResponseEntity<Customer> customerTuring = proxy.getEntity(locationToTuring);
        assert(customerTuring.equals(turing));

        // Delete customers (Clean up)
        proxy.deleteEntity(customerGerhard.getBody().getId());
        proxy.deleteEntity(customerAnna.getBody().getId());
        proxy.deleteEntity(customerTuring.getBody().getId());

        // Try to find Customers, but should not be found
        ResponseEntity<Customer> normallyGerhardNotFound = proxy.getEntity(locationToGerhard);
        assertThat(normallyGerhardNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
        assertThat(normallyGerhardNotFound.getBody(), equalTo(""));

        ResponseEntity<Customer> normallyAnnaNotFound = proxy.getEntity(locationToGerhard);
        assertThat(normallyAnnaNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
        assertThat(normallyAnnaNotFound.getBody(), equalTo(""));

        ResponseEntity<Customer> normallyTuringNotFound = proxy.getEntity(locationToGerhard);
        assertThat(normallyTuringNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
        assertThat(normallyTuringNotFound.getBody(), equalTo(""));
    }



    /**
     * This test will simulate a situation, in which the lost update problem can occur, but the the first update wins
     */
    @Test
    public void triggerLostUpdateProblem(){
        // Creates Customer
        ResponseEntity<Void> postResponse = proxy.createEntity(gerhard);

        // Two clients (proxies) gain the same Customer
        ResponseEntity<Customer> client1Response = proxy.getEntity(postResponse.getHeaders().getLocation());
        Customer customer1 = client1Response.getBody();
        ResponseEntity<Customer> client2Response = proxy.getEntity(postResponse.getHeaders().getLocation());
        Customer customer2 = client2Response.getBody();

        // The id of the resource is the same for both customers (same resource)
        assertThat(customer1.getId(), equalTo(customer2.getId()));

        Long resourceId = customer1.getId();

        // Changes will be performed on the customers firstname
        // Same situation as two clients get same customer and perform changes on resouce
        customer1.setFirstname("Antonio");
        customer2.setFirstname("Frederik");

        // Both clients try to change resource in database, but only the first update will actually be executed

        // The version number of customer1 and the customer in the database are the same
        // After the entity was updated, the version number of the resource got incremented
        proxy.updateEntity(resourceId.toString(), customer1); // This update will be executed

        // the version number of customer2 and the customer in the database are not the same
        // because the database-customer's version number got incremented after first update
        proxy.updateEntity(resourceId.toString(), customer2); // This update will not be executed

        // Only the first update got executed so the name of the customer in the database is now Antonio
        Customer updatedCustomer = proxy.getEntity(postResponse.getHeaders().getLocation()).getBody();
        assertThat(updatedCustomer.getFirstname(), equalTo("Antonio"));
    }



    /**
     * Tests if Status code 409 will be returned, if somebody tries to save same user multiple times
     * (TODO plus error message)
     */
    @Test
    public void saveSameCustomerTwice() {
        // Try to create same customer  two times
        ResponseEntity<Void> response1 = proxy.createEntity(turing);
        ResponseEntity<Void> response2 = proxy.createEntity(turing);

        // First request should have been successfully performed
        assertThat(response1.getStatusCode(), equalTo(HttpStatus.CREATED));

        // Second request should have been not created successfully. Instead a 409 Status Code should be returned
        assertThat(response2.getStatusCode(), equalTo(HttpStatus.CONFLICT));
    }
}
