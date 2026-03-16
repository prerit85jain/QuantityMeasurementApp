
 /*
  * @version 15.0
  * @author Prerit Jain
 */

package quantityMeasurement;

 import quantityMeasurement.controller.QuantityMeasurementController;
 import quantityMeasurement.entity.QuantityDTO;
 import quantityMeasurement.repository.IQuantityMeasurementRepository;
 import quantityMeasurement.repository.QuantityMeasurementCacheRepository;
 import quantityMeasurement.repository.QuantityMeasurementDatabaseRepository;
 import quantityMeasurement.service.QuantityMeasurementServiceImpl;
 import quantityMeasurement.utility.ApplicationConfig;

 import java.util.logging.Logger;

 public class QuantityMeasurementApp {

	 private static final Logger logger = Logger.getLogger(
			 QuantityMeasurementApp.class.getName());

	 private static QuantityMeasurementApp instance;

	 public QuantityMeasurementController controller;
	 public IQuantityMeasurementRepository repository;

	 private QuantityMeasurementApp() {
		 ApplicationConfig config = ApplicationConfig.getInstance();
		 String repoType = config.getProperty(
				 ApplicationConfig.ConfigKey.REPOSITORY_TYPE.getKey(), "database");

		 logger.info("Initializing QuantityMeasurementApp with repository type: " + repoType);

		 if ("database".equalsIgnoreCase(repoType)) {
			 this.repository = QuantityMeasurementDatabaseRepository.getInstance();
			 logger.info("Using Database Repository (JDBC/H2)");
		 } else {
			 this.repository = QuantityMeasurementCacheRepository.getInstance();
			 logger.info("Using Cache Repository (in-memory)");
		 }

		 QuantityMeasurementServiceImpl service =
				 new QuantityMeasurementServiceImpl(this.repository);
		 this.controller = new QuantityMeasurementController(service);
		 logger.info("QuantityMeasurementApp initialized successfully.");
	 }

	 public static QuantityMeasurementApp getInstance() {
		 if (instance == null) {
			 instance = new QuantityMeasurementApp();
		 }
		 return instance;
	 }

	 /** Close resources held by the repository when shutting down. */
	 public void closeResources() {
		 repository.releaseResources();
		 instance = null;
		 logger.info("QuantityMeasurementApp resources closed.");
	 }

	 /** Delete all measurements - useful for testing or resetting state. */
	 public void deleteAllMeasurements() {
		 repository.deleteAll();
		 logger.info("All measurements deleted.");
	 }

	 public static void main(String[] args) {
		 QuantityMeasurementApp app        = QuantityMeasurementApp.getInstance();
		 QuantityMeasurementController ctrl = app.controller;

		 logger.info("============================================================");
		 logger.info("  Quantity Measurement App - UC16 (JDBC Database Integration)");
		 logger.info("  Repository: " + app.repository.getClass().getSimpleName());
		 logger.info("  Pool Stats: " + app.repository.getPoolStatistics());
		 logger.info("============================================================");

		 // --- Length Equality ---
		 logger.info("1 FEET == 12 INCHES : " + ctrl.performComparison(
				 new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
				 new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));

		 logger.info("1 YARD == 36 INCHES : " + ctrl.performComparison(
				 new QuantityDTO(1.0,  QuantityDTO.LengthUnit.YARDS),
				 new QuantityDTO(36.0, QuantityDTO.LengthUnit.INCHES)));

		 // --- Weight ---
		 QuantityDTO grams  = new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM);
		 QuantityDTO kg     = new QuantityDTO(1.0,    QuantityDTO.WeightUnit.KILOGRAM);
		 logger.info("1000 GRAM == 1 KG   : " + ctrl.performComparison(grams, kg));
		 logger.info("Convert 1000g -> KG : " + ctrl.performConversion(grams, kg));
		 logger.info("1000g + 1kg -> KG   : " + ctrl.performAddition(grams, kg));

		 // --- Volume ---
		 logger.info("1 L == 1000 mL      : " + ctrl.performComparison(
				 new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE),
				 new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE)));

		 // --- Temperature ---
		 logger.info("100C == 212F        : " + ctrl.performComparison(
				 new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
				 new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT)));

		 // --- Arithmetic ---
		 QuantityDTO q1 = new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET);
		 QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
		 logger.info("2 FEET - 12 INCHES  : " + ctrl.performSubtraction(q1, q2));
		 logger.info("2 FEET / 12 INCHES  : " + ctrl.performDivision(q1, q2));

		 // --- Repository stats ---
		 logger.info("------------------------------------------------------------");
		 logger.info("Total measurements stored: " + app.repository.getTotalCount());
		 logger.info("COMPARE operations: " +
				 app.repository.getMeasurementsByOperation("COMPARE").size());
		 logger.info("LengthUnit operations: " +
				 app.repository.getMeasurementsByType("LengthUnit").size());
		 logger.info("Pool stats: " + app.repository.getPoolStatistics());

		 // --- Cleanup (comment out to retain data across runs) ---
		 app.deleteAllMeasurements();
		 logger.info("After deleteAll, count: " + app.repository.getTotalCount());

		 app.closeResources();
		 logger.info("============================================================");
	 }
 }