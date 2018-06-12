package vv.assignment.restful.Customer.CustomerExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown, if a customer, that a client requested for, cannot be found
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="No Customer with passed ID exists")
public class CustomerNotFoundException extends RuntimeException {
    // Do nothing -> Annotations do all the work
}
