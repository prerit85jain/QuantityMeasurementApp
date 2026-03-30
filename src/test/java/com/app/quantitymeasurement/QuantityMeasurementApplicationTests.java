package com.app.quantitymeasurement;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityInputDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Quantity Measurement Application using Spring Boot Test.
 * Uses @SpringBootTest to start the full application context and TestRestTemplate
 * for true HTTP integration testing against the embedded server.
 *
 * @author Developer
 * @version 17.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuantityMeasurementApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/quantities";
    }

    // ─── Helper factory methods ───────────────────────────────────────────────

    private QuantityDTO dto(double value, String unit, String type) {
        return new QuantityDTO(value, unit, type);
    }

    private QuantityInputDTO input(QuantityDTO a, QuantityDTO b) {
        QuantityInputDTO in = new QuantityInputDTO();
        in.setThisQuantityDTO(a);
        in.setThatQuantityDTO(b);
        return in;
    }

    private QuantityInputDTO inputWithTarget(QuantityDTO a, QuantityDTO b, QuantityDTO t) {
        QuantityInputDTO in = new QuantityInputDTO();
        in.setThisQuantityDTO(a);
        in.setThatQuantityDTO(b);
        in.setTargetQuantityDTO(t);
        return in;
    }

    // ─── Application context ──────────────────────────────────────────────────

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts successfully
        assertNotNull(restTemplate);
    }

    // ─── Compare ─────────────────────────────────────────────────────────────

    @Test
    void testCompare_1Foot_12Inches_ReturnsTrue() {
        QuantityInputDTO request = input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/compare", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("true", response.getBody().getResultString());
        assertEquals("compare", response.getBody().getOperation());
    }

    @Test
    void testCompare_1Yard_36Inches_ReturnsTrue() {
        QuantityInputDTO request = input(
            dto(1.0, "YARDS", "LengthUnit"),
            dto(36.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/compare", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody().getResultString());
    }

    @Test
    void testCompare_1000Gram_1Kilogram_ReturnsTrue() {
        QuantityInputDTO request = input(
            dto(1000.0, "GRAM", "WeightUnit"),
            dto(1.0, "KILOGRAM", "WeightUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/compare", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody().getResultString());
    }

    @Test
    void testCompare_100Celsius_212Fahrenheit_ReturnsTrue() {
        QuantityInputDTO request = input(
            dto(100.0, "CELSIUS", "TemperatureUnit"),
            dto(212.0, "FAHRENHEIT", "TemperatureUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/compare", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody().getResultString());
    }

    @Test
    void testCompare_1Litre_1000Millilitre_ReturnsTrue() {
        QuantityInputDTO request = input(
            dto(1.0, "LITRE", "VolumeUnit"),
            dto(1000.0, "MILLILITRE", "VolumeUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/compare", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody().getResultString());
    }

    // ─── Convert ─────────────────────────────────────────────────────────────

    @Test
    void testConvert_1Foot_ToInches_Returns12() {
        QuantityInputDTO request = input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(0.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/convert", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(12.0, response.getBody().getResultValue(), 0.01);
        assertEquals("convert", response.getBody().getOperation());
    }

    @Test
    void testConvert_1000Gram_ToKilogram_Returns1() {
        QuantityInputDTO request = input(
            dto(1000.0, "GRAM", "WeightUnit"),
            dto(0.0, "KILOGRAM", "WeightUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/convert", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1.0, response.getBody().getResultValue(), 0.01);
    }

    @Test
    void testConvert_0Celsius_ToFahrenheit_Returns32() {
        QuantityInputDTO request = input(
            dto(0.0, "CELSIUS", "TemperatureUnit"),
            dto(0.0, "FAHRENHEIT", "TemperatureUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/convert", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(32.0, response.getBody().getResultValue(), 0.01);
    }

    // ─── Add ─────────────────────────────────────────────────────────────────

    @Test
    void testAdd_1Foot_12Inches_Returns2Feet() {
        QuantityInputDTO request = input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/add", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 0.01);
        assertEquals("FEET", response.getBody().getResultUnit());
        assertEquals("add", response.getBody().getOperation());
    }

    @Test
    void testAdd_1Litre_1000Millilitre_Returns2Litres() {
        QuantityInputDTO request = input(
            dto(1.0, "LITRE", "VolumeUnit"),
            dto(1000.0, "MILLILITRE", "VolumeUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/add", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 0.01);
    }

    @Test
    void testAddWithTargetUnit_FeetPlusInches_ResultInInches() {
        QuantityInputDTO request = inputWithTarget(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"),
            dto(0.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/add-with-target-unit",
                request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(24.0, response.getBody().getResultValue(), 0.01);
        assertEquals("INCHES", response.getBody().getResultUnit());
    }

    // ─── Subtract ────────────────────────────────────────────────────────────

    @Test
    void testSubtract_2Feet_12Inches_Returns1Foot() {
        QuantityInputDTO request = input(
            dto(2.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/subtract", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1.0, response.getBody().getResultValue(), 0.01);
        assertEquals("subtract", response.getBody().getOperation());
    }

    // ─── Divide ──────────────────────────────────────────────────────────────

    @Test
    void testDivide_10Feet_5Feet_Returns2() {
        QuantityInputDTO request = input(
            dto(10.0, "FEET", "LengthUnit"),
            dto(5.0, "FEET", "LengthUnit"));

        ResponseEntity<QuantityMeasurementDTO> response =
            restTemplate.postForEntity(baseUrl + "/divide", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 0.001);
    }

    // ─── Error scenarios ──────────────────────────────────────────────────────

    @Test
    void testAdd_IncompatibleTypes_Returns400() {
        QuantityInputDTO request = input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(1.0, "KILOGRAM", "WeightUnit"));

        ResponseEntity<String> response =
            restTemplate.postForEntity(baseUrl + "/add", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDivide_ByZero_Returns500() {
        QuantityInputDTO request = input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(0.0, "INCHES", "LengthUnit"));

        ResponseEntity<String> response =
            restTemplate.postForEntity(baseUrl + "/divide", request, String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ─── History endpoints ────────────────────────────────────────────────────

    @Test
    void testGetOperationHistory_CompareOperation() {
        // First perform a comparison
        restTemplate.postForEntity(baseUrl + "/compare", input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit")), QuantityMeasurementDTO.class);

        // Then check history
        ResponseEntity<List> response =
            restTemplate.getForEntity(baseUrl + "/history/operation/compare", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetOperationHistoryByType_LengthUnit() {
        restTemplate.postForEntity(baseUrl + "/compare", input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit")), QuantityMeasurementDTO.class);

        ResponseEntity<List> response =
            restTemplate.getForEntity(baseUrl + "/history/type/LengthUnit", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetOperationCount_CompareOperation() {
        restTemplate.postForEntity(baseUrl + "/compare", input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit")), QuantityMeasurementDTO.class);

        ResponseEntity<Long> response =
            restTemplate.getForEntity(baseUrl + "/count/compare", Long.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() >= 1L);
    }

    @Test
    void testGetErrorHistory_AfterInvalidOperation() {
        // Trigger an error
        restTemplate.postForEntity(baseUrl + "/add", input(
            dto(1.0, "FEET", "LengthUnit"),
            dto(1.0, "KILOGRAM", "WeightUnit")), String.class);

        ResponseEntity<List> response =
            restTemplate.getForEntity(baseUrl + "/history/errored", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    // ─── Swagger / Actuator ───────────────────────────────────────────────────

    @Test
    void testSwaggerUILoads() {
        String url = "http://localhost:" + port + "/swagger-ui/index.html";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testActuatorHealthEndpoint() {
        String url = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("UP"));
    }

    @Test
    void testH2ConsoleLoads() {
        String url = "http://localhost:" + port + "/h2-console";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        // H2 console redirects or returns 200
        assertTrue(response.getStatusCode().is2xxSuccessful() ||
                   response.getStatusCode().is3xxRedirection());
    }
}
