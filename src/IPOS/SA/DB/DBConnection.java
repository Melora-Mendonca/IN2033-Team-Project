package IPOS.SA.DB;

import java.sql.*;
import java.util.Properties;

import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private Connection conn = null;

    // Retries up to 5 times with 3s delay; handles slow Docker container startup
    public DBConnection() {
        int retries = 5;
        while (retries > 0) {
            conn = this.getConnection();
            if (conn != null) break;
            retries--;
            System.out.println("Retrying connection... " + retries + " attempts left");
            try { Thread.sleep(3000); } catch (InterruptedException e) {}
        }
    }

    // Creates the database connection
    private Connection getConnection() {
        System.out.println("About to create a connection");
        Connection con = null;

        String userName = System.getenv().getOrDefault("DB_USER", "root");
        String pwd = System.getenv().getOrDefault("DB_PASSWORD", "Kamaal19");
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

    // Parameterised SELECT; use for all reads
    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);

        // Set parameters
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeQuery();
    }

    // Parameterised INSERT/UPDATE/DELETE; returns rows affected
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
