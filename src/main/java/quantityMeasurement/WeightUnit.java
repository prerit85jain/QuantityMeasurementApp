package quantityMeasurement;

public enum WeightUnit {
    // Conversion factor to the base unit (grams)
    MILLIGRAM(0.001),
    GRAM(1.0),
    KILOGRAM(1000.0),
    POUND(453.592),
    TONNE(1000000.0);

    private final double conversionFactor;

    WeightUnit(double conversionFactor){
        this.conversionFactor = conversionFactor;
    }

    // Get convertion factor to the base unit
    public double getConversionFactor(){return conversionFactor;}

    // Convert value from this unit to base unit (gram)
    public double convertToBaseUnit(double value){
        return value * this.conversionFactor;
    }

    // Convert value from base unit (gram) to this unit
    public double convertFromBaseUnit(double baseValue){
        return baseValue/conversionFactor;
    }
}
