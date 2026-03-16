package quantityMeasurement.repository;

import quantityMeasurement.entity.QuantityMeasurementEntity;
import quantityMeasurement.exeption.DatabaseException;
import quantityMeasurement.utility.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

    private static final Logger logger =
            Logger.getLogger(QuantityMeasurementDatabaseRepository.class.getName());

    private static QuantityMeasurementDatabaseRepository instance;

    private static final String INSERT_QUERY =
            "INSERT INTO quantity_measurement_entity " +
                    "(this_value, this_unit, this_measurement_type, that_value, that_unit, " +
                    "that_measurement_type, operation, result_value, result_unit, " +
                    "result_measurement_type, result_string, is_error, error_message, " +
                    "created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

    private static final String SELECT_ALL_QUERY =
            "SELECT * FROM quantity_measurement_entity ORDER BY created_at DESC";

    private static final String SELECT_BY_OPERATION =
            "SELECT * FROM quantity_measurement_entity WHERE operation = ? ORDER BY created_at DESC";

    private static final String SELECT_BY_MEASUREMENT_TYPE =
            "SELECT * FROM quantity_measurement_entity " +
                    "WHERE this_measurement_type = ? ORDER BY created_at DESC";

    private static final String DELETE_ALL_QUERY =
            "DELETE FROM quantity_measurement_entity";

    private static final String COUNT_QUERY =
            "SELECT COUNT(*) FROM quantity_measurement_entity";

    private ConnectionPool connectionPool;

    private QuantityMeasurementDatabaseRepository() {
        try {
            this.connectionPool = ConnectionPool.getInstance();
            initializeDatabase();
            logger.info("QuantityMeasurementDatabaseRepository initialized.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database repository", e);
        }
    }

    private void initializeDatabase() {
        String sql =
                "CREATE TABLE IF NOT EXISTS quantity_measurement_entity (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "this_value DOUBLE NOT NULL," +
                        "this_unit VARCHAR(50) NOT NULL," +
                        "this_measurement_type VARCHAR(50) NOT NULL," +
                        "that_value DOUBLE," +
                        "that_unit VARCHAR(50)," +
                        "that_measurement_type VARCHAR(50)," +
                        "operation VARCHAR(20) NOT NULL," +
                        "result_value DOUBLE," +
                        "result_unit VARCHAR(50)," +
                        "result_measurement_type VARCHAR(50)," +
                        "result_string VARCHAR(100)," +
                        "is_error BOOLEAN NOT NULL DEFAULT FALSE," +
                        "error_message VARCHAR(500)," +
                        "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = connectionPool.getConnection();
            stmt = conn.createStatement();
            stmt.execute(sql);
            logger.info("Database schema initialized successfully.");
        } catch (SQLException e) {
            logger.severe("Error initializing schema: " + e.getMessage());
        } finally {
            closeResources(stmt, conn);
        }
    }

    public static synchronized QuantityMeasurementDatabaseRepository getInstance() {
        if (instance == null) instance = new QuantityMeasurementDatabaseRepository();
        return instance;
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(INSERT_QUERY);
            stmt.setDouble( 1,  entity.thisValue);
            stmt.setString( 2,  entity.thisUnit);
            stmt.setString( 3,  entity.thisMeasurementType);
            stmt.setDouble( 4,  entity.thatValue);
            stmt.setString( 5,  entity.thatUnit);
            stmt.setString( 6,  entity.thatMeasurementType);
            stmt.setString( 7,  entity.operation);
            stmt.setDouble( 8,  entity.resultValue);
            stmt.setString( 9,  entity.resultUnit);
            stmt.setString( 10, entity.resultMeasurementType);
            stmt.setString( 11, entity.resultString);
            stmt.setBoolean(12, entity.isError);
            stmt.setString( 13, entity.errorMessage);
            stmt.executeUpdate();
            logger.fine("Saved: [" + entity.operation + "] " + entity.thisValue + " " + entity.thisUnit);
        } catch (SQLException e) {
            throw DatabaseException.queryFailed("INSERT entity", e);
        } finally {
            closeResources(stmt, conn);
        }
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        Connection conn = null;
        Statement  stmt = null;
        ResultSet rs   = null;
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            stmt = conn.createStatement();
            rs   = stmt.executeQuery(SELECT_ALL_QUERY);
            while (rs.next()) results.add(mapResultSetToEntity(rs));
            logger.fine("Retrieved " + results.size() + " measurements.");
        } catch (SQLException e) {
            throw DatabaseException.queryFailed("SELECT ALL", e);
        } finally {
            closeResources(rs, stmt, conn);
        }
        return results;
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_OPERATION);
            stmt.setString(1, operation);
            rs = stmt.executeQuery();
            while (rs.next()) results.add(mapResultSetToEntity(rs));
            logger.fine("Retrieved " + results.size() + " by operation: " + operation);
        } catch (SQLException e) {
            throw DatabaseException.queryFailed("SELECT BY OPERATION: " + operation, e);
        } finally {
            closeResources(rs, stmt, conn);
        }
        return results;
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        try {
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_MEASUREMENT_TYPE);
            stmt.setString(1, measurementType);
            rs = stmt.executeQuery();
            while (rs.next()) results.add(mapResultSetToEntity(rs));
            logger.fine("Retrieved " + results.size() + " by type: " + measurementType);
        } catch (SQLException e) {
            throw DatabaseException.queryFailed("SELECT BY TYPE: " + measurementType, e);
        } finally {
            closeResources(rs, stmt, conn);
        }
        return results;
    }

    @Override
    public int getTotalCount() {
        Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        try {
            conn = connectionPool.getConnection();
            stmt = conn.createStatement();
            rs   = stmt.executeQuery(COUNT_QUERY);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw DatabaseException.queryFailed("COUNT", e);
        } finally {
            closeResources(rs, stmt, conn);
        }
        return 0;
    }

    @Override
    public void deleteAll() {
        Connection conn = null;
        Statement  stmt = null;
        try {
            conn = connectionPool.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(DELETE_ALL_QUERY);
            logger.info("All measurements deleted from database.");
        } catch (SQLException e) {
            throw DatabaseException.queryFailed("DELETE ALL", e);
        } finally {
            closeResources(stmt, conn);
        }
    }

    @Override
    public String getPoolStatistics() { return connectionPool.toString(); }

    @Override
    public void releaseResources() {
        connectionPool.closeAll();
        instance = null;
        logger.info("DatabaseRepository resources released.");
    }

    private QuantityMeasurementEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity(null, null, "");
        e.thisValue             = rs.getDouble("this_value");
        e.thisUnit              = rs.getString("this_unit");
        e.thisMeasurementType   = rs.getString("this_measurement_type");
        e.thatValue             = rs.getDouble("that_value");
        e.thatUnit              = rs.getString("that_unit");
        e.thatMeasurementType   = rs.getString("that_measurement_type");
        e.operation             = rs.getString("operation");
        e.resultValue           = rs.getDouble("result_value");
        e.resultUnit            = rs.getString("result_unit");
        e.resultMeasurementType = rs.getString("result_measurement_type");
        e.resultString          = rs.getString("result_string");
        e.isError               = rs.getBoolean("is_error");
        e.errorMessage          = rs.getString("error_message");
        return e;
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) try { rs.close(); } catch (SQLException e) {
            logger.warning("Error closing ResultSet: " + e.getMessage()); }
        closeResources(stmt, conn);
    }

    private void closeResources(Statement stmt, Connection conn) {
        if (stmt != null) try { stmt.close(); } catch (SQLException e) {
            logger.warning("Error closing Statement: " + e.getMessage()); }
        if (conn != null) connectionPool.releaseConnection(conn);
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        try {
            QuantityMeasurementDatabaseRepository repo = getInstance();
            logger.info("Total count: " + repo.getTotalCount());
            logger.info("Pool stats: "  + repo.getPoolStatistics());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}