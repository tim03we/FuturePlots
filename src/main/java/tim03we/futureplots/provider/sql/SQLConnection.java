package tim03we.futureplots.provider.sql;

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

    public SQLDatabase getDatabase(String database) {
        return new SQLDatabase(database, this);
    }
}
