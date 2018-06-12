package vv.assignment.restful.Contract.ContractExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown when somebody got a entity of a contract and performed changes on it,
 * but during that, somebody updated this entity already and so, the new update would lead to a lost of the old one
 * (LOST UPDATE PROBLEM)
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason="Somebody changed this contract before you did")
public class ContractAlreadyChangedException extends RuntimeException {
    // ...
}
