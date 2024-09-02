package com.homefit.backend;

import com.homefit.backend.login.config.properties.AppProperties;
import com.homefit.backend.login.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
		CorsProperties.class,
		AppProperties.class
})
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
