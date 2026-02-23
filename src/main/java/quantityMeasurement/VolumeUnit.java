package quantityMeasurement;

public enum VolumeUnit implements IMeasurable{
    LITRE(1.0), // base unit
    MILLILITRE(0.001), // 1L = 1000ml
    GALLON(3.78541); // 1L = 3.78541 gallon

    private final double conversionValue;

    VolumeUnit(double conversionValue){
        this.conversionValue = conversionValue;
    }

    @Override
    public double getConversionValue() {return conversionValue;}

    @Override
    public double convertToBaseUnit(double value) {return value*conversionValue;}

    @Override
    public double convertFromBaseUnit(double baseValue) {return baseValue/conversionValue;}

    @Override
    public String getUnitName() {return this.name();}
}
