package quantityMeasurement;

import org.junit.Test;
import static org.junit.Assert.*;

public class QuantityMeasurementAppTest {
    // Interface Implementation Tests
    @Test
    public void testIMeasurableInterface_LengthUnitImplementation() {
        assertTrue(LengthUnit.FEET instanceof IMeasurable);
        assertEquals(12.0, LengthUnit.FEET.convertToBaseUnit(1.0), 0.01); // 1 ft = 12 in (base)
    }

    @Test
    public void testIMeasurableInterface_WeightUnitImplementation() {
        assertTrue(WeightUnit.KILOGRAM instanceof IMeasurable);
        assertEquals(1000.0, WeightUnit.KILOGRAM.convertToBaseUnit(1.0), 0.01); // 1 kg = 1000 g (base)
    }

    @Test
    public void testIMeasurableInterface_ConsistentBehavior() {
        assertEquals(
                LengthUnit.FEET.convertToBaseUnit(1.0),
                LengthUnit.INCHES.convertToBaseUnit(12.0), 0.01
        );
        assertEquals(
                WeightUnit.KILOGRAM.convertToBaseUnit(1.0),
                WeightUnit.GRAM.convertToBaseUnit(1000.0), 0.01
        );
    }

    // --- Equality Tests ---
    @Test
    public void testGenericQuantity_LengthOperations_Equality() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        assertEquals(q1, q2);
    }

    @Test
    public void testGenericQuantity_WeightOperations_Equality() {
        Quantity<WeightUnit> q1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> q2 = new Quantity<>(1000.0, WeightUnit.GRAM);
        assertEquals(q1, q2);
    }

    // --- Conversion Tests ---
    @Test
    public void testGenericQuantity_LengthOperations_Conversion() {
        Quantity<LengthUnit> q = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> converted = q.convertTo(LengthUnit.INCHES);
        assertEquals(12.0, converted.getValue(), 0.01);
        assertEquals(LengthUnit.INCHES, converted.getUnit());
    }

    @Test
    public void testGenericQuantity_WeightOperations_Conversion() {
        Quantity<WeightUnit> q = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> converted = q.convertTo(WeightUnit.GRAM);
        assertEquals(1000.0, converted.getValue(), 0.01);
        assertEquals(WeightUnit.GRAM, converted.getUnit());
    }

    // --- Addition Tests ---
    @Test
    public void testGenericQuantity_LengthOperations_Addition() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        Quantity<LengthUnit> sum = q1.add(q2, LengthUnit.FEET);
        assertEquals(2.0, sum.getValue(), 0.01);
        assertEquals(LengthUnit.FEET, sum.getUnit());
    }

    @Test
    public void testGenericQuantity_WeightOperations_Addition() {
        Quantity<WeightUnit> q1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> q2 = new Quantity<>(1000.0, WeightUnit.GRAM);
        Quantity<WeightUnit> sum = q1.add(q2, WeightUnit.KILOGRAM);
        assertEquals(2.0, sum.getValue(), 0.01);
        assertEquals(WeightUnit.KILOGRAM, sum.getUnit());
    }

    // --- Cross Category Prevention ---
    @Test
    public void testCrossCategoryPrevention_LengthVsWeight() {
        Quantity<LengthUnit> length = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertNotEquals(length, weight);
    }

    // --- Constructor Validation ---
    @Test
    public void testGenericQuantity_ConstructorValidation_NullUnit() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity<>(1.0, null));
    }

    @Test
    public void testGenericQuantity_ConstructorValidation_InvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity<>(Double.NaN, LengthUnit.FEET));
    }

    // --- Immutability ---
    @Test
    public void testImmutability_GenericQuantity() {
        Quantity<LengthUnit> q = new Quantity<>(1.0, LengthUnit.FEET);
        // No setters exist, only getters
        assertEquals(1.0, q.getValue(), 0.01);
        assertEquals(LengthUnit.FEET, q.getUnit());
    }

}
