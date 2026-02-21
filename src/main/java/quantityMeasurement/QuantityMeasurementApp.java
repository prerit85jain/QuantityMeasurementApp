
 /*
  * @version 6.0
  * @author Prerit Jain
 */



package quantityMeasurement;

import quantityMeasurement.Length.LengthUnit;

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

	// Main method
	public static void main(String[] args) {
		// Demonstrate Feet with Feet addition
		System.out.println(demonstrateLengthAddition(new Length(1, LengthUnit.FEET), new Length(2, LengthUnit.FEET)));

		// Demonstrate Feet with inches addition
		System.out.println(demonstrateLengthAddition(new Length(1, LengthUnit.FEET), new Length(12, LengthUnit.INCHES)));

		// Demonstrate inches with feet addition
		System.out.println(demonstrateLengthAddition(new Length(12, LengthUnit.INCHES), new Length(1, LengthUnit.FEET)));

		// Demonstrate yards with feet addition
		System.out.println(demonstrateLengthAddition(new Length(1, LengthUnit.YARDS), new Length(3, LengthUnit.FEET)));

		// Demonstrate inches with yards addition
		System.out.println(demonstrateLengthAddition(new Length(36, LengthUnit.INCHES), new Length(1, LengthUnit.YARDS)));

		// Demonstrate centimeter to inches
		System.out.println(demonstrateLengthAddition(new Length(2.54, LengthUnit.CENTIMETERS), new Length(1, LengthUnit.INCHES)));

		// Demonstrate feet to inches
		System.out.println(demonstrateLengthAddition(new Length(5, LengthUnit.FEET), new Length(0, LengthUnit.INCHES)));

		// Demonstrate feet to feet
		System.out.println(demonstrateLengthAddition(new Length(5, LengthUnit.FEET), new Length(-2, LengthUnit.FEET)));
	}
}
