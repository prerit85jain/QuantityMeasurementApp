package quantityMeasurement;

import org.junit.Test;

import quantityMeasurement.LengthUnit;

import static org.junit.Assert.*;

public class QuantityMeasurementAppTest {

    @Test
    public void kilogramEquals1000Grams(){
        Weight kg = new Weight(1, WeightUnit.KILOGRAM);
        assertEquals(new Weight(1000, WeightUnit.GRAM), kg.convertTo(WeightUnit.GRAM));
    }

    @Test
    public void poundEquals453Point592Grams() {
        Weight pound = new Weight(1, WeightUnit.POUND);
        Weight grams = new Weight(453.592, WeightUnit.GRAM);

        assertEquals(pound, grams);
    }

    @Test
    public void tonneEquals1000000Grams() {
        Weight tonne = new Weight(1, WeightUnit.TONNE);
        Weight grams = new Weight(1000000, WeightUnit.GRAM);

        assertEquals(tonne, grams);
    }

    @Test
    public void kilogramNotEqualToPound() {
        Weight kilogram = new Weight(1, WeightUnit.KILOGRAM);
        Weight pound = new Weight(1, WeightUnit.POUND);

        assertNotEquals(kilogram, pound);
    }

    @Test
    public void additionOfWeightsEqualsExpected() {
        Weight weight1 = new Weight(1, WeightUnit.KILOGRAM);
        Weight weight2 = new Weight(1000, WeightUnit.GRAM);

        Weight result = weight1.add(weight2);

        Weight expected = new Weight(2, WeightUnit.KILOGRAM);

        assertEquals(expected, result);
    }

    @Test
    public void testFeetEquality() {
        Length length1 = new Length(1, LengthUnit.FEET);
        Length length2 = new Length(1, LengthUnit.FEET);

        assertEquals(length1, length2);
    }

    // testInchesEquality
    @Test
    public void testInchesEquality() {
        Length length1 = new Length(12, LengthUnit.INCHES);
        Length length2 = new Length(12, LengthUnit.INCHES);

        assertEquals(length1, length2);
    }

    // testFeetInchesComparison
    @Test
    public void testFeetInchesComparison() {
        Length feet = new Length(1, LengthUnit.FEET);
        Length inches = new Length(12, LengthUnit.INCHES);

        assertEquals(feet, inches);
    }

    // testFeetInequality
    @Test
    public void testFeetInequality() {
        Length length1 = new Length(1, LengthUnit.FEET);
        Length length2 = new Length(2, LengthUnit.FEET);

        assertNotEquals(length1, length2);
    }

    // testInchesInequality
    @Test
    public void testInchesInequality() {
        Length length1 = new Length(12, LengthUnit.INCHES);
        Length length2 = new Length(13, LengthUnit.INCHES);

        assertNotEquals(length1, length2);
    }

    // testCrossUnitInequality
    @Test
    public void testCrossUnitInequality() {
        Length feet = new Length(1, LengthUnit.FEET);
        Length inches = new Length(13, LengthUnit.INCHES);

        assertNotEquals(feet, inches);
    }

    // testMultipleFeetComparison
    @Test
    public void testMultipleFeetComparison() {
        Length length1 = new Length(2, LengthUnit.FEET);
        Length length2 = new Length(24, LengthUnit.INCHES);

        assertEquals(length1, length2);
    }

    // yardEquals36Inches
    @Test
    public void yardEquals36Inches() {
        Length yard = new Length(1, LengthUnit.YARDS);
        Length inches = new Length(36, LengthUnit.INCHES);

        assertEquals(yard, inches);
    }

    // centimeterEquals39Point3701Inches
    @Test
    public void centimeterEquals39Point3701Inches() {
        Length centimeters = new Length(100, LengthUnit.CENTIMETERS);
        Length inches = new Length(39.3701, LengthUnit.INCHES);

        assertEquals(centimeters, inches);
    }
}
