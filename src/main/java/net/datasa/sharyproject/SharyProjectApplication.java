package net.datasa.sharyproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class SharyProjectApplication {

	public static void main(String[] args) {

		SpringApplication.run(SharyProjectApplication.class, args);
	}

}
