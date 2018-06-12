package vv.assignment.restful.Proxy;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Customer.Customer;

import java.net.URI;

import static vv.assignment.restful.Proxy.LocalRequestsUtil.REST_SERVICE_URI;
import static vv.assignment.restful.Proxy.LocalRequestsUtil.getAuthenticatedRestTemplate;

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


    public void deleteAll() {
        restTemplate.exchange(REST_SERVICE_URI+"customers", HttpMethod.DELETE, null, Void.class);
    }
}
