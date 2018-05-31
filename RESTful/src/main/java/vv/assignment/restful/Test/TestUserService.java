package vv.assignment.restful.Test;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.user.User;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static vv.assignment.restful.Test.TestConstants.REST_SERVICE_URI;

public class TestUserService {

    @Before
    public static void deleteAccounts0(){
        RestTemplate restTemplate = new RestTemplate();
        /**
         * Check if user already exists
         */
        User user = new User("TestClient", "123", null);
        ResponseEntity<User> response0 = restTemplate
                .getForEntity(REST_SERVICE_URI+"/user/"+user.getUsername(), User.class);
        // If so, delete this user
        if(response0.getStatusCode().equals(HttpStatus.OK))
            restTemplate.delete(REST_SERVICE_URI+"/user/"+user.getUsername(), User.class);
    }
    // Does the Account/User creation work ?
    @Test
    public void createAccount(){
        RestTemplate restTemplate = new RestTemplate();
        User user = new User("TestClient", "123", null);
        /**
         * Post user to endpoint
         */
        ResponseEntity<User> response1 = restTemplate
                .postForEntity(REST_SERVICE_URI+"/user", user, User.class);

        // New User was created
        assertThat(response1.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * ResponseEntity contains created user
         */
        assertThat(response1.getBody().getUsername(), equalTo("TestClient"));
        // Password is encoded with BCrypt, so the BCryptPasswordEncoder have to compare raw and encoded password
        assert(passwordEncoder().matches("123", response1.getBody().getPassword()));
        assertThat(response1.getBody().getRole(), equalTo(null));
    }

    @Before
    public static void deleteAccounts1(){
        RestTemplate restTemplate = new RestTemplate();
        /**
         * Check if user already exists
         */
        User user = new User("Account", "123", null);
        ResponseEntity<User> response0 = restTemplate
                .getForEntity(REST_SERVICE_URI+"/user/"+user.getUsername(), User.class);
        // If so, delete this user
        if(response0.getStatusCode().equals(HttpStatus.OK))
            restTemplate.delete(REST_SERVICE_URI+"/user/"+user.getUsername(), User.class);
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
        ResponseEntity<User> response1 = restTemplate
                .postForEntity(REST_SERVICE_URI+"/user", user, User.class);
        /**
         * Post same user two times
         */
        ResponseEntity<User> response2 = restTemplate
                .postForEntity(REST_SERVICE_URI+"/user", sameUser, User.class);

        // New User was created
        assertThat(response1.getStatusCode(), equalTo(HttpStatus.CREATED));
        /**
         * Second response should be a bad request
         */
        assertThat(response2.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assert(response2.getHeaders().containsKey("ConstraintViolation"));
    }

    // TODO implement delete account

    // TODO implement change account

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
