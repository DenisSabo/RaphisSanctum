package vv.assignment.restful.Test;

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
        /**
         * New customer
         */
        Adress customersAdress = new Adress("Hochschulstraße 1", "83022", "Rosenheim");
        Customer customer = new Customer("Günther", "Schmidt",
                LocalDate.of(1984, 04, 20),customersAdress);
        /**
         * Request itself
         */
        HttpEntity<Customer> request = new HttpEntity<Customer>(customer, TestConstants.getBasicAuthHeaders());
        /**
         * Post new customer
         */
        ResponseEntity<Void> response0 =
                restTemplate.postForEntity(REST_SERVICE_URI+"/customer", request, Void.class);

        assertThat(response0.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * Location to new user is saved in Headers
         */
        // TODO Fragen was tun?
        HttpEntity<Void> request2 = new HttpEntity<Void>(TestConstants.getBasicAuthHeaders());
        URI locationToCustomer = response0.getHeaders().getLocation();
        ResponseEntity<Customer> response =
                restTemplate.getForEntity(locationToCustomer.toString(), Customer.class, request2);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody().getFirstname(), equalTo(customer.getFirstname()));
        assertThat(response.getBody().getLastname(), equalTo(customer.getLastname()));
        assertThat(response.getBody().getDateOfBirth(), equalTo(customer.getDateOfBirth()));
        assertThat(response.getBody().getAdress().getPlace(), equalTo(customer.getAdress().getPlace()));
        assertThat(response.getBody().getAdress().getPostalcode(), equalTo(customer.getAdress().getPostalcode()));
        assertThat(response.getBody().getAdress().getStreet(), equalTo(customer.getAdress().getStreet()));
    }

    @AfterAll
    public static void deleteTestingAccount(){
        restTemplate.delete(REST_SERVICE_URI+"/user/"+TestConstants.username);
    }
}
