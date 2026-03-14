package quantityMeasurement.entity;


interface IMeasurableUnit{
    String getUnitName();
    String getMeasurementType();
}
public class QuantityDTO {

    public enum LengthUnit implements IMeasurableUnit{
        FEET, INCHES, YARDS, CENTIMETERS;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }
    }

    public enum VolumeUnit implements IMeasurableUnit{
        LITRE, MILLILITRE, GALLON;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }
    }

    public enum WeightUnit implements IMeasurableUnit{
        MILLIGRAM, GRAM, KILOGRAM, POUND, TONNE;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;

        @Override
        public String getUnitName() {
            return this.name();
        }

        @Override
        public String getMeasurementType() {
            return this.getClass().getSimpleName();
        }
    }

    public double value;
    public String unit;
    public String measurementType;

    public QuantityDTO(double value, IMeasurableUnit unit) {
        this.value           = value;
        this.unit            = unit.getUnitName();
        this.measurementType = unit.getMeasurementType();
    }

    public QuantityDTO(double value, String unit, String measurementType) {
        this.value           = value;
        this.unit            = unit;
        this.measurementType = measurementType;
    }

    public double getValue() {return value;}

    public String getUnit() {return unit;}

    public String getMeasurementType() {return measurementType;}

    @Override
    public String toString() {return String.format("%.2f %s", value, unit);}

    // Main method for testing purposes
    public static void main(String[] args) {
        QuantityDTO dto1 = new QuantityDTO(2.0,  LengthUnit.FEET);
        QuantityDTO dto2 = new QuantityDTO(24.0, LengthUnit.INCHES);
        System.out.println("dto1: " + dto1);
        System.out.println("dto2: " + dto2);
        System.out.println("measurementType: " + dto1.getMeasurementType());
    }
}
