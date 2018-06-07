package vv.assignment.restful;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import vv.assignment.restful.Proxy.ContractProxy.ContractManagement;
import vv.assignment.restful.Proxy.CustomerProxy.CustomerManagement;
import vv.assignment.restful.Proxy.LocalCallConstants;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Proxy.LocalCallConstants.REST_SERVICE_URI;


@WebMvcTest(value = Contract.class, secure = false)
public class TestCustomerContractService {
    // Proxies for making requests to API easier
    CustomerManagement customerProxy = new CustomerManagement();
    ContractManagement contractProxy = new ContractManagement();

    // Used for testing the Spring-MVC-Application
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractService contractService;

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
        LocalCallConstants.createTestUser();
        // Add contracts to customers
        List<Contract> contractPackage = new LinkedList<Contract>();
        contractPackage.add(healthInsurance);
        contractPackage.add(liabilityInsurance);
        gerhard.setContracts(contractPackage);
        anna.setContracts(contractPackage);

    }

    @AfterAll
    public static void cleanUp(){
        LocalCallConstants.deleteTestUser();
    }

    @Test
    public void createCustomersWithContracts(){
        /**
         * Create customers in database and test if created successfully
         */
        ResponseEntity<Void> gerhardResponse = customerProxy.createEntity(gerhard);
        assertThat(gerhardResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        ResponseEntity<Void> annaResponse = customerProxy.createEntity(anna);
        assertThat(annaResponse.getStatusCode(), equalTo(HttpStatus.CREATED));

        /**
         * Get all Customers, because response of the post-request only returns URI to created Customer
         */
        URI locationToGerhard = gerhardResponse.getHeaders().getLocation();
        URI locationToAnna = annaResponse.getHeaders().getLocation();
        /**
         * Request for new Customers
         */
        ResponseEntity<Customer> customerGerhard = customerProxy.getEntity(locationToGerhard);
        assert(customerGerhard.getBody().getContracts().contains(healthInsurance));
        assert(customerGerhard.getBody().getContracts().contains(liabilityInsurance));

        ResponseEntity<Customer> customerAnna = customerProxy.getEntity(locationToAnna);
        assert(customerAnna.getBody().getContracts().contains(healthInsurance));
        assert(customerAnna.getBody().getContracts().contains(liabilityInsurance));

        /**
         * Delete customers from database -> Clean up
         */

        customerProxy.deleteEntity(customerAnna.getBody().getId());
        customerProxy.deleteEntity(customerGerhard.getBody().getId());

    }

    @Test
    public void deleteContractWhileUserHasIt() throws Exception {
        /**
         * Delete contract while user has contract, should not be possible
         */
        ResponseEntity<Void> responseCreate = customerProxy.createEntity(gerhard);

        ResponseEntity<Customer> responseGet = customerProxy.getEntity(responseCreate.getHeaders().getLocation());
        /**
         * Get contractId of first element in customers contracts, and try to delete it
         */
        Long firstContractId = responseGet.getBody().getContracts().get(0).getId();

        contractProxy.deleteEntity(firstContractId);

        //TODO improve proxy for this case
        /**
         * Assert that contract can still be found
         */
        ResponseEntity<Contract> getResponse =
                contractProxy.getEntity(URI.create(REST_SERVICE_URI+"/contract/"+firstContractId));
        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }
}
