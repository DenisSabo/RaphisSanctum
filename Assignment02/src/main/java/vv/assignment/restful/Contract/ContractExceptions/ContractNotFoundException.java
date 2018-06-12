package vv.assignment.restful.Contract.ContractExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown, if a contract, that a client requested for, cannot be found
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="No Contract with passed ID exists")
public class ContractNotFoundException extends RuntimeException {
    // ...
}
