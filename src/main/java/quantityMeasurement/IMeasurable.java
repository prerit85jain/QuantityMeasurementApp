package quantityMeasurement;

public interface IMeasurable {
    // Get the conversion value to the base unit
    double getConversionValue();

    // Convert the value of this unit to base unit
    double convertToBaseUnit(double value);

    // Convert the value of base unit to this unit
    double convertFromBaseUnit(double baseValue);

    // Get the unit name
    String getUnitName();

    public static void main(String[] args) {
        System.out.println("IMeasurable Interface");
    }
}
