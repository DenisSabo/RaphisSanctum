package vv.assignment.restful.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ComponentScan("CustomerRepository")
@RestController
public class CustomerService {

    @Autowired
    private CustomerRepository repo;


    // Finds customer by ID
    @GetMapping(value = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> findById(@PathVariable String id) {
        Optional<Customer> customer = repo.findById(Long.parseLong(id));
        //  Returns Customer or empty customer
        if(customer.isPresent()){
            return new ResponseEntity<Customer>(customer.get(), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Customer>(HttpStatus.NO_CONTENT);
        }
    }

    // returns all customer
    @GetMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< List<Customer>> findAll() {
        List<Customer> liste = new ArrayList<>();
        Iterable<Customer> iterator = repo.findAll();
        iterator.forEach(liste::add);
        System.out.println(liste);
        return new ResponseEntity<List<Customer>>(liste, HttpStatus.OK);
    }

    /**
     * Endpoint for updating an existing customer
     * @param id
     * @param newCustomer
     * @return
     */
    @PutMapping(value = "/customer/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> changeCustomer(
            @PathVariable String id, @RequestBody Customer newCustomer) {
        Optional<Customer> maybeOldCustomer = repo.findById(Long.parseLong(id));
        if(maybeOldCustomer.isPresent()){
            // Customer have been found
            Customer oldCustomer = maybeOldCustomer.get();
            /**
             * Now prevent lost update problem
             */
            if(oldCustomer.versionnumber == newCustomer.versionnumber){
                // No update will be lost -> Allowed to update
                oldCustomer.setFirstname(newCustomer.getFirstname());
                oldCustomer.setLastname(newCustomer.getLastname());
                oldCustomer.setDateOfBirth(newCustomer.getDateOfBirth());
                // Increments version number, so other clients can know, that this entity was updated
                oldCustomer.increment();
                // Save entity to repo(database)
                repo.save(oldCustomer);
                // Everything went fine
                Customer updatedCustomer = oldCustomer;
                return new ResponseEntity<Customer> (updatedCustomer, HttpStatus.OK);
            }
            else{
                /**
                 * If the version numbers do not match, another client has updated the entity before
                 */
                // TODO maybe additional information in body
                return new ResponseEntity<Customer>(HttpStatus.CONFLICT);
            }
        }
        else{
            // Customer has not been found -> return fitting status code
            return new ResponseEntity<Customer> (HttpStatus.NOT_FOUND);
        }
    }

    // Create new Customer
    @PostMapping(value = "/customer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>  newCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        Long savedCustomerId = repo.save(customer).getId();
        /**
         * For each contract in customer, a Contract instance with foreign key to customer must be saved
         */
        HttpHeaders headers = new HttpHeaders();
        // Sets a header with direct path to created Customer
        headers.setLocation(ucBuilder.path("/customer/{id}").buildAndExpand(customer.getId()).toUri());
        // Sends header to client
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // Delete Customer by id
    // TODO Einmal nach spezifizierten Standard, siehe CustomerResponseWrapper -> Returned Error-Infromation im Body

    @DeleteMapping(value = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> deleteCustomer(@PathVariable String id) {
        Optional<Customer> maybeOldCustomer = repo.findById(Long.parseLong(id));
        // If Customer was found delete it, else return NOT_FOUND status with additional error information in body
        if(maybeOldCustomer.isPresent()){
            Customer oldCustomer = maybeOldCustomer.get();
            repo.delete(oldCustomer);
            return new ResponseEntity<Customer>(oldCustomer, HttpStatus.OK);
        }
        else{
            ResponseEntity<Customer> response = new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
            return response;
        }
    }
}
