
 /*
  * @version 5.0
  * @author Prerit Jain
 */



package quantityMeasurement;

import quantityMeasurement.Length.LengthUnit;

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
	
	// Main method
	public static void main(String[] args) {
		// Demonstrate Feet and Inches conversion
		System.out.println(demonstrateLengthConversion(1, LengthUnit.FEET, LengthUnit.INCHES));
		
		// Demonstrate Yards and Feet conversion
		System.out.println(demonstrateLengthConversion(3, LengthUnit.YARDS, LengthUnit.FEET));
		
		// Demonstrate Inches and Yards conversion
		System.out.println(demonstrateLengthConversion(36, LengthUnit.INCHES, LengthUnit.YARDS));
		
		// Demonstrate Centimeter and Inches conversion
		System.out.println(demonstrateLengthConversion(1, LengthUnit.CENTIMETERS, LengthUnit.INCHES));
		
		// Demonstrate Feet and Inches conversion
		System.out.println(demonstrateLengthConversion(0, LengthUnit.FEET, LengthUnit.INCHES));	
		
	}
	
	
}
