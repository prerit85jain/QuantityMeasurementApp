package quantityMeasurement;

import org.junit.Test;

import quantityMeasurement.Length.LengthUnit;

import static org.junit.Assert.*;

public class QuantityMeasurementAppTest {
	
	@Test
	public void testConversion_FeetToInches() {
		Length feet = new Length(1, LengthUnit.FEET);
		Length inch = feet.convertTo(LengthUnit.INCHES);
		
		assertTrue(Double.compare(12.0, inch.getValue())==0);
	}

	@Test
	public void testConversion_InchesToFeet(){
		Length inches = new Length(24, LengthUnit.INCHES);
		Length feet = inches.convertTo(LengthUnit.FEET);

		assertTrue(Double.compare(2, feet.getValue())==0);
	}

	@Test
	public void testConversion_YardsToInches(){
		Length yards = new Length(1, LengthUnit.YARDS);
		Length inches = yards.convertTo(LengthUnit.INCHES);

		assertTrue(Double.compare(36, inches.getValue())==0);
	}

	@Test
	public void testConversion_InchesToYards(){
		Length inches = new Length(72, LengthUnit.INCHES);
		Length yards = inches.convertTo(LengthUnit.YARDS);

		assertTrue(Double.compare(2, yards.getValue())==0);
	}

	@Test
	public void testConversion_CentimetersToInches(){
		Length cm = new Length(2.54, LengthUnit.CENTIMETERS);
		Length inches = cm.convertTo(LengthUnit.INCHES);

		assertTrue(Double.compare(1, inches.getValue())==0);
	}

	@Test
	public void testConversion_FeatToYard(){
		Length feet = new Length(6, LengthUnit.FEET);
		Length yards = feet.convertTo(LengthUnit.YARDS);

		assertTrue(Double.compare(2, yards.getValue())==0);
	}

	@Test
	public void testConversion_RoundTrip_PreservesValue(){
		Length feet = new Length(1, LengthUnit.FEET);
		Length inch = feet.convertTo(LengthUnit.INCHES);
		Length revFeet = inch.convertTo(LengthUnit.FEET);

		assertTrue(Double.compare(1, revFeet.getValue())== 0);
	}

	@Test
	public void testConversion_ZeroValue(){
		Length feet = new Length(0, LengthUnit.FEET);
		Length inches = feet.convertTo(LengthUnit.INCHES);

		assertTrue(Double.compare(0, inches.getValue())==0);
	}

	@Test
	public void testConversion_NegativeValue(){
		Length feet = new Length(-1, LengthUnit.FEET);
		Length inches = feet.convertTo(LengthUnit.INCHES);

		assertTrue(Double.compare(-12, inches.getValue())==0);
	}

	@Test
	public void testConversion_InvalidUnit_Throws(){
		assertThrows(IllegalArgumentException.class,()->{
			new Length(1, null);
		});
	}

	@Test
	public void testConversion_NaNOrInfinite_Throws(){
		assertThrows(IllegalArgumentException.class, ()->{
			new Length(Double.POSITIVE_INFINITY, LengthUnit.FEET);
		});
	}

}
