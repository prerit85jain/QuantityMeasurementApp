package com.app.quantitymeasurement.controller;

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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * UC18 update: all endpoints now require a valid JWT.
 *   - Regular endpoints : any authenticated user (ROLE_USER or ROLE_ADMIN)
 *   - GET /history/**   : any authenticated user
 *   - GET /all          : ROLE_ADMIN only  (@PreAuthorize)
 *
 * Request body stays exactly as UC17 — QuantityInputDTO with nested
 * thisQuantityDTO / thatQuantityDTO / targetQuantityDTO fields.
 *
 * How to call from Swagger UI:
 *   1. POST /auth/register  → copy the token
 *   2. Click "Authorize" (top right) → paste token (without "Bearer ")
 *   3. All requests now include Authorization: Bearer <token> automatically
 */
@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
@SecurityRequirement(name = "bearerAuth")
public class QuantityMeasurementController {

    private static final Logger logger = Logger.getLogger(
        QuantityMeasurementController.class.getName()
    );

    @Autowired
    private IQuantityMeasurementService service;

    // ─────────────────── Reusable JSON example snippets ─────────────────────

    private static final String EX_FEET_INCH =
        """
        { "thisQuantityDTO": {"value":1.0,"unit":"FEET","measurementType":"LengthUnit"},
          "thatQuantityDTO": {"value":12.0,"unit":"INCHES","measurementType":"LengthUnit"} }""";

    private static final String EX_YARD_FEET =
        """
        { "thisQuantityDTO": {"value":1.0,"unit":"YARDS","measurementType":"LengthUnit"},
          "thatQuantityDTO": {"value":3.0,"unit":"FEET","measurementType":"LengthUnit"} }""";

    private static final String EX_GALLON_LITRE =
        """
        { "thisQuantityDTO": {"value":1.0,"unit":"GALLON","measurementType":"VolumeUnit"},
          "thatQuantityDTO": {"value":3.785,"unit":"LITRE","measurementType":"VolumeUnit"} }""";

    private static final String EX_TEMP =
        """
        { "thisQuantityDTO": {"value":212.0,"unit":"FAHRENHEIT","measurementType":"TemperatureUnit"},
          "thatQuantityDTO": {"value":100.0,"unit":"CELSIUS","measurementType":"TemperatureUnit"} }""";

    private static final String EX_WITH_TARGET =
        """
        { "thisQuantityDTO":   {"value":1.0, "unit":"FEET",   "measurementType":"LengthUnit"},
          "thatQuantityDTO":   {"value":12.0,"unit":"INCHES", "measurementType":"LengthUnit"},
          "targetQuantityDTO": {"value":0.0, "unit":"INCHES", "measurementType":"LengthUnit"} }""";

    // ─────────────────────────── Compare ────────────────────────────────────

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities",
        description = "Returns true if the two quantities are equal after unit conversion. Requires JWT.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet = 12 Inches", value = EX_FEET_INCH),
                @ExampleObject(name = "Yard = 3 Feet",    value = EX_YARD_FEET),
                @ExampleObject(name = "Gallon = Litres",  value = EX_GALLON_LITRE),
                @ExampleObject(name = "212 F = 100 C",    value = EX_TEMP)
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

    // ─────────────────────────── Convert ────────────────────────────────────

    @PostMapping("/convert")
    @Operation(summary = "Convert quantity to target unit",
        description = "Converts a value from one unit to another. Requires JWT.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                @ExampleObject(name = "Feet -> Inches",   value = EX_FEET_INCH),
                @ExampleObject(name = "Yard -> Feet",     value = EX_YARD_FEET),
                @ExampleObject(name = "Gallon -> Litres", value = EX_GALLON_LITRE),
                @ExampleObject(name = "212 F -> 100 C",   value = EX_TEMP)
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

    // ──────────────────────────── Add ───────────────────────────────────────

    @PostMapping("/add")
    @Operation(summary = "Add two quantities",
        description = "Adds two quantities, converting units as needed. Requires JWT.",
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

    // ─────────────────────────── Subtract ───────────────────────────────────

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities",
        description = "Subtracts the second quantity from the first. Requires JWT.",
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

    // ──────────────────────────── Divide ────────────────────────────────────

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities",
        description = "Divides the first quantity by the second. Requires JWT.",
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

    // ─────────────────── History endpoints (any authenticated user) ──────────

    @GetMapping("/history/operation/{operation}")
    @Operation(
        summary     = "Get operation history",
        description = "Valid operations: ADD, SUBTRACT, MULTIPLY, DIVIDE, CONVERT. Requires JWT."
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(
            @PathVariable String operation) {
        return ResponseEntity.ok(service.getOperationHistory(operation));
    }

    @GetMapping("/history/type/{type}")
    @Operation(
        summary     = "Get operation history by measurement type",
        description = "Valid types: LengthUnit, VolumeUnit, WeightUnit, TemperatureUnit. Requires JWT."
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistoryByType(
            @PathVariable String type) {
        return ResponseEntity.ok(service.getMeasurementsByType(type));
    }

    @GetMapping("/count/{operation}")
    @Operation(
        summary     = "Get operation count",
        description = "Valid operations: ADD, SUBTRACT, MULTIPLY, DIVIDE, CONVERT. Requires JWT."
    )
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get errored operations history. Requires JWT.")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErroredOperations() {
        return ResponseEntity.ok(service.getErrorHistory());
    }

    // ─────────────────── Admin-only endpoint ────────────────────────────────

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary     = "Get all measurements (ADMIN only)",
        description = "Returns all measurement records. Requires ROLE_ADMIN JWT."
    )
    public ResponseEntity<List<QuantityMeasurementDTO>> getAllMeasurements() {
        return ResponseEntity.ok(service.getOperationHistory(""));
    }
}
