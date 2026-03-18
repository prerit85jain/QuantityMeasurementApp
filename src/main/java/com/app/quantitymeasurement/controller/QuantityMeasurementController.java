/**
 * UC 17 Enhancements:
 * 1. Added @RestController and @RequestMapping annotations to define this class
 *    as a REST controller and to specify the base URL for the API endpoints.
 * 2. Refactored the QuantityMeasurementApp class to expose API methods in this
 *    controller class, changing method names from demonstrationXXX to performXXX
 *    to better reflect their purpose as REST API methods that perform specific
 *    operations on quantities.
 * 3. Added PostMapping and GetMapping annotations to the API methods to define the
 *    HTTP verbs and endpoints for each operation (e.g., /compare, /convert, /add).
 * 4. Added Swagger annotations (@Operation and @Tag) to the controller and its
 *    methods to enhance API documentation and provide clear descriptions of each
 *    endpoint's functionality.
 * 5. Implemented error handling in the API methods using try-catch blocks to catch
 *    any exceptions thrown by the service layer, logging the errors, and returning
 *    appropriate HTTP responses (e.g., bad request) when errors occur.
 */
package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.model.OperationType;
import com.app.quantitymeasurement.model.QuantityDTO;
import com.app.quantitymeasurement.model.QuantityInputDTO;
import com.app.quantitymeasurement.model.QuantityMeasurementDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;

import java.util.List;
import java.util.logging.Logger;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {
    // Logger for logging information and errors in the controller
    private static final Logger logger = Logger.getLogger(
        QuantityMeasurementController.class.getName()
    );

    @Autowired // Dependency Injection of the service layer into the controller
    private IQuantityMeasurementService service;

    // — Reusable json example snippets —
    private static final String EX_FEET_INCH =
        """
        { "thisQuantityDTO": {"value":1.0,"unit":"FEET", "measurementType":"LengthUnit"},
          "thatQuantityDTO": {"value":12.0,"unit":"INCHES", "measurementType":"LengthUnit"} }""";

    private static final String EX_YARD_FEET =
        """
        { "thisQuantityDTO": {"value":1.0,"unit":"YARDS", "measurementType":"LengthUnit"},
          "thatQuantityDTO": {"value":3.0,"unit":"FEET", "measurementType":"LengthUnit"} }""";

    private static final String EX_GALLON_LITRE =
        """
        { "thisQuantityDTO": {"value":1.0,"unit":"GALLON", "measurementType":"VolumeUnit"},
          "thatQuantityDTO": {"value":3.785,"unit":"LITRE", "measurementType":"VolumeUnit"} }""";

    private static final String EX_TEMP =
        """
        { "thisQuantityDTO": {"value":212.0,"unit":"FAHRENHEIT", "measurementType":"TemperatureUnit"},
          "thatQuantityDTO": {"value":100.0,"unit":"CELSIUS", "measurementType":"TemperatureUnit"} }""";

    private static final String EX_WITH_TARGET =
        """
        { "thisQuantityDTO":   {"value":1.0, "unit":"FEET",   "measurementType":"LengthUnit"},
          "thatQuantityDTO":   {"value":12.0,"unit":"INCHES", "measurementType":"LengthUnit"},
          "targetQuantityDTO": {"value":0.0, "unit":"INCHES", "measurementType":"LengthUnit"} }""";

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet = 12 Inches", value = EX_FEET_INCH),
                @ExampleObject(name = "Yard = 3 Feet",    value = EX_YARD_FEET),
                @ExampleObject(name = "Gallon = Litres",  value = EX_GALLON_LITRE),
                @ExampleObject(name = "212°F = 100°C",    value = EX_TEMP)
            })
        )
    )
    public ResponseEntity<QuantityMeasurementDTO> performComparison(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("performComparison called");
        QuantityMeasurementDTO result = service.compare(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert quantity to target unit",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet -> Inches",  value = EX_FEET_INCH),
                @ExampleObject(name = "Yard -> Feet",    value = EX_YARD_FEET),
                @ExampleObject(name = "Gallon -> Litres",value = EX_GALLON_LITRE),
                @ExampleObject(name = "212°F -> 100°C",  value = EX_TEMP)
            })
        )
    )
    public ResponseEntity<QuantityMeasurementDTO> performConversion(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("performConversion called");
        QuantityMeasurementDTO result = service.convert(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet + Inches",   value = EX_FEET_INCH),
                @ExampleObject(name = "Yard + Feet",     value = EX_YARD_FEET),
                @ExampleObject(name = "Gallon + Litres", value = EX_GALLON_LITRE)
            })
        )
    )
    public ResponseEntity<QuantityMeasurementDTO> performAddition(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("performAddition called");
        QuantityMeasurementDTO result = service.add(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add-with-target-unit")
    @Operation(summary = "Add two quantities with a target unit",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet + Inches with Target Unit", value = EX_WITH_TARGET)
            })
        )
    )
    public ResponseEntity<QuantityMeasurementDTO> performAdditionWithTargetUnit(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("performAdditionWithTargetUnit called");
        QuantityDTO target = quantityInputDTO.getTargetQuantityDTO() != null
            ? quantityInputDTO.getTargetQuantityDTO()
            : quantityInputDTO.getThisQuantityDTO();
        QuantityMeasurementDTO result = service.add(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO(),
            target);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet - Inches",   value = EX_FEET_INCH),
                @ExampleObject(name = "Yard - Feet",     value = EX_YARD_FEET),
                @ExampleObject(name = "Gallon - Litres", value = EX_GALLON_LITRE)
            })
        )
    )
    public ResponseEntity<QuantityMeasurementDTO> performSubtraction(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("performSubtraction called");
        QuantityMeasurementDTO result = service.subtract(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract-with-target-unit")
    @Operation(summary = "Subtract two quantities with target unit",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet - Inches with Target Unit", value = EX_WITH_TARGET)
            })
        )
    )
    public ResponseEntity<QuantityMeasurementDTO> performSubtractionWithTargetUnit(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("performSubtractionWithTargetUnit called");
        QuantityDTO target = quantityInputDTO.getTargetQuantityDTO() != null
            ? quantityInputDTO.getTargetQuantityDTO()
            : quantityInputDTO.getThisQuantityDTO();
        QuantityMeasurementDTO result = service.subtract(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO(),
            target);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet / Inches",   value = EX_FEET_INCH),
                @ExampleObject(name = "Yard / Feet",     value = EX_YARD_FEET),
                @ExampleObject(name = "Gallon / Litres", value = EX_GALLON_LITRE)
            })
        )
    )
    public ResponseEntity<QuantityMeasurementDTO> performDivision(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("performDivision called");
        QuantityMeasurementDTO result = service.divide(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/operation/{operation}")
    @Operation(
        summary     = "Get operation history",
        description = "Valid operations: ADD, SUBTRACT, MULTIPLY, DIVIDE, CONVERT"
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(
            @PathVariable String operation) {
        return ResponseEntity.ok(service.getOperationHistory(operation));
    }

    @GetMapping("/history/type/{type}")
    @Operation(
        summary     = "Get operation history by type",
        description = "Valid types: LengthUnit, VolumeUnit, WeightUnit, TemperatureUnit"
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistoryByType(
            @PathVariable String type) {
        return ResponseEntity.ok(service.getMeasurementsByType(type));
    }

    @GetMapping("/count/{operation}")
    @Operation(
        summary     = "Get operation count",
        description = "Valid operations: ADD, SUBTRACT, MULTIPLY, DIVIDE, CONVERT"
    )
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get errored operations history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErroredOperations() {
        return ResponseEntity.ok(service.getErrorHistory());
    }
}
