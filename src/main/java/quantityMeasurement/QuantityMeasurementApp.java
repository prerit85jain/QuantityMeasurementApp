
 /*
  * @version 8.0
  * @author Prerit Jain
 */



package quantityMeasurement;

import quantityMeasurement.LengthUnit;

import java.time.Year;

 public class QuantityMeasurementApp {
	
	// Create a generic method to demonstrate Length equality check
	public static boolean demonstrateLengthEquality(Length length1, Length length2) {
		System.out.println("Equal (" + length1.compare(length2) + ")");
		return length1.equals(length2);
	}
		
	// Create a static method to take in method parameters and  demonstrate equality check
	public static boolean demonstrateLengthComparison(Length length1, Length length2) {
		System.out.println("Compare (" + length1.compare(length2) + ")");
		return length1.compare(length2);
	}
	
	// Demonstrate length conversion from one unit to another
	public static Length demonstrateLengthConversion(double value, LengthUnit fromUnit, LengthUnit toUnit) {
		Length length = new Length(value, fromUnit);
		
		return length.convertTo(toUnit);
	}
	
	// Overloading length conversion from length object to another length object
	public static Length demonstrateLengthConversion(Length length, LengthUnit toUnit) {
		return length.convertTo(toUnit);
	}

	// Demonstrate addition of second length to first length
	public static Length demonstrateLengthAddition(Length length1, Length length2){
		return length1.add(length2);
	}

	// Demonstrate addition of second length to first length with target unit.
	public static Length demonstrateLengthAddition(Length length1, Length length2, LengthUnit targetUnit){
		return length1.add(length2, targetUnit);
	}

	// Main method
	public static void main(String[] args) {
		// Demonstrate convertion Quantity(1.0, FEET).convertTo(INCHES)
		Length l1 = new Length(1.0, LengthUnit.FEET);
		System.out.println(demonstrateLengthConversion(l1, LengthUnit.INCHES));
		// Expected: Quantity(12.0, INCHES)

		// Quantity(1.0, FEET).add(Quantity(12.0, INCHES), FEET)
		Length l2 = new Length(12.0, LengthUnit.INCHES);
		System.out.println(demonstrateLengthAddition(l1, l2, LengthUnit.FEET));
		// Expected: Quantity(2.0, FEET)

		// Quantity(36.0, INCHES).equals(Quantity(1.0, YARDS))
		Length l3 = new Length(36.0, LengthUnit.INCHES);
		Length l4 = new Length(1.0, LengthUnit.YARDS);
		demonstrateLengthEquality(l3, l4);
		// Expected: true

		// Quantity(1.0, YARDS).add(Quantity(3.0, FEET), YARDS)
		Length l5 = new Length(1.0, LengthUnit.YARDS);
		Length l6 = new Length(3.0, LengthUnit.FEET);
		System.out.println(demonstrateLengthAddition(l5, l6, LengthUnit.YARDS));
		// Expected: Quantity(2.0, YARDS)

		// Quantity(2.54, CENTIMETERS).convertTo(INCHES)
		Length l7 = new Length(2.54, LengthUnit.CENTIMETERS);
		System.out.println(demonstrateLengthConversion(l7, LengthUnit.INCHES));
		// Expected: Quantity(~1.0, INCHES)

		// Quantity(5.0, FEET).add(Quantity(0.0, INCHES), FEET)
		Length l8 = new Length(5.0, LengthUnit.FEET);
		Length l9 = new Length(0.0, LengthUnit.INCHES);
		System.out.println(demonstrateLengthAddition(l8, l9, LengthUnit.FEET));
		// Expected: Quantity(5.0, FEET)

		// LengthUnit.FEET.convertToBaseUnit(12.0)
		System.out.println(LengthUnit.FEET.convertToBaseUnit(12.0));
		// Expected: 12.0

		// LengthUnit.INCHES.convertToBaseUnit(12.0)
		System.out.println(LengthUnit.INCHES.convertToBaseUnit(12.0));
		// Expected: 1.0
	}
}
