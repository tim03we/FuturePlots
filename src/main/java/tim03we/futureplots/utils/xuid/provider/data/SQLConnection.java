package tim03we.futureplots.utils.xuid.provider.data;

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

import tim03we.futureplots.FuturePlots;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

    public Connection connection;

    // SQLite
    public SQLConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + FuturePlots.getInstance().getDataFolder().getPath() + "/DONT_DELETE.db");
            connection.setAutoCommit(true);
            FuturePlots.getInstance().getLogger().info("[XUID] Connection to SQLite database successful.");
        } catch (SQLException | ClassNotFoundException ex) {
            FuturePlots.getInstance().getLogger().error("[XUID] No connection to the database could be established.");
            ex.printStackTrace();
        }
    }

    // type = sqlite
    public SQLDatabase getDatabase(String type, String database) {
        return new SQLDatabase(type, database, this);
    }
}
