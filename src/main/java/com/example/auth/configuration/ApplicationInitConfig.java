package com.example.auth.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationInitConfig {

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("Run this at running application");
        };
    }
}
