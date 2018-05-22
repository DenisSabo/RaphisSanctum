package fh.vv.assignment02.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

// TODO FRAGEN:
// 1. One to many oder many to one ??
// TODO Aufgaben:
// 1. Vielleicht alles in Englisch
// 2. Service implementieren

@SpringBootApplication
public class RestfulApplication {

	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}

	public static void main(String[] args) {
		SpringApplication.run(RestfulApplication.class, args);
	}
}
