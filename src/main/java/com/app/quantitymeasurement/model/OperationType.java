package com.app.quantitymeasurement.model;

/**
 * OperationType enum - defines all operation types supported by the
 * Quantity Measurement application. Provides type-safe operation constants
 * used across QuantityDTO, QuantityMeasurementDTO, and service layer.
 *
 * @author Developer
 * @version 17.0
 */
public enum OperationType {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    COMPARE,
    CONVERT;

    /** Case-insensitive lookup; returns null for unknown values. */
    public static OperationType fromString(String value) {
        if (value == null) return null;
        try { return OperationType.valueOf(value.toUpperCase()); }
        catch (IllegalArgumentException e) { return null; }
    }
}
