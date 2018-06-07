package vv.assignment.restful.Proxy.CustomerProxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Customer.Customer;
import vv.assignment.restful.Proxy.CRUDable;

import java.net.URI;

import static vv.assignment.restful.Proxy.LocalCallConstants.REST_SERVICE_URI;
import static vv.assignment.restful.Proxy.LocalCallConstants.getAuthenticatedRestTemplate;

public class CustomerManagement implements CRUDable<Customer> {

    static RestTemplate restTemplate = getAuthenticatedRestTemplate();

    public CustomerManagement(){
        // default constructor
    }

    @Override
    public void listAllEntities() {

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
