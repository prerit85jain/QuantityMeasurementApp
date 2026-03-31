package com.app.quantitymeasurement.quantity;

import com.app.quantitymeasurement.unit.IMeasurable;

/**
 * Generic class representing a measurable quantity with a value and unit.
 * Supports conversion, equality comparison, and arithmetic operations.
 *
 * @param <U> Any unit type that implements IMeasurable
 */
public class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        if (unit == null)            throw new IllegalArgumentException("Unit cannot be null");
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() { return value; }
    public U getUnit()       { return unit; }

    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!targetUnit.getClass().equals(unit.getClass()))
            throw new IllegalArgumentException("Target unit should belong to same class");
        double base = unit.convertToBaseUnit(value);
        return new Quantity<>(round(targetUnit.convertFromBaseUnit(base)), targetUnit);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Quantity<?> other = (Quantity<?>) o;
        if (!this.unit.getClass().equals(other.unit.getClass())) return false;
        return Double.compare(
            unit.convertToBaseUnit(value),
            other.unit.convertToBaseUnit(other.value)) == 0;
    }

    private enum ArithOp {
        ADD      { @Override public double compute(double a, double b) { return a + b; } },
        SUBTRACT { @Override public double compute(double a, double b) { return a - b; } },
        DIVIDE   {
            @Override public double compute(double a, double b) {
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                return a / b;
            }
        };
        public abstract double compute(double a, double b);
    }

    public Quantity<U> add(Quantity<U> other)              { return add(other, unit); }
    public Quantity<U> add(Quantity<U> other, U target)    { return build(arith(other, target, ArithOp.ADD, true), target); }
    public Quantity<U> subtract(Quantity<U> other)         { return subtract(other, unit); }
    public Quantity<U> subtract(Quantity<U> other, U target){ return build(arith(other, target, ArithOp.SUBTRACT, true), target); }
    public double divide(Quantity<U> other)                 { return arith(other, null, ArithOp.DIVIDE, false); }

    private double arith(Quantity<U> other, U target, ArithOp op, boolean needTarget) {
        validate(other, target, needTarget);
        return op.compute(unit.convertToBaseUnit(value), other.unit.convertToBaseUnit(other.value));
    }

    private void validate(Quantity<U> q, U target, boolean needTarget) {
        if (q == null) throw new IllegalArgumentException("Operand cannot be null");
        if (!Double.isFinite(this.value) || !Double.isFinite(q.value))
            throw new IllegalArgumentException("Values must be finite");
        if (!this.unit.getClass().equals(q.unit.getClass()))
            throw new IllegalArgumentException("Cross category operation not allowed");
        if (needTarget) {
            if (target == null) throw new IllegalArgumentException("Target unit cannot be null");
            if (!this.unit.getClass().equals(target.getClass()))
                throw new IllegalArgumentException("Invalid target unit category");
        }
    }

    private Quantity<U> build(double base, U target) {
        return new Quantity<>(round(target.convertFromBaseUnit(base)), target);
    }

    private double round(double v) { return (double) Math.round(v * 100) / 100; }

    @Override
    public String toString() { return String.format("%.2f %s", value, unit.getUnitName()); }
}
