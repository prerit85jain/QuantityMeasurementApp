package quantityMeasurement.service;

import quantityMeasurement.entity.QuantityDTO;

public interface IQuantityMeasurementService {

    // Compare this quantity to that quantity to determine if they are equal in value.
    boolean compare(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);

    // Convert the given this quantity to a different unit with corresponding that quantity.
    QuantityDTO convert(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);

    QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
    QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO);

    QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
    QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO);

    double divide(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);

    // Main method for testing purposes
    public static void main(String[] args) {
        System.out.println("IQuantityMeasurementService Interface");
    }
}
