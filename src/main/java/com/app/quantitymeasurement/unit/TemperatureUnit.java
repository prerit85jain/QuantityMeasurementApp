package com.app.quantitymeasurement.unit;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {
    CELSIUS, FAHRENHEIT, KELVIN;

    private static final Function<Double,Double> F_TO_C = f -> (f - 32) * 5.0 / 9.0;
    private final SupportsArithmetic arithmeticSupport = () -> false;

    @Override public double getConversionValue() { return 1.0; }

    @Override
    public double convertToBaseUnit(double value) {
        switch (this) {
            case FAHRENHEIT: return F_TO_C.apply(value);
            case KELVIN:     return value - 273.15;
            default:         return value;
        }
    }

    @Override
    public double convertFromBaseUnit(double base) {
        switch (this) {
            case FAHRENHEIT: return (base * 9.0 / 5.0) + 32;
            case KELVIN:     return base + 273.15;
            default:         return base;
        }
    }

    @Override public String getUnitName()        { return this.name(); }
    @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }

    @Override
    public IMeasurable getUnitInstance(String unitName) {
        for (TemperatureUnit u : TemperatureUnit.values())
            if (u.name().equalsIgnoreCase(unitName)) return u;
        throw new IllegalArgumentException("Invalid temperature unit: " + unitName);
    }

    @Override public boolean supportArithmetic() { return arithmeticSupport.isSupported(); }

    @Override
    public void validOperationSupport(String operation) {
        if (!arithmeticSupport.isSupported())
            throw new UnsupportedOperationException(
                this.name() + " does not support " + operation + " operations.");
    }
}
