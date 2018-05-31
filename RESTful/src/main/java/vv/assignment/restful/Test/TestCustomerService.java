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

import java.lang.invoke.MethodType;
import java.net.URI;
import java.time.LocalDate;
import static vv.assignment.restful.Test.TestConstants.REST_SERVICE_URI;

public class TestCustomerService {

    static RestTemplate restTemplate = new RestTemplate();

    /**
     * Creates
     */
    @BeforeAll
    public static void testingAccountExists(){
        User testUser = new User("TESTRUNNER", "Pass123", null);
        /**
         * Creates User which is needed for authentication
         */
        ResponseEntity<User> response =
                restTemplate.getForEntity(REST_SERVICE_URI+"/user/"+ testUser.getUsername(), User.class);
        if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)){
            ResponseEntity<User> anotherResponse =
                    restTemplate.postForEntity(REST_SERVICE_URI+"/user", testUser, User.class);
            if(anotherResponse.getStatusCode().equals(HttpStatus.CREATED)){
                // Everything is fine
            }
            else{
                // TODO do not execute tests
            }
        }
        else{
            // Do nothing
        }
    }
    @Test
    public void createCustomer(){
        ResponseEntity<Void> postResponse = createGünther();
        assertThat(postResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        URI locationToCustomer = postResponse.getHeaders().getLocation();
        ResponseEntity<Customer> getResponse = getCustomer(locationToCustomer);

        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(getResponse.getBody().getFirstname(), equalTo("Günther"));
    }

    @After
    public static void deleteGünther(){
        // TODO implement
    }

    @AfterAll
    public static void deleteTestingAccount(){
        restTemplate.delete(REST_SERVICE_URI+"/user/"+TestConstants.username);
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

        return postResponse;
    }

    private ResponseEntity<Customer> getCustomer(URI location){
        HttpEntity<Void> getCustomerReq = new HttpEntity<Void>(TestConstants.getBasicAuthHeaders());
        ResponseEntity<Customer> getResponse =
                restTemplate.getForEntity(location.toString(), Customer.class, getCustomerReq);
        return getResponse;
    }
}
