package com.tutorial.ecommerce.apiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.tutorial.ecommerce"})
@EnableJpaRepositories("com.tutorial.ecommerce.repository")
@EntityScan("com.tutorial.ecommerce.model")
@ComponentScan({"com.tutorial.ecommerce.security", "com.tutorial.ecommerce.apiservice"})
@EnableAsync
public class ApiserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiserviceApplication.class, args);
    }

}
