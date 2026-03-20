package IPOS.SA.DB;

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
        String userName   = "root";
        String pwd        = "Karkala1998?";
        String dbName     = "ipos_sa";
        String serverName = "localhost";
        int portNumber    = 3306;

        try {
            Properties connectionProps = new Properties();
            connectionProps.put("user", userName);
            connectionProps.put("password", pwd);

            con = DriverManager.getConnection(
                    "jdbc:mysql://" + serverName + ":" + portNumber + "/" + dbName,
                    connectionProps
            );

            System.out.println("Successfully connected to database");

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            return con;
        }
    }

    public Connection getConn() {
        return conn;
    }

    // Runs a SELECT query and returns a ResultSet
    public ResultSet query(String sql, Object... params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Runs an INSERT, UPDATE or DELETE and returns rows affected
    public int update(String sql, Object... params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Runs an INSERT and returns the generated key
    public int insert(String sql, Object... params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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
