package vv.assignment.restful.Proxy;

import org.springframework.http.ResponseEntity;

import java.net.URI;

public interface CRUDable<T> {
    void listAllEntities();
    ResponseEntity<Void> createEntity(T entity);
    ResponseEntity<T> getEntity(URI location);
    void updateEntity(String id, T newEntity);
    void deleteEntity(Long id);
}
