package quantityMeasurement;

// Enum with conversion factor to base unit (inches)

public enum LengthUnit implements IMeasurable{
    FEET(12.0), // 1 feet = 12 inches
    INCHES(1.0), // 1 inch = 1 inch
    YARDS(36.0), // 1 yard = 36 inches
    CENTIMETERS(0.393701); // 1 cm = 0.393701 inch

    private final double conversionFactor;

    // Constructor
    LengthUnit(double conversionFactor){
        this.conversionFactor = conversionFactor;
    }

    // Getter method of conversionFactor
    @Override
    public double getConversionValue() {
        return conversionFactor;
    }

    // Convert value from this unit to base unit (inches)
    @Override
    public double convertToBaseUnit(double value){
        return value * conversionFactor;
    }

    // Convert value from base unit (inch) to this unit
    @Override
    public double convertFromBaseUnit(double baseValue){return baseValue/conversionFactor;}

    // Get unit name
    @Override
    public String getUnitName() {return this.name();}
}
