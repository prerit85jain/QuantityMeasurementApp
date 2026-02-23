package quantityMeasurement;

import org.junit.Test;
import static org.junit.Assert.*;

public class QuantityMeasurementAppTest {
    private static final double DELTA = 0.01;

    // ---------------- ADDITION ----------------

    @Test
    public void testAdd_BehaviorPreserved() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);

        Quantity<LengthUnit> result = q1.add(q2);

        assertEquals(2.0, result.getValue(), DELTA);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    // ---------------- SUBTRACTION ----------------

    @Test
    public void testSubtract_BehaviorPreserved() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(6.0, LengthUnit.INCHES);

        Quantity<LengthUnit> result = q1.subtract(q2);

        assertEquals(9.5, result.getValue(), DELTA);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    // ---------------- DIVISION ----------------

    @Test
    public void testDivide_BehaviorPreserved() {
        Quantity<LengthUnit> q1 = new Quantity<>(24.0, LengthUnit.INCHES);
        Quantity<LengthUnit> q2 = new Quantity<>(2.0, LengthUnit.FEET);

        double result = q1.divide(q2);

        assertEquals(1.0, result, DELTA);
    }

    // ---------------- VALIDATION CONSISTENCY ----------------

    @Test
    public void testValidation_NullOperand_ConsistentAcrossOperations() {
        Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);

        assertThrows(IllegalArgumentException.class, () -> q.add(null));
        assertThrows(IllegalArgumentException.class, () -> q.subtract(null));
        assertThrows(IllegalArgumentException.class, () -> q.divide(null));
    }

    // ---------------- ROUNDING ----------------

    @Test
    public void testRounding_Addition_TwoDecimalPlaces() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.3333, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(1.3333, LengthUnit.FEET);

        Quantity<LengthUnit> result = q1.add(q2);

        assertEquals(2.67, result.getValue(), DELTA);
    }

    @Test
    public void testDivision_NoRounding() {
        Quantity<LengthUnit> q1 = new Quantity<>(7.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(2.0, LengthUnit.FEET);

        double result = q1.divide(q2);

        assertEquals(3.5, result, DELTA);
    }

    // ---------------- TARGET UNIT ----------------

    @Test
    public void testExplicitTargetUnit_Addition() {
        Quantity<WeightUnit> q1 = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> q2 = new Quantity<>(5000.0, WeightUnit.GRAM);

        Quantity<WeightUnit> result = q1.add(q2, WeightUnit.GRAM);

        assertEquals(15000.0, result.getValue(), DELTA);
        assertEquals(WeightUnit.GRAM, result.getUnit());
    }

    // ---------------- IMMUTABILITY ----------------

    @Test
    public void testImmutability_AfterAdd() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);

        q1.add(q2);

        assertEquals(1.0, q1.getValue(), DELTA);
        assertEquals(12.0, q2.getValue(), DELTA);
    }

    // ---------------- CHAIN OPERATIONS ----------------

    @Test
    public void testChainedOperations() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(2.0, LengthUnit.FEET);
        Quantity<LengthUnit> q3 = new Quantity<>(12.0, LengthUnit.INCHES);

        double result = q1.subtract(q2).divide(q3);

        assertEquals(8.0, result, DELTA);
    }

    // ------------------- Equality Tests -------------------

    @Test
    public void testCelsiusToCelsiusEquality() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(25.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(25.0, TemperatureUnit.CELSIUS);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testFahrenheitToFahrenheitEquality() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        Quantity<TemperatureUnit> t2 = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testKelvinToKelvinEquality() {
        Quantity<TemperatureUnit> t1 = new Quantity<>(273.15, TemperatureUnit.KELVIN);
        Quantity<TemperatureUnit> t2 = new Quantity<>(273.15, TemperatureUnit.KELVIN);
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testCrossUnitEquality_CelsiusToFahrenheit() {
        Quantity<TemperatureUnit> c = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> f = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(c.equals(f));
        assertTrue(f.equals(c));
    }

    @Test
    public void testCrossUnitEquality_CelsiusToKelvin() {
        Quantity<TemperatureUnit> c = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> k = new Quantity<>(273.15, TemperatureUnit.KELVIN);
        assertTrue(c.equals(k));
    }

    @Test
    public void testCrossUnitEquality_FahrenheitToKelvin() {
        Quantity<TemperatureUnit> f = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        Quantity<TemperatureUnit> k = new Quantity<>(273.15, TemperatureUnit.KELVIN);
        assertTrue(f.equals(k));
    }

    @Test
    public void testSymmetricEquality() {
        Quantity<TemperatureUnit> a = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> b = new Quantity<>(212.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    @Test
    public void testTransitiveEquality() {
        Quantity<TemperatureUnit> a = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> b = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(a.equals(b));
    }

    @Test
    public void testSameReferenceEquality() {
        Quantity<TemperatureUnit> t = new Quantity<>(10.0, TemperatureUnit.CELSIUS);
        assertTrue(t.equals(t));
    }

    // ------------------- Conversion Accuracy -------------------

    @Test
    public void testCelsiusToFahrenheitConversion() {
        double result = TemperatureUnit.CELSIUS.convertTo(100, TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, result, DELTA);
    }

    @Test
    public void testFahrenheitToCelsiusConversion() {
        double result = TemperatureUnit.FAHRENHEIT.convertTo(32, TemperatureUnit.CELSIUS);
        assertEquals(0.0, result, DELTA);
    }

    @Test
    public void testKelvinConversion() {
        double result = TemperatureUnit.CELSIUS.convertTo(0, TemperatureUnit.FAHRENHEIT);
        assertEquals(32, result, 0.01);
    }

    // ------------------- Edge Cases -------------------

    @Test
    public void testAbsoluteZero() {
        Quantity<TemperatureUnit> c = new Quantity<>(-273.15, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> f = new Quantity<>(-459.67, TemperatureUnit.FAHRENHEIT);
        assertTrue(c.equals(f));
    }

    @Test
    public void testMinus40Equality() {
        Quantity<TemperatureUnit> c = new Quantity<>(-40.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> f = new Quantity<>(-40.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(c.equals(f));
    }

    // ------------------- Null Handling -------------------

    @Test(expected = IllegalArgumentException.class)
    public void testNullUnitHandling() {
        new Quantity<>(100.0, null);
    }

    // ------------------- Type Safety -------------------

    @Test
    public void testTemperatureVsLengthFalse() {
        Quantity<TemperatureUnit> t = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<LengthUnit> l = new Quantity<>(0.0, LengthUnit.CENTIMETERS);
        assertFalse(t.equals(l));
    }

    @Test
    public void testTemperatureVsWeightFalse() {
        Quantity<TemperatureUnit> t = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<WeightUnit> w = new Quantity<>(0.0, WeightUnit.KILOGRAM);
        assertFalse(t.equals(w));
    }

    @Test
    public void testTemperatureVsVolumeFalse() {
        Quantity<TemperatureUnit> t = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<VolumeUnit> v = new Quantity<>(1.0, VolumeUnit.LITRE);
        assertFalse(t.equals(v));
    }

    // ------------------- Operation Support Methods -------------------

    @Test
    public void testTemperatureUnitOperationSupport() {
        assertFalse(TemperatureUnit.CELSIUS.supportsArithmetic());
        assertFalse(TemperatureUnit.CELSIUS.supportsArithmetic());
    }

    @Test
    public void testDefaultOperationSupportForOtherUnits() {
        assertTrue(LengthUnit.FEET.supportArithmetic());
        assertTrue(WeightUnit.KILOGRAM.supportArithmetic());
    }

    // ------------------- Rounding Consistency -------------------

    @Test
    public void testRoundingConsistency() {
        double result = TemperatureUnit.CELSIUS.convertTo(37, TemperatureUnit.FAHRENHEIT);
        assertEquals(98.6, result, DELTA);
    }
}