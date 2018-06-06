package vv.assignment.restful;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * This class accepts a generic so any entity can be returned.
 * Will be used in addition to ResponseEntity<ResponseBodyWrapper<T>>
 *     so additional information will be contained in the body
 * @param <T> entity that will be returned in Body
 */
public class ResponseBodyWrapper<T>{
    /**
     * Entity that will be wrapped
     */
    T responseEntity;

    // Status code (contained in ResponseEntity<>, but saved here too for understanding)
    HttpStatus status;

    // Message, that adds additional information to Http-Status-Code
    String message;

    // additional fields possible like "Int apiCode;" and "URL more_info;" referencing to an doc for API

    /**
     * This API will reduce the usage of Status-Codes to the most important ones
     * Source: https://blog.restcase.com/rest-api-error-codes-101/
     */
    public static final HttpStatus[] USED_STATUS =
            new HttpStatus[] { HttpStatus.OK, HttpStatus.CREATED, HttpStatus.BAD_REQUEST,
                                HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN, HttpStatus.INTERNAL_SERVER_ERROR};
    public static final Set<HttpStatus> API_USED_STATUS = new HashSet<>(Arrays.asList(USED_STATUS));

    public ResponseBodyWrapper(T responseEnitity, HttpStatus status){
        // API only uses certain HttpStatus
        if(!isAllowedStatus(status))
            throw new IllegalArgumentException("Following Http-Status are allowed: " + API_USED_STATUS.toString());

        this.responseEntity = responseEnitity;
        this.status = status;
    }

    public ResponseBodyWrapper(T responseEntity, HttpStatus status, String message){
        if(!isAllowedStatus(status))
            throw new IllegalArgumentException("Following Http-Status are allowed: " + API_USED_STATUS.toString());

        this.responseEntity = responseEntity;
        this.status = status;
        this.message = message;
    }

    private boolean isAllowedStatus(HttpStatus status){
        return API_USED_STATUS.contains(status);
    }

}
