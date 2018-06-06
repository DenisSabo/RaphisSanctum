package vv.assignment.restful.Test;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Adress;
import vv.assignment.restful.Contract;
import vv.assignment.restful.Customer;
import vv.assignment.restful.user.User;

import javax.xml.ws.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Test.TestConstants.*;

import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;

public class TestCustomerService {

    // This restTemplate uses a predefined User for basic authentication
    static RestTemplate restTemplate = TestConstants.getAuthenticatedRestTemplate();

    /**
     * Creates a User, that can be used for authentication by the test cases
     */
    @BeforeAll
    public static void createTestUser() throws ServerNotTunedOnRequestException {
        TestConstants.createTestUser();
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
        ResponseEntity<Void> gerhardResponse = createCustomer(Gerhard);
        assertThat(gerhardResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> annaResponse = createCustomer(Anna);
        assertThat(annaResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> turingResponse = createCustomer(Turing);
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
        ResponseEntity<Customer> customerGerhard = getCustomer(locationToGerhard);
        assertThat(customerGerhard.getBody().getFirstname(), equalTo("Gerhard"));

        ResponseEntity<Customer> customerAnna = getCustomer(locationToAnna);
        assertThat(customerAnna.getBody().getFirstname(), equalTo("Anna"));

        ResponseEntity<Customer> customerTuring = getCustomer(locationToTuring);
        assertThat(customerTuring.getBody().getFirstname(), equalTo("Alan"));
        /**
         * Delete customers
         */
        deleteCustomer(customerGerhard.getBody().getId());
        deleteCustomer(customerAnna.getBody().getId());
        deleteCustomer(customerTuring.getBody().getId());
        /**
         * Try to find Customers
         */
        ResponseEntity<Customer> normallyGerhardNotFound = getCustomer(locationToGerhard);
        assertThat(normallyGerhardNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        ResponseEntity<Customer> normallyAnnaNotFound = getCustomer(locationToGerhard);
        assertThat(normallyAnnaNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        ResponseEntity<Customer> normallyTuringNotFound = getCustomer(locationToGerhard);
        assertThat(normallyTuringNotFound.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    /**
     * Functions for easier interaction with API
     * @param customer
     * @return
     */
    private ResponseEntity<Void> createCustomer(Customer customer){
        ResponseEntity<Void> postResponse =
                restTemplate.postForEntity(REST_SERVICE_URI+"/customer", customer, Void.class);
        return postResponse;
    }

    private ResponseEntity<Customer> getCustomer(URI location){
        ResponseEntity<Customer> response =
                restTemplate.getForEntity(location.toString(), Customer.class);
        return response;
    }

    private void updateCustomer(String id, Customer newCustomer){
        restTemplate.put(REST_SERVICE_URI+"/customer/"+id, newCustomer, Void.class);
    }

    private void deleteCustomer(Long id){
        restTemplate.delete(REST_SERVICE_URI+"/customer/"+id, Customer.class);
    }
}
