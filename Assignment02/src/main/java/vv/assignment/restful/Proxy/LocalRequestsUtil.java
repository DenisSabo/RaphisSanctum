package vv.assignment.restful.Proxy;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.user.User;

public interface LocalRequestsUtil {
    /**
     * URL to server
     */
    public static final String REST_SERVICE_URI = "https://localhost:8443";
    RestTemplate restTemplate = new RestTemplate();



    /**
     *  User credentials for Account, that will be used for making requests to secured endpoints
     *  (Avoids "Not-Authorized"-responses (401))
     */
    public static final String username = "TESTRUNNER";
    public static final String password = "Pass123";
    public static final String role = null;


    public static String plainCredentials=username + ":" + password;
    public static final String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));

    /**
     *
     * @returns a header with a BasicAuth credentials, that can be used for authentication
     */
    public static HttpHeaders getBasicAuthHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        return headers;
    }

    public static RestTemplate getAuthenticatedRestTemplate(){
        // disables the default error handling in tests
        disableErrorHandler(restTemplate);
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        return restTemplate;
    }

    /**
     * Test-User may not exists, so we a function that creates one, if none exists
     */
    public static void createTestUser() {
        RestTemplate noAuthTemplate = new RestTemplate();
        /**
         * User that will be used for authentication during requests to secured endpoints
         */
        User testUser = new User(username, password, null);
        /**
         * Check if user exists already
         */
        ResponseEntity<User> mayGotUserRes =
                noAuthTemplate.getForEntity(REST_SERVICE_URI+"/user/"+ testUser.getUsername(), User.class);

        // If no user was found, make post request so server can save user
        if(mayGotUserRes.getStatusCode().equals(HttpStatus.NO_CONTENT)){

            ResponseEntity<User> postUserRes =
                    noAuthTemplate.postForEntity(REST_SERVICE_URI+"/user", testUser, User.class);
        }
        else if(mayGotUserRes.getStatusCode().equals(HttpStatus.OK)){
            // User exists already -> Do nothing
        }
    }

    public static void deleteTestUser(){
        RestTemplate noAuthTemplate = new RestTemplate();
        noAuthTemplate.delete(REST_SERVICE_URI+"/user/"+ username);
    }

    public static RestTemplate disableErrorHandler(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }});
        return restTemplate;
    }
}
