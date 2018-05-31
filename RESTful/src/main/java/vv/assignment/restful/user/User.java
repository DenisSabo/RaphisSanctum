package vv.assignment.restful.user;

import org.springframework.context.annotation.Bean;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Entity
public class User {
    @GeneratedValue
    @Id
    private int id;
    @NotNull @Column(unique=true)
    private String username;
    @NotNull
    private String password;
    @Null
    private String role;

    protected User() {
        super();
    }

    public User(String username, String password, String role) {
        super();
        this.username = username;
        this.password = password;
        this.role = role;
    }

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
}
