package vv.assignment.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

@ComponentScan("ContractRepository")
@RestController
public class ContractService {

    @Autowired
    private ContractRepository repo;

    // TODO Implement Search

    // Update existing Contract
    @PutMapping(value = "/contract/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contract> changeContract(
            @PathVariable String id, @RequestBody Contract newContract) {
        // Find Contract by ID
        Optional<Contract> maybeOldContract = repo.findById(Long.parseLong(id));
        if(maybeOldContract.isPresent()){
            Contract oldContract = maybeOldContract.get();
            oldContract.setKindOfContract(newContract.getKindOfContract());
            oldContract.setYearlyFee(newContract.getYearlyFee());
            //Saves altered contract to repo
            repo.save(oldContract);
            return new ResponseEntity<Contract> (oldContract, HttpStatus.OK);
        }
        else{
            // Customer was not found -> return empty customer
            return new ResponseEntity<Contract> (new Contract(), HttpStatus.NOT_MODIFIED);
        }
    }

    @GetMapping(value="/contract/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contract> getContract(@PathVariable String id){
        Optional<Contract> contract = repo.findById(Long.parseLong(id));
        //  Returns Customer or empty customer
        if(contract.isPresent()){
            return new ResponseEntity<Contract>(contract.get(), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Contract>(HttpStatus.NO_CONTENT);
        }
    }

    // Create new Customer
    @PostMapping(value = "/contract", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>  newContract(@RequestBody Contract contract, UriComponentsBuilder ucBuilder) {
        repo.save(contract);
        HttpHeaders headers = new HttpHeaders();
        // Sets a header with direct path to created Contract
        headers.setLocation(ucBuilder.path("/contract/{id}").buildAndExpand(contract.getId()).toUri());
        // Sends header to client
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // Delete Contract by ID
    @DeleteMapping(value = "/contract/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contract> deleteContract(@PathVariable String id) {
        Optional<Contract> maybeOldContract = repo.findById(Long.parseLong(id));
        // If Contract was found, try to delete it
        if(maybeOldContract.isPresent()){
            Contract oldContract = maybeOldContract.get();
            try{
                repo.delete(oldContract);
            }
            catch(org.springframework.dao.DataIntegrityViolationException ex){
                /**
                 * Sometimes it is not possible to delete a contract
                 * because it is referenced by a customer.
                 */
                System.out.println(ex);
                return new ResponseEntity<Contract>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<Contract> (oldContract, HttpStatus.OK);
        }
        else{
            // Contract was not found -> return empty Contract
            // TODO maybe wastes ID-Counter
            return new ResponseEntity<Contract> (new Contract(), HttpStatus.OK);
        }
    }
}
