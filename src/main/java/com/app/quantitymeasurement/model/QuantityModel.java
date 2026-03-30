package com.app.quantitymeasurement.model;

import com.app.quantitymeasurement.unit.IMeasurable;

public class QuantityModel<U extends IMeasurable> {
    public double value;
    public U unit;

    public QuantityModel(double value, U unit) { this.value = value; this.unit = unit; }
    public double getValue() { return value; }
    public U getUnit()       { return unit; }
    @Override public String toString() { return String.format("%.1f %s", value, unit.getUnitName()); }
}
