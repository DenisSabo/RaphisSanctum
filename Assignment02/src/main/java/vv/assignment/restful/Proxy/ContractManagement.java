package vv.assignment.restful.Proxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Contract.Contract;
import vv.assignment.restful.Proxy.CRUDable;

import java.net.URI;

import static vv.assignment.restful.Proxy.LocalCallConstants.REST_SERVICE_URI;
import static vv.assignment.restful.Proxy.LocalCallConstants.getAuthenticatedRestTemplate;

public class ContractManagement implements CRUDable<Contract> {

    static RestTemplate restTemplate = getAuthenticatedRestTemplate();

    public ContractManagement(){
        // default constructor
    }

    @Override
    public void listAllEntities() {

    }

    @Override
    public ResponseEntity<Void> createEntity(Contract contract) {
        return restTemplate.postForEntity(REST_SERVICE_URI + "/contract", contract, Void.class);
    }

    @Override
    public ResponseEntity<Contract> getEntity(URI location) {
        ResponseEntity<Contract> response =
                restTemplate.getForEntity(location.toString(), Contract.class);
        return response;
    }

    @Override
    public void updateEntity(String id, Contract newContract) {
        restTemplate.put(REST_SERVICE_URI+"/contract/"+id, newContract, Void.class);
    }

    @Override
    public void deleteEntity(Long id) {
        restTemplate.delete(REST_SERVICE_URI+"/contract/"+id, Contract.class);
    }
}
