package vv.assignment.restful;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Represents a User of this application
 */
@Entity
public class User {
    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    // Email, firstname and other data is possible

    // Constructor
    public User(){
        //default constructor
    }

    public User(@NotNull @NotEmpty String username, @NotNull @NotEmpty String password, String matchingPassword) {
        this.username = username;
        this.password = password;
        this.matchingPassword = matchingPassword;
    }

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
