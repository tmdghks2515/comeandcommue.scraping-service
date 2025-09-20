package io.comeandcommue.scraping;

import io.comeandcommue.lib.data.auditor.EnableAuditorAware;
import io.comeandcommue.lib.web.exception.EnableGlobalExceptionHandling;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableAuditorAware
@EnableGlobalExceptionHandling
@ComponentScan(basePackages = {
		"io.comeandcommue.scraping",
		"io.comeandcommue.lib.web",
		"io.comeandcommue.lib.data"
})
public class ScrapingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapingApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// timezone 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
