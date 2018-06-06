package vv.assignment.restful.Proxy.CustomerProxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Customer;
import vv.assignment.restful.Proxy.CRUDable;
import vv.assignment.restful.Test.TestConstants;

import java.net.URI;

import static vv.assignment.restful.Test.TestConstants.REST_SERVICE_URI;

public class RealCustomerManagement implements CRUDable<Customer> {

    static RestTemplate restTemplate = TestConstants.getAuthenticatedRestTemplate();

    public RealCustomerManagement(){
        // default constructor
    }

    @Override
    public void listAllCustomer() {

    }

    @Override
    public ResponseEntity<Void> createEntity(Customer customer) {
        return restTemplate.postForEntity(REST_SERVICE_URI + "/customer", customer, Void.class);
    }

    @Override
    public ResponseEntity<Customer> getEntity(URI location) {
        ResponseEntity<Customer> response =
                restTemplate.getForEntity(location.toString(), Customer.class);
        return response;
    }

    @Override
    public void updateEntity(String id, Customer newCustomer) {
        restTemplate.put(REST_SERVICE_URI+"/customer/"+id, newCustomer, Void.class);
    }

    @Override
    public void deleteEntity(Long id) {
        restTemplate.delete(REST_SERVICE_URI+"/customer/"+id, Customer.class);
    }
}
