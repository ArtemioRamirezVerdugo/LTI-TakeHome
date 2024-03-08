package com.oimzen.services.movieschallenge;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
@NoArgsConstructor
public class MoviesChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviesChallengeApplication.class, args);
    }

}
