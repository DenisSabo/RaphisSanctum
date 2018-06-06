package vv.assignment.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("vv.assignment.restful")
@ComponentScan({"vv.assignment.restful"})
@EntityScan("vv.assignment.restful")
@EnableWebSecurity
@EnableWebMvc
// TODO Endpoint: GET: /customer/{id}/contracts (wenn möglich), sonst /customer/contracts?customer_id
// TODO TestCustomerContractService fertig
// TODO Refactoring der redundanten Funktionen in den Test-Klassen
// TODO Verschieben der Test-Klassen in den vorgesehenen Ordner
// TODO Aufgabe 4 weiter machen
public class RestfulApplication {

	/**
	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}
	*/

	public static void main(String[] args) {
		SpringApplication.run(RestfulApplication.class, args);
	}
}
