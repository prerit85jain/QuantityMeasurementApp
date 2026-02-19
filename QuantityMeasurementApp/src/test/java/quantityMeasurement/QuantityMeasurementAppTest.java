package quantityMeasurement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import quantityMeasurement.Length.LengthUnit;

public class QuantityMeasurementAppTest {
	
	@Test
	public void testEquality_FeetToFeet_SameValue() {
		Length feet1 = new Length(1, LengthUnit.FEET);
		Length feet2 = new Length(1, LengthUnit.FEET);
		
		assertTrue(feet1.equals(feet2));
	}
	
	@Test
	public void testEquality_InchToInch_SameValue() {
		Length inches1 = new Length(1, LengthUnit.Inches);
		Length inches2 = new Length(1, LengthUnit.Inches);
		
		assertTrue(inches1.equals(inches2));
	}
	
	@Test
	public void testEquality_NullComparison() {
		Length l1 = new Length(1.0, LengthUnit.FEET);

        assertFalse(l1.equals(null));
	}
	
	@Test
	public void testEquality_InchToFeet_EquivalentValue() {
		Length inch = new Length(12, LengthUnit.Inches);
		Length feet = new Length(1, LengthUnit.FEET);
		
		assertTrue(inch.equals(feet));
	}
	
	@Test
	public void testEquality_FeetToFeet_DifferentValue() {
		Length feet1 = new Length(1, LengthUnit.FEET);
		Length feet2 = new Length(2, LengthUnit.FEET);
		
		assertFalse(feet1.equals(feet2));
	}
	
	@Test
	public void testEquality_InchToInch_DifferentValue() {
		Length inch1 = new Length(1, LengthUnit.Inches);
		Length inch2 = new Length(2, LengthUnit.Inches);
		
		assertFalse(inch1.equals(inch2));
	}
	
	
	@Test
	public void testEquality_NullUnit() {
		
		assertThrows(IllegalArgumentException.class, ()->{
			new Length(2, null);
		});
	}
	
	@Test
	public void testEquality_SameReference() {
		Length inch = new Length(2, LengthUnit.Inches);
		
		assertTrue(inch.equals(inch));
	}
	
}
