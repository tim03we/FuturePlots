package tim03we.futureplots.provider.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLDatabase {

    private String database;
    private SQLConnection instance;

    public SQLDatabase(String database, SQLConnection instance) {
        this.database = database;
        this.instance = instance;
        set();
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
