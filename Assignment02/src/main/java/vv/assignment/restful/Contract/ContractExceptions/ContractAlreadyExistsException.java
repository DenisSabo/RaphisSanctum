package vv.assignment.restful.Contract.ContractExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown, if client tries to save contract which already exists in database
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason="This contract already exists")
public class ContractAlreadyExistsException extends RuntimeException{
    // ...
}
