package vv.assignment.restful.Test;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Proxy.UserProxy.UserManagement;
import vv.assignment.restful.user.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Proxy.LocalCallConstants.REST_SERVICE_URI;

public class TestUserService {

    UserManagement userProxy = new UserManagement();

    /**
     * Test if creation of new users work
     */
    @Test
    public void createAccount(){
        RestTemplate restTemplate = new RestTemplate();
        User user = new User("TestClient", "123", null);
        /**
         * Post user to endpoint
         */
        ResponseEntity<Void> response1 = userProxy.createEntity(user);

        // Test if new user was created successfully
        assertThat(response1.getStatusCode(), equalTo(HttpStatus.CREATED));

        // now get recently created user
        ResponseEntity<User> userInResponse = userProxy.getEntity(response1.getHeaders().getLocation());
        assertThat(userInResponse.getBody().getUsername(), equalTo("TestClient"));

        // Password is encoded with BCrypt, so the BCryptPasswordEncoder have to compare raw and encoded password
        assert(passwordEncoder().matches("123", userInResponse.getBody().getPassword()));
        assertThat(userInResponse.getBody().getRole(), equalTo(null));
    }


    /**
     * Testcase: Somebody tries to create same account two times
     * Expected Result: Fitting HttpStatus-Code and Header
     */
    @Test
    public void createSameAccounts(){
        RestTemplate restTemplate = new RestTemplate();

        User user = new User("Account", "123", null);
        User sameUser = new User("Account", "123", null);
        /**
         * Post user to endpoint
         */
        ResponseEntity<Void> response1 = userProxy.createEntity(user);
        /**
         * Post same user again
         */
        ResponseEntity<Void> response2 = userProxy.createEntity(sameUser);

        // First request should be successful
        assertThat(response1.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * Second response should be a bad request
         */
        assertThat(response2.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assert(response2.getHeaders().containsKey("ConstraintViolation"));
    }

    // TODO implement delete account

    // TODO implement change account

    // Later there could be tests which try to create invalid accounts (For example, to short password, etc.)

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
