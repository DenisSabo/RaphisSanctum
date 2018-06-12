package vv.assignment.restful.user.UserExceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="User was not found")
public class UserNotFoundException extends RuntimeException {
}
