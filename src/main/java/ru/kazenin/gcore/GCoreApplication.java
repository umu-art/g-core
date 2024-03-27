package ru.kazenin.gcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GCoreApplication.class, args);
    }

}
