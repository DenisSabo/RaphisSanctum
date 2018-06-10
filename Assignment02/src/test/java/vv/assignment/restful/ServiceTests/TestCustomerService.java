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

    @Test
    public void createAndDeleteMultipleCustomers() {
        /**
         * We assert that all Customers were created successfully
         */
        ResponseEntity<Void> gerhardResponse = proxy.createEntity(gerhard);
        assertThat(gerhardResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> annaResponse = proxy.createEntity(anna);
        assertThat(annaResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> turingResponse = proxy.createEntity(turing);
        assertThat(turingResponse.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * Get all customers by looking up the URI in the response headers of the post requests
         */
        URI locationToGerhard = gerhardResponse.getHeaders().getLocation();
        URI locationToAnna = annaResponse.getHeaders().getLocation();
        URI locationToTuring = turingResponse.getHeaders().getLocation();
        /**
         * Request for new Customers
         */
        ResponseEntity<Customer> customerGerhard = proxy.getEntity(locationToGerhard);
        assertThat(customerGerhard.getBody().getFirstname(), equalTo("Gerhard"));
        assertThat(customerGerhard.getBody().getLastname(), equalTo("Schröder"));
        assertThat(customerGerhard.getBody().getDateOfBirth(), equalTo(LocalDate.of(1944, 4, 7)));
        assertThat(customerGerhard.getBody().getAddress(),
                equalTo(new Address("Hochschulstraße 1", "83022", "Rosenheim")));

        // For the rest, only the first name will be tested
        ResponseEntity<Customer> customerAnna = proxy.getEntity(locationToAnna);
        assertThat(customerAnna.getBody().getFirstname(), equalTo("Anna"));

        ResponseEntity<Customer> customerTuring = proxy.getEntity(locationToTuring);
        assertThat(customerTuring.getBody().getFirstname(), equalTo("Alan"));
        /**
         * Delete customers
         */
        proxy.deleteEntity(customerGerhard.getBody().getId());
        proxy.deleteEntity(customerAnna.getBody().getId());
        proxy.deleteEntity(customerTuring.getBody().getId());
        /**
         * Try to find Customers
         */
        ResponseEntity<Customer> normallyGerhardNotFound = proxy.getEntity(locationToGerhard);
        assertThat(normallyGerhardNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        ResponseEntity<Customer> normallyAnnaNotFound = proxy.getEntity(locationToGerhard);
        assertThat(normallyAnnaNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        ResponseEntity<Customer> normallyTuringNotFound = proxy.getEntity(locationToGerhard);
        assertThat(normallyTuringNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    /**
     * This test will simulate a situation, in which the lost update problem can occur, but the the first update wins
     */
    @Test
    public void triggerLostUpdateProblem(){
        /**
         * Customer resource that will be requested later
         */
        ResponseEntity<Void> postResponse = proxy.createEntity(gerhard);
        /**
         * Two clients gain the same resource
         */
        ResponseEntity<Customer> client1Response = proxy.getEntity(postResponse.getHeaders().getLocation());
        Customer customer1 = client1Response.getBody();
        ResponseEntity<Customer> client2Response = proxy.getEntity(postResponse.getHeaders().getLocation());
        Customer customer2 = client2Response.getBody();
        // The id of the resource is the same for both customers (same resource)
        Long resourceId = customer1.getId();
        /**
         * The clients change the resource
         */
        customer1.setFirstname("Antonio");
        customer2.setFirstname("Frederik");
        /**
         * Both clients try to change resource in database, but only the first update will actually be executed
         */
        proxy.updateEntity(resourceId.toString(), customer1); // This update will be executed
        // After the entity was updated, the version number of the resource got incremented
        proxy.updateEntity(resourceId.toString(), customer2); // This update will not be executed
        /**
         * Only the first update got executed so the name of the customer in the database is now Antonio
         */
        Customer updatedCustomer = proxy.getEntity(postResponse.getHeaders().getLocation()).getBody();
        assertThat(updatedCustomer.getFirstname(), equalTo("Antonio"));
    }

}
