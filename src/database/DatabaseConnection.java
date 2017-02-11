package database;

import tools.Logger;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

public class DatabaseConnection {

    public static final int RETURN_GENERATED_KEYS = 1;
    private static final ThreadLocal<Connection> con = new DatabaseConnection.ThreadLocalConnection();

    public static final Connection getConnection() {
        return con.get();
    }

    public static final void closeAll() throws SQLException {
        for (final Connection con : DatabaseConnection.ThreadLocalConnection.allConnections)
            if (con != null)
                con.close();
    }

    private static final class ThreadLocalConnection extends ThreadLocal<Connection> {

        public static final Collection<Connection> allConnections = new LinkedList<>();

        private static String host = "localhost", user = "root", password = "", schema = "nexus117";

        @Override
        protected final Connection initialValue() {
            try {
                Class.forName("com.mysql.jdbc.Driver"); // Touch the mysql driver
            } catch (final ClassNotFoundException e) {
                System.err.println("Could not find the mysql driver" + e);
            }

            //Get from properties file
            try {
                Properties props = new Properties();
                FileInputStream fileInputStream = new FileInputStream("database.properties");
                props.load(fileInputStream);
                fileInputStream.close();

                host = props.getProperty("host");
                user = props.getProperty("user");
                password = props.getProperty("password");
                schema = props.getProperty("schema");
            } catch (Exception e){
                System.err.println("Could not find database.properties");
            }

            try {
                Logger.println("Attempting to connect to " + host + ":3306 as " + user + " to schema " + schema);
                Connection con = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + schema + "?autoReconnect=true", user, password);
                allConnections.add(con);
                return con;
            } catch (SQLException e) {
                System.err.println("MySQL Error: " + e);
                return null;
            }
        }
    }
}