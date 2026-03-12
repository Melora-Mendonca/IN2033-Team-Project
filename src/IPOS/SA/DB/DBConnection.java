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
        String userName   = "root";
        String pwd        = "";       // your MySQL root password here
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
