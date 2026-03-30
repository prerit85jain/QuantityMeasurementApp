package com.app.quantitymeasurement;

import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // Helpers

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> json(String body) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, h);
    }

    // Context 

    @Test
    void contextLoads() {
        assertTrue(port > 0, "Server should start on a port > 0");
    }

    //  POST /compare 

    @Test
    void testCompare_1FeetEquals12Inches() {
        String body = """
            {
              "thisQuantity": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantity": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;
        ResponseEntity<QuantityMeasurementDTO> r =
                restTemplate.postForEntity(url("/api/measurements/compare"), json(body), QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("Equal", r.getBody().getResultString());
        assertFalse(r.getBody().isError());
    }

    @Test
    void testCompare_100CelsiusEquals212Fahrenheit() {
        String body = """
            {
              "thisQuantity": { "value": 100.0, "unit": "CELSIUS",    "measurementType": "TemperatureUnit" },
              "thatQuantity": { "value": 212.0, "unit": "FAHRENHEIT", "measurementType": "TemperatureUnit" }
            }
            """;
        ResponseEntity<QuantityMeasurementDTO> r =
                restTemplate.postForEntity(url("/api/measurements/compare"), json(body), QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("Equal", r.getBody().getResultString());
    }

    // POST /convert 

    @Test
    void testConvert_1FeetTo12Inches() {
        String body = """
            {
              "thisQuantity": { "value": 1.0, "unit": "FEET", "measurementType": "LengthUnit" },
              "targetUnit": "INCHES"
            }
            """;
        ResponseEntity<QuantityMeasurementDTO> r =
                restTemplate.postForEntity(url("/api/measurements/convert"), json(body), QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(12.0, r.getBody().getResultValue(), 0.001);
        assertFalse(r.getBody().isError());
    }

    // POST /add

    @Test
    void testAdd_1FeetPlus12InchesEquals2Feet() {
        String body = """
            {
              "thisQuantity": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantity": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;
        ResponseEntity<QuantityMeasurementDTO> r =
                restTemplate.postForEntity(url("/api/measurements/add"), json(body), QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2.0, r.getBody().getResultValue(), 0.001);
        assertEquals("FEET", r.getBody().getResultUnit());
    }

    @Test
    void testAdd_1000GramPlus1Kg() {
        String body = """
            {
              "thisQuantity": { "value": 1000.0, "unit": "GRAM",     "measurementType": "WeightUnit" },
              "thatQuantity": { "value": 1.0,    "unit": "KILOGRAM", "measurementType": "WeightUnit" }
            }
            """;
        ResponseEntity<QuantityMeasurementDTO> r =
                restTemplate.postForEntity(url("/api/measurements/add"), json(body), QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2000.0, r.getBody().getResultValue(), 0.001);
    }

    // POST /subtract

    @Test
    void testSubtract_2FeetMinus12Inches() {
        String body = """
            {
              "thisQuantity": { "value": 2.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantity": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;
        ResponseEntity<QuantityMeasurementDTO> r =
                restTemplate.postForEntity(url("/api/measurements/subtract"), json(body), QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1.0, r.getBody().getResultValue(), 0.001);
    }

    // POST /divide

    @Test
    void testDivide_2FeetBy12Inches() {
        String body = """
            {
              "thisQuantity": { "value": 2.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantity": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;
        ResponseEntity<QuantityMeasurementDTO> r =
                restTemplate.postForEntity(url("/api/measurements/divide"), json(body), QuantityMeasurementDTO.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        // 2ft = 24in, 24/12 = 2.0
        assertEquals(2.0, Double.parseDouble(r.getBody().getResultString()), 0.001);
    }

    // Validation errors

    @Test
    void testCompare_invalidMeasurementType_returns400() {
        String body = """
            {
              "thisQuantity": { "value": 1.0, "unit": "FEET", "measurementType": "BadType" },
              "thatQuantity": { "value": 1.0, "unit": "FEET", "measurementType": "LengthUnit" }
            }
            """;
        ResponseEntity<Map> r =
                restTemplate.postForEntity(url("/api/measurements/compare"), json(body), Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(400, r.getBody().get("status"));
    }

    @Test
    void testAdd_nullValue_returns400() {
        String body = """
            {
              "thisQuantity": { "value": null, "unit": "FEET", "measurementType": "LengthUnit" },
              "thatQuantity": { "value": 12.0, "unit": "INCHES","measurementType": "LengthUnit" }
            }
            """;
        ResponseEntity<Map> r =
                restTemplate.postForEntity(url("/api/measurements/add"), json(body), Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
    }

    //  GET history endpoints 

    @Test
    void testGetAllHistory_returnsList() {
        ResponseEntity<QuantityMeasurementDTO[]> r =
                restTemplate.getForEntity(url("/api/measurements/history"), QuantityMeasurementDTO[].class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
    }

    @Test
    void testGetHistoryByOperation_returnsList() {
        ResponseEntity<QuantityMeasurementDTO[]> r =
                restTemplate.getForEntity(url("/api/measurements/history/COMPARE"), QuantityMeasurementDTO[].class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
    }

    @Test
    void testGetHistoryByType_returnsList() {
        ResponseEntity<QuantityMeasurementDTO[]> r =
                restTemplate.getForEntity(url("/api/measurements/history/type/LengthUnit"), QuantityMeasurementDTO[].class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
    }

    @Test
    void testGetErrorHistory_returnsList() {
        ResponseEntity<QuantityMeasurementDTO[]> r =
                restTemplate.getForEntity(url("/api/measurements/history/errored"), QuantityMeasurementDTO[].class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
    }

    @Test
    void testGetOperationCount_returnsMap() {
        ResponseEntity<Map> r =
                restTemplate.getForEntity(url("/api/measurements/count/ADD"), Map.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody().get("count"));
        assertEquals("ADD", r.getBody().get("operation"));
    }

    // Actuator

    @Test
    void testActuatorHealth_returnsUp() {
        ResponseEntity<Map> r =
                restTemplate.getForEntity(url("/actuator/health"), Map.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("UP", r.getBody().get("status"));
    }
}
