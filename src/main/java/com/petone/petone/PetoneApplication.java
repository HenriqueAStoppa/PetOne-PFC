package com.petone.petone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Ponto de entrada principal da aplicação Spring Boot.
 */

@SpringBootApplication

@EnableMongoRepositories(basePackages = "com.petone.petone.repository")
@ComponentScan(basePackages = "com.petone.petone")
public class PetoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetoneApplication.class, args);
    }
}