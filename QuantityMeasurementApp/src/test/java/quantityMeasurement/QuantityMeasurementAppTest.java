package quantityMeasurement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import quantityMeasurement.QuantityMeasurementApp.Feet;
import quantityMeasurement.QuantityMeasurementApp.Inches;

public class QuantityMeasurementAppTest {
	
	// Feet test cases

    @Test
    public void testFeetEquality_SameValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);

        assertTrue(feet1.equals(feet2));
    }
    
    @Test
    public void testFeetEquality_DifferentValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(2.0);

        assertFalse(feet1.equals(feet2));
    }
    
    @Test
    public void testFeetEquality_NullComparison() {
        Feet feet1 = new Feet(1.0);

        assertFalse(feet1.equals(null));
    }
    
    @Test
    public void testFeetEquality_DifferentClass() {
        Feet feet1 = new Feet(1.0);
        String other = "1.0";

        assertFalse(feet1.equals(other));
    }
    
    @Test
    public void testFeetEquality_SameReference() {
        Feet feet1 = new Feet(1.0);

        assertTrue(feet1.equals(feet1));
    }
    
    
    // Inches test cases
    @Test
    public void testInchessEquality_SameValue() {
        Inches i1 = new Inches(1.0);
        Inches i2 = new Inches(1.0);

        assertTrue(i1.equals(i2));
    }

    @Test
    public void testInchesEquality_DifferentValue() {
        Inches inch1 = new Inches(1.0);
        Inches inch2 = new Inches(2.0);

        assertFalse(inch1.equals(inch2));
    }


    @Test
    public void testInchesEquality_NullComparison() {
        Inches i1 = new Inches(1.0);

        assertFalse(i1.equals(null));
    }

    @Test
    public void testInchesEquality_DifferentClass() {
        Inches i1 = new Inches(1.0);
        Integer obj = 1;

        assertFalse(i1.equals(obj));
    }

    @Test
    public void testInchesEquality_SameReference() {
        Inches i1 = new Inches(1.0);

        assertTrue(i1.equals(i1));
    }
}
