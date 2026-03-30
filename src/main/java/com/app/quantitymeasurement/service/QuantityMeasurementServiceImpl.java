/**
 * UC 17 Enhancements - Refactor Service Layer to Use Spring and Repository Pattern.
 * Here are the key enhancements made in the service layer for UC17:
 * 1. Refactored the service layer to use Spring's @Service annotation and leverage
 *    Spring Data JPA repository for data access. This involves injecting the repository
 *    into the service class and using it to perform database operations instead of
 *    manual JDBC code.
 * 2. The service methods are also annotated with @Transactional where necessary to
 *    manage transactions declaratively.
 * 3. The service layer as usual handles any business logic related to quantity measurements,
 *    such as comparisons, conversions, and arithmetic operations, while delegating data
 *    persistence to the repository.
 * 4. Used Spring's dependency injection to provide the repository instance to the service,
 *    allowing for easy swapping of repository implementations (e.g., cache or database)
 *    without modifying the service logic.
 * 5. Note there is no need of Constructor as we are using field injection with @Autowired
 *    for the repository, which is a common practice in Spring applications. However, constructor
 *    injection can also be used if preferred for better testability and immutability.
 * 6. Added new API to retrieve the history of quantity measurement operations for a specific
 *    measurement type and to get the count of operations for a specific operation type.
 * 7. The service implementation now interacts with the repository to save the results of
 *    operations for future extensions such as audit or history purposes, and to retrieve
 *    historical data based on operation type and measurement type.
 * 8. The service implementation also includes error handling to save error information to
 *    the repository when exceptions occur during operations, allowing for better tracking
 *    and management of errors in the quantity measurement operations.
 */
package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.model.OperationType;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.unit.IMeasurable;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.unit.WeightUnit;
import com.app.quantitymeasurement.unit.VolumeUnit;
import com.app.quantitymeasurement.unit.TemperatureUnit;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.quantity.Quantity;

