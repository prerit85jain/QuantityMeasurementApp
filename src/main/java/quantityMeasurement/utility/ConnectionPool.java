package quantityMeasurement.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConnectionPool {
    private static final Logger logger = Logger.getLogger(
            ConnectionPool.class.getName()
    );
    private static ConnectionPool instance;
    private List<Connection> availableConnections;
    private List<Connection> usedConnections;
    private final int poolSize;
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String driverClass;
    private final String testQuery;

    /**
     * Private constructor to initialize the connection pool based on the configuration.
     * @throws SQLException
     */
    private ConnectionPool() throws SQLException {
        ApplicationConfig config = ApplicationConfig.getInstance();
        this.driverClass  = config.getProperty(
                ApplicationConfig.ConfigKey.DB_DRIVER_CLASS.getKey(), "org.h2.Driver");
        this.dbUrl        = config.getProperty(
                ApplicationConfig.ConfigKey.DB_URL.getKey(),
                "jdbc:h2:mem:quantity_measurement_db;DB_CLOSE_DELAY=-1;MODE=MySQL");
        this.dbUsername   = config.getProperty(
                ApplicationConfig.ConfigKey.DB_USERNAME.getKey(), "sa");
        this.dbPassword   = config.getProperty(
                ApplicationConfig.ConfigKey.DB_PASSWORD.getKey(), "");
        this.poolSize     = config.getIntProperty(
                ApplicationConfig.ConfigKey.DB_POOL_SIZE.getKey(), 5);
        this.testQuery    = config.getProperty(
                ApplicationConfig.ConfigKey.HIKARI_CONNECTION_TEST_QUERY.getKey(), "SELECT 1");

        availableConnections = new ArrayList<>();
        usedConnections      = new ArrayList<>();

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC driver not found: " + driverClass, e);
        }
        initializeConnections();
        logger.info("ConnectionPool initialized with " + poolSize + " connections.");
    }

    /**
     * Get the singleton instance of the ConnectionPool.
     * @return the singleton instance of the ConnectionPool
     * @throws SQLException if there is an error initializing the connection pool
     */
    public static synchronized ConnectionPool getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    /**
     * Initializes the connection pool by creating the specified number of connections.
     * @throws SQLException if there is an error creating connections
     */
    private void initializeConnections() throws SQLException {
        for (int i = 0; i < poolSize; i++) {
            availableConnections.add(createConnection());
        }
    }

    /**
     * Creates a new database connection using the configured parameters.
     * @return a new database connection
     * @throws SQLException if there is an error creating the connection
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    /**
     * Acquires a connection from the pool. If no connections are available and the
     * pool has not reached its maximum size, a new connection will be created. If
     * the pool has reached its maximum size, an SQLException will be thrown.
     *
     * @return a database connection from the pool
     * @throws SQLException if there are no available connections and the pool has
     *         reached its maximum size
     */
    public synchronized Connection getConnection() throws SQLException {
        if (!availableConnections.isEmpty()) {
            Connection conn = availableConnections.remove(availableConnections.size() - 1);
            usedConnections.add(conn);
            logger.fine("Connection acquired. Available: " + availableConnections.size()
                    + ", Used: " + usedConnections.size());
            return conn;
        }
        if (usedConnections.size() < poolSize) {
            Connection conn = createConnection();
            usedConnections.add(conn);
            logger.info("New connection created. Total used: " + usedConnections.size());
            return conn;
        }
        throw new SQLException(
                "Connection pool exhausted. Max pool size: " + poolSize);
    }

    /**
     * Releases a connection back to the pool. The connection is moved from the used
     * connections list to the available connections list. If the connection is null,
     * it will be ignored.
     *
     * @param connection the database connection to be released back to the pool
     */
    public synchronized void releaseConnection(Connection connection) {
        if (connection == null) return;
        usedConnections.remove(connection);
        availableConnections.add(connection);
        logger.fine("Connection released. Available: " + availableConnections.size()
                + ", Used: " + usedConnections.size());
    }

    /**
     * Execute Test Query to validate a connection. This method can be used to check
     * if a connection is valid and can successfully communicate with the database.
     * It executes a simple query (e.g., "SELECT 1") and returns true if the query
     * executes successfully, indicating that the connection is valid. If the query fails,
     * it returns false, indicating that the connection is not valid. This method can be
     * useful for connection validation before using a connection from the pool.
     */
    public boolean validateConnection(Connection connection) {
        try (var stmt = connection.createStatement()) {
            stmt.execute(this.testQuery);
            return true;
        } catch (SQLException e) {
            logger.warning("Connection validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes all connections in the pool. This method should be called when the
     * application is shutting down to ensure that all database connections are properly
     * closed. It iterates through both the available and used connections and attempts
     * to close each one, logging any exceptions that occur during the closure process.
     */
    public synchronized void closeAll() {
        for (Connection conn : availableConnections) {
            try { conn.close(); } catch (SQLException e) {
                logger.warning("Error closing available connection: " + e.getMessage());
            }
        }
        for (Connection conn : usedConnections) {
            try { conn.close(); } catch (SQLException e) {
                logger.warning("Error closing used connection: " + e.getMessage());
            }
        }
        availableConnections.clear();
        usedConnections.clear();
        instance = null;
        logger.info("All connections closed.");
    }

    public int getAvailableConnectionCount() { return availableConnections.size(); }
    public int getUsedConnectionCount()      { return usedConnections.size(); }
    public int getTotalConnectionCount()     { return availableConnections.size() + usedConnections.size(); }

    /**
     * toString method for debugging purposes. This method provides a string
     * representation of the connection pool, including the number of available
     * and used connections. It can be useful for logging the state of the connection
     * pool during application execution, especially when monitoring connection
     * usage and debugging connection-related issues.
     *
     * @return a string representation of the connection pool, including the number of
     *         available and used connections
     */
    @Override
    public String toString() {
        return String.format("ConnectionPool[available=%d, used=%d, total=%d]",
                getAvailableConnectionCount(),
                getUsedConnectionCount(),
                getTotalConnectionCount());
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        try {
            ConnectionPool pool  = ConnectionPool.getInstance();
            Connection     conn1 = pool.getConnection();
            logger.info("Validate connection: " +
                    (pool.validateConnection(conn1) ? "Success" : "Failure"));
            logger.info("Available connections after acquiring 1: " +
                    pool.getAvailableConnectionCount());
            logger.info("Used connections after acquiring 1: " +
                    pool.getUsedConnectionCount());
            pool.releaseConnection(conn1);
            logger.info("Available connections after releasing 1: " +
                    pool.getAvailableConnectionCount());
            logger.info("Used connections after releasing 1: " +
                    pool.getUsedConnectionCount());
            pool.closeAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}