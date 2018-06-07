package vv.assignment.restful.Test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import vv.assignment.restful.Adress;
import vv.assignment.restful.Customer;
import vv.assignment.restful.Proxy.LocalCallConstants;
import vv.assignment.restful.Proxy.CustomerProxy.CustomerManagement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Proxy.LocalCallConstants.deleteTestUser;

import java.net.URI;
import java.time.LocalDate;

public class TestCustomerService {
    CustomerManagement proxy = new CustomerManagement();

    /**
     * Creates a User, that can be used for authentication by the test cases
     */

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
         * Customer data
         */
        Adress gerhardsAdress = new Adress("Hochschulstraße 1", "83022", "Rosenheim");
        Customer Gerhard = new Customer("Gerhard", "Schröder",
                LocalDate.of(1944, 4, 7), gerhardsAdress);

        Adress annasAdress = new Adress("Hochschulstraße 2", "83022", "Rosenheim");
        Customer Anna = new Customer("Anna", "Schmidt",
                LocalDate.of(2018, 6, 3), annasAdress);

        Adress turingsAdress = new Adress("Hochschulstraße 3", "83022", "Rosenheim");
        Customer Turing = new Customer("Alan", "Turing",
                LocalDate.of(1912, 6, 23), turingsAdress);
        /**
         * We assert that all Customers were created
         */
        ResponseEntity<Void> gerhardResponse = proxy.createEntity(Gerhard);
        assertThat(gerhardResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> annaResponse = proxy.createEntity(Anna);
        assertThat(annaResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> turingResponse = proxy.createEntity(Turing);
        assertThat(turingResponse.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * Get all Customers, because response of the post-request only returns URI to created Customer
         */
        URI locationToGerhard = gerhardResponse.getHeaders().getLocation();
        URI locationToAnna = annaResponse.getHeaders().getLocation();
        URI locationToTuring = turingResponse.getHeaders().getLocation();
        /**
         * Request for new Customers
         */
        ResponseEntity<Customer> customerGerhard = proxy.getEntity(locationToGerhard);
        assertThat(customerGerhard.getBody().getFirstname(), equalTo("Gerhard"));

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

}
