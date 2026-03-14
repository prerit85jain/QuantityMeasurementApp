package quantityMeasurement.exeption;

public class QuantityMeasurementException extends RuntimeException{
    public QuantityMeasurementException(String msg){
        super(msg);
    }

    public QuantityMeasurementException(String msg, Throwable cause){
        super(msg, cause);
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        try {
            throw new QuantityMeasurementException(
                    "This is a test exception for quantity measurement."
            );
        } catch (QuantityMeasurementException ex) {
            System.out.println("Caught QuantityMeasurementException: " +
                    ex.getMessage());
        }
    }
}
