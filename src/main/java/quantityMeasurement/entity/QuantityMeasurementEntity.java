package quantityMeasurement.entity;

import quantityMeasurement.model.IMeasurable;

import java.io.Serializable;

public class QuantityMeasurementEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public double thisValue;
    public String thisUnit;
    public String thisMeasurementType;
    public double thatValue;
    public String thatUnit;
    public String thatMeasurementType;
    // e.g., "COMPARE", "CONVER", "ADD", "SUBTRACT", "DEVIDE"
    public String operation;
    public double resultValue;
    public String resultUnit;
    public String resultMeasurementType;
    // For comparison results like "Equal" or "Not Equal"
    public String resultString;
    // Flag to indicate if an error occurred during the operation
    public boolean isError;
    // For capturing any error messages during operations
    public String errorMessage;

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQuantity, QuantityModel<IMeasurable> thatQuantity,
            String operation, String result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultString = result;
    }

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation, QuantityModel<IMeasurable> result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultValue           = result.getValue();
        this.resultUnit            = result.getUnit().getUnitName();
        this.resultMeasurementType = result.getUnit().getMeasurementType();
    }

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation, double result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultValue = result;
    }

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation, String errorMessage, boolean isError) {
        this(thisQuantity, thatQuantity, operation);
        this.errorMessage = errorMessage;
        this.isError      = isError;
    }

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation) {
        if (thisQuantity != null && thisQuantity.getUnit() != null) {
            this.thisValue           = thisQuantity.getValue();
            this.thisUnit            = thisQuantity.getUnit().getUnitName();
            this.thisMeasurementType = thisQuantity.getUnit().getMeasurementType();
        }
        if (thatQuantity != null && thatQuantity.getUnit() != null) {
            this.thatValue           = thatQuantity.getValue();
            this.thatUnit            = thatQuantity.getUnit().getUnitName();
            this.thatMeasurementType = thatQuantity.getUnit().getMeasurementType();
        }
        this.operation = operation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuantityMeasurementEntity other = (QuantityMeasurementEntity) obj;
        return Double.compare(thisValue, other.thisValue) == 0
                && Double.compare(thatValue, other.thatValue) == 0
                && java.util.Objects.equals(thisUnit,            other.thisUnit)
                && java.util.Objects.equals(thatUnit,            other.thatUnit)
                && java.util.Objects.equals(thisMeasurementType, other.thisMeasurementType)
                && java.util.Objects.equals(thatMeasurementType, other.thatMeasurementType)
                && java.util.Objects.equals(operation,           other.operation)
                && java.util.Objects.equals(resultString,        other.resultString)
                && Double.compare(resultValue, other.resultValue) == 0
                && isError == other.isError
                && java.util.Objects.equals(errorMessage, other.errorMessage);
    }

    @Override
    public String toString() {
        if (isError) {
            return String.format("[%s] %.1f %s + %.1f %s | ERROR: %s",
                    operation, thisValue, thisUnit, thatValue, thatUnit, errorMessage);
        }
        if (resultString != null) {
            return String.format("[%s] %.1f %s == %.1f %s | Result: %s",
                    operation, thisValue, thisUnit, thatValue, thatUnit, resultString);
        }
        return String.format("[%s] %.1f %s + %.1f %s | Result: %.2f %s",
                operation, thisValue, thisUnit, thatValue, thatUnit, resultValue, resultUnit);
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        System.out.println("QuantityMeasurementEntity test");
    }
}
