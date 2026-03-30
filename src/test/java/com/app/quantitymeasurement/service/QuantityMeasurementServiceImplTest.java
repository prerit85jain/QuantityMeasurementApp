package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for QuantityMeasurementServiceImpl.
 * Mocks the QuantityMeasurementRepository to test service logic in isolation.
 *
 * @author Developer
 * @version 17.0
 */
class QuantityMeasurementServiceImplTest {

    @Mock
    private QuantityMeasurementRepository repository;

    @InjectMocks
    private QuantityMeasurementServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock repository.save() to return the entity as-is
        when(repository.save(any(QuantityMeasurementEntity.class)))
            .thenAnswer(inv -> inv.getArgument(0));
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private QuantityDTO dto(double v, String unit, String type) {
        return new QuantityDTO(v, unit, type);
    }

    // ─── Compare ─────────────────────────────────────────────────────────────

    @Test
    void testCompare_1Foot_12Inches_ResultStringIsTrue() {
        QuantityMeasurementDTO result = service.compare(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"));

        assertNotNull(result);
        assertEquals("true", result.getResultString());
        assertEquals("compare", result.getOperation());
        verify(repository, times(1)).save(any());
    }

    @Test
    void testCompare_1Foot_1Inch_ResultStringIsFalse() {
        QuantityMeasurementDTO result = service.compare(
            dto(1.0, "FEET", "LengthUnit"),
            dto(1.0, "INCHES", "LengthUnit"));

        assertEquals("false", result.getResultString());
    }

    @Test
    void testCompare_1000Gram_1Kilogram_ReturnsTrue() {
        QuantityMeasurementDTO result = service.compare(
            dto(1000.0, "GRAM", "WeightUnit"),
            dto(1.0, "KILOGRAM", "WeightUnit"));

        assertEquals("true", result.getResultString());
    }

    @Test
    void testCompare_100Celsius_212Fahrenheit_ReturnsTrue() {
        QuantityMeasurementDTO result = service.compare(
            dto(100.0, "CELSIUS", "TemperatureUnit"),
            dto(212.0, "FAHRENHEIT", "TemperatureUnit"));

        assertEquals("true", result.getResultString());
    }

    @Test
    void testCompare_1Litre_1000Millilitre_ReturnsTrue() {
        QuantityMeasurementDTO result = service.compare(
            dto(1.0, "LITRE", "VolumeUnit"),
            dto(1000.0, "MILLILITRE", "VolumeUnit"));

        assertEquals("true", result.getResultString());
    }

    // ─── Convert ─────────────────────────────────────────────────────────────

    @Test
    void testConvert_1Foot_ToInches_Returns12() {
        QuantityMeasurementDTO result = service.convert(
            dto(1.0, "FEET", "LengthUnit"),
            dto(0.0, "INCHES", "LengthUnit"));

        assertEquals(12.0, result.getResultValue(), 0.01);
        assertEquals("convert", result.getOperation());
    }

    @Test
    void testConvert_1000Gram_ToKilogram_Returns1() {
        QuantityMeasurementDTO result = service.convert(
            dto(1000.0, "GRAM", "WeightUnit"),
            dto(0.0, "KILOGRAM", "WeightUnit"));

        assertEquals(1.0, result.getResultValue(), 0.01);
    }

    @Test
    void testConvert_0Celsius_ToFahrenheit_Returns32() {
        QuantityMeasurementDTO result = service.convert(
            dto(0.0, "CELSIUS", "TemperatureUnit"),
            dto(0.0, "FAHRENHEIT", "TemperatureUnit"));

        assertEquals(32.0, result.getResultValue(), 0.01);
    }

    @Test
    void testConvert_100Celsius_ToFahrenheit_Returns212() {
        QuantityMeasurementDTO result = service.convert(
            dto(100.0, "CELSIUS", "TemperatureUnit"),
            dto(0.0, "FAHRENHEIT", "TemperatureUnit"));

        assertEquals(212.0, result.getResultValue(), 0.01);
    }

    // ─── Add ─────────────────────────────────────────────────────────────────

    @Test
    void testAdd_1Foot_12Inches_Returns2Feet() {
        QuantityMeasurementDTO result = service.add(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"));

        assertEquals(2.0, result.getResultValue(), 0.01);
        assertEquals("FEET", result.getResultUnit());
        assertEquals("add", result.getOperation());
    }

    @Test
    void testAdd_1Litre_1000Millilitre_Returns2Litres() {
        QuantityMeasurementDTO result = service.add(
            dto(1.0, "LITRE", "VolumeUnit"),
            dto(1000.0, "MILLILITRE", "VolumeUnit"));

        assertEquals(2.0, result.getResultValue(), 0.01);
    }

    @Test
    void testAddWithTargetUnit_FeetPlusInches_ResultInInches() {
        QuantityMeasurementDTO result = service.add(
            dto(1.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"),
            dto(0.0, "INCHES", "LengthUnit"));

        assertEquals(24.0, result.getResultValue(), 0.01);
        assertEquals("INCHES", result.getResultUnit());
    }

    // ─── Subtract ────────────────────────────────────────────────────────────

    @Test
    void testSubtract_2Feet_12Inches_Returns1Foot() {
        QuantityMeasurementDTO result = service.subtract(
            dto(2.0, "FEET", "LengthUnit"),
            dto(12.0, "INCHES", "LengthUnit"));

        assertEquals(1.0, result.getResultValue(), 0.01);
        assertEquals("subtract", result.getOperation());
    }

    // ─── Divide ──────────────────────────────────────────────────────────────

    @Test
    void testDivide_10Feet_5Feet_Returns2() {
        QuantityMeasurementDTO result = service.divide(
            dto(10.0, "FEET", "LengthUnit"),
            dto(5.0, "FEET", "LengthUnit"));

        assertEquals(2.0, result.getResultValue(), 0.001);
        assertEquals("divide", result.getOperation());
    }

    // ─── Error handling ───────────────────────────────────────────────────────

    @Test
    void testAdd_CrossCategory_SavesErrorEntity_AndThrows() {
        assertThrows(Exception.class, () ->
            service.add(
                dto(1.0, "FEET", "LengthUnit"),
                dto(1.0, "KILOGRAM", "WeightUnit")));

        // Error entity should have been saved to repository
        verify(repository, atLeastOnce()).save(any());
    }

    @Test
    void testDivide_ByZero_SavesErrorAndThrows() {
        assertThrows(Exception.class, () ->
            service.divide(
                dto(1.0, "FEET", "LengthUnit"),
                dto(0.0, "INCHES", "LengthUnit")));

        verify(repository, atLeastOnce()).save(any());
    }

    // ─── History methods ──────────────────────────────────────────────────────

    @Test
    void testGetOperationHistory_DelegatesToRepository() {
        when(repository.findByOperation("compare")).thenReturn(List.of());
        List<QuantityMeasurementDTO> result = service.getOperationHistory("compare");
        assertNotNull(result);
        verify(repository).findByOperation("compare");
    }

    @Test
    void testGetMeasurementsByType_DelegatesToRepository() {
        when(repository.findByThisMeasurementType("LengthUnit")).thenReturn(List.of());
        List<QuantityMeasurementDTO> result = service.getMeasurementsByType("LengthUnit");
        assertNotNull(result);
        verify(repository).findByThisMeasurementType("LengthUnit");
    }

    @Test
    void testGetOperationCount_DelegatesToRepository() {
        when(repository.countByOperationAndIsErrorFalse("compare")).thenReturn(5L);
        long count = service.getOperationCount("compare");
        assertEquals(5L, count);
        verify(repository).countByOperationAndIsErrorFalse("compare");
    }

    @Test
    void testGetErrorHistory_DelegatesToRepository() {
        when(repository.findByIsErrorTrue()).thenReturn(List.of());
        List<QuantityMeasurementDTO> result = service.getErrorHistory();
        assertNotNull(result);
        verify(repository).findByIsErrorTrue();
    }

    // ─── Repository save called for every operation ───────────────────────────

    @Test
    void testEveryOperation_SavesEntityToRepository() {
        service.compare(dto(1.0,"FEET","LengthUnit"), dto(12.0,"INCHES","LengthUnit"));
        service.convert(dto(1.0,"FEET","LengthUnit"), dto(0.0,"INCHES","LengthUnit"));
        service.add(dto(1.0,"FEET","LengthUnit"),     dto(12.0,"INCHES","LengthUnit"));
        service.subtract(dto(2.0,"FEET","LengthUnit"),dto(12.0,"INCHES","LengthUnit"));
        service.divide(dto(10.0,"FEET","LengthUnit"), dto(5.0,"FEET","LengthUnit"));

        verify(repository, times(5)).save(any());
    }
}
