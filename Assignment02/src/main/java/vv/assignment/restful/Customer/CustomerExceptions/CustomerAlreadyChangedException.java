package vv.assignment.restful.Customer.CustomerExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that will be thrown when somebody got a entity of a customer and performed changes on it,
 * but during that, somebody updated this entity already and so, the new update would lead to a lost of the old one
 * (LOST UPDATE PROBLEM)
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason="Somebody changed the customer before you did")
public class CustomerAlreadyChangedException extends RuntimeException {
    // Do nothing -> Annotations do all the work
}
