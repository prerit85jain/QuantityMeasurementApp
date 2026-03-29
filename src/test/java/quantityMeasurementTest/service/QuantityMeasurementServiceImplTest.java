package quantityMeasurementTest.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import quantityMeasurement.entity.QuantityDTO;
import quantityMeasurement.entity.QuantityMeasurementEntity;
import quantityMeasurement.repository.IQuantityMeasurementRepository;
import quantityMeasurement.service.QuantityMeasurementServiceImpl;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuantityMeasurementServiceImplTest {

    @Mock
    private IQuantityMeasurementRepository mockRepository;
    private QuantityMeasurementServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new QuantityMeasurementServiceImpl(mockRepository);
        when(mockRepository.getAllMeasurements()).thenReturn(new ArrayList<>());
        doNothing().when(mockRepository).save(any(QuantityMeasurementEntity.class));
    }

    // ─── Compare ─────────────────────────────────────────────────────────────
    @Test
    public void testCompare_1Feet_12Inches_ReturnsTrue() {
        assertTrue(service.compare(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test public void testCompare_1Foot_1Inch_ReturnsFalse() {
        assertFalse(service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test public void testCompare_1000Gram_1Kilogram_ReturnsTrue() {
        assertTrue(service.compare(
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(1.0,    QuantityDTO.WeightUnit.KILOGRAM)));
    }

    @Test public void testCompare_100Celsius_212Fahrenheit_ReturnsTrue() {
        assertTrue(service.compare(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT)));
    }

    // ─── Convert ─────────────────────────────────────────────────────────────
    @Test public void testConvert_1Foot_ToInches_Returns12() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 0.01);
    }

    @Test public void testConvert_1000Gram_ToKilogram_Returns1() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(0.0,    QuantityDTO.WeightUnit.KILOGRAM));
        assertEquals(1.0, result.getValue(), 0.01);
    }

    @Test public void testConvert_0Celsius_ToFahrenheit_Returns32() {
        QuantityDTO result = service.convert(
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));
        assertEquals(32.0, result.getValue(), 0.01);
    }

    // ─── Add ─────────────────────────────────────────────────────────────────
    @Test public void testAdd_1Foot_12Inches_Returns2Feet() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(2.0, result.getValue(), 0.01);
    }

    @Test public void testAdd_1Litre_1000mL_Returns2Litres() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE));
        assertEquals(2.0, result.getValue(), 0.01);
    }

    // ─── Subtract ────────────────────────────────────────────────────────────
    @Test public void testSubtract_2Feet_12Inches_Returns1Foot() {
        QuantityDTO result = service.subtract(
                new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1.0, result.getValue(), 0.01);
    }

    // ─── Divide ──────────────────────────────────────────────────────────────
    @Test public void testDivide_10Feet_5Feet_Returns2() {
        double result = service.divide(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(5.0,  QuantityDTO.LengthUnit.FEET));
        assertEquals(2.0, result, 0.001);
    }

}