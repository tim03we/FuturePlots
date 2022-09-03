package tim03we.futureplots.utils.xuid.provider;

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

import cn.nukkit.Server;

import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Settings;
import tim03we.futureplots.utils.xuid.provider.data.SQLConnection;
import tim03we.futureplots.utils.xuid.provider.data.SQLDatabase;
import tim03we.futureplots.utils.xuid.provider.data.SQLEntity;
import tim03we.futureplots.utils.xuid.provider.data.SQLTable;
import tim03we.futureplots.utils.xuid.web.PostXUID;

import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public class SQLiteProvider {

    private static SQLDatabase database;

    public void connect() {
        if(Server.getInstance().getPluginManager().getPlugin("DbLib") == null) {
            FuturePlots.getInstance().getLogger().error("[XUID] The plugin DbLib could not be found.");
            Server.getInstance().getPluginManager().disablePlugin(Server.getInstance().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        try {
            Class.forName("org.sqlite.JDBC");
            SQLConnection sqlConnection = new SQLConnection();
            database = sqlConnection.getDatabase("sqlite", "cache");
            checkAndRun();
            FuturePlots.getInstance().getLogger().info("[XUID] Connection to SQLite database successful.");
        } catch (ClassNotFoundException ex) {
            FuturePlots.getInstance().getLogger().error("[XUID] No connection to the database could be established.");
            ex.printStackTrace();
        }
    }

    private void checkAndRun() {
        CompletableFuture.runAsync(() -> {
            if (database.getConnection() != null) {
                try {
                    PreparedStatement statement = database.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS xuid(gamertag VARCHAR(255), xuid VARCHAR(255));");
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    if(Settings.debug) e.printStackTrace();
                }
            }
        });
    }

    public void updateEntry(String gamertag, String xuid) {
        SQLTable table = database.getTable("xuid");

        SQLEntity directFind = table.find(new SQLEntity("gamertag", gamertag).append("xuid", xuid), false, null);
        if(directFind != null) {
            return;
        }

        SQLEntity find = table.find(new SQLEntity("gamertag", gamertag), true, "gamertag");
        if(find != null) {
            if (!find.getString("xuid").equalsIgnoreCase(xuid)) {
                table.update(find, new SQLEntity("xuid", xuid));
                new PostXUID(xuid, gamertag).send();
                return;
            }
        }

        SQLEntity find2 = table.find(new SQLEntity("xuid", xuid), false, null);
        if(find2 != null) {
            if(!find2.getString("gamertag").equalsIgnoreCase(gamertag)) {
                table.update(find, new SQLEntity("gamertag", gamertag));
                new PostXUID(xuid, gamertag).send();
                return;
            }
        }
        table.insert(new SQLEntity("gamertag", gamertag).append("xuid", xuid));
        Settings.playerXuids.put(gamertag.toLowerCase(), xuid);
        new PostXUID(xuid, gamertag).send();
    }

    public String getGamertag(String xuid) {
        SQLTable table = database.getTable("xuid");
        SQLEntity find = table.find(new SQLEntity("xuid", xuid), false, null);
        if(find == null) return null;
        return find.getString("gamertag");
    }

    public String getXuid(String gamertag) {
        String cachedXuid = Settings.playerXuids.get(gamertag.toLowerCase());
        if(cachedXuid != null) {
            return cachedXuid;
        }
        SQLTable table = database.getTable("xuid");
        for (SQLEntity find : table.find()) {
            if(find.getString("gamertag").equalsIgnoreCase(gamertag)) {
                Settings.playerXuids.put(gamertag.toLowerCase(), find.getString("xuid"));
                return find.getString("xuid");
            }
        }
        return null;
    }

    public boolean exists(String gamertag) {
        return getXuid(gamertag) != null;
    }
}
