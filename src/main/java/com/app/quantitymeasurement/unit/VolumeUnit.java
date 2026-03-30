package com.app.quantitymeasurement.unit;

public enum VolumeUnit implements IMeasurable {
    LITRE(1.0), MILLILITRE(0.001), GALLON(3.78541);

    private final double conversionValue;
    VolumeUnit(double v) { this.conversionValue = v; }

    @Override public double getConversionValue()             { return conversionValue; }
    @Override public double convertToBaseUnit(double v)      { return v * conversionValue; }
    @Override public double convertFromBaseUnit(double b)    { return b / conversionValue; }
    @Override public String getUnitName()                    { return this.name(); }
    @Override public String getMeasurementType()             { return this.getClass().getSimpleName(); }

    @Override
    public IMeasurable getUnitInstance(String unitName) {
        for (VolumeUnit u : VolumeUnit.values())
            if (u.name().equalsIgnoreCase(unitName)) return u;
        throw new IllegalArgumentException("Invalid volume unit: " + unitName);
    }
}
