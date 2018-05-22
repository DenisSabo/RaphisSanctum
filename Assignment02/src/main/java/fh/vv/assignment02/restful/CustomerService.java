package fh.vv.assignment02.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    @RequestMapping(value = "/customer/{id}", method= RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> findById(@PathVariable String id) {
        Optional<Customer> k = repo.findById(Long.parseLong(id));
                                                        // TODO new Customer() gute Lösung??
        //  Returns Customer or empty customer
        return new ResponseEntity<Customer> (k.orElse(new Customer()), HttpStatus.OK);
    }

    @RequestMapping(value = "/customers", method= RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< List<Customer>> findAll() {
        List<Customer> liste = new ArrayList<>();
        Iterable<Customer> iterator = repo.findAll();
        iterator.forEach(liste::add);
        return new ResponseEntity<List<Customer>>(liste, HttpStatus.OK);
    }

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
            // TODO
        }
        else if(street.isPresent() && postalCode.isPresent() && place.isPresent()){
            // TODO
        }
        else if(postalCode.isPresent() && place.isPresent()){
            // TODO
        }
        else if(place.isPresent()){
            // TODO
        }
        else if(dateOfBirth.isPresent()){
            // TODO
        }
        else{
            // ...
        }
    }
    /**

    @RequestMapping(value = "/customer/credentials", method= RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Customer>> findAllByFirstnameAndLastname
            (@RequestParam(value="firstname") String firstname, @RequestParam(value="lastname", required = true) String lastname) {
        List<Customer> customers = new ArrayList<>();
        Iterable<Customer> customersWithSameName = repo.findCustomersByFirstnameAndLastname(firstname, lastname);
        customersWithSameName.forEach(customers::add);
        return new ResponseEntity<List<Customer>> (customers, HttpStatus.OK);
    }

    @RequestMapping(value = "/customer/credentials", method= RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Customer>> findAllByDateofbirth(@RequestParam(value="dateofbirth") Date dateofbirth) {
        List<Customer> customers = new ArrayList<>();
        Iterable<Customer> customersWithSameName = repo.findCustomersByDateOfBirth(dateofbirth);
        customersWithSameName.forEach(customers::add);
        return new ResponseEntity<List<Customer>> (customers, HttpStatus.OK);
    }
    */
}
