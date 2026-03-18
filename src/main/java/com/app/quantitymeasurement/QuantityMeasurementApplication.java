package com.app.quantitymeasurement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QuantityMeasurementApplication - UC17 Spring Boot entry point.
 * Bootstraps the Spring context, embedded Tomcat, JPA, Security, and Swagger.
 *
 * Run with: mvn spring-boot:run
 * Swagger:  http://localhost:8080/swagger-ui.html
 * H2:       http://localhost:8080/h2-console
 * Health:   http://localhost:8080/actuator/health
 *
 * Prerit Jain
 * 17.0
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title       = "Quantity Measurement REST API",
        version     = "17.0",
        description = "Spring Boot REST API for quantity measurement operations " +
                      "including comparison, conversion, addition, subtraction, and division."
    )
)
public class QuantityMeasurementApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementApplication.class, args);
    }
}
