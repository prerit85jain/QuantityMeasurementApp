/**
 * UC 17 Enhancements:
 * 1. Added @Data annotation from Lombok to QuantityDTO to automatically generate
 *    getters, setters, and other utility methods, reducing boilerplate code and
 *    improving maintainability.
 * 2. Added validation annotations (@NotEmpty, @NotNull) to the fields in QuantityDTO
 *    to ensure that the input data is valid and to provide better error handling.
 * 3. Added a default constructor to QuantityDTO for flexibility in instantiation, allowing
 *    for frameworks that require a no-argument constructor to create instances of the class.
 * 4. Added @AssertTrue validation method to ensure that the unit provided is valid for
 *    the specified measurement type, enhancing the robustness of the input validation.
 */
package com.app.quantitymeasurement.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.logging.Logger;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * IMeasurableUnit - Interface to represent measurable units for quantity
 * measurements. This interface defines the contract for measurable units,
 * including methods to get the unit name and the measurement type. It is
 * implemented by the various unit enums defined within the QuantityDTO class,
 * such as LengthUnit, VolumeUnit, WeightUnit, and TemperatureUnit.
 *
 * The purpose of redefining the IMeasurableUnit interface and the MeasurableUnit
 * enums within this DTO class is to provide a self-contained representation of
 * the quantity input. This design allows the QuantityDTO to be easily instantiated
 * with specific units and values without requiring external dependencies on the
 * IMeasurableUnit interface or the unit enums defined elsewhere in the application.
 * It also simplifies the process of creating QuantityDTO instances for testing and
 * demonstration purposes, as all necessary unit definitions are included within
 * the class itself.
 */
interface IMeasurableUnit {
    String getUnitName();
    String getMeasurementType();
}

@Data
@Schema(description = "A quantity with a value and unit")
public class QuantityDTO {
    // Logger for debugging purposes
    private static final Logger logger = Logger.getLogger(
        QuantityDTO.class.getName()
    );

    // Enums for different measurable units, implementing the IMeasurableUnit
    // interface. Please note the names of the enums like LengthUnit and their
    // Objects are defined such a way that they can be easily mapped to the
    // existing LengthUnit, WeightUnit, VolumeUnit, and TemperatureUnit enums
    // defined in the application for conversion, comparison and Arithmetic
    // operations in the service layer.
    public enum LengthUnit implements IMeasurableUnit {
        FEET, INCHES, YARDS, CENTIMETERS;
        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    public enum VolumeUnit implements IMeasurableUnit {
        LITRE, MILLILITRE, GALLON;
        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    public enum WeightUnit implements IMeasurableUnit {
        MILLIGRAM, GRAM, KILOGRAM, POUND, TONNE;
        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;
        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

    @NotNull(message = "Value cannot be empty")
    @Schema(example = "1.0")
    public double value;

    @NotNull(message = "Unit cannot be null")
    @Schema(example = "FEET", allowableValues = {
        "FEET", "INCHES", "YARDS", "CENTIMETERS",
        "LITRE", "MILLILITRE", "GALLON",
        "MILLIGRAM", "GRAM", "KILOGRAM", "POUND", "TONNE",
        "CELSIUS", "FAHRENHEIT"
    })
    public String unit;

    @NotNull(message = "Measurement type cannot be null")
    @Pattern(regexp = "LengthUnit|VolumeUnit|WeightUnit|TemperatureUnit",
             message = "Measurement type must be one of: LengthUnit, VolumeUnit, " +
                       "WeightUnit, TemperatureUnit")
    @Schema(example = "LengthUnit", allowableValues = {
        "LengthUnit", "VolumeUnit", "WeightUnit", "TemperatureUnit"
    })
    public String measurementType;

    /** Default no-arg constructor (required by Lombok @Data + frameworks) */
    public QuantityDTO() {}

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

    @AssertTrue(message = "Unit must be valid for the specified measurement type")
    public boolean isValidUnit() {
        logger.info("Validating unit: " + unit + " for measurement type: " + measurementType);
        try {
            switch (measurementType) {
                case "LengthUnit":      LengthUnit.valueOf(unit);      break;
                case "VolumeUnit":      VolumeUnit.valueOf(unit);      break;
                case "WeightUnit":      WeightUnit.valueOf(unit);      break;
                case "TemperatureUnit": TemperatureUnit.valueOf(unit); break;
                default: return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        QuantityDTO dto = new QuantityDTO(1.0, LengthUnit.FEET);
        System.out.println(dto.value + " " + dto.unit + " type=" + dto.measurementType);
    }
}
