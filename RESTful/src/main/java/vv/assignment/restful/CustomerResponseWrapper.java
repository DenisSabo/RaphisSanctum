package vv.assignment.restful;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that contains a Customer instance and additional information for response to client
 * TODO Frage: Möglicherweise unnötige Klasse, weil Informationen auch über Header gesendet werden können
 * TODO https://stackoverflow.com/questions/13963932/rest-error-message-in-http-header-or-response-body
 */
public class CustomerResponseWrapper extends Customer{
    String errorFor404;

    public CustomerResponseWrapper(Customer customer, String errorFor404) {
        super(customer.firstname, customer.lastname, customer.dateOfBirth,
                customer.getAdress(), customer.getContracts());
        this.errorFor404 = errorFor404;
    }
}
