package quantityMeasurement;

public enum WeightUnit implements IMeasurable {
    // Conversion factor to the base unit (grams)
    MILLIGRAM(0.001),
    GRAM(1.0),
    KILOGRAM(1000.0),
    POUND(453.592),
    TONNE(1000000.0);

    private final double conversionValue;

    WeightUnit(double conversionValue){
        this.conversionValue = conversionValue;
    }

    // Get convertion factor to the base unit
    @Override
    public double getConversionValue(){return conversionValue;}

    // Convert value from this unit to base unit (gram)
    @Override
    public double convertToBaseUnit(double value){return value * this.conversionValue;}

    // Convert value from base unit (gram) to this unit
    @Override
    public double convertFromBaseUnit(double baseValue){return baseValue/conversionValue;}

    // Get Unit name
    @Override
    public String getUnitName(){return this.name();}
}
