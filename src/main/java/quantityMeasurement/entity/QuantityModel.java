package quantityMeasurement.entity;

import quantityMeasurement.model.IMeasurable;
import quantityMeasurement.model.LengthUnit;

public class QuantityModel<U extends IMeasurable> {
    public double value;
    public U unit;

    public QuantityModel(double value, U unit) {
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() {return value;}

    public U getUnit() {return unit;}

    @Override
    public String toString() {return String.format("%.1f %s", value, unit.getUnitName());}

    // Main method for testing purposes
    public static void main(String[] args) {
        QuantityModel<LengthUnit> model = new QuantityModel<>(2.0, LengthUnit.FEET);
        System.out.println("model: " + model);
        System.out.println("value: " + model.getValue());
        System.out.println("unit:  " + model.getUnit());
    }
}
