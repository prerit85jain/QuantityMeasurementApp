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

public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService{

    private IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    private enum Operation {COMPARISON, CONVERSION, ARITHMETIC}

    @Override
    public boolean compare(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> thisModel = getQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> thatModel = getQuantityModel(thatQuantityDTO);
        boolean result = compare(thisModel, thatModel);
        String resultStr = result ? "Equal" : "Not Equal";
        repository.save(new QuantityMeasurementEntity(thisModel, thatModel, "COMPARE", resultStr));
        return result;
    }

    private <U extends IMeasurable> boolean compare(
            QuantityModel<U> thisModel, QuantityModel<U> thatModel) {
        Quantity<U> thisQty = new Quantity<>(thisModel.getValue(), thisModel.getUnit());
        Quantity<U> thatQty = new Quantity<>(thatModel.getValue(), thatModel.getUnit());
        return thisQty.equals(thatQty);
    }

    @Override
    public QuantityDTO convert(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> thisModel = getQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> thatModel = getQuantityModel(thatQuantityDTO);
        double convertedValue = convertTo(thisModel, thatModel.getUnit());
        QuantityModel<IMeasurable> resultModel =
                new QuantityModel<>(convertedValue, thatModel.getUnit());
        repository.save(new QuantityMeasurementEntity(thisModel, thatModel, "CONVERT", resultModel));
        return new QuantityDTO(convertedValue, thatQuantityDTO.getUnit(), thatQuantityDTO.getMeasurementType());
    }

    private <U extends IMeasurable> double convertTo(QuantityModel<U> model, U targetUnit) {
        if (model.getUnit() instanceof TemperatureUnit) {
            return convertTemperatureUnit(model, targetUnit);
        }
        Quantity<U> qty = new Quantity<>(model.getValue(), model.getUnit());
        return qty.convertTo(targetUnit).getValue();
    }

    private <U extends IMeasurable> double convertTemperatureUnit(
            QuantityModel<U> thisUnitU, U thatUnitU) {
        double baseValue = thisUnitU.getUnit().convertToBaseUnit(thisUnitU.getValue());
        return thatUnitU.convertFromBaseUnit(baseValue);
    }

    @Override
    public QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return add(thisQuantityDTO, thatQuantityDTO, thisQuantityDTO);
    }

    @Override
    public QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
                           QuantityDTO targetUnitDTO) {
        QuantityModel<IMeasurable> thisModel   = getQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> thatModel   = getQuantityModel(thatQuantityDTO);
        QuantityModel<IMeasurable> targetModel = getQuantityModel(targetUnitDTO);

        validateArithmeticOperands(thisModel, thatModel, targetModel, true);

        double resultBase = performArithmetic(thisModel, thatModel, targetModel,
                (a, b) -> a + b);
        double resultValue = targetModel.getUnit().convertFromBaseUnit(resultBase);
        QuantityModel<IMeasurable> resultModel =
                new QuantityModel<>(resultValue, targetModel.getUnit());

        repository.save(new QuantityMeasurementEntity(thisModel, thatModel, "ADD", resultModel));
        return new QuantityDTO(resultValue, targetUnitDTO.getUnit(), targetUnitDTO.getMeasurementType());
    }

    @Override
    public QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return subtract(thisQuantityDTO, thatQuantityDTO, thisQuantityDTO);
    }

    @Override
    public QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
                                QuantityDTO targetUnitDTO) {
        QuantityModel<IMeasurable> thisModel   = getQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> thatModel   = getQuantityModel(thatQuantityDTO);
        QuantityModel<IMeasurable> targetModel = getQuantityModel(targetUnitDTO);

        validateArithmeticOperands(thisModel, thatModel, targetModel, true);

        double resultBase = performArithmetic(thisModel, thatModel, targetModel,
                (a, b) -> a - b);
        double resultValue = targetModel.getUnit().convertFromBaseUnit(resultBase);
        QuantityModel<IMeasurable> resultModel =
                new QuantityModel<>(resultValue, targetModel.getUnit());

        repository.save(new QuantityMeasurementEntity(thisModel, thatModel, "SUBTRACT", resultModel));
        return new QuantityDTO(resultValue, targetUnitDTO.getUnit(), targetUnitDTO.getMeasurementType());
    }

    @Override
    public double divide(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> thisModel = getQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> thatModel = getQuantityModel(thatQuantityDTO);

        validateArithmeticOperands(thisModel, thatModel, null, false);

        double result = performArithmetic(thisModel, thatModel, null,
                (a, b) -> {
                    if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                    return a / b;
                });

        repository.save(new QuantityMeasurementEntity(thisModel, thatModel, "DIVIDE", result));
        return result;
    }

    private QuantityModel<IMeasurable> getQuantityModel(QuantityDTO quantity) {
        if (quantity == null)
            throw new QuantityMeasurementException("Quantity cannot be null");
        if (quantity.getUnit() == null || quantity.getMeasurementType() == null)
            throw new QuantityMeasurementException("Quantity unit or measurement type cannot be null");

        IMeasurable unit;
        switch (quantity.getMeasurementType()) {
            case "LengthUnit":
                unit = LengthUnit.FEET.getUnitInstance(quantity.getUnit());
                break;
            case "WeightUnit":
                unit = WeightUnit.GRAM.getUnitInstance(quantity.getUnit());
                break;
            case "VolumeUnit":
                unit = VolumeUnit.LITRE.getUnitInstance(quantity.getUnit());
                break;
            case "TemperatureUnit":
                unit = TemperatureUnit.CELSIUS.getUnitInstance(quantity.getUnit());
                break;
            default:
                throw new QuantityMeasurementException(
                        "Unsupported measurement type: " + quantity.getMeasurementType());
        }
        return new QuantityModel<>(quantity.getValue(), unit);
    }

    private <U extends IMeasurable> void validateArithmeticOperands(
            QuantityModel<U> thisModel,
            QuantityModel<U> other,
            QuantityModel<U> targetUnit,
            boolean targetUnitRequired) {
        if (thisModel == null || other == null)
            throw new QuantityMeasurementException("Operands cannot be null");
        if (!Double.isFinite(thisModel.getValue()) || !Double.isFinite(other.getValue()))
            throw new QuantityMeasurementException("Values must be finite");
        if (!thisModel.getUnit().getClass().equals(other.getUnit().getClass()))
            throw new QuantityMeasurementException(
                    "Cross-category operation not allowed: " +
                            thisModel.getUnit().getMeasurementType() + " vs " +
                            other.getUnit().getMeasurementType());
        if (targetUnitRequired) {
            if (targetUnit == null)
                throw new QuantityMeasurementException("Target unit cannot be null");
            if (!thisModel.getUnit().getClass().equals(targetUnit.getUnit().getClass()))
                throw new QuantityMeasurementException("Invalid target unit category");
        }
        // Validate arithmetic support (e.g., Temperature doesn't support arithmetic)
        thisModel.getUnit().validOperationSupport("arithmetic");
    }

    private enum ArithmeticOperation {ADD, SUBTRACT, DIVIDE}

    private <U extends IMeasurable> double performArithmetic(
            QuantityModel<U> thisModel,
            QuantityModel<U> other,
            QuantityModel<U> targetUnit,
            DoubleBinaryOperator operation) {
        double thisBase  = thisModel.getUnit().convertToBaseUnit(thisModel.getValue());
        double otherBase = other.getUnit().convertToBaseUnit(other.getValue());
        return operation.applyAsDouble(thisBase, otherBase);
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        IQuantityMeasurementRepository repo =
                QuantityMeasurementCacheRepository.getInstance();
        QuantityMeasurementServiceImpl service =
                new QuantityMeasurementServiceImpl(repo);

        QuantityDTO dto1 = new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET);
        QuantityDTO dto2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        System.out.println("Compare 1 FEET == 12 INCHES: " + service.compare(dto1, dto2));
    }
}
