package C195.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class DB {
    private static Connection dbConnection;
    private static final String DBNAME = "U04cre";
    private static final String USERNAME = "U04cre";
    private static final String PASSWORD = "53688204115";
    private static final String URL = "jdbc:mysql://52.206.157.109/";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    public DB() {
    }

    public static void connectDB() {
        try {
            Class.forName(JDBC_DRIVER);
            dbConnection = DriverManager.getConnection(URL + DBNAME,USERNAME,PASSWORD);
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class Not Found: " + e.getMessage());
        } finally {
            System.out.println("Database connected.");
        }
    }

    public static Connection getDbConnection(){
        return dbConnection;
    }

    public static void disconnect() {
        try {
            dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Database disconnected.");
        }
    }
}