import java.util.List;
import java.util.logging.Logger;
import java.util.function.DoubleBinaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    // Logger for logging information and errors
    private static final Logger logger = Logger.getLogger(
        QuantityMeasurementServiceImpl.class.getName()
    );

    // Quantity Measurement Repository for storing and retrieving quantity data
    @Autowired
    private QuantityMeasurementRepository repository;

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO thisQ, QuantityDTO thatQ) {
        QuantityModel<IMeasurable> m1 = convertDtoToModel(thisQ);
        QuantityModel<IMeasurable> m2 = convertDtoToModel(thatQ);
        try {
            boolean result = compare(m1, m2);
            String  res    = String.valueOf(result);
            QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity(m1, m2, "compare", res);
            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);
        } catch (Exception e) {
            logger.severe("compare error: " + e.getMessage());
            QuantityMeasurementEntity errEntity =
                new QuantityMeasurementEntity(m1, m2, "compare", e.getMessage(), true);
            repository.save(errEntity);
            throw new QuantityMeasurementException("compare Error: " + e.getMessage(), e);
        }
    }

    private <U extends IMeasurable> boolean compare(
            QuantityModel<U> m1, QuantityModel<U> m2) {
        return new Quantity<>(m1.getValue(), m1.getUnit())
            .equals(new Quantity<>(m2.getValue(), m2.getUnit()));
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO thisQ, QuantityDTO thatQ) {
        QuantityModel<IMeasurable> m1 = convertDtoToModel(thisQ);
        QuantityModel<IMeasurable> m2 = convertDtoToModel(thatQ);
        try {
            double val = convertTo(m1, m2.getUnit());
            QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity(m1, m2, "convert",
                    new QuantityModel<>(val, m2.getUnit()));
            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);
        } catch (Exception e) {
            logger.severe("convert error: " + e.getMessage());
            QuantityMeasurementEntity errEntity =
                new QuantityMeasurementEntity(m1, m2, "convert", e.getMessage(), true);
            repository.save(errEntity);
            throw new QuantityMeasurementException("convert Error: " + e.getMessage(), e);
        }
    }

    private <U extends IMeasurable> double convertTo(QuantityModel<U> model, U targetUnit) {
        if (model.getUnit() instanceof TemperatureUnit) {
            return convertTemperatureUnit(model, targetUnit);
        }
        return new Quantity<>(model.getValue(), model.getUnit()).convertTo(targetUnit).getValue();
    }

    private <U extends IMeasurable> double convertTemperatureUnit(
            QuantityModel<U> model, U targetUnit) {
        double base = model.getUnit().convertToBaseUnit(model.getValue());
        return targetUnit.convertFromBaseUnit(base);
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ) {
        return this.add(thisQ, thatQ, thisQ);
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ) {
        QuantityModel<IMeasurable> m1 = convertDtoToModel(thisQ);
        QuantityModel<IMeasurable> m2 = convertDtoToModel(thatQ);
        QuantityModel<IMeasurable> mt = convertDtoToModel(targetQ);
        try {
            validateArithmeticOperands(m1, m2, mt, true);
            double base = performArithmetic(m1, m2, mt, (a, b) -> a + b);
            double val  = mt.getUnit().convertFromBaseUnit(base);
            QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity(m1, m2, "add",
                    new QuantityModel<>(val, mt.getUnit()));
            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);
        } catch (Exception e) {
            logger.severe("add error: " + e.getMessage());
            QuantityMeasurementEntity errEntity =
                new QuantityMeasurementEntity(m1, m2, "add", e.getMessage(), true);
            repository.save(errEntity);
            throw new QuantityMeasurementException("add Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ) {
        return this.subtract(thisQ, thatQ, thisQ);
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ,
                                            QuantityDTO targetQ) {
        QuantityModel<IMeasurable> m1 = convertDtoToModel(thisQ);
        QuantityModel<IMeasurable> m2 = convertDtoToModel(thatQ);
        QuantityModel<IMeasurable> mt = convertDtoToModel(targetQ);
        try {
            validateArithmeticOperands(m1, m2, mt, true);
            double base = performArithmetic(m1, m2, mt, (a, b) -> a - b);
            double val  = mt.getUnit().convertFromBaseUnit(base);
            QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity(m1, m2, "subtract",
                    new QuantityModel<>(val, mt.getUnit()));
            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);
        } catch (Exception e) {
            logger.severe("subtract error: " + e.getMessage());
            QuantityMeasurementEntity errEntity =
                new QuantityMeasurementEntity(m1, m2, "subtract", e.getMessage(), true);
            repository.save(errEntity);
            throw new QuantityMeasurementException("subtract Error: " + e.getMessage(), e);
        }
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO thisQ, QuantityDTO thatQ) {
        QuantityModel<IMeasurable> m1 = convertDtoToModel(thisQ);
        QuantityModel<IMeasurable> m2 = convertDtoToModel(thatQ);
        try {
            validateArithmeticOperands(m1, m2, null, false);
            double base1 = m1.getUnit().convertToBaseUnit(m1.getValue());
            double base2 = m2.getUnit().convertToBaseUnit(m2.getValue());
            if (base2 == 0) throw new ArithmeticException("Divide by zero");
            double result = base1 / base2;
            QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity(m1, m2, "divide", result);
            repository.save(entity);
            return QuantityMeasurementDTO.from(entity);
        } catch (Exception e) {
            logger.severe("divide error: " + e.getMessage());
            QuantityMeasurementEntity errEntity =
                new QuantityMeasurementEntity(m1, m2, "divide", e.getMessage(), true);
            repository.save(errEntity);
            throw new QuantityMeasurementException(e.getMessage(), e);
        }
    }

    /**
     * Retrieve the history of quantity measurement operations for a specific operation type.
     *
     * The operation history retrieval process involves the following steps:
     * 1. The method takes an operation type as a parameter (e.g., "conversion",
     *    "comparison", "addition", "subtraction", "division") and queries the
     *    repository for all entries that match the operation type.
     * 2. The retrieved entities are converted to DTOs and returned as a list.
     *
     * @param operation the type of operation for which to retrieve the history
     * @return a list of {@code QuantityMeasurementDTO} representing the history of
     *         operations for the specified type
     */
    @Override
    public List<QuantityMeasurementDTO> getOperationHistory(String operation) {
        return QuantityMeasurementDTO.fromList(
            repository.findByOperation(operation.toLowerCase()));
    }

    /**
     * Retrieve the history of quantity measurement operations for a specific measurement type.
     *
     * @param type the measurement type for which to retrieve the history
     * @return a list of {@code QuantityMeasurementDTO} representing the history
     *         of operations for the specified type
     */
    @Override
    public List<QuantityMeasurementDTO> getMeasurementsByType(String type) {
        return QuantityMeasurementDTO.fromList(
            repository.findByThisMeasurementType(type));
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation.toLowerCase());
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromList(repository.findByIsErrorTrue());
    }

    /**
     * Helper: Convert QuantityDTO to QuantityModel<IMeasurable>.
     * Renamed from getQuantityModel() to convertDtoToModel() for clarity.
     */
    private QuantityModel<IMeasurable> convertDtoToModel(QuantityDTO q) {
        if (q == null) throw new QuantityMeasurementException("Quantity cannot be null");
        IMeasurable unit;
        switch (q.getMeasurementType()) {
            case "LengthUnit":      unit = LengthUnit.FEET.getUnitInstance(q.getUnit());     break;
            case "WeightUnit":      unit = WeightUnit.GRAM.getUnitInstance(q.getUnit());     break;
            case "VolumeUnit":      unit = VolumeUnit.LITRE.getUnitInstance(q.getUnit());    break;
            case "TemperatureUnit": unit = TemperatureUnit.CELSIUS.getUnitInstance(q.getUnit()); break;
            default: throw new QuantityMeasurementException(
                "Unsupported measurement type: " + q.getMeasurementType());
        }
        return new QuantityModel<>(q.getValue(), unit);
    }

    private <U extends IMeasurable> void validateArithmeticOperands(
            QuantityModel<U> a, QuantityModel<U> b,
            QuantityModel<U> target, boolean needTarget) {
        if (a == null || b == null)
            throw new QuantityMeasurementException("Operands cannot be null");
        if (!a.getUnit().getClass().equals(b.getUnit().getClass()))
            throw new QuantityMeasurementException(
                "Cannot perform arithmetic between different measurement categories: " +
                a.getUnit().getMeasurementType() + " and " + b.getUnit().getMeasurementType());
        if (needTarget) {
            if (target == null)
                throw new QuantityMeasurementException("Target unit cannot be null");
            if (!a.getUnit().getClass().equals(target.getUnit().getClass()))
                throw new QuantityMeasurementException("Invalid target unit category");
        }
        a.getUnit().validOperationSupport("arithmetic");
    }

    private enum ArithmeticOperation { ADD, SUBTRACT, DIVIDE }

    private <U extends IMeasurable> double performArithmetic(
            QuantityModel<U> m1, QuantityModel<U> m2,
            QuantityModel<U> target, DoubleBinaryOperator fn) {
        double base1 = m1.getUnit().convertToBaseUnit(m1.getValue());
        double base2 = m2.getUnit().convertToBaseUnit(m2.getValue());
        return fn.applyAsDouble(base1, base2);
    }
}
