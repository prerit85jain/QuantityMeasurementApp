package quantityMeasurementTest.integrationTests;

import org.junit.*;
import quantityMeasurement.QuantityMeasurementApp;
import quantityMeasurement.controller.QuantityMeasurementController;
import quantityMeasurement.entity.QuantityDTO;
import quantityMeasurement.entity.QuantityMeasurementEntity;
import quantityMeasurement.repository.IQuantityMeasurementRepository;

import java.util.List;

import static org.junit.Assert.*;

public class QuantityMeasurementIntegrationTest {

    private static QuantityMeasurementApp app;
    private static QuantityMeasurementController controller;
    private static IQuantityMeasurementRepository repository;

    @BeforeClass
    public static void setUpClass() {
        app = QuantityMeasurementApp.getInstance();
        controller = app.controller;
        repository = app.repository;
    }

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @AfterClass
    public static void tearDownClass() {
        if (app != null) app.closeResources();
    }

    // ─── Compare persisted to DB ──────────────────────────────────────────────

    @Test
    public void testCompare_1Foot_12Inches_PersistedToDatabase() {
        boolean result = controller.performComparison(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertTrue(result);
        assertEquals(1, repository.getTotalCount());
        List<QuantityMeasurementEntity> ops =
                repository.getMeasurementsByOperation("COMPARE");
        assertEquals(1, ops.size());
        assertEquals("Equal", ops.get(0).resultString);
    }

    @Test
    public void testCompare_1Yard_36Inches_PersistedToDatabase() {
        assertTrue(controller.performComparison(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.YARDS),
                new QuantityDTO(36.0, QuantityDTO.LengthUnit.INCHES)));
        assertEquals(1, repository.getTotalCount());
    }

    // ─── Convert persisted ────────────────────────────────────────────────────

    @Test
    public void testConvert_1000Gram_ToKg_PersistedToDatabase() {
        QuantityDTO result = controller.performConversion(
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(0.0,    QuantityDTO.WeightUnit.KILOGRAM));
        assertEquals(1.0, result.getValue(), 0.01);
        assertEquals(1, repository.getTotalCount());
    }

    @Test
    public void testConvert_100Celsius_ToFahrenheit_Returns212() {
        QuantityDTO result = controller.performConversion(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.FAHRENHEIT));
        assertEquals(212.0, result.getValue(), 0.01);
    }

    // ─── Add persisted ────────────────────────────────────────────────────────

    @Test
    public void testAdd_1Foot_12Inches_Returns2Feet_Persisted() {
        QuantityDTO result = controller.performAddition(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(2.0, result.getValue(), 0.01);
        assertEquals(1, repository.getTotalCount());
    }

    @Test
    public void testAdd_1Litre_1000mL_Returns2Litres_Persisted() {
        QuantityDTO result = controller.performAddition(
                new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE));
        assertEquals(2.0, result.getValue(), 0.01);
        assertEquals(1, repository.getTotalCount());
    }

    // ─── Subtract / Divide persisted ─────────────────────────────────────────

    @Test
    public void testSubtract_2Feet_12Inches_Returns1Foot_Persisted() {
        QuantityDTO result = controller.performSubtraction(
                new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1.0, result.getValue(), 0.01);
        assertEquals(1, repository.getTotalCount());
    }

    @Test
    public void testDivide_10Feet_5Feet_Returns2_Persisted() {
        double result = controller.performDivision(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(5.0,  QuantityDTO.LengthUnit.FEET));
        assertEquals(2.0, result, 0.001);
        assertEquals(1, repository.getTotalCount());
    }

    // ─── Multiple ops & query by type ────────────────────────────────────────

    @Test
    public void testMultipleOperations_QueryByType_ReturnsCorrect() {
        controller.performComparison(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performComparison(
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM),
                new QuantityDTO(1.0,    QuantityDTO.WeightUnit.KILOGRAM));
        controller.performAddition(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));

        assertEquals(3, repository.getTotalCount());
        assertEquals(2, repository.getMeasurementsByType("LengthUnit").size());
        assertEquals(1, repository.getMeasurementsByType("WeightUnit").size());
        assertEquals(2, repository.getMeasurementsByOperation("COMPARE").size());
        assertEquals(1, repository.getMeasurementsByOperation("ADD").size());
    }

    // ─── deleteAllMeasurements ────────────────────────────────────────────────

    @Test
    public void testDeleteAllMeasurements_ClearsDatabase() {
        controller.performComparison(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1, repository.getTotalCount());
        app.deleteAllMeasurements();
        assertEquals(0, repository.getTotalCount());
    }

    // ─── Pool Statistics ──────────────────────────────────────────────────────

    @Test
    public void testPoolStatistics_NotNullOrEmpty() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }

    // ─── Data isolation between tests ─────────────────────────────────────────

    @Test
    public void testIsolation_DatabaseCleanBetweenTests() {
        // @Before calls deleteAll, so count must be 0 at start of each test
        assertEquals(0, repository.getTotalCount());
    }

    // ─── Temperature comparison ───────────────────────────────────────────────

    @Test
    public void testCompare_100Celsius_212Fahrenheit_Equal_Persisted() {
        boolean result = controller.performComparison(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT));
        assertTrue(result);
        assertEquals(1, repository.getTotalCount());
        assertEquals("Equal",
                repository.getMeasurementsByOperation("COMPARE").get(0).resultString);
    }
}