package vv.assignment.restful.Customer.CustomerExceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown, if a customer, that a client requested for, cannot be found
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Passed customer is not valid")
public class InvalidCustomerException extends RuntimeException {
    // ...
}
