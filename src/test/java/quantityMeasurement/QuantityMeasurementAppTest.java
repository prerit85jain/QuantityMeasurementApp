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

    // ---------------- Equality Tests ----------------

    @Test
    public void testEquality_LitreToLitre_SameValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1.0, VolumeUnit.LITRE);

        assertEquals(v1, v2);
    }

    @Test
    public void testEquality_LitreToMillilitre_EquivalentValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        assertEquals(v1, v2);
    }

    @Test
    public void testEquality_GallonToLitre_EquivalentValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.GALLON);
        Quantity<VolumeUnit> v2 = new Quantity<>(3.78541, VolumeUnit.LITRE);

        assertEquals(v1, v2);
    }

    @Test
    public void testEquality_VolumeVsLength_Incompatible() {
        Quantity<VolumeUnit> volume = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<LengthUnit> length = new Quantity<>(1.0, LengthUnit.FEET);

        assertNotEquals(volume, length);
    }

    @Test
    public void testEquality_NullComparison() {
        Quantity<VolumeUnit> volume = new Quantity<>(1.0, VolumeUnit.LITRE);

        assertNotEquals(volume, null);
    }

    // ---------------- Conversion Tests ----------------

    @Test
    public void testConversion_LitreToMillilitre() {
        Quantity<VolumeUnit> volume = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> result = volume.convertTo(VolumeUnit.MILLILITRE);

        assertEquals(1000.0, result.getValue(), 0.01);
    }

    @Test
    public void testConversion_GallonToLitre() {
        Quantity<VolumeUnit> volume = new Quantity<>(1.0, VolumeUnit.GALLON);
        Quantity<VolumeUnit> result = volume.convertTo(VolumeUnit.LITRE);

        assertEquals(3.78541, result.getValue(), 0.01);
    }

    @Test
    public void testConversion_MillilitreToGallon() {
        Quantity<VolumeUnit> volume = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> result = volume.convertTo(VolumeUnit.GALLON);

        assertEquals(0.26417, result.getValue(), 0.01);
    }

    // ---------------- Addition Tests ----------------

    @Test
    public void testAddition_SameUnit_LitrePlusLitre() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(2.0, VolumeUnit.LITRE);

        Quantity<VolumeUnit> result = v1.add(v2);

        assertEquals(new Quantity<>(3.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_CrossUnit_LitrePlusMillilitre() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        Quantity<VolumeUnit> result = v1.add(v2);

        assertEquals(new Quantity<>(2.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testAddition_ExplicitTargetUnit_Millilitre() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        Quantity<VolumeUnit> result = v1.add(v2, VolumeUnit.MILLILITRE);

        assertEquals(new Quantity<>(2000.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testAddition_GallonPlusLitre() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.GALLON);
        Quantity<VolumeUnit> v2 = new Quantity<>(3.78541, VolumeUnit.LITRE);

        Quantity<VolumeUnit> result = v1.add(v2);

        assertEquals(new Quantity<>(2.0, VolumeUnit.GALLON), result);
    }

    // ---------------- Edge Case Tests ----------------

    @Test
    public void testZeroVolumeEquality() {
        Quantity<VolumeUnit> v1 = new Quantity<>(0.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(0.0, VolumeUnit.MILLILITRE);

        assertEquals(v1, v2);
    }

    @Test
    public void testNegativeVolumeEquality() {
        Quantity<VolumeUnit> v1 = new Quantity<>(-1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(-1000.0, VolumeUnit.MILLILITRE);

        assertEquals(v1, v2);
    }

    @Test
    public void testSameReferenceEquality() {
        Quantity<VolumeUnit> volume = new Quantity<>(5.0, VolumeUnit.LITRE);

        assertEquals(volume, volume);
    }

    // ---------------- SUBTRACTION TESTS ----------------

    @Test
    public void testSubtraction_SameUnit_FeetMinusFeet() {
        Quantity<LengthUnit> result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(5.0, LengthUnit.FEET));

        assertEquals(new Quantity<>(5.0, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_SameUnit_LitreMinusLitre() {
        Quantity<VolumeUnit> result =
                new Quantity<>(10.0, VolumeUnit.LITRE)
                        .subtract(new Quantity<>(3.0, VolumeUnit.LITRE));

        assertEquals(new Quantity<>(7.0, VolumeUnit.LITRE), result);
    }

    @Test
    public void testSubtraction_CrossUnit_FeetMinusInches() {
        Quantity<LengthUnit> result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(6.0, LengthUnit.INCHES));

        assertEquals(new Quantity<>(9.5, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_CrossUnit_InchesMinusFeet() {
        Quantity<LengthUnit> result =
                new Quantity<>(120.0, LengthUnit.INCHES)
                        .subtract(new Quantity<>(5.0, LengthUnit.FEET));

        assertEquals(new Quantity<>(60.0, LengthUnit.INCHES), result);
    }

    @Test
    public void testSubtraction_ExplicitTargetUnit_Feet() {
        Quantity<LengthUnit> result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(6.0, LengthUnit.INCHES), LengthUnit.FEET);

        assertEquals(new Quantity<>(9.5, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_ExplicitTargetUnit_Inches() {
        Quantity<LengthUnit> result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(6.0, LengthUnit.INCHES), LengthUnit.INCHES);

        assertEquals(new Quantity<>(114.0, LengthUnit.INCHES), result);
    }

    @Test
    public void testSubtraction_ExplicitTargetUnit_Millilitre() {
        Quantity<VolumeUnit> result =
                new Quantity<>(5.0, VolumeUnit.LITRE)
                        .subtract(new Quantity<>(2.0, VolumeUnit.LITRE), VolumeUnit.MILLILITRE);

        assertEquals(new Quantity<>(3000.0, VolumeUnit.MILLILITRE), result);
    }

    @Test
    public void testSubtraction_ResultingInNegative() {
        Quantity<LengthUnit> result =
                new Quantity<>(5.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(10.0, LengthUnit.FEET));

        assertEquals(new Quantity<>(-5.0, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_ResultingInZero() {
        Quantity<LengthUnit> result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(120.0, LengthUnit.INCHES));

        assertEquals(new Quantity<>(0.0, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_WithZeroOperand() {
        Quantity<LengthUnit> result =
                new Quantity<>(5.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(0.0, LengthUnit.INCHES));

        assertEquals(new Quantity<>(5.0, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_WithNegativeValues() {
        Quantity<LengthUnit> result =
                new Quantity<>(5.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(-2.0, LengthUnit.FEET));

        assertEquals(new Quantity<>(7.0, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_NonCommutative() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(5.0, LengthUnit.FEET);

        assertNotEquals(a.subtract(b), b.subtract(a));
    }

    @Test
    public void testSubtraction_NullOperand() {
        Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
        assertThrows(IllegalArgumentException.class, () -> q.subtract(null));
    }

    @Test
    public void testSubtraction_NullTargetUnit() {
        Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
        assertThrows(IllegalArgumentException.class,
                () -> q.subtract(new Quantity<>(5.0, LengthUnit.FEET), null));
    }

    @Test
    public void testSubtraction_CrossCategory() {
        Quantity<LengthUnit> length = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(5.0, WeightUnit.KILOGRAM);

        assertThrows(IllegalArgumentException.class, () -> length.subtract((Quantity) weight));
    }

    @Test
    public void testSubtraction_ChainedOperations() {
        Quantity<LengthUnit> result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(2.0, LengthUnit.FEET))
                        .subtract(new Quantity<>(1.0, LengthUnit.FEET));

        assertEquals(new Quantity<>(7.0, LengthUnit.FEET), result);
    }

    @Test
    public void testSubtraction_Immutability() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(2.0, LengthUnit.FEET);

        a.subtract(b);

        assertEquals(new Quantity<>(10.0, LengthUnit.FEET), a);
        assertEquals(new Quantity<>(2.0, LengthUnit.FEET), b);
    }

    // ---------------- DIVISION TESTS ----------------

    @Test
    public void testDivision_SameUnit_FeetDividedByFeet() {
        double result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .divide(new Quantity<>(2.0, LengthUnit.FEET));

        assertEquals(5.0, result, 0.01);
    }

    @Test
    public void testDivision_CrossUnit_FeetDividedByInches() {
        double result =
                new Quantity<>(24.0, LengthUnit.INCHES)
                        .divide(new Quantity<>(2.0, LengthUnit.FEET));

        assertEquals(1.0, result, 0.01);
    }

    @Test
    public void testDivision_RatioLessThanOne() {
        double result =
                new Quantity<>(5.0, LengthUnit.FEET)
                        .divide(new Quantity<>(10.0, LengthUnit.FEET));

        assertEquals(0.5, result, 0.01);
    }

    @Test
    public void testDivision_RatioEqualToOne() {
        double result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .divide(new Quantity<>(10.0, LengthUnit.FEET));

        assertEquals(1.0, result, 0.01);
    }

    @Test
    public void testDivision_NonCommutative() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(5.0, LengthUnit.FEET);

        assertNotEquals(a.divide(b), b.divide(a));
    }

    @Test
    public void testDivision_ByZero() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> zero = new Quantity<>(0.0, LengthUnit.FEET);

        assertThrows(ArithmeticException.class, () -> a.divide(zero));
    }

    @Test
    public void testDivision_NullOperand() {
        Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
        assertThrows(IllegalArgumentException.class, () -> q.divide(null));
    }

    @Test
    public void testDivision_CrossCategory() {
        Quantity<LengthUnit> length = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(5.0, WeightUnit.KILOGRAM);

        assertThrows(IllegalArgumentException.class, () -> length.divide((Quantity) weight));
    }

    @Test
    public void testDivision_Immutability() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(2.0, LengthUnit.FEET);

        a.divide(b);

        assertEquals(new Quantity<>(10.0, LengthUnit.FEET), a);
        assertEquals(new Quantity<>(2.0, LengthUnit.FEET), b);
    }

    @Test
    public void testSubtractionAddition_Inverse() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(3.0, LengthUnit.FEET);

        Quantity<LengthUnit> result = a.add(b).subtract(b);

        assertEquals(a, result);
    }


}
