package vv.assignment.restful;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.security.KeyStore;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;



public class TestClient {
    public static final String REST_SERVICE_URI = "http://localhost:8080";

    /**
    RestTemplate restTemplate = new RestTemplate();
    String fooResourceUrl
            = "http://localhost:8080/spring-rest/foos";
    ResponseEntity<String> response
            = restTemplate.getForEntity(fooResourceUrl + "/1", String.class);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    */

    private static void listAllCustomers(){
        RestTemplate restTemplate = new RestTemplate();
        // Get request to endpoint which returns all customers
        // TODO returns List<Customer>: How does it become List<LinkedHashMap<String, Object>>
        List<LinkedHashMap<String, Object>> customerMap =
                restTemplate.getForObject(REST_SERVICE_URI+"/customers/", List.class);

        if(customerMap != null){
            for(LinkedHashMap<String, Object> map : customerMap){
                System.out.println("Customer : id="+map.get("id")
                        + ", Firstname="+map.get("firstname")
                        + ", Lastname="+map.get("lastname")
                        + ", Date of Birth="+map.get("dateofbirth"));
            }
        }else{
            System.out.println("No customers");
        }
    }

    private static void getCustomer(){
        RestTemplate restTemplate = new RestTemplate();
        // Customer customer = restTemplate.getForObject(REST_SERVICE_URI+"/customer/1", Customer.class);
        ResponseEntity<Customer> customer = restTemplate.getForEntity(REST_SERVICE_URI+"/customer/1", Customer.class);
        System.out.println(customer);
    }

    private static void createCustomer() {
        RestTemplate restTemplate = new RestTemplate();
        Adress adressOfKlaus = new Adress("Hochschulstraße 1", "83022", "Rosenheim");
        Customer klaus = new Customer
                ("Klaus", "Kaiser", LocalDate.of( 2015 , Month.JUNE , 7 ), adressOfKlaus);
        URI uri = restTemplate.postForLocation(REST_SERVICE_URI+"/customer/"
                , klaus, Customer.class);
        System.out.println("Location : "+uri.toASCIIString());
    }

    // Updates Customer Klaus (ID = 1) to Tommy
    private static void updateCustomer() {
        RestTemplate restTemplate = new RestTemplate();
        // Gets old customer
        Customer oldCustomer = restTemplate.getForObject
                (REST_SERVICE_URI+"/customer/credentials?firstname=Klaus&lastname=Kaiser&birthofdate=2015-06-07", Customer.class);
        // Adress remains the same
        Adress oldAdress = oldCustomer.getAdress();
        // New customer Tommy
        Customer tommy  = new Customer
                ("Tommy", "Pinnball", LocalDate.of( 2018 , Month.MAY , 24 ), oldAdress);
        restTemplate.put(REST_SERVICE_URI+"/customer/1", tommy);
        System.out.println(tommy);
    }

    private static void deleteCustomer() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(REST_SERVICE_URI+"/customer/3");
    }

    public static void main(String args[]){
        //listAllCustomers();
        getCustomer();
        createCustomer();
        createCustomer();
        listAllCustomers();
        updateCustomer();
        createCustomer();
        listAllCustomers();
        deleteCustomer();
        listAllCustomers();
    }
}
