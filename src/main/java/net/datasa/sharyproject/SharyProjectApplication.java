package net.datasa.sharyproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SharyProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharyProjectApplication.class, args);
	}

}
