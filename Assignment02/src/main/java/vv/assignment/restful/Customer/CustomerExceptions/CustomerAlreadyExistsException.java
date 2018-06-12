package vv.assignment.restful.Customer.CustomerExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown, if client tries to save customer who already exists in database
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason="This customer already exists")
public class CustomerAlreadyExistsException extends RuntimeException {
    // Do nothing -> Annotations do all the work
}
