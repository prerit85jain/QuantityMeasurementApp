package quantityMeasurementTest.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import quantityMeasurement.controller.QuantityMeasurementController;
import quantityMeasurement.entity.QuantityDTO;
import quantityMeasurement.service.IQuantityMeasurementService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuantityMeasurementControllerTest {

    @Mock
    private IQuantityMeasurementService mockService;
    private QuantityMeasurementController controller;

    private final QuantityDTO feet   = new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET);
    private final QuantityDTO inches = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new QuantityMeasurementController(mockService);
    }

    @Test
    public void testPerformComparison_DelegatesToService() {
        when(mockService.compare(feet, inches)).thenReturn(true);
        assertTrue(controller.performComparison(feet, inches));
        verify(mockService).compare(feet, inches);
    }

    @Test public void testPerformConversion_DelegatesToService() {
        QuantityDTO expected = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        when(mockService.convert(feet, inches)).thenReturn(expected);
        QuantityDTO result = controller.performConversion(feet, inches);
        assertEquals(expected, result);
        verify(mockService).convert(feet, inches);
    }

    @Test public void testPerformAddition_DelegatesToService() {
        QuantityDTO expected = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        when(mockService.add(feet, inches)).thenReturn(expected);
        assertEquals(expected, controller.performAddition(feet, inches));
        verify(mockService).add(feet, inches);
    }

    @Test public void testPerformSubtraction_DelegatesToService() {
        QuantityDTO f2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO expected = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        when(mockService.subtract(f2, inches)).thenReturn(expected);
        assertEquals(expected, controller.performSubtraction(f2, inches));
        verify(mockService).subtract(f2, inches);
    }

    @Test public void testPerformDivision_DelegatesToService() {
        when(mockService.divide(feet, inches)).thenReturn(2.0);
        assertEquals(2.0, controller.performDivision(feet, inches), 0.001);
        verify(mockService).divide(feet, inches);
    }
}

