package vv.assignment.restful.ServiceTests;

import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vv.assignment.restful.Contract.Contract;
import vv.assignment.restful.Customer.Address;
import vv.assignment.restful.Customer.Customer;
import vv.assignment.restful.Proxy.ContractManagement;
import vv.assignment.restful.Proxy.CustomerManagement;
import vv.assignment.restful.Proxy.LocalRequestsUtil;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Contract.Contract.AllowedContracts.HAFTPFLICHT;
import static vv.assignment.restful.Contract.Contract.AllowedContracts.KRANKENVERSICHERUNG;
import static vv.assignment.restful.Proxy.LocalRequestsUtil.REST_SERVICE_URI;


public class TestCustomerContractService {
    // Proxies for making requests to API easier
    CustomerManagement customerProxy = new CustomerManagement();
    ContractManagement contractProxy = new ContractManagement();

    /**
     * Customers that can be used for tests
     */
    Customer gerhard, anna;


    /**
     * Contracts that can be used for tests
     */
    Contract healthInsurance;
    Contract liabilityInsurance;

    /**
     * creates test user
     */
    @BeforeAll
    public static void createTestUser() {
        LocalRequestsUtil.createTestUser();
    }

    /**
     * deletes test user
     */
    @AfterAll
    public static void cleanUp(){
        LocalRequestsUtil.deleteTestUser();
    }


    /**
     * re-initialises instances  so tests do not influence each other
     */
    @BeforeEach
    public void reinitialiseInstances() {
        // some customers
        gerhard = new Customer("Gerhard", "Schröder",
                LocalDate.of(1944, 4, 7),
                new Address("Hochschulstraße 1", "83022", "Rosenheim"));

        anna = new Customer("Anna", "Schmidt",
                LocalDate.of(2018, 6, 3),
                new Address("Hochschulstraße 2", "83022", "Rosenheim"));

        // some contracts
        healthInsurance = new Contract(KRANKENVERSICHERUNG, new BigDecimal("222.22"));
        liabilityInsurance = new Contract(HAFTPFLICHT, new BigDecimal("220.56"));

        // contracts bundled
        List<Contract> contractPackage = new LinkedList<Contract>();
        contractPackage.add(healthInsurance);
        contractPackage.add(liabilityInsurance);

        // Add contract package to customers
        gerhard.setContracts(contractPackage);
        anna.setContracts(contractPackage);
    }

    @AfterEach
    public void deleteContractsAndCustomers() {
        customerProxy.deleteAll();
        contractProxy.deleteAll();
    }

    /**
     * Creates a customer with some contracts
     */
    @Test
    public void createCustomerWithContracts(){

        // Create customers
        ResponseEntity<Void> gerhardResponse = customerProxy.createEntity(gerhard);

        // check status codes of responses
        assertThat(gerhardResponse.getStatusCode(), equalTo(HttpStatus.CREATED));


        // get location of new customers
        URI locationToGerhard = gerhardResponse.getHeaders().getLocation();

        // actually request for new customers and test if customers contain contracts as expected
        ResponseEntity<Customer> customerGerhard = customerProxy.getEntity(locationToGerhard);
        assert(customerGerhard.getBody().getContracts().contains(healthInsurance));
        assert(customerGerhard.getBody().getContracts().contains(liabilityInsurance));
    }


    /**
     * Try to delete contract while customer has it, should not be possible
     */
    @Test
    public void deleteContractWhileCustomerHasIt() throws Exception {

        // create customer with contracts
        ResponseEntity<Void> responseCreate = customerProxy.createEntity(gerhard);

        // get customer from database, so customer- and contract-entity has IDs now
        ResponseEntity<Customer> responseGet = customerProxy.getEntity(responseCreate.getHeaders().getLocation());

        // Get contractId of first element in customers contracts, and try to delete it
        Long firstContractId = responseGet.getBody().getContracts().get(0).getId();

        // try to delete first contract from customer
        contractProxy.deleteEntity(firstContractId);

        // Assert that contract can still be found
        ResponseEntity<Contract> getResponse =
                contractProxy.getEntity(URI.create(REST_SERVICE_URI+"/contract/"+firstContractId));
        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }
}
