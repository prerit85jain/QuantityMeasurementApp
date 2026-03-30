/**
 * QuantityMeasurementDTO is a Data Transfer Object (DTO) class that serves as a
 * data carrier for quantity measurement operations. It encapsulates all the necessary
 * information related to a quantity measurement operation, including the values and
 * units of the operands, the type of operation being performed, the result of the
 * operation, and any error information if applicable. This class is designed to be
 * used in the service layer and REST controllers to facilitate communication between
 * different layers of the application while maintaining a clear separation of concerns.
 *
 * @author Developer
 * @version 17.0
 * @since 17.0
 */
package com.app.quantitymeasurement.model;

import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonProperty;

public @Data class QuantityMeasurementDTO {
    public double thisValue;
    public String thisUnit;
    public String thisMeasurementType;
    public double thatValue;
    public String thatUnit;
    public String thatMeasurementType;
    public String operation;
    public String resultString;
    public double resultValue;
    public String resultUnit;
    public String resultMeasurementType;
    public String errorMessage;

    // FIX 1: Rename field from isError -> error
    //   Lombok generates isError() for boolean isError – Jackson strips "is"
    //   and maps it to JSON key "error". On deserialization it can't find
    //   "error" -> "isError" and crashes. Renaming to plain "error" fixes this.
    //
    // FIX 2: Add @JsonProperty("error") to explicitly tell Jackson
    //   the JSON key name – no more ambiguity between field name and getter name.
    @JsonProperty("error")
    public boolean error;

    /**
     * Convert Entity to DTO using Static Factory Method. This method takes as input a
     * QuantityMeasurementEntity and creates a corresponding QuantityMeasurementDTO
     * by copying the relevant fields. It performs a null check on the input entity to
     * ensure robustness. This approach provides a clear and centralized way to convert
     * between the entity and DTO representations, which is especially useful when
     * retrieving data from the database and preparing it for use in the service layer
     * or for returning in REST API responses.
     *
     * @param entity the QuantityMeasurementEntity to be converted to a DTO
     * @return a QuantityMeasurementDTO representing the data from the input entity
     */
    public static QuantityMeasurementDTO from(QuantityMeasurementEntity entity) {
        if (entity == null) return null;
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.thisValue             = entity.thisValue;
        dto.thisUnit              = entity.thisUnit;
        dto.thisMeasurementType   = entity.thisMeasurementType;
        dto.thatValue             = entity.thatValue;
        dto.thatUnit              = entity.thatUnit;
        dto.thatMeasurementType   = entity.thatMeasurementType;
        dto.operation             = entity.operation;
        dto.resultString          = entity.resultString;
        dto.resultValue           = entity.resultValue;
        dto.resultUnit            = entity.resultUnit;
        dto.resultMeasurementType = entity.resultMeasurementType;
        dto.errorMessage          = entity.errorMessage;
        dto.error                 = entity.isError;
        return dto;
    }

    /**
     * Convert DTO to Entity using Instance Method. This method creates a new
     * QuantityMeasurementEntity and copies all the relevant fields from this
     * DTO to the new entity.
     *
     * @return a QuantityMeasurementEntity representing the data from this DTO
     */
    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.thisValue             = this.thisValue;
        e.thisUnit              = this.thisUnit;
        e.thisMeasurementType   = this.thisMeasurementType;
        e.thatValue             = this.thatValue;
        e.thatUnit              = this.thatUnit;
        e.thatMeasurementType   = this.thatMeasurementType;
        e.operation             = this.operation;
        e.resultString          = this.resultString;
        e.resultValue           = this.resultValue;
        e.resultUnit            = this.resultUnit;
        e.resultMeasurementType = this.resultMeasurementType;
        e.errorMessage          = this.errorMessage;
        e.isError               = this.error;
        return e;
    }

    /**
     * Convert List of Entities to List of DTOs. For this, we can use Java Streams to
     * map each entity in the list to a DTO using the `from` method defined above,
     * and then collect the results into a new list.
     *
     * @param entities the list of QuantityMeasurementEntity objects to be converted to DTOs
     * @return a list of QuantityMeasurementDTO objects representing the data from the input entities
     */
    public static List<QuantityMeasurementDTO> fromList(List<QuantityMeasurementEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
            .map(QuantityMeasurementDTO::from)
            .collect(Collectors.toList());
    }

    /**
     * Convert List of DTOs to List of Entities. Similar to the previous method,
     * it uses Java Stream API to perform the mapping.
     *
     * @param dtos the list of QuantityMeasurementDTO objects to be converted to entities
     * @return a list of QuantityMeasurementEntity objects
     */
    public static List<QuantityMeasurementEntity> toEntityList(List<QuantityMeasurementDTO> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream()
            .map(QuantityMeasurementDTO::toEntity)
            .collect(Collectors.toList());
    }
}
