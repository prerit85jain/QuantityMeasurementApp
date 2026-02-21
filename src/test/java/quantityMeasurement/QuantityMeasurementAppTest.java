package quantityMeasurement;

import org.junit.Test;

import quantityMeasurement.LengthUnit;

import static org.junit.Assert.*;

public class QuantityMeasurementAppTest {

	@Test
	public void testLengthUnitEnum_FeetConstant(){
		assertTrue(LengthUnit.FEET.getConversionFactor()==12);
	}

	@Test
	public void testConvertToBaseUnit_InchesToFeet(){
		assertTrue(LengthUnit.INCHES.convertToBaseUnit(12.0)==12);
	}

	@Test
	public void testConvertFromBaseUnit_FeetToYards(){
		assertTrue(LengthUnit.YARDS.convertFromBaseUnit(36.0)==1);
	}

	// ---- QuantityLength refactored tests ----

	@Test
	public void testQuantityLengthRefactored_Equality(){
		Length l1 = new Length(1.0, LengthUnit.FEET);
		Length l2 = new Length(12.0, LengthUnit.INCHES);

		assertEquals(l1, l2);
	}

	@Test
	public void testQuantityLengthRefactored_ConvertTo(){
		Length length = new Length(1.0, LengthUnit.FEET);

		Length result = new Length(12.0, LengthUnit.INCHES);
		assertEquals(result, length.convertTo(LengthUnit.INCHES));
	}

	@Test
	public void testQuantityLengthRefactored_Add(){
		Length l1 = new Length(1.0, LengthUnit.FEET);
		Length l2 = new Length(12.0, LengthUnit.INCHES);

		Length result = new Length(2.0, LengthUnit.FEET);
		assertEquals(result, l1.add(l2, LengthUnit.FEET));
	}

	@Test
	public void testQuantityLengthRefactored_AddWithTargetUnit(){
		Length l1 = new Length(1.0, LengthUnit.FEET);
		Length l2 = new Length(12.0, LengthUnit.INCHES);

		Length result = new Length(0.67, LengthUnit.YARDS);
		assertEquals(result, l1.add(l2, LengthUnit.YARDS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testQuantityLengthRefactored_NullUnit(){
		new Length(1.0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testQuantityLengthRefactored_InvalidValue(){
		new Length(Double.NaN, LengthUnit.FEET);
	}

	@Test
	public void testRoundTripConversion(){
		Length length = new Length(1.0, LengthUnit.FEET);

		Length converted = length.convertTo(LengthUnit.INCHES)
				.convertTo(LengthUnit.FEET);

		assertEquals(length, converted);
	}
}
