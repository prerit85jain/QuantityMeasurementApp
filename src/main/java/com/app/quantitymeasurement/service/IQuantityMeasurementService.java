/**
 * IQuantityMeasurementService interface modifications for UC17: You will notice that
 * practically at broad level all the methods are same only some following changes are
 * made to accommodate the new requirements of UC17:
 * 1. Added new methods to retrieve operation history and count operations based on type.
 * 2. Added new method to retrieve error history of operations that resulted in errors.
 * 3. Updated method signatures to return lists of QuantityMeasurementDTO for history retrieval.
 * 4. Added JavaDoc comments to new methods for clarity and documentation.
 */
package com.app.quantitymeasurement.service;

import java.util.List;

import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;

public interface IQuantityMeasurementService {

    /**
     * Compare two quantities and return a QuantityMeasurementDTO with the result.
     */
    public QuantityMeasurementDTO compare(
        QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    /**
     * Convert a quantity to a target unit.
     */
    public QuantityMeasurementDTO convert(
        QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    /**
     * Add two quantities, result in same unit as first operand.
     */
    public QuantityMeasurementDTO add(
        QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    /**
     * Add two quantities with a specified target unit.
     */
    public QuantityMeasurementDTO add(
        QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
        QuantityDTO targetUnitDTO
    );

    /**
     * Subtract thatQuantity from thisQuantity, result in same unit as first operand.
     */
    public QuantityMeasurementDTO subtract(
        QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    /**
     * Subtract thatQuantity from thisQuantity with a specified target unit.
     */
    public QuantityMeasurementDTO subtract(
        QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
        QuantityDTO targetUnitDTO
    );

    /**
     * Divide thisQuantity by thatQuantity, returning result as QuantityMeasurementDTO.
     */
    public QuantityMeasurementDTO divide(
        QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO
    );

    /**
     * Retrieve the history of quantity measurement operations for a specific operation type.
     *
     * @param operation the type of operation for which to retrieve the history
     *        (e.g., "conversion", "comparison",
     * @return a list of {@code QuantityMeasurementDTO} representing the history of
     *         operations for the specified type
     */
    List<QuantityMeasurementDTO> getOperationHistory(String operation);

    /**
     * Retrieve the history of quantity measurement operations for a specific
     * measurement type.
     *
     * @param type the measurement type for which to retrieve the history (e.g.,
     *             "length", "weight", "volume", "temperature")
     * @return a list of {@code QuantityMeasurementDTO} representing the history
     *         of operations for the specified type
     */
    List<QuantityMeasurementDTO> getMeasurementsByType(String type);

    /**
     * Get the count of quantity measurement operations for a specific operation type.
     *
     * @param operation the type of operation for which to count operations
     * @return the number of operations of the specified type
     */
    long getOperationCount(String operation);

    /**
     * Retrieve the history of quantity measurement operations that resulted in errors.
     *
     * @return a list of {@code QuantityMeasurementDTO} representing the history of
     *         operations that resulted in errors
     */
    List<QuantityMeasurementDTO> getErrorHistory();

    // Main method for testing purposes
    public static void main(String[] args) {
        System.out.println("IQuantityMeasurementService Interface - UC17");
    }
}
