package tim03we.chickenmc.lobbysystem.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLDatabase {

    private SQLConnection instance;

    public SQLDatabase(String database, SQLConnection instance) {
        this.instance = instance;
        try {
            PreparedStatement statement = instance.connection.prepareStatement("USE " + database + ";");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLTable getTable(String table) {
        return new SQLTable(table, this.instance);
    }
}
