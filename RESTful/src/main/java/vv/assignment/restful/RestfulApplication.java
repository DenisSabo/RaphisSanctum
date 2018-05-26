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
