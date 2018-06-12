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
import org.springframework.web.util.UriComponentsBuilder;
import vv.assignment.restful.user.UserExceptions.InvalidUserException;
import vv.assignment.restful.user.UserExceptions.UserAlreadyExistsException;
import vv.assignment.restful.user.UserExceptions.UserNotFoundException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RegistrationService {
    @Autowired
    UserRepository repo;

    @Autowired
    CustomUserDetailsService userService;
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates new user entry in database
     * @param user that will be saved to database
     * @returns the new user
     */
    @PostMapping(value = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> submitRegistration(@Valid @RequestBody User user, UriComponentsBuilder ucBuilder,
                                                   BindingResult result) {

        // result of validation
        if(result.hasErrors()){
            throw new InvalidUserException();
        }
        else{
            // Encodes password
            user.setPassword(passwordEncoder().encode(user.getPassword()));

            try{
                // try to save new user
                userService.saveUser(user);
            }
            // throws error if user already exists
            catch(org.springframework.dao.DataIntegrityViolationException ex){
                throw new UserAlreadyExistsException();
            }

            HttpHeaders headers = new HttpHeaders();

            // Sets a header with direct path to created Customer
            headers.setLocation(ucBuilder.path("/user/{username}").buildAndExpand(user.getUsername()).toUri());

            return new ResponseEntity(headers, HttpStatus.CREATED);
        }
    }



    /**
     * @returns all users from database
     */
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< List<User>> findAll() {
        List<User> liste = new ArrayList<>();
        Iterable<User> iterator = userService.getAllUsers();
        iterator.forEach(liste::add);
        return new ResponseEntity<List<User>>(liste, HttpStatus.OK);
    }


    /**
     * gets one specific user by username
     * @param username of user that will be searched for
     * @returns the user with passed username if exists
     */
    @GetMapping(value = "/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> findOneByUsername(@PathVariable String username) {

        Optional<User> maybeUser = repo.findByUsername(username);

        if(maybeUser.isPresent()){
            return new ResponseEntity<User>(maybeUser.get(), HttpStatus.OK);
        }
        else{
            throw new UserNotFoundException();
        }
    }



    /**
     * deletes user by username
     * @param username of user that will be deleted if exists
     * @returns user with passed username
     */
    @DeleteMapping(value = "/user/{username}")
    public ResponseEntity<User> deleteByUsername(@PathVariable String username) {

        Optional<User> maybeUser= repo.findByUsername(username);

        if(maybeUser.isPresent()){

            User oldUser = maybeUser.get();

            // delete user
            repo.delete(oldUser);

            // returns deleted user and status
            return new ResponseEntity<> (oldUser, HttpStatus.OK);
        }
        else{

            throw new UserNotFoundException();
        }
    }

    @DeleteMapping(value="/users")
    public void deleteAll(){
        repo.deleteAll();
    }
}
