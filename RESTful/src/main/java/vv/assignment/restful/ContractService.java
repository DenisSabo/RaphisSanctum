package vv.assignment.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
        // If Contract was found, delete it
        if(maybeOldContract.isPresent()){
            Contract oldContract = maybeOldContract.get();
            repo.delete(oldContract);
            return new ResponseEntity<Contract> (oldContract, HttpStatus.OK);
        }
        else{
            // Contract was not found -> return empty Contract
            // TODO maybe wastes ID-Counter
            return new ResponseEntity<Contract> (new Contract(), HttpStatus.OK);
        }
    }
}
