package vv.assignment.restful.ServiceTests;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vv.assignment.restful.Contract.Contract;
import vv.assignment.restful.Contract.ContractService;
import vv.assignment.restful.Proxy.ContractManagement;
import vv.assignment.restful.Proxy.LocalRequestsUtil;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Contract.Contract.AllowedContracts.KFZ;
import static vv.assignment.restful.Contract.Contract.AllowedContracts.KRANKENVERSICHERUNG;
import static vv.assignment.restful.Proxy.LocalRequestsUtil.deleteTestUser;



@WebMvcTest(ContractService.class) // The only thing we want to test in this JUNIT test is the Contract Service
public class TestContractsService {

    /**
     * The proxy provides the possibility to make requests to the "Contract-Endpoints" easily
     */
    ContractManagement proxy = new ContractManagement();


    // local contracts that can be used in tests
    Contract healthInsurance;
    Contract kfz;


    /**
     * Creates a User, that can be used for authentication by the test cases
     */
    @BeforeAll
    public static void createTestUser() {
        LocalRequestsUtil.createTestUser();
    }



    /**
     * Deletes test user
     */
    @AfterAll
    public static void cleanUp(){
        deleteTestUser();
    }


    /**
     * re-initialises instances  so tests do not influence each other
     */
    @BeforeEach
    public void reinitialisesContracts(){
        healthInsurance = new Contract(KRANKENVERSICHERUNG, new BigDecimal("416.45"));
        kfz = new Contract(KFZ, new BigDecimal("42.42"));
    }


    /**
     * Clears database from all contracts
     */
    @AfterEach
    public void deleteContracts() {
        proxy.deleteAll();
    }



    /**
     * Tests if application creates and deletes contracts correctly
     */
    @Test
    public void createAndDeleteContracts(){

        // create new contracts in database
        ResponseEntity<Void> postResponseHealth = proxy.createEntity(healthInsurance);
        ResponseEntity<Void> postResponseKfz = proxy.createEntity(kfz);

        // Check if right status codes have been set
        assertThat(postResponseHealth.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(postResponseKfz.getStatusCode(), equalTo(HttpStatus.CREATED));

        // get created contracts from database
        ResponseEntity<Contract> healtInsuranceFromDatabase =
                proxy.getEntity(postResponseHealth.getHeaders().getLocation());
        ResponseEntity<Contract> kfzFromDatabase =
                proxy.getEntity(postResponseKfz.getHeaders().getLocation());

        // check if response contains right status codes
        assertThat(healtInsuranceFromDatabase.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(kfzFromDatabase.getStatusCode(), equalTo(HttpStatus.OK));

        // Compare local health insurance with health insurance from database
        assert(healtInsuranceFromDatabase.getBody().equals(healthInsurance));
        assert(kfzFromDatabase.getBody().equals(kfz));

        // delete created contracts
        proxy.deleteEntity(healtInsuranceFromDatabase.getBody().getId());
        proxy.deleteEntity(kfzFromDatabase.getBody().getId());

        // try to get deleted contracts
        healtInsuranceFromDatabase =
                proxy.getEntity(postResponseHealth.getHeaders().getLocation());
        kfzFromDatabase =
                proxy.getEntity(postResponseKfz.getHeaders().getLocation());

        // check status
        assertThat(healtInsuranceFromDatabase.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(kfzFromDatabase.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));

    }



    /**
     * This test will simulate a situation, in which the lost update problem can occur, but the the first update wins
     */
    @Test
    public void triggerLostUpdateProblem(){
        // creates contract
        ResponseEntity<Void> postResponse = proxy.createEntity(healthInsurance);

        // Two clients gain the same contract
        ResponseEntity<Contract> client1Response = proxy.getEntity(postResponse.getHeaders().getLocation());
        Contract contract1 = client1Response.getBody();

        ResponseEntity<Contract> client2Response = proxy.getEntity(postResponse.getHeaders().getLocation());
        Contract contract2 = client2Response.getBody();

        // The id of the resource is the same for both customers (same resource)
        assertThat(contract1.getId(), equalTo(contract2.getId()));

        Long resourceId = contract1.getId();

        // Changes will be performed on the customers firstname
        // Same situation as two clients get same customer and perform changes on resouce
        contract1.setYearlyFee(new BigDecimal("1.10"));
        contract2.setYearlyFee(new BigDecimal("99999999.2123"));

        // Both clients try to change resource in database, but only the first update will actually be executed

        // The version number of customer1 and the customer in the database are the same
        // After the entity was updated, the version number of the resource got incremented
        proxy.updateEntity(resourceId.toString(), contract1); // This update will be executed

        // the version number of customer2 and the customer in the database are not the same
        // because the database-customer's version number got incremented after first update
        proxy.updateEntity(resourceId.toString(), contract2); // This update will not be executed

        // Only the first update got executed so the name of the customer in the database is now Antonio
        Contract updatedContract = proxy.getEntity(postResponse.getHeaders().getLocation()).getBody();
        assertThat(updatedContract.getYearlyFee(), equalTo(new BigDecimal("1.10")));
    }
}
