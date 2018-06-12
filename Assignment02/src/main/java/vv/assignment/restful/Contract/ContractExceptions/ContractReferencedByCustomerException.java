package vv.assignment.restful.Contract.ContractExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown, if client tries to delete contract, but contract is used by customer
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason="This contract is currently used by a customer. You cannot delete it")
public class ContractReferencedByCustomerException extends RuntimeException {
    // ...
}
