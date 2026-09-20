package com.papersense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the PaperSense backend application.
 * Run this class (or `.\gradlew.bat bootRun` / `./gradlew bootRun`) to start the server.
 */
@SpringBootApplication
public class PaperSenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaperSenseApplication.class, args);
    }
}
