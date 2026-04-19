package IPOS.SA.DB;

import java.sql.*;
import java.util.Properties;

import java.sql.*;
import java.util.Properties;
/**
 * Manages the MySQL database connection for IPOS-SA.
 * Provides helper methods for executing parameterised queries and updates.
 *
 * Connection credentials are read from environment variables with fallback
 * defaults for local development:
 * - DB_USER     (default: root)
 * - DB_PASSWORD (default: Karkala1998?)
 * - DB_NAME     (default: ipos_sa)
 * - DB_HOST     (default: localhost)
 * - DB_PORT     (default: 3306)
 *
 * The constructor retries the connection up to 5 times with a 3 second
 * delay between attempts to handle temporary database unavailability.
 */
public class DBConnection {

    private Connection conn = null;
    /**
     * Constructor; attempts to establish a database connection.
     * Retries up to 5 times with a 3 second delay between attempts.
     * Logs progress to the console.
     */
    public DBConnection() {
        int retries = 5; // A new connection is created with the database.
        // Multiple tries are given due to the project being deployed on docker, which easily disconnects from the sql server
        while (retries > 0) {
            conn = this.getConnection();
            if (conn != null) break;
            retries--;
            System.out.println("Retrying connection... " + retries + " attempts left");
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
        }
    }

    /**
     * Creates and returns a new MySQL database connection.
     * Reads connection details from environment variables,
     * falling back to local development defaults if not set.
     *
     * @return a new Connection, or null if the connection failed
     */
    private Connection getConnection() {
        System.out.println("About to create a connection");
        Connection con = null;

        String userName = System.getenv().getOrDefault("DB_USER", "root");
        String pwd = System.getenv().getOrDefault("DB_PASSWORD", "Karkala1998?");
        String dbName = System.getenv().getOrDefault("DB_NAME", "ipos_sa");
        String serverName = System.getenv().getOrDefault("DB_HOST", "localhost");
        String portNumber = System.getenv().getOrDefault("DB_PORT", "3306");

        // creates a connection with the MySQl database using the root credentials
        try {
            Properties connectionProps = new Properties();
            connectionProps.put("user", userName);
            connectionProps.put("password", pwd);
            connectionProps.put("useSSL", "false");
            connectionProps.put("allowPublicKeyRetrieval", "true");

            String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + dbName;
            System.out.println("Connecting to: " + url);

            con = DriverManager.getConnection(url, connectionProps);
            System.out.println("Successfully connected to database");

        } catch (SQLException sqle) {
            System.err.println("Database connection failed!");
            sqle.printStackTrace();
        }
        return con;
    }
    /**
     * Executes a parameterised SELECT query and returns the ResultSet.
     * Uses a PreparedStatement to safely bind the provided parameters.
     *
     * @param sql    the SQL query string with ? placeholders for parameters
     * @param params the values to bind to the query parameters in order
     * @return the ResultSet containing the query results
     * @throws SQLException if the query fails or the connection is unavailable
     */
    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);

        // Sets parameters
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeQuery();
    }
    /**
     * Executes a parameterised INSERT, UPDATE or DELETE statement.
     * Uses a PreparedStatement to safely bind the provided parameters.
     *
     * @param sql the SQL statement with ? placeholders for parameters
     * @param params the values to bind to the statement parameters in order
     * @return the number of rows affected by the statement
     * @throws SQLException if the statement fails or the connection is unavailable
     */
    public int update(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);

        // Sets parameters
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeUpdate();
    }
    /**
     * Returns the raw database connection.
     * Used when a direct Connection reference is needed,
     * for example when constructing PreparedStatements manually.
     *
     * @return the active Connection
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * Returns the raw database connection.
     * Used when a direct Connection reference is needed,
     * for example when constructing PreparedStatements manually.
     *
     * @return the active Connection
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }
}
