package com.translogistics.fleetmanagmentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FleetManagmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetManagmentSystemApplication.class, args);
    }

}
