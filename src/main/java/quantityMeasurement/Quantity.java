package quantityMeasurement;

public class Quantity<U extends IMeasurable> {
    private double value;
    private U unit;

    public Quantity(double value, U unit){
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    public double getValue(){return value;}

    public U getUnit(){return unit;}

    // Convert this unit to the specific target unit
    public Quantity<U> convertTo(U targetUnit){
        if(targetUnit == null){
            throw new IllegalArgumentException("Unit cannot be null");
        }
        if(!targetUnit.getClass().equals(unit.getClass())){
            throw new IllegalArgumentException("Target unit should belong to same class");
        }

        double baseValue = unit.convertToBaseUnit(value);
        double convertValue = targetUnit.convertFromBaseUnit(baseValue);

        return new Quantity<U>(round(convertValue), targetUnit);
    }

    // Add the quantity to the another quantity of the same unit type
    public Quantity<U> add(Quantity<U> other){
        return addAndConvert(other, unit);
    }

    // Add this quantity to another quantity of the same unit type and return the result in the specific unit.
    public Quantity<U> add(Quantity<U> other, U targetUnit){
        return addAndConvert(other, targetUnit);
    }

    // Compares this quantity with other object for equality.
    @Override
    public boolean equals(Object o){
        if(o == this){return true;}
        if(o == null || o.getClass() != this.getClass()){return false;}

        Quantity<?> other = (Quantity<?>) o;
        if(!this.unit.getClass().equals(other.unit.getClass())){return false;}

        return Double.compare(unit.convertToBaseUnit(value), other.unit.convertToBaseUnit(other.value))==0;

    }

    // Add the quantity value and return it in target unit
    private Quantity<U> addAndConvert(Quantity<U> other, U targetUnit){
        if(targetUnit == null){
            throw new IllegalArgumentException("Unit cannot be null");
        }
        if (!this.unit.getClass().equals(other.unit.getClass())) {
            throw new IllegalArgumentException("Cannot add different measurement categories");
        }
        if(!targetUnit.getClass().equals(unit.getClass())){
            throw new IllegalArgumentException("Target unit should belong to same class");
        }
        double thisBaseValue = unit.convertToBaseUnit(value);
        double otherBaseValue = other.unit.convertToBaseUnit(other.value);

        double totalValue = targetUnit.convertFromBaseUnit(thisBaseValue+otherBaseValue);
        return new Quantity<>(round(totalValue), targetUnit);

    }

    // Round the value to two decimal value
    private double round(double value){return (double) Math.round(value*100)/100;}

    public String toString(){
        return String.format("%.2f %s", value, unit);
    }

    // Main method to demonstrate
    public static void main(String[] args) {
        // Example usage
        Quantity<LengthUnit> lengthInFeet = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> lengthInInches = new Quantity<>(120.0, LengthUnit.INCHES);
        boolean isEqual = lengthInFeet.equals(lengthInInches); // true
        System.out.println("Are lengths equal? " + isEqual);

        // Example usage for WeightUnit
        Quantity<WeightUnit> weightInKilograms = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> weightInGrams = new Quantity<>(1000.0, WeightUnit.GRAM);
        isEqual = weightInKilograms.equals(weightInGrams); // true
        System.out.println("Are weights equal? " + isEqual);

        // Example Conversion
        double convertedLength = (lengthInFeet.convertTo(LengthUnit.INCHES)).getValue();
        System.out.println("10 feet in inches: " + convertedLength);

        // Example Addition
        Quantity<LengthUnit> totalLength = lengthInFeet.add(lengthInInches, LengthUnit.FEET);
        System.out.println("Total Length in feet: " + totalLength.getValue() + " " + totalLength.getUnit());

        // Example Addition for WeightUnit
        Quantity<WeightUnit> weightInPounds = new Quantity<>(2.0, WeightUnit.POUND);
        Quantity<WeightUnit> totalWeight = weightInKilograms.add(weightInPounds, WeightUnit.KILOGRAM);
        System.out.println("Total Weight in kilograms: " + totalWeight.getValue() + " " + totalWeight.getUnit());
    }

}