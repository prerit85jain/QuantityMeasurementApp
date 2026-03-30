package com.app.quantitymeasurement.exception;

public class DatabaseException extends QuantityMeasurementException {
    public DatabaseException(String message)                  { super(message); }
    public DatabaseException(String message, Throwable cause) { super(message, cause); }
    public static DatabaseException connectionFailed(String d, Throwable c)  { return new DatabaseException("Database connection failed: " + d, c); }
    public static DatabaseException queryFailed(String q, Throwable c)       { return new DatabaseException("Query execution failed: " + q, c); }
    public static DatabaseException transactionFailed(String op, Throwable c){ return new DatabaseException("Transaction failed during " + op, c); }
}
