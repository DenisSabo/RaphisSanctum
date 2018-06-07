package vv.assignment.restful.Proxy.UserProxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import vv.assignment.restful.Customer;
import vv.assignment.restful.Proxy.CRUDable;
import vv.assignment.restful.user.User;

import java.net.URI;

import static vv.assignment.restful.Proxy.LocalCallConstants.REST_SERVICE_URI;
import static vv.assignment.restful.Proxy.LocalCallConstants.getAuthenticatedRestTemplate;

public class UserManagement implements CRUDable<User>{

    static RestTemplate restTemplate = getAuthenticatedRestTemplate();

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

    /**
     * User API works a lot with usernames, so additional methods are needed
     */

    public ResponseEntity<User> getByUsername(String username){
        return restTemplate.getForEntity(REST_SERVICE_URI+"/user/"+ username, User.class);
    }
}
