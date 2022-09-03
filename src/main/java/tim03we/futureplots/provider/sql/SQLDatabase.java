package tim03we.futureplots.provider.sql;

/*
 * This software is distributed under "GNU General Public License v3.0".
 * This license allows you to use it and/or modify it but you are not at
 * all allowed to sell this plugin at any cost. If found doing so the
 * necessary action required would be taken.
 *
 * FuturePlots is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License v3.0 for more details.
 *
 * You should have received a copy of the GNU General Public License v3.0
 * along with this program. If not, see
 * <https://opensource.org/licenses/GPL-3.0>.
 */

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
