package vv.assignment.restful;

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

    // Different credentials will make it possible to use different search functions
    // localhost:8080/customer/credentials?firstname="Denis"&lastname="Sabolotni"
    // localhost:8080/customer/credentials?firstname="Denis"&lastname="Sabolotni"&street="Schillerstraße 14"
    // Frage was ist wenn ich alle möglichen Kombinationen von möglichen Werten implementieren möchte
    // D.h. 6! Möglichkeiten, da 6 versch. mögliche Werte
    // D.h. User kann mit jeder möglichen Eingabe eine Suche tätigen
    // Mögliche Lösungen else if
    // Oder mehrere Mappings für alle Möglichen Anfragen (SpringBoot sucht dann die beste Lösung)
    /**
     @GetMapping(value = "/customer/credentials",
     params = {"firstname", "lastname", "dateofbirth", "street", "postalcode", "place"},
     produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<List<Customer>> findAllByLastname
     (@RequestParam(value="firstname") Optional<String> firstname,
     @RequestParam(value="lastname", required = true) String lastname,
     @RequestParam(value="dateofbirth") Optional<Date> dateOfBirth,
     @RequestParam(value="street") Optional<String> street,
     @RequestParam(value="postalcode") Optional<String> postalCode,
     @RequestParam(value="place") Optional<String> place) {
     List<Customer> customers = new ArrayList<>();
     Iterable<Customer> customerItr;
     // All possible params where given
     if(firstname.isPresent() && dateOfBirth.isPresent() && street.isPresent() && postalCode.isPresent() && place.isPresent()){
     Iterable<Customer> customersWithSameName =
     customerItr = repo.findCustomersByFirstnameAndLastnameAndDateOfBirthAndStreetAndPostalCodeAndPlace
     (firstname.get(), lastname, dateOfBirth.get(), street.get(), postalCode.get(), place.get());
     customerItr.forEach(customers::add);
     return new ResponseEntity<List<Customer>> (customers, HttpStatus.OK);
     }
     else if(dateOfBirth.isPresent() && street.isPresent() && postalCode.isPresent() && place.isPresent()){

     }
     else if(street.isPresent() && postalCode.isPresent() && place.isPresent()){

     }
     else if(dateOfBirth.isPresent() && postalCode.isPresent() && place.isPresent()){

     }
     else if(postalCode.isPresent() && place.isPresent()){

     }
     else if(place.isPresent()){

     }
     else if(postalCode.isPresent()){

     }
     else if(street.isPresent()){

     }
     }
     */

    // Finds customer by ID
    @GetMapping(value = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> findById(@PathVariable String id) {
        Optional<Customer> c = repo.findById(Long.parseLong(id));
        //  Returns Customer or empty customer
        if(c.isPresent()){
            return new ResponseEntity<Customer>(c.get(), HttpStatus.OK);
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

    // Update existing Customer
    @PutMapping(value = "/customer/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> changeCustomer(
            @PathVariable String id, @RequestBody Customer newCustomer) {
        Optional<Customer> maybeOldCustomer = repo.findById(Long.parseLong(id));
        if(maybeOldCustomer.isPresent()){
            // Customer have been found
            Customer oldCustomer = maybeOldCustomer.get();
            oldCustomer.setFirstname(newCustomer.getFirstname());
            oldCustomer.setLastname(newCustomer.getLastname());
            oldCustomer.setDateOfBirth(newCustomer.getDateOfBirth());
            repo.save(oldCustomer);
            return new ResponseEntity<Customer> (oldCustomer, HttpStatus.OK);
        }
        else{
            // Customer have not been found -> return empty customer
            return new ResponseEntity<Customer> (new Customer(), HttpStatus.OK);
        }
    }

    // Create new Customer
    @PostMapping(value = "/customer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>  newCustomer(@RequestBody Customer customer, UriComponentsBuilder ucBuilder) {
        Long savedCustomerId = repo.save(customer).getId();
        HttpHeaders headers = new HttpHeaders();
        // Sets a header with direct path to created Customer
        headers.setLocation(ucBuilder.path("/customer/{id}").buildAndExpand(customer.getId()).toUri());
        // Sends header to client
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // Delete Customer by id
    @DeleteMapping(value = "/customer/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> deleteCustomer(@PathVariable String id) {
        Optional<Customer> maybeOldCustomer = repo.findById(Long.parseLong(id));
        // If Contract was found, delete it
        if(maybeOldCustomer.isPresent()){
            Customer oldCustomer = maybeOldCustomer.get();
            repo.delete(oldCustomer);
            return new ResponseEntity<Customer> (oldCustomer, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Customer> (HttpStatus.NO_CONTENT);
        }
    }
}
