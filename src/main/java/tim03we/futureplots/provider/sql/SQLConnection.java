package tim03we.futureplots.provider.sql;

import tim03we.futureplots.FuturePlots;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

    public Connection connection;

    public SQLConnection(String host, String port, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "?autoReconnect=true&useTimezone=true&serverTimezone=GMT%2B8", user, password);
            connection.setAutoCommit(true);
            System.out.println("Connection to MySQL database successful.");
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("No connection to the database could be established.");
            ex.printStackTrace();
        }
    }

    // SQLite
    public SQLConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + FuturePlots.getInstance().getDataFolder().getPath() + "/plots.db");
            connection.setAutoCommit(true);
            System.out.println("Connection to SQLite database successful.");
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("No connection to the database could be established.");
            ex.printStackTrace();
        }
    }

    // type = sqlite
    public SQLDatabase getDatabase(String type, String database) {
        return new SQLDatabase(type, database, this);
    }
}
