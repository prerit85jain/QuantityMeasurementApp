package quantityMeasurement;

@FunctionalInterface
interface SupportsArithmetic{
    boolean isSupported();
}

public interface IMeasurable {
    // Default variable to indicate that all measurable units supports arithmetic operations by default
    SupportsArithmetic supportsArithmetic = () -> true;

    // Get the conversion value to the base unit
    double getConversionValue();

    // Convert the value of this unit to base unit
    double convertToBaseUnit(double value);

    // Convert the value of base unit to this unit
    double convertFromBaseUnit(double baseValue);

    // Get the unit name
    String getUnitName();

    // The following methods are optionals
    default boolean supportArithmetic(){return supportsArithmetic.isSupported();}

    // Validate operation support at runtime
    default void validOperationSupport(String operation){}

    // Arithmetic operation (not supported)
    default double add(double a, double b){throw new UnsupportedOperationException("Addition is not supported for unit: " + getUnitName());}
    default double subtract(double a, double b){throw new UnsupportedOperationException("Subtraction is not supported for unit: " + getUnitName());}
    default double divide(double a, double b){throw new UnsupportedOperationException("Division is not supported for unit: " + getUnitName());}


    public static void main(String[] args) {
        System.out.println("IMeasurable Interface");
    }
}
