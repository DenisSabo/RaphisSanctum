package vv.assignment.restful.user;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * User has a role, which gives him different access rights
 * Not used currently.
 */
@Entity
public class RolesAccess {
    @Id
    private String role;
    private boolean readAccess;
    private boolean writeAccess;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public boolean isReadAccess() {
        return readAccess;
    }

    public void setReadAccess(boolean readAccess) {
        this.readAccess = readAccess;
    }
    public boolean isWriteAccess() {
        return writeAccess;
    }

    public void setWriteAccess(boolean writeAccess) {
        this.writeAccess = writeAccess;
    }
}