package tim03we.futureplots.provider.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLDatabase {

    public String type;
    private String database;
    private SQLConnection instance;

    public SQLDatabase(String type, String database, SQLConnection instance) {
        this.type = type;
        this.database = database;
        this.instance = instance;
        if(type.equalsIgnoreCase("mysql")) set();
    }

    public void set() {
        try {
            PreparedStatement statement = instance.connection.prepareStatement("USE " + this.database + ";");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLTable getTable(String table) {
        return new SQLTable(table, this, this.instance);
    }

    public Connection getConnection() {
        return instance.connection;
    }
}
