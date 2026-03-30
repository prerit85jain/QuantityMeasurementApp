/**
 * UC 16 Enhancement:
 * Refactoring QuantityMeasurementEntity - Refactor the QuantityMeasurementEntity class
 * to improve its design and maintainability. This includes adding JPA annotations for
 * database mapping, implementing constructors for different use cases, and ensuring that
 * the class is properly annotated with Lombok to reduce boilerplate code. The refactored
 * class should be designed to be easily persisted in a database, with appropriate
 * indexing for efficient querying of quantity measurement operations and results.
 */
package com.app.quantitymeasurement.model;

import com.app.quantitymeasurement.unit.IMeasurable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity // Marks this class as a JPA entity
@Table(name = "quantity_measurement_entity", indexes = {
    @Index(name = "idx_operation",        columnList = "operation"),
    @Index(name = "idx_measurement_type", columnList = "this_measurement_type"),
    @Index(name = "idx_created_at",       columnList = "created_at")
})
@Data                // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor   // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor  // Lombok annotation to generate an all-arguments constructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "this_value", nullable = false)
    public double thisValue;
    @Column(name = "this_unit", nullable = false)
    public String thisUnit;
    @Column(name = "this_measurement_type", nullable = false)
    public String thisMeasurementType;

    @Column(name = "that_value", nullable = false)
    public double thatValue;
    @Column(name = "that_unit", nullable = false)
    public String thatUnit;
    @Column(name = "that_measurement_type", nullable = false)
    public String thatMeasurementType;

    // e.g., "COMPARE", "CONVERT", "ADD", "SUBTRACT", "DIVIDE"
    @Column(name = "operation", nullable = false)
    public String operation;

    @Column(name = "result_value")
    public double resultValue;
    @Column(name = "result_unit")
    public String resultUnit;
    @Column(name = "result_measurement_type")
    public String resultMeasurementType;

    // For comparison results like "Equal" or "Not Equal"
    @Column(name = "result_string")
    public String resultString;

    // Flag to indicate if an error occurred during the operation
    @Column(name = "is_error")
    public boolean isError;

    // For capturing any error messages during operations
    @Column(name = "error_message")
    public String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor to initialize the QuantityMeasurementEntity with the given
     * quantities, operation, and result for a single operand operation
     * (e.g., comparison and conversion).
     */
    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation, String result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultString = result;
    }

    /**
     * Constructor to initialize the QuantityMeasurementEntity with the given
     * quantities, operation, and result for a double operand operation
     * (e.g., addition, subtraction, division).
     */
    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation, QuantityModel<IMeasurable> result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultValue           = result.getValue();
        this.resultUnit            = result.getUnit().getUnitName();
        this.resultMeasurementType = result.getUnit().getMeasurementType();
    }

    /**
     * Constructor to initialize the QuantityMeasurementEntity with the given
     * quantities, operation, and result.
     */
    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation, double result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultValue = result;
    }

    /**
     * Constructor to initialize the QuantityMeasurementEntity with the given
     * quantities, operation, and error information.
     */
    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation, String errorMessage, boolean isError) {
        this(thisQuantity, thatQuantity, operation);
        this.errorMessage = errorMessage;
        this.isError      = isError;
    }

    /**
     * Base constructor — populates operand fields.
     */
    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
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

    // Main method for testing purposes
    public static void main(String[] args) {
        System.out.println("QuantityMeasurementEntity - UC17 JPA Entity");
    }
}
