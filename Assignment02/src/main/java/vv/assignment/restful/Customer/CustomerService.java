package vv.assignment.restful.Customer;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import vv.assignment.restful.Customer.CustomerExceptions.CustomerAlreadyChangedException;
import vv.assignment.restful.Customer.CustomerExceptions.CustomerAlreadyExistsException;
import vv.assignment.restful.Customer.CustomerExceptions.CustomerNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ComponentScan("CustomerRepository")
@RestController
public class CustomerService {

    @Autowired
    private CustomerRepository repo;


    /**
     * Gets customer
     * @param id of customer
     * @return entity of customer if exists
     */
    @GetMapping(value = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> findById(@PathVariable String id) {
        Optional<Customer> customer = repo.findById(Long.parseLong(id));
        //  Returns Customer or empty customer
        if(customer.isPresent()){

            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        }
        else{

            throw new CustomerNotFoundException();
        }
    }



    /**
     * @return all existing customers in database
     */
    @GetMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< List<Customer>> findAll() {
        List<Customer> liste = new ArrayList<>();
        Iterable<Customer> iterator = repo.findAll();
        iterator.forEach(liste::add);
        System.out.println(liste);
        return new ResponseEntity<>(liste, HttpStatus.OK);
    }



    /**
     * Updates an existing customer
     * @param id of the old customer you want to change
     * @param newCustomer is the new customer data, you want to have instead of the old customers data
     * @returns a ResponseEntity<Customer> that contains the Customer in the response body
     * @throws CustomerNotFoundException if the passed id, refers to an non existent customer
     */
    @PutMapping(value = "/customer/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> changeCustomer(
            @PathVariable String id, @RequestBody Customer newCustomer) throws CustomerNotFoundException {

        // Try to find customer
        Optional<Customer> maybeOldCustomer = repo.findById(Long.parseLong(id));

        if(!maybeOldCustomer.isPresent()) { // Customer has not been found

            // Throws Exception, that lets the client know, that he requested for a customer that does not exist
            throw new CustomerNotFoundException();

        }
        else { // Customer was found

            Customer oldCustomer = maybeOldCustomer.get();

            // Check if same version, else prevent lost update problem
            if(oldCustomer.getVersion() != newCustomer.getVersion()) throw new CustomerAlreadyChangedException();

            Customer updatedCustomer = oldCustomer;

            // Initializes old customer with new data (except the id and version field)
            updatedCustomer.setCustomer(newCustomer);
            updatedCustomer.increment(); // increments version number

            try {
                repo.save(updatedCustomer);

            }
            catch(DataIntegrityViolationException ex){

                throw new CustomerAlreadyExistsException();

            }
            // Customer was updated successfully
            return new ResponseEntity<>(updatedCustomer, HttpStatus.CREATED);
        }
    }



    /**
     * Creates a new customer
     * @param customer that will be passed in the request body
     * @param ucBuilder builds the URI (location) to the created customer
     * @returns a ResponseEntity<Void>, which contains the location of the new created customer in the header
     * @throws CustomerAlreadyExistsException when client tries to save user, that has same values in primary key
     */
    @PostMapping(value = "/customer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>  newCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder)
    throws CustomerAlreadyExistsException {

        Long savedCustomerId;

        try {
            savedCustomerId = repo.save(customer).getId();
        }
        catch(DataIntegrityViolationException ex){
            throw new CustomerAlreadyExistsException();
        }

        HttpHeaders headers = new HttpHeaders();

        // Sets a header with direct path to created Customer
        headers.setLocation(ucBuilder.path("/customer/{id}").buildAndExpand(savedCustomerId).toUri());

        // Sends header to client
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }



    /**
     * Endpoint for deleting a customer
     * @param id of customer that has to be deleted
     * @returns a ResponseEntity<Customer> that contains the deleted customer in the response body
     * @throws CustomerNotFoundException if the passed id, refers to an non existent customer
     */
    @DeleteMapping(value = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> deleteCustomer(@PathVariable String id) throws CustomerNotFoundException {

        // Find customer
        Optional<Customer> maybeOldCustomer = repo.findById(Long.parseLong(id));

        // If Customer was found delete it, else throw Exception
        if(maybeOldCustomer.isPresent()){
            Customer oldCustomer = maybeOldCustomer.get();
            repo.delete(oldCustomer);
            return new ResponseEntity<Customer>(oldCustomer, HttpStatus.OK);
        }
        else{
            throw new CustomerNotFoundException();
        }
    }



    @DeleteMapping(value = "/customers")
    public void deleteCustomers(){
        repo.deleteAll();
    }
}
