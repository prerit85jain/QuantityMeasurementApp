package quantityMeasurement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import quantityMeasurement.controller.QuantityMeasurementController;
import quantityMeasurement.entity.QuantityDTO;
import quantityMeasurement.repository.IQuantityMeasurementRepository;
import quantityMeasurement.repository.QuantityMeasurementCacheRepository;


class QuantityMeasurementAppTest {

    private QuantityMeasurementApp app;
    private QuantityMeasurementController controller;
    private IQuantityMeasurementRepository repository;

    @BeforeEach
    void setUp() {
        app = QuantityMeasurementApp.getInstance();
        controller = app.controller;
        repository = app.repository;
        ((QuantityMeasurementCacheRepository) repository).clearAll();
    }

    // ─── Singleton ────────────────────────────────────────────────────────────

    @Test
    void testSingletonReturnsSameInstance() {
        assertSame(QuantityMeasurementApp.getInstance(),
                QuantityMeasurementApp.getInstance());
    }

    @Test void testAppHasNonNullControllerAndRepository() {
        assertNotNull(app.controller);
        assertNotNull(app.repository);
    }

    // ─── Length ───────────────────────────────────────────────────────────────

    @Test void test_1Foot_Equals_12Inches() {
        assertTrue(controller.performComparison(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test void test_1Yard_Equals_36Inches() {
        assertTrue(controller.performComparison(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.YARDS),
                new QuantityDTO(36.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test void test_1Foot_NotEqual_1Inch() {
        assertFalse(controller.performComparison(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test void test_AddFeetAndInches() {
        QuantityDTO result = controller.performAddition(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(2.0, result.getValue(), 0.01);
    }

    @Test void test_ConvertFeetToInches() {
        QuantityDTO result = controller.performConversion(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 0.01);
    }

    // ─── Weight ───────────────────────────────────────────────────────────────

    @Test void test_1000Gram_Equals_1Kilogram() {
        assertTrue(controller.performComparison(
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(1.0,    QuantityDTO.WeightUnit.KILOGRAM)));
    }

    @Test void test_ConvertGramToKilogram() {
        QuantityDTO result = controller.performConversion(
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(0.0,    QuantityDTO.WeightUnit.KILOGRAM));
        assertEquals(1.0, result.getValue(), 0.01);
    }

    // ─── Volume ───────────────────────────────────────────────────────────────

    @Test void test_1Litre_Equals_1000Millilitre() {
        assertTrue(controller.performComparison(
                new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE)));
    }

    @Test void test_ConvertGallonToLitre() {
        QuantityDTO result = controller.performConversion(
                new QuantityDTO(1.0, QuantityDTO.VolumeUnit.GALLON),
                new QuantityDTO(0.0, QuantityDTO.VolumeUnit.LITRE));
        assertEquals(3.79, result.getValue(), 0.01);
    }

    @Test void test_AddLitreAndMillilitre() {
        QuantityDTO result = controller.performAddition(
                new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE));
        assertEquals(2.0, result.getValue(), 0.01);
    }

    // ─── Temperature ──────────────────────────────────────────────────────────

    @Test void test_100Celsius_Equals_212Fahrenheit() {
        assertTrue(controller.performComparison(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT)));
    }

    @Test void test_0Celsius_Converts_To_32Fahrenheit() {
        QuantityDTO result = controller.performConversion(
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));
        assertEquals(32.0, result.getValue(), 0.01);
    }

    @Test void test_100Celsius_Converts_To_212Fahrenheit() {
        QuantityDTO result = controller.performConversion(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.FAHRENHEIT));
        assertEquals(212.0, result.getValue(), 0.01);
    }

    // ─── Subtraction ─────────────────────────────────────────────────────────

    @Test void test_SubtractFeetAndInches() {
        QuantityDTO result = controller.performSubtraction(
                new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1.0, result.getValue(), 0.01);
    }

    // ─── Division ────────────────────────────────────────────────────────────

    @Test void test_DivideFeetByFeet() {
        double result = controller.performDivision(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(5.0,  QuantityDTO.LengthUnit.FEET));
        assertEquals(2.0, result, 0.001);
    }

    // ─── DTO getMeasurementType uses simple class name ────────────────────────

    @Test void test_DTOMeasurementTypes() {
        assertEquals("LengthUnit",      QuantityDTO.LengthUnit.FEET.getMeasurementType());
        assertEquals("WeightUnit",      QuantityDTO.WeightUnit.GRAM.getMeasurementType());
        assertEquals("VolumeUnit",      QuantityDTO.VolumeUnit.LITRE.getMeasurementType());
        assertEquals("TemperatureUnit", QuantityDTO.TemperatureUnit.CELSIUS.getMeasurementType());
    }

    // ─── Repository ───────────────────────────────────────────────────────────

    @Test void test_RepositoryRecordsOperations() {
        controller.performComparison(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1, repository.getAllMeasurements().size());
    }
}