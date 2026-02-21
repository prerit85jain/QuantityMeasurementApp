/*
*@version 1.0
*@author Prerit Jain
 */

package quantityMeasurement;

import java.util.Objects;

public class Weight {
    // Instance variables
    private double value;
    private WeightUnit unit;

    // Constructor to initialize weight value and unit
    public Weight(double value, WeightUnit unit){
        if(!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
        if(unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    // Getter for value
    public double getValue(){return value;}
    // Getter for unit
    public WeightUnit getUnit(){return unit;}

    // Compare two weight objects for equality based on their values in the base unit
    private boolean compare(Weight weight) {
        return Double.compare(convertToBaseUnit(), weight.convertToBaseUnit())==0;
    }

    // Compare this weight with another weight for equality
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o==null ||this.getClass() != o.getClass()) {
            return false;
        }

        Weight weight = (Weight) o;
        return compare(weight);
    }

    @Override
    public int hashCode(){
        return Objects.hash(convertToBaseUnit());
    }

    // Convert this weight to a specific target unit
    public Weight convertTo(WeightUnit targetUnit){
        if(targetUnit==null){
            throw new IllegalArgumentException("Unit cannot be null");
        }

        double baseUnitValue = convertToBaseUnit();
        double targetUnitValue = convertFromBaseToTargetUnit(baseUnitValue, targetUnit);
        return new Weight(targetUnitValue, targetUnit);
    }

    // Add another weight to this one
    public Weight add(Weight thatWeight){
        return addAndConvert(thatWeight, unit);
    }

    // Add another weight to this weight with target unit specification
    public Weight add(Weight weight, WeightUnit targetUnit){
        return addAndConvert(weight, targetUnit);
    }

    public Weight addAndConvert(Weight weight, WeightUnit targetUnit){
        double thisT0Gram = unit.convertToBaseUnit(value);
        double thatToGram = weight.unit.convertToBaseUnit(weight.value);

        Weight totalWeightinGram = new Weight(round(thisT0Gram+thatToGram), WeightUnit.GRAM);
        return totalWeightinGram.convertTo(targetUnit);
    }

    // Method to round off the value with two decimal values
    private double round(double value){
        return Math.round(value*100)/100;
    }

    // Convert this weight value to the base unit (gram)
    private double convertToBaseUnit(){
        return unit.convertToBaseUnit(value);
    }

    // Convert weight value from the base unit (gram) to a specific target unit
    private double convertFromBaseToTargetUnit(double weightInGrams, WeightUnit targetUnit){
        return targetUnit.convertFromBaseUnit(weightInGrams);
    }

    // Override toString method
    @Override
    public String toString(){
        return String.format("%.2f %s", value, unit);
    }
}

