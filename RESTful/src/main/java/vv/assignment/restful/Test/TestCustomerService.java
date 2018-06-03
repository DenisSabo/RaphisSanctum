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
import vv.assignment.restful.Customer;
import vv.assignment.restful.user.User;

import javax.xml.ws.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Test.TestConstants.*;

import java.lang.invoke.MethodType;
import java.net.URI;
import java.time.LocalDate;

public class TestCustomerService {

    // This restTemplate uses a predefined User for basic authentication
    static RestTemplate restTemplate = TestConstants.getAuthenticatedRestTemplate();

    /**
     * Creates a User, that can be used for authentication by the test cases
     */
    @BeforeAll
    public static void createTestUser() throws ServerNotTunedOnRequestException {
        createTestUser();
    }

    @AfterAll
    public static void cleanUp(){
        deleteTestUser();
    }

    /**
     * Test if it is possible to create and delete a customer
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
        deleteGünther(getResponse.getBody().getId());
        // Now try to find customer like before
        getResponse = getCustomer(locationToCustomer);
        // Assertion is that user wont be found
        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
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

    }




    private ResponseEntity<Void> createGünther(){
        Adress customersAdress = new Adress("Hochschulstraße 1", "83022", "Rosenheim");
        Customer customer = new Customer("Günther", "Schmidt",
                LocalDate.of(1984, 04, 20), customersAdress);

        ResponseEntity<Void> postResponse =
                restTemplate.postForEntity(REST_SERVICE_URI+"/customer", customer, Void.class);
        /**
         * Post response will contain Customer-Location if created successfully
         */
        return postResponse;
    }

    private void deleteGünther(Long id){
        restTemplate.delete(REST_SERVICE_URI+"/customer/"+id,
                Customer.class);
    }

    private ResponseEntity<Void> createCustomer(Customer customer){
        ResponseEntity<Void> postResponse =
                restTemplate.postForEntity(REST_SERVICE_URI+"/customer", customer, Void.class);
        return postResponse;
    }

    private void deleteCustomer(Long id){
        restTemplate.delete(REST_SERVICE_URI+"/customer/"+id, Customer.class);
    }

    private ResponseEntity<Customer> getCustomer(URI location){
        ResponseEntity<Customer> response =
                restTemplate.getForEntity(location.toString(), Customer.class);
        return response;
    }
}
