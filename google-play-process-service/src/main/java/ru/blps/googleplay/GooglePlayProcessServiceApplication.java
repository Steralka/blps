package ru.blps.googleplay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.blps.googleplay.config.AppSecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppSecurityProperties.class)
public class GooglePlayProcessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GooglePlayProcessServiceApplication.class, args);
    }
}
