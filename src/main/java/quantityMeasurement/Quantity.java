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

    // Compares this quantity with other object for equality.
    @Override
    public boolean equals(Object o){
        if(o == this){return true;}
        if(o == null || o.getClass() != this.getClass()){return false;}

        Quantity<?> other = (Quantity<?>) o;
        if(!this.unit.getClass().equals(other.unit.getClass())){return false;}

        return Double.compare(unit.convertToBaseUnit(value), other.unit.convertToBaseUnit(other.value))==0;

    }

    // Add the quantity to the another quantity of the same unit type
    public Quantity<U> add(Quantity<U> other){
        return add(other, unit);
    }

    // Add this quantity to another quantity of the same unit type and return the result in the specific unit.
    public Quantity<U> add(Quantity<U> other, U targetUnit){
        validateOperation(other, targetUnit);

        double thisBaseValue = unit.convertToBaseUnit(value);
        double otherBaseValue = other.unit.convertToBaseUnit(other.value);

        double totalValue = targetUnit.convertFromBaseUnit(thisBaseValue+otherBaseValue);
        return new Quantity<>(round(totalValue), targetUnit);
    }

    // Subtracts this quantity from another quantity of the same unit type and return the result in the unit of this quantity
    public Quantity<U> subtract(Quantity<U> other){return subtract(other, unit);}

    // Subtracts this quantity from another quantity of the same unit type and return the result in the specified target unit
    public Quantity<U> subtract(Quantity<U> other, U targetUnit){
        validateOperation(other, targetUnit);

        double thisBaseUnit = unit.convertToBaseUnit(value);
        double otherBaseUnit = other.unit.convertToBaseUnit(other.value);

        double result = targetUnit.convertFromBaseUnit(thisBaseUnit-otherBaseUnit);

        return new Quantity<>(round(result), targetUnit);
    }

    // Divide this quantity by another quantity of the same unit type and return the result as a double
    public double divide(Quantity<U> other){
        validateOperation(other, unit);

        double thisBaseValue = unit.convertToBaseUnit(value);
        double otherBaseValue = other.unit.convertToBaseUnit(other.value);

        if(otherBaseValue == 0){throw new ArithmeticException("Division by zero");}

        return round(thisBaseValue/otherBaseValue);
    }

    // Validate this class and another class not be null and belongs to same Unit
    private void validateOperation(Quantity<U> quantity, U targetUnit){
        if(targetUnit == null){throw new IllegalArgumentException("Target unit cannot be null");}
        if(quantity == null){throw new IllegalArgumentException("Quantity cannot be null");}
        if(!unit.getClass().equals(quantity.unit.getClass()) || !targetUnit.getClass().equals(unit.getClass())){throw new IllegalArgumentException("Different measurement unit are not allowed");}
    }

    // Round the value to two decimal value
    private double round(double value){return (double) Math.round(value*100)/100;}

    // Override toString method
    @Override
    public String toString(){return String.format("%.2f %s", value, unit);}

}