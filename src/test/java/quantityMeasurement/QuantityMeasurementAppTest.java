package quantityMeasurement;

import org.junit.Test;

import quantityMeasurement.Length.LengthUnit;

import static org.junit.Assert.*;

public class QuantityMeasurementAppTest {
	
	@Test
	public void testAddition_SameUnit_FeetPlusFeet(){
		Length feet1 = new Length(1, LengthUnit.FEET);
		Length feet2 = new Length(2, LengthUnit.FEET);

		Length result = new Length(3, LengthUnit.FEET);
		assertEquals(result, feet1.add(feet2));
	}

	@Test
	public void testAddition_SameUnit_InchPlusInch(){
		Length inch1 = new Length(6, LengthUnit.INCHES);
		Length inch2 = new Length(6, LengthUnit.INCHES);

		Length result = new Length(12, LengthUnit.INCHES);
		assertEquals(result, inch1.add(inch2));
	}

	@Test
	public void testAddition_CrossUnit_FeetPlusInches(){
		Length feet = new Length(1, LengthUnit.FEET);
		Length inch = new Length(12, LengthUnit.INCHES);

		Length result = new Length(2, LengthUnit.FEET);
		assertEquals(result, feet.add(inch));
	}

	@Test
	public void testAddition_CrossUnit_InchPlusFeet(){
		Length inch = new Length(12, LengthUnit.INCHES);
		Length feet = new Length(1, LengthUnit.FEET);

		Length result = new Length(24, LengthUnit.INCHES);
		assertEquals(result, inch.add(feet));
	}

	@Test
	public void testAddition_CrossUnit_YardPlusFeet(){
		Length yard = new Length(1, LengthUnit.YARDS);
		Length feet = new Length(3, LengthUnit.FEET);

		Length result = new Length(2, LengthUnit.YARDS);
		assertEquals(result, yard.add(feet));
	}

	@Test
	public void testAddition_CrossUnit_CentimeterPlusInch(){
		Length cm = new Length(2.54, LengthUnit.CENTIMETERS);
		Length inch = new Length(1, LengthUnit.INCHES);
		Length result = new Length(5, LengthUnit.CENTIMETERS);
		assertEquals(result, cm.add(inch));
	}
	
	@Test
	public void testAddition_Commutativity(){
		Length feet = new Length(1, LengthUnit.FEET);
		Length inch = new Length(12, LengthUnit.INCHES);
		Length result1 = new Length(2, LengthUnit.FEET);
		Length result2 = new Length(24, LengthUnit.INCHES);
		assertEquals(result1, result2.convertTo(LengthUnit.FEET));
	}

	@Test
	public void testAddition_WithZero(){
		Length feet = new Length(5, LengthUnit.FEET);
		Length inch = new Length(0, LengthUnit.INCHES);
		Length result = new Length(5, LengthUnit.FEET);
		assertEquals(result, feet.add(inch));
	}

	@Test
	public void testAddition_NegativeValues(){
		Length feet = new Length(5, LengthUnit.FEET);
		Length fee1 = new Length(-2, LengthUnit.FEET);
		Length result = new Length(3, LengthUnit.FEET);
		assertEquals(result, feet.add(fee1));
	}

	@Test
	public void testAddition_NullSecondOperand(){
		Length feet = new Length(1, LengthUnit.FEET);
		assertThrows(IllegalArgumentException.class, ()->{
			feet.add(new Length(5, null));
		});
	}

	@Test
	public void testAddition_LargeValues(){
		Length feet = new Length(1e6, LengthUnit.FEET);
		Length result = new Length(2e6, LengthUnit.FEET);
		assertEquals(result, feet.add(feet));
	}

	@Test
	public void testAddition_SmallValues(){
		Length feet1 = new Length(0.001, LengthUnit.FEET);
		Length feet2 = new Length(0.002, LengthUnit.FEET);

		Length result = new Length(0.003, LengthUnit.FEET);
		assertEquals(result, feet1.add(feet2));
	}
}
