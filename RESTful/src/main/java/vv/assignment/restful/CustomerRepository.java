package vv.assignment.restful;

import org.springframework.data.repository.CrudRepository;

import java.util.Date;

/**
 * Created by be on 11.05.2017.
 */
// CrudRepository:

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    // All necessary "getters" for Customers
    Iterable<Customer> findCustomersByLastname(String lastname);
    Iterable<Customer> findCustomersByDateOfBirth(Date dateOfBirth);
    //Iterable<Customer> findCustomerByFirstnameAndLastnameAndDateOfBirth(Date dateOfBirth);
}

