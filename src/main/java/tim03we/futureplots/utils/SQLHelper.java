package tim03we.futureplots.utils;

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
import java.sql.SQLException;

public class SQLHelper {

    private Connection connection;

    public SQLHelper(Connection connection) {
        this.connection = connection;
    }

    public boolean columnExists(String column) {
        boolean exists;
        try {
            connection.prepareStatement("SELECT " + column + " FROM plots;");
            connection.commit();
            exists = true;
        } catch (SQLException e) {
            exists = false;
        }
        return exists;
    }
}
