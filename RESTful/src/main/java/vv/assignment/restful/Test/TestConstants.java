package vv.assignment.restful.Test;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.devtools.remote.client.HttpHeaderInterceptor;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TestConstants {

    /**
     * URL to server
     */
    public static final String REST_SERVICE_URI = "http://localhost:8080";

    /**
     *  This user is put as a Authorization-header to the RestTemplate, to prevent Non-Authorized(401) responses
     */
    public static final String username = "TESTRUNNER";
    public static final String password = "Pass123";
    public static String plainCredentials=username + ":" + password;
    public static final String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));

    public static HttpHeaders getBasicAuthHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        return headers;
    }
}
