package quantityMeasurement;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable{


    CELSIUS(false),
    FAHRENHEIT(true),
    KELVIN(false);


    // Fahrenheit to Celsius conversion
    final Function<Double, Double> FAHRENHEIT_TO_CELSIUS = (fahrenheit) -> (fahrenheit - 32)*5/9;

    // Celsius to Celsius conversion
    final Function<Double, Double> CELSIUS_TO_CELSIUS = (celsius) -> celsius;

    // Kelvin to Celsius
    final Function<Double, Double> KELVIN_TO_CELSIUS = k -> k - 273.15;

    // Conversion function to base unit (Celsius)
    Function<Double, Double> conversionValue;

    // Lambda function: temperature does not support arithmetic
    SupportsArithmetic supportsArithmetic = ()->false;

    // Constructor
    TemperatureUnit(boolean isFahrenheit){
        if (this.name().equals("FAHRENHEIT")) {
            this.conversionValue = FAHRENHEIT_TO_CELSIUS;
        } else if (this.name().equals("KELVIN")) {
            this.conversionValue = KELVIN_TO_CELSIUS;
        } else {
            this.conversionValue = CELSIUS_TO_CELSIUS;
        }
    }


    @Override
    public double getConversionValue() {
        return 1;
    }

    @Override
    public double convertToBaseUnit(double value) {
        return conversionValue.apply(value);
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        switch (this) {
            case FAHRENHEIT:
                return (baseValue * 9 / 5) + 32;
            case KELVIN:
                return baseValue + 273.15;
            default:
                return baseValue; // CELSIUS
        }
    }

    @Override
    public String getUnitName() {
        return null;
    }

    // Convert between temperature units
    public double convertTo(double value, TemperatureUnit targetUnit) {
        double baseValue = this.convertToBaseUnit(value);
        return targetUnit.convertFromBaseUnit(baseValue);
    }

    // Arithmetic support flag
    public boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    // Override validation
    @Override
    public void validOperationSupport(String operation) {
        if (!supportsArithmetic.isSupported()) {
            String message = this.name() + " does not support " + operation + " operations.";
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static void main(String[] args) {
        System.out.println("TemperatureUnit Enum");

        for (TemperatureUnit unit : TemperatureUnit.values()) {
            System.out.println(unit + " supports arithmetic: " + unit.supportsArithmetic());
        }

        System.out.println("0C to F = " +
                TemperatureUnit.CELSIUS.convertTo(0, TemperatureUnit.FAHRENHEIT));

        System.out.println("32F to C = " +
                TemperatureUnit.FAHRENHEIT.convertTo(32, TemperatureUnit.CELSIUS));
    }
}
