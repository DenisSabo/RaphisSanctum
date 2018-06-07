package vv.assignment.restful.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Role can be found by it's primary key, which is the role name
 */
@Repository
public interface RolesAccessRepository extends JpaRepository<RolesAccess, String> {
    RolesAccess findByRole(String role);
}
