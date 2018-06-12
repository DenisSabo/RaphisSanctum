package vv.assignment.restful.Contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import vv.assignment.restful.Contract.ContractExceptions.ContractAlreadyChangedException;
import vv.assignment.restful.Contract.ContractExceptions.ContractAlreadyExistsException;
import vv.assignment.restful.Contract.ContractExceptions.ContractNotFoundException;
import vv.assignment.restful.Contract.ContractExceptions.ContractReferencedByCustomerException;

import java.util.Optional;


@ComponentScan("ContractRepository")
@RestController
public class ContractService {

    /**
     * Operations on database will be made with this repo
     */
    @Autowired
    private ContractRepository repo;



    /**
     * Updates existing contract
     * @param id of contract that has to be updated
     * @param newContract that will update contract with defined id
     * @returns ResponseEntity<Contract> that contains updated contract
     */
    @PutMapping(value = "/contract/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contract> changeContract(
            @PathVariable String id, @RequestBody Contract newContract) {

        // Find Contract by ID
        Optional<Contract> maybeOldContract = repo.findById(Long.parseLong(id));

        if(!maybeOldContract.isPresent()) {
            throw new ContractNotFoundException();
        }
        else{

            Contract oldContract = maybeOldContract.get();

            // Instance that will contain the updated data
            Contract updatedContract = oldContract;

            // Perform set of changes
            updatedContract.setKindOfContract(newContract.getKindOfContract());
            updatedContract.setYearlyFee(newContract.getYearlyFee());

            // Prevent lost update problem (Strategy: First update wins)
            if(oldContract.getVersion() != newContract.getVersion()) throw new ContractAlreadyChangedException();

            // increment version
            updatedContract.increment();

            //Saves altered contract to repo
            try{
                repo.save(updatedContract);
            }
            catch(DataIntegrityViolationException ex){
                // it could be, that this changed contract will be the same as one, that is already existing in db
                throw new ContractAlreadyExistsException();
            }

            return new ResponseEntity<Contract> (updatedContract, HttpStatus.OK);
        }
    }



    /**
     * Gets contract by id
     * @param id of contract
     * @returns a ResponseEntity<Contract> containing the requested contract
     */
    @GetMapping(value="/contract/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contract> getContract(@PathVariable String id){

        Optional<Contract> contract = repo.findById(Long.parseLong(id));

        //  Returns Customer or empty customer
        if(!contract.isPresent()) {
            throw new ContractNotFoundException();
        }
        else{
            return new ResponseEntity<Contract>(contract.get(), HttpStatus.OK);
        }
    }



    /**
     * Creates new contract
     * @param contract that will be saved in database
     * @param ucBuilder
     * @returns ResponseEntity<Void> with location to created resource
     */
    @PostMapping(value = "/contract", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void>  newContract(@RequestBody Contract contract, UriComponentsBuilder ucBuilder) {
        try{
            repo.save(contract);
        }
        catch(DataIntegrityViolationException ex){
            throw new ContractAlreadyExistsException();
        }

        HttpHeaders headers = new HttpHeaders();

        // Sets a header with direct path to created Contract
        headers.setLocation(ucBuilder.path("/contract/{id}").buildAndExpand(contract.getId()).toUri());

        // Sends header to client
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }



    /**
     * Deletes contract by ID
     * @param id of contract that has to be deleted
     * @returns a ResponseEntity<Contract> containing the old contract in response body
     */
    @DeleteMapping(value = "/contract/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contract> deleteContract(@PathVariable String id){

        Optional<Contract> maybeOldContract = repo.findById(Long.parseLong(id));

        if(!maybeOldContract.isPresent()) {
            throw new ContractNotFoundException();
        }
        else{
            Contract oldContract = maybeOldContract.get();

            try {
                repo.delete(oldContract);
            }
            catch(DataIntegrityViolationException ex){
                // You cannot delete a contract if it is still in use by a Customer
                throw new ContractReferencedByCustomerException();
            }

            return new ResponseEntity<Contract> (oldContract, HttpStatus.OK);
        }
    }



    /**
     * Deletes all contracts in database
     */
    @DeleteMapping(value = "/contracts")
    public void deleteContracts() {
        repo.deleteAll();
    }
}
