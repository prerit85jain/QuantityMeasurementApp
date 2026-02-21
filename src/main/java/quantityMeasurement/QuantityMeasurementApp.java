
 /*
  * @version 9.0
  * @author Prerit Jain
 */



package quantityMeasurement;

import quantityMeasurement.LengthUnit;

import java.time.Year;

 public class QuantityMeasurementApp {
	 /*
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
	*/

	 // Demonstrate weight equality between two weight instances
	 public static boolean demonstrateWeightEquality(Weight weight1, Weight weight2){
		 return weight1.equals(weight2);
	 }

	 // Demonstrate weight comparison between two weights specified by value and unit
	 public static boolean demonstrateWeightComparison(double value1, WeightUnit unit1, double value2, WeightUnit unit2){
		 return new Weight(value1, unit1).equals(new Weight(value2, unit2));
	 }

	 // Demonstrate weight conversion from one unit to another
	 public static Weight demonstrateWeightConversion(double value, WeightUnit fromUnit, WeightUnit toUnit){
		 Weight weight = new Weight(value, fromUnit).convertTo(toUnit);
		 return weight;
	 }

	 // Demonstrate weight conversion from one weight instance to another unit
	 public static Weight demonstrateWeightConversion(Weight weight, WeightUnit toUnit){
		 return weight.convertTo(toUnit);
	 }

	 // Demonstrate addition of second weight to first weight
	 public static Weight demonstrateWeightAddition(Weight weight1, Weight weight2){
		 return weight1.add(weight2);
	 }

	 // Demonstrate addition of second weight to first weight with target unit
	 public static Weight demonstrateWeightAddition(Weight weight1, Weight weight2, WeightUnit targetUnit){
		 return weight1.add(weight2, targetUnit);
	 }

	// Main method
	public static void main(String[] args) {
		System.out.println(demonstrateWeightAddition(new Weight(1, WeightUnit.KILOGRAM), new Weight(1, WeightUnit.KILOGRAM)));

		System.out.println(demonstrateWeightAddition(new Weight(1, WeightUnit.KILOGRAM), new Weight(1000, WeightUnit.GRAM)));

		System.out.println(demonstrateWeightAddition(new Weight(1, WeightUnit.KILOGRAM), new Weight(3, WeightUnit.KILOGRAM), WeightUnit.GRAM));

		System.out.println(demonstrateWeightConversion(new Weight(1, WeightUnit.KILOGRAM), WeightUnit.POUND));

		System.out.println(demonstrateWeightConversion(new Weight(2.20, WeightUnit.POUND), WeightUnit.KILOGRAM));

		Weight pound = new Weight(1, WeightUnit.POUND);
		Weight gram = new Weight(453.592, WeightUnit.GRAM);
		System.out.println(demonstrateWeightConversion(pound, WeightUnit.GRAM));
		System.out.println(demonstrateWeightConversion(gram, WeightUnit.POUND));
 	}
}
