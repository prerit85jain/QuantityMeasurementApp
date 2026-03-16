package quantityMeasurement.repository;

import quantityMeasurement.entity.QuantityMeasurementEntity;

import java.util.List;

public interface IQuantityMeasurementRepository {

    // Saves a QuantityMeasurementEntity to the repository.
    void save(QuantityMeasurementEntity entity);

    // Retrieves all QuantityMeasurementEntity instances from the repository.
    List<QuantityMeasurementEntity> getAllMeasurements();

    List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation);

    List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType);

    int getTotalCount();

    void deleteAll();

    default String getPoolStatistics() {
        return "Pool statistics not available for this repository implementation.";
    }

    default void releaseResources() { }

    // Main method for testing purposes
    public static void main(String[] args) {
        System.out.println("Testing IQuantityMeasurementRepository interface");
    }
}
