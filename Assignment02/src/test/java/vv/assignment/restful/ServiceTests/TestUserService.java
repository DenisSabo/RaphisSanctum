package vv.assignment.restful.ServiceTests;

import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Proxy.UserManagement;
import vv.assignment.restful.user.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TestUserService {

    /**
     * Proxy for interacting with user controller
     */
    UserManagement userProxy = new UserManagement();

    @AfterEach
    public void deleteAllUsers(){
        userProxy.deleteAll();
    }


    /**
     * Test if creation of new users work
     */
    @Test
    public void createAccount(){

        User user = new User("TestClient", "ValidPassword", null);

        // create user in database
        ResponseEntity<Void> response1 = userProxy.createEntity(user);

        // test if new user was created successfully by looking at status code
        assertThat(response1.getStatusCode(), equalTo(HttpStatus.CREATED));

        // test if created successfully by requesting for user and checking properties
        ResponseEntity<User> userInResponse = userProxy.getEntity(response1.getHeaders().getLocation());
        assertThat(userInResponse.getBody().getUsername(), equalTo("TestClient"));

        // Password is encoded with BCrypt, so the BCryptPasswordEncoder have to compare raw and encoded password
        assert(passwordEncoder().matches("ValidPassword", userInResponse.getBody().getPassword()));
        assertThat(userInResponse.getBody().getRole(), equalTo(null));
    }


    /**
     * Testcase: Somebody tries to create same account two times
     */
    @Test
    public void createSameAccounts(){

        User user = new User("Account", "ValidPassword", null);
        User sameUser = new User("Account", "ValidPassword", null);

        // create user
        ResponseEntity<Void> response1 = userProxy.createEntity(user);

        // create second user that is the same
        ResponseEntity<Void> response2 = userProxy.createEntity(sameUser);

        // First request should be successful
        assertThat(response1.getStatusCode(), equalTo(HttpStatus.CREATED));

        // second request was not successfull
        assertThat(response2.getStatusCode(), equalTo(HttpStatus.CONFLICT));
    }

    @Test
    public void createInvalidAccount(){
        // User with password that is to short
        User user = new User("Account", "1", null);

        // try to create user
        ResponseEntity<Void> response = userProxy.createEntity(user);

        // second request was not successfull
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }



    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
