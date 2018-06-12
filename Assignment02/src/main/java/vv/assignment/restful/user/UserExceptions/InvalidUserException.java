package vv.assignment.restful.user.UserExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="User is not valid")
public class InvalidUserException extends RuntimeException {
}
