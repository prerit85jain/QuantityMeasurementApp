package quantityMeasurement.utility;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ApplicationConfig {
    private static final Logger logger = Logger.getLogger(
            ApplicationConfig.class.getName()
    );
    private static ApplicationConfig instance;
    private Properties properties;
    private Environment environment;

    // Create enum for Environment to manage different environments like development,
    // testing, and production derived from system properties or environment variables
    public enum Environment {
        DEVELOPMENT, TESTING, PRODUCTION
    }

    // Creating Enum for all the configuration keys to avoid hardcoding and typos
    public enum ConfigKey {
        REPOSITORY_TYPE("repository.type"),
        DB_DRIVER_CLASS("db.driver"),
        DB_URL("db.url"),
        DB_USERNAME("db.username"),
        DB_PASSWORD("db.password"),
        DB_POOL_SIZE("db.pool-size"),
        HIKARI_MAX_POOL_SIZE("db.hikari.maximum-pool-size"),
        HIKARI_MIN_IDLE("db.hikari.minimum-idle"),
        HIKARI_CONNECTION_TIMEOUT("db.hikari.connection-timeout"),
        HIKARI_IDLE_TIMEOUT("db.hikari.idle-timeout"),
        HIKARI_MAX_LIFETIME("db.hikari.max-lifetime"),
        HIKARI_POOL_NAME("db.hikari.pool-name"),
        HIKARI_CONNECTION_TEST_QUERY("db.hikari.connection-test-query");

        private final String key;

        ConfigKey(String key) {this.key = key;}

        public String getKey() {return key;}
    }

    private ApplicationConfig() {loadConfiguration();
    }

    public static synchronized ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }

    /**
     * Loads configuration properties from a properties file, system properties, or
     * environment variables. The method first attempts to load from a properties file
     * named "application.properties" located in the classpath. If the file is not found,
     * it falls back to loading default values. The environment can be specified through
     * system properties or environment variables, and if not set, it defaults to
     * "development". The method also includes error handling to log any issues encountered
     * during the loading process and ensures that the application can still run with default
     * configurations if necessary.
     */
    private void loadConfiguration() {
        properties = new Properties();
        try {
            // Try to load from system property or default to application.properties
            String env = System.getProperty("app.env");
            if (env == null || env.isEmpty()) {
                env = System.getenv("APP_ENV");
            }

            String configFile = "application.properties";
            InputStream input = ApplicationConfig.class
                    .getClassLoader()
                    .getResourceAsStream(configFile);

            if (input != null) {
                properties.load(input);
                logger.info("Configuration loaded from " + configFile);
                // loading environment from properties file if not set in system properties or
                // environment variables
                if (env == null || env.isEmpty()) {
                    env = properties.getProperty("app.env", "development");
                }
                this.environment = Environment.valueOf(env.toUpperCase());
            } else {
                logger.warning("Configuration file not found, using defaults");
                loadDefaults();
            }
        } catch (Exception e) {
            logger.severe("Error loading configuration: " + e.getMessage());
            loadDefaults();
        }
    }

    /**
     * Loads default configuration values. This method is called when the configuration
     * file is not found or when there is an error loading the configuration. It sets
     * default values for database connection properties such as driver class, URL,
     * username, password, and connection pool size. These defaults are suitable for a
     * local H2 database, which can be used for testing and development purposes. The
     * method ensures that the application can still run with reasonable defaults even
     * if the configuration file is missing or cannot be loaded.
     */
    private void loadDefaults() {
        properties.setProperty(ConfigKey.DB_DRIVER_CLASS.getKey(),
                "org.h2.Driver");
        properties.setProperty(ConfigKey.DB_URL.getKey(),
                "jdbc:h2:mem:quantity_measurement_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
        properties.setProperty(ConfigKey.DB_USERNAME.getKey(),   "sa");
        properties.setProperty(ConfigKey.DB_PASSWORD.getKey(),   "");
        properties.setProperty(ConfigKey.DB_POOL_SIZE.getKey(),  "5");
        properties.setProperty(ConfigKey.REPOSITORY_TYPE.getKey(), "database");
        properties.setProperty(ConfigKey.HIKARI_CONNECTION_TEST_QUERY.getKey(), "SELECT 1");
        this.environment = Environment.DEVELOPMENT;
        logger.info("Loaded default configuration (H2 in-memory)");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getEnvironment() {
        return environment.name();
    }

    /**
     * Checks if the provided key is a valid configuration key defined in the
     * ConfigKey enum. This method can be used to validate configuration keys
     * before attempting to retrieve their values.
     *
     * @param key the configuration key to check
     * @return true if the key is a valid configuration key, false otherwise
     */
    public boolean isConfigKey(String key) {
        for (ConfigKey ck : ConfigKey.values()) {
            if (ck.getKey().equals(key)) return true;
        }
        return false;
    }

    /**
     * Print all configuration properties for debugging purposes. This method can be
     * called during application startup to log the loaded configuration, which can
     * help in troubleshooting configuration issues.
     */
    public void printAllProperties() {
        logger.info("=== Application Configuration ===");
        logger.info("Environment: " + getEnvironment());
        for (ConfigKey ck : ConfigKey.values()) {
            String val = getProperty(ck.getKey(), "(not set)");
            // mask password
            if (ck == ConfigKey.DB_PASSWORD && val != null && !val.isEmpty()) {
                val = "****";
            }
            logger.info(ck.getKey() + " = " + val);
        }
        logger.info("=================================");
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        ApplicationConfig config = ApplicationConfig.getInstance();
        config.printAllProperties();
    }
}