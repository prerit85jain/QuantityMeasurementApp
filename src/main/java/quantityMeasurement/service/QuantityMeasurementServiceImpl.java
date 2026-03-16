package quantityMeasurement.service;

import quantityMeasurement.core.Quantity;
import quantityMeasurement.entity.QuantityDTO;
import quantityMeasurement.entity.QuantityMeasurementEntity;
import quantityMeasurement.entity.QuantityModel;
import quantityMeasurement.exeption.QuantityMeasurementException;
import quantityMeasurement.model.*;
import quantityMeasurement.repository.IQuantityMeasurementRepository;
import quantityMeasurement.repository.QuantityMeasurementCacheRepository;

import java.util.function.DoubleBinaryOperator;
import java.util.logging.Logger;

public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = Logger.getLogger(
            QuantityMeasurementServiceImpl.class.getName());

    private IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = repository;
        logger.info("QuantityMeasurementServiceImpl initialized with: " +
                repository.getClass().getSimpleName());
    }

    @Override
    public boolean compare(QuantityDTO thisQ, QuantityDTO thatQ) {
        QuantityModel<IMeasurable> m1 = getQuantityModel(thisQ);
        QuantityModel<IMeasurable> m2 = getQuantityModel(thatQ);
        boolean result = new Quantity<>(m1.getValue(), m1.getUnit())
                .equals(new Quantity<>(m2.getValue(), m2.getUnit()));
        String res = result ? "Equal" : "Not Equal";
        repository.save(new QuantityMeasurementEntity(m1, m2, "COMPARE", res));
        return result;
    }

    @Override
    public QuantityDTO convert(QuantityDTO thisQ, QuantityDTO thatQ) {
        QuantityModel<IMeasurable> m1 = getQuantityModel(thisQ);
        QuantityModel<IMeasurable> m2 = getQuantityModel(thatQ);
        double val = (m1.getUnit() instanceof TemperatureUnit)
                ? m2.getUnit().convertFromBaseUnit(m1.getUnit().convertToBaseUnit(m1.getValue()))
                : new Quantity<>(m1.getValue(), m1.getUnit()).convertTo(m2.getUnit()).getValue();
        repository.save(new QuantityMeasurementEntity(m1, m2, "CONVERT",
                new QuantityModel<>(val, m2.getUnit())));
        return new QuantityDTO(val, thatQ.getUnit(), thatQ.getMeasurementType());
    }

    @Override public QuantityDTO add(QuantityDTO a, QuantityDTO b) { return add(a, b, a); }

    @Override
    public QuantityDTO add(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ) {
        return doArithmetic(thisQ, thatQ, targetQ, "ADD", (a, b) -> a + b);
    }

    @Override public QuantityDTO subtract(QuantityDTO a, QuantityDTO b) { return subtract(a, b, a); }

    @Override
    public QuantityDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ) {
        return doArithmetic(thisQ, thatQ, targetQ, "SUBTRACT", (a, b) -> a - b);
    }

    @Override
    public double divide(QuantityDTO thisQ, QuantityDTO thatQ) {
        QuantityModel<IMeasurable> m1 = getQuantityModel(thisQ);
        QuantityModel<IMeasurable> m2 = getQuantityModel(thatQ);
        validate(m1, m2, null, false);
        double result = m1.getUnit().convertToBaseUnit(m1.getValue()) /
                m2.getUnit().convertToBaseUnit(m2.getValue());
        repository.save(new QuantityMeasurementEntity(m1, m2, "DIVIDE", result));
        return result;
    }

    private QuantityDTO doArithmetic(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ,
                                     String op, DoubleBinaryOperator fn) {
        QuantityModel<IMeasurable> m1 = getQuantityModel(thisQ);
        QuantityModel<IMeasurable> m2 = getQuantityModel(thatQ);
        QuantityModel<IMeasurable> mt = getQuantityModel(targetQ);
        validate(m1, m2, mt, true);
        double base = fn.applyAsDouble(
                m1.getUnit().convertToBaseUnit(m1.getValue()),
                m2.getUnit().convertToBaseUnit(m2.getValue()));
        double val  = mt.getUnit().convertFromBaseUnit(base);
        repository.save(new QuantityMeasurementEntity(m1, m2, op, new QuantityModel<>(val, mt.getUnit())));
        return new QuantityDTO(val, targetQ.getUnit(), targetQ.getMeasurementType());
    }

    private QuantityModel<IMeasurable> getQuantityModel(QuantityDTO q) {
        if (q == null) throw new QuantityMeasurementException("Quantity cannot be null");
        IMeasurable unit;
        switch (q.getMeasurementType()) {
            case "LengthUnit":      unit = LengthUnit.FEET.getUnitInstance(q.getUnit());     break;
            case "WeightUnit":      unit = WeightUnit.GRAM.getUnitInstance(q.getUnit());     break;
            case "VolumeUnit":      unit = VolumeUnit.LITRE.getUnitInstance(q.getUnit());    break;
            case "TemperatureUnit": unit = TemperatureUnit.CELSIUS.getUnitInstance(q.getUnit()); break;
            default: throw new QuantityMeasurementException("Unsupported type: " + q.getMeasurementType());
        }
        return new QuantityModel<>(q.getValue(), unit);
    }

    private <U extends IMeasurable> void validate(
            QuantityModel<U> a, QuantityModel<U> b, QuantityModel<U> t, boolean needTarget) {
        if (a == null || b == null) throw new QuantityMeasurementException("Operands cannot be null");
        if (!a.getUnit().getClass().equals(b.getUnit().getClass()))
            throw new QuantityMeasurementException("Cross-category operation not allowed");
        if (needTarget) {
            if (t == null) throw new QuantityMeasurementException("Target unit cannot be null");
            if (!a.getUnit().getClass().equals(t.getUnit().getClass()))
                throw new QuantityMeasurementException("Invalid target unit category");
        }
        a.getUnit().validOperationSupport("arithmetic");
    }

    public static void main(String[] args) {
        System.out.println("QuantityMeasurementServiceImpl - UC16");
    }
}
