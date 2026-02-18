package feetEqualityTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import feetEquality.FeetMeasurmentquality.Feet;

public class FeetMeasumentEqualityTest {

	// testEquality_SameValue
    @Test
    public void testFeetEquality_SameValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);

        assertTrue(feet1.equals(feet2));
    }
    
    // testEquality_DifferentValue
    @Test
    public void testFeetEquality_DifferentValue() {
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(2.0);

        assertFalse(feet1.equals(feet2));
    }
    
    // testEquality_NullComparison
    @Test
    public void testFeetEquality_NullComparison() {
        Feet feet1 = new Feet(1.0);

        assertFalse(feet1.equals(null));
    }
    
    // testEquality_DifferentClass
    @Test
    public void testFeetEquality_DifferentClass() {
        Feet feet1 = new Feet(1.0);
        String other = "1.0";

        assertFalse(feet1.equals(other));
    }
    
    // testEquality_SameReference
    @Test
    public void testFeetEquality_SameReference() {
        Feet feet1 = new Feet(1.0);

        assertTrue(feet1.equals(feet1));
    }
    
}
