package fh.vv.assignment02.restful;

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
    Iterable<Customer> findCustomersByFirstnameAndLastname(String firstname, String lastname);
    Iterable<Customer> findCustomersByLastnameAndDateOfBirth(String lastname, Date dateOfBirth);
    Iterable<Customer> findCustomersByFirstnameAndLastnameAndDateOfBirth(String firstname, String lastname, Date dateOfBirth);
    Iterable<Customer> findCustomersByFirstnameAndLastnameAndDateOfBirthAndStreetAndPostalCodeAndPlace
            (String firstname, String lastname, Date dateOfBirth, String street, String postalcode, String place);

    void deleteCustomersByLastname(String lastname);
    void deleteCustomersByFirstnameAndLastname(String firstname, String lastname);
}

