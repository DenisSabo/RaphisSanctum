package vv.assignment.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("vv.assignment.restful")
@ComponentScan({"vv.assignment.restful"})
@EntityScan("vv.assignment.restful")
@EnableWebSecurity
@EnableWebMvc
// TODO TestCustomerContractService fertig
// TODO Dokumentation verbessen
// TODO Tests sollen Verhalten prüfen (und nicht Status Codes usw.)
// TODO add class with static customers and stuff ...
// TODO Nicht mehr möglich nicht-enum-Werte für Contracts zu posten, aber fange Fehler ab und setzte HttpStatus
// TODO Primärschlüssel für Customer (alle spalten) oder irwas anderes (unique)
// TODO ManyToOne() -> Viele Contracts können zu einem User gehören -> API erweitern damit das möglich ist

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
