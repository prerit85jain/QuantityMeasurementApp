package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityInputDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for QuantityMeasurementController using Spring's MockMvc.
 * Uses @WebMvcTest for controller-layer-only context.
 * Mocks the service layer with @MockBean.
 *
 * @author Developer
 * @version 17.0
 */
@WebMvcTest(QuantityMeasurementController.class)
@WithMockUser
class QuantityMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IQuantityMeasurementService service;

    @Autowired
    private ObjectMapper objectMapper;

    private QuantityInputDTO compareInput;
    private QuantityMeasurementDTO compareResult;

    @BeforeEach
    void setUp() {
        // Build a typical compare request
        QuantityDTO feet   = new QuantityDTO(1.0,  "FEET",   "LengthUnit");
        QuantityDTO inches = new QuantityDTO(12.0, "INCHES", "LengthUnit");
        compareInput = new QuantityInputDTO();
        compareInput.setThisQuantityDTO(feet);
        compareInput.setThatQuantityDTO(inches);

        // Build a typical compare result DTO
        compareResult = new QuantityMeasurementDTO();
        compareResult.thisValue            = 1.0;
        compareResult.thisUnit             = "FEET";
        compareResult.thisMeasurementType  = "LengthUnit";
        compareResult.thatValue            = 12.0;
        compareResult.thatUnit             = "INCHES";
        compareResult.thatMeasurementType  = "LengthUnit";
        compareResult.operation            = "compare";
        compareResult.resultString         = "true";
    }

    // ─── Compare ─────────────────────────────────────────────────────────────

    @Test
    void testPerformComparison_Returns200_WithResult() throws Exception {
        when(service.compare(any(), any())).thenReturn(compareResult);

        mockMvc.perform(post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compareInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.operation").value("compare"))
            .andExpect(jsonPath("$.resultString").value("true"));

        verify(service).compare(any(), any());
    }

    @Test
    void testPerformComparison_ServiceCalled_Once() throws Exception {
        when(service.compare(any(), any())).thenReturn(compareResult);

        mockMvc.perform(post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compareInput)))
            .andExpect(status().isOk());

        Mockito.verify(service, Mockito.times(1)).compare(any(), any());
    }

    // ─── Convert ─────────────────────────────────────────────────────────────

    @Test
    void testPerformConversion_Returns200() throws Exception {
        QuantityMeasurementDTO result = new QuantityMeasurementDTO();
        result.operation    = "convert";
        result.resultValue  = 12.0;
        result.resultUnit   = "INCHES";

        when(service.convert(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/v1/quantities/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compareInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(12.0))
            .andExpect(jsonPath("$.operation").value("convert"));
    }

    // ─── Add ─────────────────────────────────────────────────────────────────

    @Test
    void testPerformAddition_Returns200_WithSum() throws Exception {
        QuantityMeasurementDTO result = new QuantityMeasurementDTO();
        result.operation    = "add";
        result.resultValue  = 2.0;
        result.resultUnit   = "FEET";

        when(service.add(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/v1/quantities/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compareInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(2.0))
            .andExpect(jsonPath("$.resultUnit").value("FEET"));
    }

    @Test
    void testPerformAdditionWithTargetUnit_Returns200() throws Exception {
        QuantityDTO target = new QuantityDTO(0.0, "INCHES", "LengthUnit");
        compareInput.setTargetQuantityDTO(target);

        QuantityMeasurementDTO result = new QuantityMeasurementDTO();
        result.operation   = "add";
        result.resultValue = 24.0;
        result.resultUnit  = "INCHES";

        when(service.add(any(), any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/v1/quantities/add-with-target-unit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compareInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(24.0));
    }

    // ─── Subtract ────────────────────────────────────────────────────────────

    @Test
    void testPerformSubtraction_Returns200() throws Exception {
        QuantityMeasurementDTO result = new QuantityMeasurementDTO();
        result.operation    = "subtract";
        result.resultValue  = 1.0;
        result.resultUnit   = "FEET";

        when(service.subtract(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/v1/quantities/subtract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compareInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(1.0));
    }

    // ─── Divide ──────────────────────────────────────────────────────────────

    @Test
    void testPerformDivision_Returns200() throws Exception {
        QuantityMeasurementDTO result = new QuantityMeasurementDTO();
        result.operation   = "divide";
        result.resultValue = 2.0;

        when(service.divide(any(), any())).thenReturn(result);

        mockMvc.perform(post("/api/v1/quantities/divide")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compareInput)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultValue").value(2.0));
    }

    // ─── History endpoints ────────────────────────────────────────────────────

    @Test
    void testGetOperationHistory_Returns200_WithList() throws Exception {
        when(service.getOperationHistory("compare")).thenReturn(List.of(compareResult));

        mockMvc.perform(get("/api/v1/quantities/history/operation/compare"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].operation").value("compare"));
    }

    @Test
    void testGetOperationHistoryByType_Returns200() throws Exception {
        when(service.getMeasurementsByType("LengthUnit")).thenReturn(List.of(compareResult));

        mockMvc.perform(get("/api/v1/quantities/history/type/LengthUnit"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetOperationCount_Returns200() throws Exception {
        when(service.getOperationCount("compare")).thenReturn(3L);

        mockMvc.perform(get("/api/v1/quantities/count/compare"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(3));
    }

    @Test
    void testGetErroredOperations_Returns200() throws Exception {
        QuantityMeasurementDTO errDto = new QuantityMeasurementDTO();
        errDto.error        = true;
        errDto.errorMessage = "Cross-category error";

        when(service.getErrorHistory()).thenReturn(List.of(errDto));

        mockMvc.perform(get("/api/v1/quantities/history/errored"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].error").value(true));
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    @Test
    void testInvalidMeasurementType_Returns400() throws Exception {
        QuantityDTO bad = new QuantityDTO(1.0, "FEET", "InvalidType");
        QuantityInputDTO badInput = new QuantityInputDTO();
        badInput.setThisQuantityDTO(bad);
        badInput.setThatQuantityDTO(new QuantityDTO(12.0, "INCHES", "LengthUnit"));

        mockMvc.perform(post("/api/v1/quantities/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badInput)))
            .andExpect(status().isBadRequest());
    }
}
