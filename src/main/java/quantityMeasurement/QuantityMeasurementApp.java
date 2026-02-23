
 /*
  * @version 9.0
  * @author Prerit Jain
 */



package quantityMeasurement;

import quantityMeasurement.LengthUnit;

import java.time.Year;

 public class QuantityMeasurementApp {

	 // Demonstrate Equality comparison between two quantities
	 public static <U extends IMeasurable> boolean demonstrateEquality(Quantity<U> quantity1, Quantity<U> quantity2){
		 return quantity1.equals(quantity2);
	 }

	 // Demonstrate Conversion of quantity to target unit
	 public static <U extends IMeasurable> Quantity<U> demonstrateConversion(Quantity<U> quantity, U targetUnit){
		 return quantity.convertTo(targetUnit);
	 }

	 // Demonstrate addition of two quantities and return in the unit of first quantity
	 public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> quantity1, Quantity<U> quantity2){
		 return quantity1.add(quantity2);
	 }

	 // Demonstrate addition of two quantities and return the result in the specified target quantity
	 public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> quantity1, Quantity<U> quantity2, U targetUnit){
		 return quantity1.add(quantity2, targetUnit);
	 }

	// Main method
	public static void main(String[] args) {
		// Demonstration equality between the two quantities
		Quantity<WeightUnit> weightInGrams = new Quantity<>(1000.0, WeightUnit.GRAM);
		Quantity<WeightUnit> weightInKilograms = new Quantity<>(1.0, WeightUnit.KILOGRAM);
		boolean areEqual = demonstrateEquality(weightInGrams, weightInKilograms);
		System.out.println("Are weights equal? " + areEqual);

		// Demonstration conversion between the two quantities
		Quantity<WeightUnit> convertedWeight = demonstrateConversion(weightInGrams,
				WeightUnit.KILOGRAM);
		System.out.println("Converted Weight: " + convertedWeight.getValue() + " " +
				convertedWeight.getUnit());

		// Demonstration addition of two quantities and return the result in the unit
		// of the first quantity
		Quantity<WeightUnit> weightInPounds = new Quantity<>(2.20462, WeightUnit.POUND);
		Quantity<WeightUnit> sumWeight = demonstrateAddition(weightInKilograms, weightInPounds);
		System.out.println("Sum Weight: " + sumWeight.getValue() + " " +
				sumWeight.getUnit());

		// Demonstration addition of two quantities and return the result in a specified
		// target unit
		Quantity<WeightUnit> sumWeightInGrams = demonstrateAddition(weightInKilograms,
				weightInPounds,
				WeightUnit.GRAM);
		System.out.println("Sum weight in Grams: " + sumWeightInGrams.getValue() + " " +
				sumWeightInGrams.getUnit());
	}

 }
