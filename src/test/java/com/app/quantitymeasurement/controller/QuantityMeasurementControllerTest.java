package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UC17 - Controller unit tests using @WebMvcTest + MockMvc.
 *
 * @WebMvcTest  — loads only the web layer, no DB, no full context.
 * @MockBean    — injects a Mockito mock of IQuantityMeasurementService.
 * MockMvc      — simulates HTTP requests without a real server.
 */
@WebMvcTest(QuantityMeasurementController.class)
class QuantityMeasurementControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private IQuantityMeasurementService service;

    // Helpers

    private QuantityMeasurementDTO compareDTO() {
        QuantityMeasurementDTO d = new QuantityMeasurementDTO();
        d.setThisValue(1.0);  d.setThisUnit("FEET");   d.setThisMeasurementType("LengthUnit");
        d.setThatValue(12.0); d.setThatUnit("INCHES");  d.setThatMeasurementType("LengthUnit");
        d.setOperation("COMPARE"); d.setResultString("Equal"); d.setError(false);
        return d;
    }

    private QuantityMeasurementDTO addDTO() {
        QuantityMeasurementDTO d = new QuantityMeasurementDTO();
        d.setThisValue(1.0);  d.setThisUnit("FEET");   d.setThisMeasurementType("LengthUnit");
        d.setThatValue(12.0); d.setThatUnit("INCHES");  d.setThatMeasurementType("LengthUnit");
        d.setOperation("ADD"); d.setResultValue(2.0); d.setResultUnit("FEET"); d.setError(false);
        return d;
    }

    // POST /compare

    @Test
    void testCompare_returnsOk() throws Exception {
        Mockito.when(service.compare(Mockito.any(), Mockito.any())).thenReturn(compareDTO());

        mockMvc.perform(post("/api/measurements/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "thisQuantity": { "value": 1.0, "unit": "FEET", "measurementType": "LengthUnit" },
                      "thatQuantity": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("COMPARE"))
                .andExpect(jsonPath("$.resultString").value("Equal"))
                .andExpect(jsonPath("$.error").value(false));
    }

    // POST /add

    @Test
    void testAdd_returnsOk() throws Exception {
        Mockito.when(service.add(Mockito.any(), Mockito.any())).thenReturn(addDTO());

        mockMvc.perform(post("/api/measurements/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "thisQuantity": { "value": 1.0, "unit": "FEET", "measurementType": "LengthUnit" },
                      "thatQuantity": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("ADD"))
                .andExpect(jsonPath("$.resultValue").value(2.0))
                .andExpect(jsonPath("$.resultUnit").value("FEET"));
    }

    // Bean Validation. invalid measurementType returns 400.

    @Test
    void testCompare_invalidMeasurementType_returns400() throws Exception {
        mockMvc.perform(post("/api/measurements/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "thisQuantity": { "value": 1.0, "unit": "FEET", "measurementType": "InvalidType" },
                      "thatQuantity": { "value": 1.0, "unit": "FEET", "measurementType": "LengthUnit" }
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Bean Validation null value returns 400.

    @Test
    void testAdd_nullValue_returns400() throws Exception {
        mockMvc.perform(post("/api/measurements/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "thisQuantity": { "value": null, "unit": "FEET", "measurementType": "LengthUnit" },
                      "thatQuantity": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    // GET /history

    @Test
    void testGetHistory_returnsOkList() throws Exception {
        Mockito.when(service.getOperationHistory("")).thenReturn(List.of(compareDTO()));
        mockMvc.perform(get("/api/measurements/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operation").value("COMPARE"));
    }

    @Test
    void testGetHistoryByOperation_returnsOkList() throws Exception {
        Mockito.when(service.getOperationHistory("COMPARE")).thenReturn(List.of(compareDTO()));
        mockMvc.perform(get("/api/measurements/history/COMPARE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operation").value("COMPARE"));
    }

    // GET /history/type/{type}

    @Test
    void testGetHistoryByType_returnsOkList() throws Exception {
        Mockito.when(service.getMeasurementsByType("LengthUnit")).thenReturn(List.of(compareDTO()));
        mockMvc.perform(get("/api/measurements/history/type/LengthUnit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].thisMeasurementType").value("LengthUnit"));
    }

    // GET /history/errored 

    @Test
    void testGetErrorHistory_returnsEmptyList() throws Exception {
        Mockito.when(service.getErrorHistory()).thenReturn(List.of());
        mockMvc.perform(get("/api/measurements/history/errored"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // GET /count/{operation}

    @Test
    void testGetOperationCount_returnsCountJson() throws Exception {
        Mockito.when(service.getOperationCount("COMPARE")).thenReturn(3L);
        mockMvc.perform(get("/api/measurements/count/COMPARE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("COMPARE"))
                .andExpect(jsonPath("$.count").value(3));
    }
}
