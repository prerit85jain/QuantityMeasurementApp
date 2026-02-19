package quantityMeasurement;

import quantityMeasurement.Length.LengthUnit;

public class QuantityMeasurementApp {
	
	// Create a generic method to demonstrate Length equality check
	public static boolean demonstrateLengthEquality(Length length1, Length length2) {
		return length1.equals(length2);
	}
	
	// Define a static method to demonstrate Feet equality check
	public static void demonstrateFeetEquality() {
		Length feet1 = new Length(1.0, LengthUnit.FEET);
		Length feet2 = new Length(1.0, LengthUnit.FEET);
		
		boolean result = feet1.equals(feet2);

        System.out.println("Feet Equality -> " + feet1.equals(feet2));
	}
	
	// Define a static method to demonstrate Inches equality check
	public static void demonstrateInchesEquality() {
		Length inch1 = new Length(1.0, LengthUnit.Inches);
		Length inch2 = new Length(1.0, LengthUnit.Inches);
	
		boolean result = inch1.equals(inch2);

	    System.out.println("Inches Equality -> " + inch1.equals(inch2));
	}
		
	// Create a static method to demonstrate Feet and Inches comparison
	public static void demonstarteFeetInchesComparison() {
		Length length1 = new Length(1, LengthUnit.FEET);
		Length length2 = new Length(12, LengthUnit.Inches);
		
		System.out.println("Feet-Inches Equality -> " + length1.equals(length2));
	}
	
	// Main method
	public static void main(String[] args) {
		demonstrateFeetEquality();
		demonstrateInchesEquality();
		demonstarteFeetInchesComparison();
	}
	
	
}
