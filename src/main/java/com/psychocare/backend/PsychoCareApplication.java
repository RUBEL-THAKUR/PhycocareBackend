package com.psychocare.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PsychoCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsychoCareApplication.class, args);
    }
}
