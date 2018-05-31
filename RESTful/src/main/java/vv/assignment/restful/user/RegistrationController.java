package vv.assignment.restful.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import vv.assignment.restful.Customer;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RegistrationController {
    @Autowired
    UserRepository repo;

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
            try{
                userService.saveUser(user);
                return new ResponseEntity<User>(user, HttpStatus.CREATED);
            }
            // Sometimes User cannot be saved, because of Constraints in definition (for example @NotNull)
            catch(org.hibernate.exception.ConstraintViolationException ex){
                // Additional information about what constraint got violated
                HttpHeaders headers = new HttpHeaders();
                // TODO mehr Info und eher in Body
                headers.add("ConstraintViolation", ex.getConstraintName());
                return new ResponseEntity<User>(headers, HttpStatus.CONFLICT);
            }
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

    // Get one specific user
    @GetMapping(value = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> findOne(@PathVariable String username) {
        Optional<User> maybeUser = repo.findByUsername(username);
        if(maybeUser.isPresent()){
            return new ResponseEntity<User>(maybeUser.get(), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<User>(new User(), HttpStatus.NO_CONTENT);
        }
    }

    // Deletes user by username
    @DeleteMapping(value = "/user/{username}")
    public ResponseEntity<User> deleteByUsername(@PathVariable String username) {
        Optional<User> maybeUser= repo.findByUsername(username);
        if(maybeUser.isPresent()){
            User oldUser = maybeUser.get();
            repo.delete(oldUser);
            return new ResponseEntity<User> (oldUser, HttpStatus.OK);
        }
        else{
            // Customer was not found -> return empty customer
            return new ResponseEntity<User> (new User(), HttpStatus.NO_CONTENT);
        }
    }
}
