package vv.assignment.restful;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Proxy.LocalCallConstants;

import java.math.BigDecimal;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Proxy.LocalCallConstants.*;

public class TestContractsService {
    // This restTemplate uses a predefined User for basic authentication
    static RestTemplate restTemplate = getAuthenticatedRestTemplate();

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
    public void createAndDeleteContracts(){
        /**
         * Create new Contracts
         */
        Contract healthInsurance = new Contract("Krankenversicherung", new BigDecimal("416.45"));
        ResponseEntity<Void> locationToContract1 = createContract(healthInsurance);
        assertThat(locationToContract1.getStatusCode(), equalTo(HttpStatus.CREATED));

        Contract anotherHealthInsurance = new Contract("Krankenversicherung", new BigDecimal("600.00"));
        ResponseEntity<Void> locationToContract2 = createContract(anotherHealthInsurance);
        assertThat(locationToContract2.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * Try to get created Contracts
         */
        ResponseEntity<Contract> healthInsurance1FromDatabase =
                getContract(locationToContract1.getHeaders().getLocation());
        assertThat(healthInsurance1FromDatabase.getStatusCode(), equalTo(HttpStatus.OK));
        // Compare local health insurance with health insurance from database, gained through get request
        assertThat(healthInsurance1FromDatabase.getBody().getKindOfContract(),
                equalTo(healthInsurance.getKindOfContract()));
        assertThat(healthInsurance1FromDatabase.getBody().getYearlyFee(),
                equalTo(healthInsurance.getYearlyFee()));

        ResponseEntity<Contract> healthInsurance2FromDatabase =
                getContract(locationToContract2.getHeaders().getLocation());
        assertThat(healthInsurance2FromDatabase.getStatusCode(), equalTo(HttpStatus.OK));
        // Compare local health insurance with health insurance from database, gained through get request
        assertThat(healthInsurance2FromDatabase.getBody().getKindOfContract(),
                equalTo(anotherHealthInsurance.getKindOfContract()));
        assertThat(healthInsurance2FromDatabase.getBody().getYearlyFee(),
                equalTo(anotherHealthInsurance.getYearlyFee()));

        /**
         * Delete created Contracts
         */
        deleteContract(locationToContract1.getHeaders().getLocation());
        deleteContract(locationToContract2.getHeaders().getLocation());
        /**
         * Try to get Contracts from Database again
         */
        healthInsurance1FromDatabase =
                getContract(locationToContract1.getHeaders().getLocation());
        assertThat(healthInsurance1FromDatabase.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

        healthInsurance2FromDatabase =
                getContract(locationToContract2.getHeaders().getLocation());
        assertThat(healthInsurance2FromDatabase.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
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
