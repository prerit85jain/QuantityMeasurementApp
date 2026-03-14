
 /*
  * @version 15.0
  * @author Prerit Jain
 */

package quantityMeasurement;

 import quantityMeasurement.controller.QuantityMeasurementController;
 import quantityMeasurement.entity.QuantityDTO;
 import quantityMeasurement.repository.IQuantityMeasurementRepository;
 import quantityMeasurement.repository.QuantityMeasurementCacheRepository;
 import quantityMeasurement.service.QuantityMeasurementServiceImpl;

 public class QuantityMeasurementApp {
	 // Singleton instance of QuantityMeasurementApp for application-wide access if needed
	 private static QuantityMeasurementApp instance;

	 // Maintaining a reference of the Controller this created to invoke the controller
	 // methods for performing operations on quantities
	 public QuantityMeasurementController controller;

	 // Maintaining a reference of the Repository to report all measurements stored in
	 // the repository at the end of the main method
	 public IQuantityMeasurementRepository repository;

	 // Private constructor to prevent instantiation from outside the class
	 private QuantityMeasurementApp() {
		 this.repository = QuantityMeasurementCacheRepository.getInstance();
		 QuantityMeasurementServiceImpl service = new QuantityMeasurementServiceImpl(
				 this.repository
		 );
		 this.controller = new QuantityMeasurementController(service);
	 }

	 /**
	  * Get the singleton instance of the QuantityMeasurementApp.
	  *
	  * @return the singleton instance of the application
	  */
	 public static QuantityMeasurementApp getInstance() {
		 if (instance == null) {
			 instance = new QuantityMeasurementApp();
		 }
		 return instance;
	 }

	 /**
	  * The main method serves as the entry point of the application. It demonstrates the
	  * functionality of the Quantity Measurement system by invoking the controller to perform
	  * various operations such as comparisons, conversions, and arithmetic operations on quantities.
	  */
	 public static void main(String[] args) {
		 QuantityMeasurementApp app = QuantityMeasurementApp.getInstance();
		 QuantityMeasurementController controller = app.controller;

		 System.out.println("============================================================");
		 System.out.println("  Quantity Measurement App — UC15 (N-Tier Architecture)");
		 System.out.println("  Package: com.apps.quantitymeasurement");
		 System.out.println("============================================================");

		 // --- Length Equality ---
		 System.out.println("\n--- Length Equality ---");
		 System.out.println("1 FEET == 12 INCHES : " + controller.performComparison(
				 new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
				 new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
		 System.out.println("1 YARD  == 36 INCHES : " + controller.performComparison(
				 new QuantityDTO(1.0,  QuantityDTO.LengthUnit.YARDS),
				 new QuantityDTO(36.0, QuantityDTO.LengthUnit.INCHES)));

		 // --- Weight Conversion & Comparison ---
		 System.out.println("\n--- Weight Conversion & Comparison ---");
		 QuantityDTO grams = new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM);
		 QuantityDTO kg    = new QuantityDTO(1.0,    QuantityDTO.WeightUnit.KILOGRAM);
		 System.out.println("1000 GRAM == 1 KG    : " + controller.performComparison(grams, kg));
		 System.out.println("Convert 1000 GRAM -> KG : " + controller.performConversion(grams, kg));

		 // --- Weight Addition ---
		 System.out.println("\n--- Weight Addition ---");
		 QuantityDTO pounds = new QuantityDTO(2.20462, QuantityDTO.WeightUnit.POUND);
		 System.out.println("1 KG + 2.20462 LB -> KG   : " +
				 controller.performAddition(kg, pounds));
		 System.out.println("1 KG + 2.20462 LB -> GRAM : " +
				 controller.performAddition(kg, pounds,
						 new QuantityDTO(0, QuantityDTO.WeightUnit.GRAM)));

		 // --- Volume Operations ---
		 System.out.println("\n--- Volume Operations ---");
		 QuantityDTO litre  = new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE);
		 QuantityDTO ml     = new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE);
		 QuantityDTO gallon = new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.GALLON);
		 System.out.println("1 L == 1000 mL        : " + controller.performComparison(litre, ml));
		 System.out.println("Convert 1 GAL -> L    : " + controller.performConversion(gallon, litre));
		 System.out.println("1 L + 1000 mL         : " + controller.performAddition(litre, ml));

		 // --- Temperature Conversion & Comparison ---
		 System.out.println("\n--- Temperature Conversion & Comparison ---");
		 QuantityDTO celsius = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
		 QuantityDTO fahr    = new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT);
		 System.out.println("100 C == 212 F        : " + controller.performComparison(celsius, fahr));
		 System.out.println("Convert 100 C -> F    : " +
				 controller.performConversion(celsius,
						 new QuantityDTO(0, QuantityDTO.TemperatureUnit.FAHRENHEIT)));

		 // --- Subtraction & Division ---
		 System.out.println("\n--- Subtraction & Division ---");
		 QuantityDTO q1 = new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET);
		 QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
		 System.out.println("2 FEET - 12 INCHES    : " + controller.performSubtraction(q1, q2));
		 System.out.println("2 FEET / 12 INCHES    : " + controller.performDivision(q1, q2));

		 System.out.println("\n============================================================");
		 System.out.println("  Repository: " + app.repository.getAllMeasurements().size() +
				 " operation(s) recorded.");
		 System.out.println("============================================================");
	 }
 }