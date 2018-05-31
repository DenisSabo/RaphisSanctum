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
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Adress;
import vv.assignment.restful.Customer;
import vv.assignment.restful.user.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Test.TestConstants.*;

import java.lang.invoke.MethodType;
import java.net.URI;
import java.time.LocalDate;

public class TestCustomerService {

    /**
     * TODO am besten wäre es, wenn alle RestTemplate-Anfragen direkt mit dem Authorization-Header für
     * TODO BasicAuth ausgestatted wären
     */


    static RestTemplate restTemplate = new RestTemplate();

    /**
     * Creates
     */
    @BeforeAll
    public static void testingAccountExists() throws ServerNotTunedOnRequestException {

        /**
         * Tries to create a User, that can be used for authentication by the test cases
         */
        createTestUser();
    }

    @AfterAll
    public static void cleanUp(){
        deleteTestUser();
    }

    /**
     * Test if it is possible to create a customer
     */
    @Test
    public void createAndDeleteCustomer(){
        /**
         * Creates a predefined customer
         */
        ResponseEntity<Void> postResponse = createGünther();
        assertThat(postResponse.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * post response normally contains URI to created User
         */
        URI locationToCustomer = postResponse.getHeaders().getLocation();
        ResponseEntity<Customer> getResponse = getCustomer(locationToCustomer);
        /**
         * Assertion that user was found
         */
        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(getResponse.getBody().getFirstname(), equalTo("Günther"));
        /**
         * Now delete user Günther by Customer-Entity
         */
        deleteGünther(getResponse.getBody());
        // Now try to find customer like before
        getResponse = getCustomer(locationToCustomer);
        // Assertion is that user wont be found
        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    private ResponseEntity<Void> createGünther(){
        Adress customersAdress = new Adress("Hochschulstraße 1", "83022", "Rosenheim");
        Customer customer = new Customer("Günther", "Schmidt",
                LocalDate.of(1984, 04, 20),customersAdress);
        /**
         * Request-structure with BasicAuthorization-Header for posting new Customer
          */
        HttpEntity<Customer> postCustomer = new HttpEntity<Customer>(customer, TestConstants.getBasicAuthHeaders());

        ResponseEntity<Void> postResponse =
                restTemplate.postForEntity(REST_SERVICE_URI+"/customer", postCustomer, Void.class);
        /**
         * Post response will contain Customer-Location if created successfully
         */
        return postResponse;
    }

    private void deleteGünther(Customer customer){
                restTemplate.delete(REST_SERVICE_URI+"/customer",
                        Customer.class , getBasicAuthHeaders());
    }

    private ResponseEntity<Customer> getCustomer(URI location){
        HttpEntity<Void> authedEntity = new HttpEntity<Void>(TestConstants.getBasicAuthHeaders());
        ResponseEntity<Customer> response =
                restTemplate.getForEntity(location.toString(), Customer.class, authedEntity);
        return response;
    }
}
