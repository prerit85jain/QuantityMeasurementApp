package quantityMeasurement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import quantityMeasurement.Length.LengthUnit;

public class QuantityMeasurementAppTest {
	
	@Test
	public void testEquality_YardToYard_SameValue() {
		Length yard1 = new Length(1, LengthUnit.YARDS);
		Length yard2 = new Length(1, LengthUnit.YARDS);
		
		assertTrue(yard1.equals(yard2));
	}
	
	@Test
	public void testEquality_YardToYard_DifferentValue() {
		Length yard1 = new Length(1, LengthUnit.YARDS);
		Length yard2 = new Length(2, LengthUnit.YARDS);
		
		assertFalse(yard1.equals(yard2));
	}
	
	@Test
	public void testEquality_YardToFeet_EquivalentValue() {
		Length yard = new Length(1, LengthUnit.YARDS);
		Length feet = new Length(3, LengthUnit.FEET);
		
		assertTrue(yard.equals(feet));
	}
	
	@Test
	public void testEquality_FeetToYard_EquivalentValue() {
		Length yard = new Length(1, LengthUnit.YARDS);
		Length feet = new Length(3, LengthUnit.FEET);
		
		assertTrue(feet.equals(yard));
	}
	
	@Test
	public void testEquality_YardToInches_EquivalentValue() {
		Length yard = new Length(1, LengthUnit.YARDS);
		Length inch = new Length(36, LengthUnit.INCHES);
		
		assertTrue(yard.equals(inch));
	}
	
	@Test
	public void testEquality_InchesToYard_EquivalentValue() {
		Length yard = new Length(1, LengthUnit.YARDS);
		Length inch = new Length(36, LengthUnit.INCHES);
		
		assertTrue(inch.equals(yard));
	}
	
	
	@Test
	public void testEquality_YardToFeet_NonEquivalentValue() {
		Length yard = new Length(1, LengthUnit.YARDS);
		Length feet = new Length(2, LengthUnit.FEET);
		
		assertFalse(yard.equals(feet));
	}
	
	@Test
	public void testEquality_centimetersToInches_EquivalentValue() {
		Length cm = new Length(1, LengthUnit.CENTIMETERS);
		Length inch = new Length(0.393701, LengthUnit.INCHES);
		
		assertTrue(cm.equals(inch));
	}
	
	@Test
	public void testEquality_centimetersToFeet_NonEquivalentValue() {
		Length cm = new Length(1, LengthUnit.CENTIMETERS);
		Length feet = new Length(1, LengthUnit.FEET);
		
		assertFalse(cm.equals(feet));
	}
	
}
