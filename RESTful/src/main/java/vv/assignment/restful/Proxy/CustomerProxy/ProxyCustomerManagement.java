package vv.assignment.restful.Proxy.CustomerProxy;

import org.springframework.http.ResponseEntity;
import vv.assignment.restful.Customer;
import vv.assignment.restful.Proxy.CRUDable;

import java.net.URI;

public class ProxyCustomerManagement implements CRUDable<Customer> {
    private RealCustomerManagement realCustomerManagement;

    @Override
    public void listAllCustomer() {
        checkRealCustomerManagement();
        // TODO
    }

    @Override
    public ResponseEntity<Void> createEntity(Customer entity) {
        checkRealCustomerManagement();
        return realCustomerManagement.createEntity(entity);
    }

    @Override
    public ResponseEntity<Customer> getEntity(URI location) {
        checkRealCustomerManagement();
        return realCustomerManagement.getEntity(location);
    }

    @Override
    public void updateEntity(String id, Customer newEntity) {
        checkRealCustomerManagement();
        realCustomerManagement.updateEntity(id, newEntity);
    }

    @Override
    public void deleteEntity(Long id) {
        checkRealCustomerManagement();
        realCustomerManagement.deleteEntity(id);
    }

    private void checkRealCustomerManagement(){
        if(realCustomerManagement == null){
            realCustomerManagement = new RealCustomerManagement();
        }
    }
}
