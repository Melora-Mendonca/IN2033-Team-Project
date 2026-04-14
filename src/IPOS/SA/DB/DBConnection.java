package IPOS.SA.DB;

import java.sql.*;
import java.util.Properties;

import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private Connection conn = null;

    public DBConnection() {
        conn = this.getConnection();
    }

    private Connection getConnection() {
        System.out.println("About to create a connection");
        Connection con = null;

        // Read from environment variables (Docker) or use defaults (local development)
        String userName = System.getenv().getOrDefault("DB_USER", "root");
        String pwd = System.getenv().getOrDefault("DB_PASSWORD", "Jordan04.");
        String dbName = System.getenv().getOrDefault("DB_NAME", "ipos_sa");
        String serverName = System.getenv().getOrDefault("DB_HOST", "localhost");
        String portNumber = System.getenv().getOrDefault("DB_PORT", "3306");

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

    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);

        // Set parameters
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeQuery();
    }

    public int update(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);

        // Set parameters
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeUpdate();
    }

    public Connection getConn() {
        return conn;
    }

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
