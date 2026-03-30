package com.app.quantitymeasurement.unit;

public enum LengthUnit implements IMeasurable {
    FEET(12.0), INCHES(1.0), YARDS(36.0), CENTIMETERS(0.393701);

    private final double conversionFactor;
    LengthUnit(double f) { this.conversionFactor = f; }

    @Override public double getConversionValue()             { return conversionFactor; }
    @Override public double convertToBaseUnit(double v)      { return v * conversionFactor; }
    @Override public double convertFromBaseUnit(double b)    { return b / conversionFactor; }
    @Override public String getUnitName()                    { return this.name(); }
    @Override public String getMeasurementType()             { return this.getClass().getSimpleName(); }

    @Override
    public IMeasurable getUnitInstance(String unitName) {
        for (LengthUnit u : LengthUnit.values())
            if (u.name().equalsIgnoreCase(unitName)) return u;
        throw new IllegalArgumentException("Invalid length unit: " + unitName);
    }
}
