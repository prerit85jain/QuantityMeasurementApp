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

    // Arithmetic enum
    private enum ArithmeticOperation{
        ADD{
            @Override
            public double compute(double thisBase, double otherBase){
                return thisBase + otherBase;
            }
        },
        SUBTRACT{
            @Override
            public double compute(double thisBase, double otherBase){
                return thisBase - otherBase;
            }
        },
        Divide{
            @Override
            public double compute(double thisBase, double otherBase){
                if(otherBase==0){throw new ArithmeticException("Cannot divide by zero");}
                return thisBase/otherBase;
            }
        };

        public abstract double compute(double thisBase, double otherBase);
    }

    // Add the quantity to the another quantity of the same unit type
    public Quantity<U> add(Quantity<U> other){
        return add(other, unit);
    }

    // Add this quantity to another quantity of the same unit type and return the result in the specific unit.
    public Quantity<U> add(Quantity<U> other, U targetUnit){
        double baseResult = performBaseArithmetic(other, targetUnit, ArithmeticOperation.ADD, true);
        return buildQuantityFromBase(baseResult, targetUnit);
    }

    // Subtracts this quantity from another quantity of the same unit type and return the result in the unit of this quantity
    public Quantity<U> subtract(Quantity<U> other){return subtract(other, unit);}

    // Subtracts this quantity from another quantity of the same unit type and return the result in the specified target unit
    public Quantity<U> subtract(Quantity<U> other, U targetUnit){
        double baseResult = performBaseArithmetic(other, targetUnit, ArithmeticOperation.SUBTRACT, true);
        return buildQuantityFromBase(baseResult, targetUnit);
    }

    // Divide this quantity by another quantity of the same unit type and return the result as a double
    public double divide(Quantity<U> other){
        return performBaseArithmetic(other, null, ArithmeticOperation.Divide, false);
    }

    // Centralized core logic
    private double performBaseArithmetic(Quantity<U> other, U targetUnit, ArithmeticOperation operation, boolean targetUnitRequired){
        validateArithmeticOperands(other, targetUnit, targetUnitRequired);

        double thisBase = unit.convertToBaseUnit(value);
        double otherBase = other.unit.convertToBaseUnit(other.value);

        return operation.compute(thisBase, otherBase);
    }

    // Validate this class and another class not be null and belongs to same Unit
    private void validateArithmeticOperands(Quantity<U> quantity, U targetUnit, boolean targetUnitRequired){
        if (quantity == null) {
            throw new IllegalArgumentException("Operand cannot be null");
        }

        if (!Double.isFinite(this.value) || !Double.isFinite(quantity.value)) {
            throw new IllegalArgumentException("Values must be finite");
        }

        if (!this.unit.getClass().equals(quantity.unit.getClass())) {
            throw new IllegalArgumentException("Cross category operation not allowed");
        }

        if (targetUnitRequired) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            if (!this.unit.getClass().equals(targetUnit.getClass())) {
                throw new IllegalArgumentException("Invalid target unit category");
            }
        }
    }

    // Helper Builder
    private Quantity<U> buildQuantityFromBase(double baseValue, U targetUnit){
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        return new Quantity<>(round(convertedValue), targetUnit);
    }

    // Round the value to two decimal value
    private double round(double value){return (double) Math.round(value*100)/100;}

    // Override toString method
    @Override
    public String toString(){return String.format("%.2f %s", value, unit);}

}