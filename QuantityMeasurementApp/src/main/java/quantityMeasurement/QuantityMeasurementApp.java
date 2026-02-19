
 /*
  * @version 4.0
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
	public static boolean demonstarteLengthComparison(Length length1, Length length2) {
		System.out.println("Compare (" + length1.compare(length2) + ")");
		return length1.compare(length2);
	}
	
	// Main method
	public static void main(String[] args) {
		// Demonstrate Feet and Inches comparison
		demonstarteLengthComparison(new Length(1, LengthUnit.FEET), new Length(12, LengthUnit.INCHES));
		// Demonstrate Yards and Inches comparison
		demonstarteLengthComparison(new Length(1, LengthUnit.YARDS), new Length(36, LengthUnit.INCHES));
		// Demonstrate Centimeters and Inches comparison
		demonstarteLengthComparison(new Length(100, LengthUnit.CENTIMETERS), new Length(39.3701, LengthUnit.INCHES));
		// Demonstrate Feet and Yards comparison
		demonstarteLengthComparison(new Length(3, LengthUnit.FEET), new Length(1, LengthUnit.YARDS));
		// Demonstrate Centimeters and Feet comparison
		demonstarteLengthComparison(new Length(30.48, LengthUnit.CENTIMETERS), new Length(1, LengthUnit.FEET));
		
		
	}
	
	
}
