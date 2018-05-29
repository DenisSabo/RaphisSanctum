package vv.assignment.restful.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vv.assignment.restful.Customer;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RegistrationController {

    @Autowired
    CustomUserDetailsService userService;
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Saves user to database
     */
    @PostMapping(value = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> submitRegistration(@Valid @RequestBody User user, BindingResult result) {
        // Result of validation
        if(result.hasErrors()){
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
        else{
            user.setPassword(passwordEncoder().encode(user.getPassword()));
            userService.saveUser(user);
            return new ResponseEntity<User>(user, HttpStatus.CREATED);
        }
    }

    // Get all users
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< List<User>> findAll() {
        List<User> liste = new ArrayList<>();
        Iterable<User> iterator = userService.getAllUsers();
        iterator.forEach(liste::add);
        return new ResponseEntity<List<User>>(liste, HttpStatus.OK);
    }
}
