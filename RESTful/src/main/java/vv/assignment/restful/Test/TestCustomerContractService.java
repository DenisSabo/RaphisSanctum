package vv.assignment.restful.Test;

import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Adress;
import vv.assignment.restful.Contract;
import vv.assignment.restful.Customer;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Test.TestConstants.REST_SERVICE_URI;
import static vv.assignment.restful.Test.TestConstants.deleteTestUser;

public class TestCustomerContractService {
    // This restTemplate uses a predefined User for basic authentication
    static RestTemplate restTemplate = TestConstants.getAuthenticatedRestTemplate();

    /**
     * Customers that can be used in the test cases
     */
    static Customer gerhard = new Customer("Gerhard", "Schröder",
            LocalDate.of(1944, 4, 7),
            new Adress("Hochschulstraße 1", "83022", "Rosenheim"));

    static Customer anna = new Customer("Anna", "Schmidt",
            LocalDate.of(2018, 6, 3),
            new Adress("Hochschulstraße 2", "83022", "Rosenheim"));
    /**
     * Contracts that can be used in test cases
     */
    static Contract healthInsurance = new Contract("Krankenversicherung", new BigDecimal("222.22"));
    static Contract liabilityInsurance = new Contract("Haftpflicht", new BigDecimal("220.56"));

    /**
     * Creates a User, that can be used for authentication by the test cases
     */
    @BeforeAll
    public static void createTestUser() throws ServerNotTunedOnRequestException {
        TestConstants.createTestUser();
        // Add contracts to customers
        List<Contract> contractPackage = new LinkedList<Contract>();
        contractPackage.add(healthInsurance);
        contractPackage.add(liabilityInsurance);
        gerhard.setContracts(contractPackage);
        anna.setContracts(contractPackage);

    }

    @AfterAll
    public static void cleanUp(){
        deleteTestUser();
    }


    @Test
    public void createCustomersWithContracts(){
        /**
         * Create customers in database and test if created successfully
         */
        ResponseEntity<Void> gerhardResponse = createCustomer(gerhard);
        assertThat(gerhardResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> annaResponse = createCustomer(anna);
        assertThat(annaResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        /**
         * Get all Customers, because response of the post-request only returns URI to created Customer
         */
        URI locationToGerhard = gerhardResponse.getHeaders().getLocation();
        URI locationToAnna = annaResponse.getHeaders().getLocation();
        /**
         * Request for new Customers
         */
        ResponseEntity<Customer> customerGerhard = getCustomer(locationToGerhard);
        assert(customerGerhard.getBody().getContracts().contains(healthInsurance));
        assert(customerGerhard.getBody().getContracts().contains(liabilityInsurance));

        ResponseEntity<Customer> customerAnna = getCustomer(locationToAnna);
        assert(customerAnna.getBody().getContracts().contains(healthInsurance));
        assert(customerAnna.getBody().getContracts().contains(liabilityInsurance));

        /**
         * Delete customers from database -> Clean up
         */

        deleteCustomer(customerAnna.getBody().getId());
        deleteCustomer(customerGerhard.getBody().getId());

    }

    @Test
    public void deleteContractWhileUserHasIt(){
        /**
         * Delete contract while user has contract, should not be possible
         */
        ResponseEntity<Void> responseCreate = createCustomer(gerhard);

        ResponseEntity<Customer> responseGet = getCustomer(responseCreate.getHeaders().getLocation());
        /**
         * Get contractId of first element in customers contracts, and try to delete it
         */
        Long firstContractId = responseGet.getBody().getContracts().get(0).getId();
        try {
            deleteContract(URI.create(REST_SERVICE_URI + "/contract/" + firstContractId));
        }
        catch(ConstraintViolationException ex){
            // Thats expected
        }
        // TODO 500 -> ConstraintViolationException -> ConstraintViolation is excepted
    }

    // TODO Kommen doppelt vor !
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

    private ResponseEntity<Void> createContract(Contract contract){
        ResponseEntity<Void> response = restTemplate.postForEntity(REST_SERVICE_URI+"/contract",
                contract, Void.class);
        return response;
    }

    private ResponseEntity<Contract> getContract(URI location){
        return restTemplate.getForEntity(location.toString(), Contract.class);
    }

    private void updateContract(String id, Contract newContract){
        // TODO restTemplate returned nichts ???????? Zumindest ResponseEntity wäre ganz nützlich!
        restTemplate.put(REST_SERVICE_URI+"/contract/"+id, newContract, Contract.class);
    }

    private void deleteContract(URI location){
        // TODO implement
        // TODO What to do, if user has this kind of contract ? Do not delete until no user has this contract anymore
        // TODO add Status to contract -> Boolean deprecatedContract -> prevents more of these in Constructor
        restTemplate.delete(location);
    }
}
