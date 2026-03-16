package quantityMeasurement.controller;

import quantityMeasurement.service.IQuantityMeasurementService;
import quantityMeasurement.entity.QuantityDTO;
import quantityMeasurement.repository.*;
import quantityMeasurement.service.QuantityMeasurementServiceImpl;

import java.util.logging.Logger;

public class QuantityMeasurementController {

    private static final Logger logger = Logger.getLogger(
            QuantityMeasurementController.class.getName());

    private IQuantityMeasurementService quantityMeasurementService;

    public QuantityMeasurementController(IQuantityMeasurementService quantityMeasurementService) {
        this.quantityMeasurementService = quantityMeasurementService;
        logger.info("QuantityMeasurementController initialized.");
    }

    public boolean performComparison(QuantityDTO thisQ, QuantityDTO thatQ) {
        return quantityMeasurementService.compare(thisQ, thatQ);
    }

    public QuantityDTO performConversion(QuantityDTO thisQ, QuantityDTO thatQ) {
        return quantityMeasurementService.convert(thisQ, thatQ);
    }

    public QuantityDTO performAddition(QuantityDTO thisQ, QuantityDTO thatQ) {
        return quantityMeasurementService.add(thisQ, thatQ);
    }

    public QuantityDTO performAddition(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ) {
        return quantityMeasurementService.add(thisQ, thatQ, targetQ);
    }

    public QuantityDTO performSubtraction(QuantityDTO thisQ, QuantityDTO thatQ) {
        return quantityMeasurementService.subtract(thisQ, thatQ);
    }

    public QuantityDTO performSubtraction(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ) {
        return quantityMeasurementService.subtract(thisQ, thatQ, targetQ);
    }

    public double performDivision(QuantityDTO thisQ, QuantityDTO thatQ) {
        return quantityMeasurementService.divide(thisQ, thatQ);
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        logger.info("QuantityMeasurementController - UC16");
    }
}

