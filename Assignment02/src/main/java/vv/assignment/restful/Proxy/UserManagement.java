package vv.assignment.restful.Proxy;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.user.User;

import java.net.URI;

import static vv.assignment.restful.Proxy.LocalRequestsUtil.REST_SERVICE_URI;
import static vv.assignment.restful.Proxy.LocalRequestsUtil.disableErrorHandler;
import static vv.assignment.restful.Proxy.LocalRequestsUtil.getAuthenticatedRestTemplate;

public class UserManagement implements CRUDable<User>{

    static RestTemplate restTemplate = disableErrorHandler(new RestTemplate());

    public UserManagement(){
        // default constructor
    }

    @Override
    public void listAllEntities() {

    }

    @Override
    public ResponseEntity<Void> createEntity(User user) {
        return restTemplate.postForEntity(REST_SERVICE_URI + "/user", user, Void.class);
    }

    @Override
    public ResponseEntity<User> getEntity(URI location) {
        ResponseEntity<User> response =
                restTemplate.getForEntity(location.toString(), User.class);
        return response;
    }

    @Override
    public void updateEntity(String id, User newUser) {
        restTemplate.put(REST_SERVICE_URI+"/user/"+id, newUser, Void.class);
    }

    @Override
    public void deleteEntity(Long id) {
        restTemplate.delete(REST_SERVICE_URI+"/user/"+id, User.class);
    }


    public void deleteAll(){
        restTemplate.exchange(REST_SERVICE_URI + "/users", HttpMethod.DELETE, null, Void.class);
    }


    /**
     * User API works a lot with usernames, so additional methods are needed
     */

    public ResponseEntity<User> getByUsername(String username){
        return restTemplate.getForEntity(REST_SERVICE_URI+"/user/"+ username, User.class);
    }
}
