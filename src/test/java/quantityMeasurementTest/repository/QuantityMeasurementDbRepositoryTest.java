package quantityMeasurementTest.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import quantityMeasurement.entity.QuantityMeasurementEntity;
import quantityMeasurement.entity.QuantityModel;
import quantityMeasurement.model.IMeasurable;
import quantityMeasurement.model.LengthUnit;
import quantityMeasurement.repository.QuantityMeasurementDatabaseRepository;

import java.util.List;

import static org.junit.Assert.*;

public class QuantityMeasurementDbRepositoryTest {

    private static QuantityMeasurementDatabaseRepository repository;

    @BeforeClass
    public static void setUpClass() {
        repository = QuantityMeasurementDatabaseRepository.getInstance();
    }

    @Before
    public void setUp() {
        repository.deleteAll();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private QuantityMeasurementEntity makeCompareEntity(
            double v1, String u1, double v2, String u2, String result) {
        QuantityModel<IMeasurable> m1 =
                new QuantityModel<>(v1, (IMeasurable) LengthUnit.FEET.getUnitInstance(u1));
        QuantityModel<IMeasurable> m2 =
                new QuantityModel<>(v2, (IMeasurable) LengthUnit.FEET.getUnitInstance(u2));
        return new QuantityMeasurementEntity(m1, m2, "COMPARE", result);
    }

    // ─── Save & Retrieve ─────────────────────────────────────────────────────

    @Test
    public void testSaveEntity_ThenGetAllMeasurements_ReturnsOne() {
        repository.save(makeCompareEntity(1.0, "FEET", 12.0, "INCHES", "Equal"));
        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertEquals(1, all.size());
        assertEquals("COMPARE", all.get(0).operation);
        assertEquals("Equal",   all.get(0).resultString);
    }

    @Test
    public void testSaveMultiple_GetAll_ReturnsAll() {
        repository.save(makeCompareEntity(1.0, "FEET",  12.0, "INCHES", "Equal"));
        repository.save(makeCompareEntity(1.0, "YARDS", 36.0, "INCHES", "Equal"));
        repository.save(makeCompareEntity(1.0, "FEET",   1.0, "INCHES", "Not Equal"));
        assertEquals(3, repository.getAllMeasurements().size());
    }

    // ─── getTotalCount ────────────────────────────────────────────────────────

    @Test
    public void testGetTotalCount_AfterSave_ReturnsCorrectCount() {
        assertEquals(0, repository.getTotalCount());
        repository.save(makeCompareEntity(1.0, "FEET", 12.0, "INCHES", "Equal"));
        assertEquals(1, repository.getTotalCount());
        repository.save(makeCompareEntity(1.0, "FEET",  1.0, "FEET",   "Equal"));
        assertEquals(2, repository.getTotalCount());
    }

    // ─── getMeasurementsByOperation ──────────────────────────────────────────

    @Test
    public void testGetMeasurementsByOperation_FiltersByOperation() {
        repository.save(makeCompareEntity(1.0, "FEET", 12.0, "INCHES", "Equal"));

        QuantityModel<IMeasurable> m1 =
                new QuantityModel<>(1.0, (IMeasurable) LengthUnit.FEET);
        QuantityModel<IMeasurable> m2 =
                new QuantityModel<>(12.0, (IMeasurable) LengthUnit.INCHES);
        QuantityModel<IMeasurable> result =
                new QuantityModel<>(2.0, (IMeasurable) LengthUnit.FEET);
        repository.save(new QuantityMeasurementEntity(m1, m2, "ADD", result));

        List<QuantityMeasurementEntity> compareOps =
                repository.getMeasurementsByOperation("COMPARE");
        assertEquals(1, compareOps.size());
        assertEquals("COMPARE", compareOps.get(0).operation);
    }

    // ─── getMeasurementsByType ────────────────────────────────────────────────

    @Test
    public void testGetMeasurementsByType_FiltersByMeasurementType() {
        repository.save(makeCompareEntity(1.0, "FEET", 12.0, "INCHES", "Equal"));
        List<QuantityMeasurementEntity> lengthOps =
                repository.getMeasurementsByType("LengthUnit");
        assertFalse(lengthOps.isEmpty());
        assertEquals("LengthUnit", lengthOps.get(0).thisMeasurementType);
    }

    // ─── deleteAll ────────────────────────────────────────────────────────────

    @Test
    public void testDeleteAll_ClearsAllRecords() {
        repository.save(makeCompareEntity(1.0, "FEET", 12.0, "INCHES", "Equal"));
        repository.save(makeCompareEntity(1.0, "FEET", 12.0, "INCHES", "Equal"));
        assertEquals(2, repository.getTotalCount());
        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
    }

    // ─── Pool Statistics ──────────────────────────────────────────────────────

    @Test
    public void testGetPoolStatistics_ReturnsNonNull() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }

    // ─── SQL Injection Prevention ─────────────────────────────────────────────

    @Test
    public void testGetMeasurementsByOperation_SQLInjectionSafe() {
        repository.save(makeCompareEntity(1.0, "FEET", 12.0, "INCHES", "Equal"));
        // Parameterized query treats this as a literal value — no rows returned
        List<QuantityMeasurementEntity> result =
                repository.getMeasurementsByOperation("COMPARE'; DROP TABLE quantity_measurement_entity; --");
        assertTrue("SQL injection should not execute", result.isEmpty());
        // Table must still exist
        assertTrue("Table should still have data", repository.getTotalCount() > 0);
    }

    // ─── Singleton ────────────────────────────────────────────────────────────

    @Test
    public void testGetInstance_ReturnsSameInstance() {
        assertSame(QuantityMeasurementDatabaseRepository.getInstance(),
                QuantityMeasurementDatabaseRepository.getInstance());
    }
}