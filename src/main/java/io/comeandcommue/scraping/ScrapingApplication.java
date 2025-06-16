package io.comeandcommue.scraping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScrapingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapingApplication.class, args);
	}

}
