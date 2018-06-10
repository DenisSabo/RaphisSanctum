package vv.assignment.restful.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Entity
public class User {
    @GeneratedValue
    @Id
    private int id;

    @Size(min = 3, message = "Username must be at least 3 characters long")
    @Column(unique=true) // Real primary key
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    // Possible: Own constraint validator -> Check if password is secure (At least one number, capital case letter, ...)
    private String password;

    @Null // Not actully implemented -> So can only be null at current state
    private String role;

    /**
     * @Email(message = "Email should be valid")
     *   private String email;
     */

    protected User() {
        super();
    }

    /**
     * Normally this constructor will be used, because there is no role yet
     * @param username
     * @param password
     */
    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor that can be used if role is implemented
     * @param username
     * @param password
     * @param role
     */
    public User(String username, String password, String role) {
        super();
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Basic getter and setter
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    // Equals, hashCode and toString

    /**
     * Tests if username is equal, since username has to be unique (real primary)
     * This method does not compare the ID of two instances.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        return result;
    }
}
