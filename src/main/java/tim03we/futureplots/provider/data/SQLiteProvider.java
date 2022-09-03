package tim03we.futureplots.provider.data;

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

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.provider.DataProvider;
import tim03we.futureplots.provider.sql.SQLConnection;
import tim03we.futureplots.provider.sql.SQLDatabase;
import tim03we.futureplots.provider.sql.SQLEntity;
import tim03we.futureplots.provider.sql.SQLTable;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;
import tim03we.futureplots.utils.Utils;
import tim03we.futureplots.utils.xuid.web.RequestXUID;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SQLiteProvider implements DataProvider {

    private static SQLDatabase database;

    @Override
    public void connect() {
        if(Server.getInstance().getPluginManager().getPlugin("DbLib") == null) {
            Server.getInstance().getLogger().error("[FuturePlots] The plugin DbLib could not be found.");
            Server.getInstance().getPluginManager().disablePlugin(Server.getInstance().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        Config config = FuturePlots.getInstance().getConfig();
        try {
            Class.forName("org.sqlite.JDBC");
            SQLConnection sqlConnection = new SQLConnection();
            database = sqlConnection.getDatabase("sqlite", config.getString("mysql.database"));
            checkAndRun();
            FuturePlots.getInstance().getLogger().info("[FuturePlots] Connection to SQLite database successful.");
        } catch (ClassNotFoundException ex) {
            FuturePlots.getInstance().getLogger().error("[FuturePlots] No connection to the database could be established.");
            ex.printStackTrace();
        }
    }

    private void checkAndRun() {
        CompletableFuture.runAsync(() -> {
            SQLTable table = database.getTable("plots");
            String[] array = new String[]{"home VARCHAR(999)", "merge VARCHAR(999)", "merges VARCHAR(999)", "merge_check VARCHAR(999)"};
            if (database.getConnection() != null) {
                try {
                    PreparedStatement statement = database.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS plots(level VARCHAR(255), plotid VARCHAR(255), owner VARCHAR(255), helpers VARCHAR(999), members VARCHAR(999), denied VARCHAR(999), flags VARCHAR(999), merge VARCHAR(999), merges VARCHAR(999), merge_check VARCHAR(999));");
                    statement.executeUpdate();
                    statement.close();

                    for (String s : array) {
                        String[] ex = s.split(" ");
                        if(!table.columnExists(ex[0])) {
                            statement = database.getConnection().prepareStatement("ALTER TABLE plots ADD COLUMN " + s + ";");
                            statement.executeUpdate();
                            statement.close();
                            database.getConnection().commit();
                        }
                    }
                } catch (Exception e) {
                    if(Settings.debug) e.printStackTrace();
                }
            }
        });
        checkData();
    }

    private void checkData() {
        FuturePlots.getInstance().getLogger().warning("[XUID] Start checking for missing XUIDs... During this check, no players can enter the server.");
        int missingXuid = 0;

        SQLTable table = database.getTable("plots");
        for (SQLEntity sqlEntity : table.find()) {
            String owner = sqlEntity.getString("owner");
            if(!Utils.isLong(owner) && owner.length() != 16 && !owner.equals("none")) {
                RequestXUID requestXUID = new RequestXUID(owner);
                String xuid = requestXUID.sendAndGetXuid();
                table.update(sqlEntity, new SQLEntity("owner", xuid));
                missingXuid++;
                FuturePlots.getInstance().getLogger().info("[XUID] " + owner + " has been converted to an XUID..");
                FuturePlots.xuidProvider.updateEntry(owner, xuid);
            }
            List<String> newList = new Gson().fromJson(sqlEntity.getString("helpers"), List.class);
            for (String key : newList) {
                 if(!Utils.isLong(key) && key.length() != 16 && !owner.equals("none")) {
                     RequestXUID requestXUID = new RequestXUID(key);
                     String xuid = requestXUID.sendAndGetXuid();
                     if(xuid != null) {
                         newList.remove(key);
                         newList.add(xuid);
                         missingXuid++;
                         FuturePlots.getInstance().getLogger().info("[XUID] " + key + " has been converted to an XUID..");
                         FuturePlots.xuidProvider.updateEntry(key, xuid);
                     }
                 }
            }
            table.update(new SQLEntity("level", sqlEntity.getString("level")).append("plotid", sqlEntity.getString("plotid")), new SQLEntity("helpers", new Gson().toJson(newList)));

            newList = new Gson().fromJson(sqlEntity.getString("members"), List.class);
            for (String key : newList) {
                if(!Utils.isLong(key) && key.length() != 16 && !owner.equals("none")) {
                    RequestXUID requestXUID = new RequestXUID(key);
                    String xuid = requestXUID.sendAndGetXuid();
                    if(xuid != null) {
                        newList.remove(key);
                        newList.add(xuid);
                        missingXuid++;
                        FuturePlots.getInstance().getLogger().info("[XUID] " + key + " has been converted to an XUID..");
                        FuturePlots.xuidProvider.updateEntry(key, xuid);
                    }
                }
            }
            table.update(new SQLEntity("level", sqlEntity.getString("level")).append("plotid", sqlEntity.getString("plotid")), new SQLEntity("members", new Gson().toJson(newList)));

            newList = new Gson().fromJson(sqlEntity.getString("denied"), List.class);
            for (String key : newList) {
                if(!Utils.isLong(key) && key.length() != 16 && !owner.equals("none")) {
                    RequestXUID requestXUID = new RequestXUID(key);
                    String xuid = requestXUID.sendAndGetXuid();
                    if(xuid != null) {
                        newList.remove(key);
                        newList.add(xuid);
                        missingXuid++;
                        FuturePlots.getInstance().getLogger().info("[XUID] " + key + " has been converted to an XUID..");
                        FuturePlots.xuidProvider.updateEntry(key, xuid);
                    }
                }
            }
            table.update(new SQLEntity("level", sqlEntity.getString("level")).append("plotid", sqlEntity.getString("plotid")), new SQLEntity("denied", new Gson().toJson(newList)));
        }
        FuturePlots.getInstance().getLogger().warning("[XUID] " + missingXuid + " names were converted to XUID. The server can now be accessed.");
        Settings.joinServer = true;
    }

    @Override
    public void save() {
    }

    @Override
    public boolean claimPlot(String playerId, Plot plot) {
        CompletableFuture.runAsync(() -> {
            SQLTable table = database.getTable("plots");
            SQLEntity insertEntity = new SQLEntity("level", plot.getLevelName());
            insertEntity.append("plotid", plot.getFullID());
            if(getMerges(plot).size() > 0) {
                table.update(insertEntity, new SQLEntity("owner", playerId));
            } else {
                insertEntity.append("owner", playerId);
                table.insert(insertEntity);
            }
        });
        return true;
    }

    @Override
    public void deletePlot(Plot plot) {
        CompletableFuture.runAsync(() -> {
            SQLTable table = database.getTable("plots");
            SQLEntity deleteEntity = new SQLEntity("level", plot.getLevelName());
            deleteEntity.append("plotid", plot.getFullID());
            table.delete(deleteEntity);
        });
    }

    @Override
    public void disposePlot(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity disposeEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        table.update(disposeEntity, new SQLEntity("owner", "none")
                .append("helpers", "")
                .append("members", "")
                .append("denied", "")
                .append("flags", "")
                .append("home", ""));
    }

    @Override
    public boolean isHelper(String playerId, Plot plot) {
        return getHelpers(plot) != null && getHelpers(plot).contains(playerId);
    }

    @Override
    public boolean isDenied(String playerId, Plot plot) {
        return getDenied(plot) != null && getDenied(plot).contains(playerId);
    }

    @Override
    public boolean isMember(String playerId, Plot plot) {
        return getMembers(plot) != null && getMembers(plot).contains(playerId);
    }

    @Override
    public boolean hasOwner(Plot plot) {
        return !getOwner(plot).equals("none");
    }

    @Override
    public void setOwner(String playerId, Plot plot) {
        CompletableFuture.runAsync(() -> {
            SQLTable table = database.getTable("plots");
            SQLEntity updateEntity = new SQLEntity("owner", playerId);
            table.update(new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID()), updateEntity);
        });
    }

    @Override
    public String getOwner(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        if(find != null) {
            return find.getString("owner");
        }
        return "none";
    }

    @Override
    public List<String> getHelpers(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        if(find != null) {
            if(find.getString("helpers") == null) return new ArrayList<>();
            return new Gson().fromJson(find.getString("helpers"), List.class);
        }
        return null;
    }

    @Override
    public List<String> getMembers(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        if(find != null) {
            if(find.getString("members") == null) return new ArrayList<>();
            return new Gson().fromJson(find.getString("members"), List.class);
        }
        return null;
    }

    @Override
    public List<String> getDenied(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        if(find != null) {
            if(find.getString("denied") == null) return new ArrayList<>();
            return new Gson().fromJson(find.getString("denied"), List.class);
        }
        return null;
    }

    @Override
    public void addHelper(String playerId, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.add(playerId.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("helpers", new Gson().toJson(helpers));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void addMember(String playerId, Plot plot) {
        List<String> members = getMembers(plot);
        members.add(playerId.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("members", new Gson().toJson(members));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void addDenied(String playerId, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.add(playerId.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("denied", new Gson().toJson(denied));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void removeHelper(String playerId, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.remove(playerId.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("helpers", new Gson().toJson(helpers));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void removeMember(String playerId, Plot plot) {
        List<String> members = getMembers(plot);
        members.remove(playerId.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("members", new Gson().toJson(members));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void removeDenied(String playerId, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.remove(playerId.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("denied", new Gson().toJson(denied));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void setHome(Plot plot, Location location) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        String locationString = location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
        SQLEntity updateEntity = new SQLEntity("home", locationString);
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void deleteHome(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("home", null);
        table.update(searchEntity, updateEntity);
    }

    @Override
    public Location getHome(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        if(find != null) {
            String locationString = find.getString("home");
            if(locationString != null) {
                String[] ex = find.getString("home").split(":");
                return new Location(Double.parseDouble(ex[0]), Double.parseDouble(ex[1]), Double.parseDouble(ex[2]), Double.parseDouble(ex[3]), Double.parseDouble(ex[4]), Server.getInstance().getLevelByName(plot.getLevelName()));
            }
        }
        return null;
    }

    @Override
    public Plot getPlot(String playerId, Object number, Object level) {
        int i = 1;
        if(number != null && (int) number > 0) {
            i = (int) number;
        }
        List<Plot> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : getPlots(playerId, level)) {
                String[] ex = plot.split(";");
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }

        } else {
            for (String plot : getPlots(playerId, null)) {
                String[] ex = plot.split(";");
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }
        }
        if((i - 1) >= plots.size()) return null;
        if(plots.size() > 0) {
            Plot plot = plots.get(i - 1);
            return plot;
        }
        return null;
    }

    @Override
    public List<String> getPlots(String playerId, Object level) {
        List<String> plots = new ArrayList<>();
        if(level != null) {
            SQLTable table = database.getTable("plots");
            for (SQLEntity resultSet : table.find()) {
                if(resultSet.getString("level").equals(level) && resultSet.getString("owner").equals(playerId)) {
                    String[] plotid = resultSet.getString("plotid").split(";");
                    plots.add(level + ";" + plotid[0] + ";" + plotid[1]);
                }
            }
        } else {
            SQLTable table = database.getTable("plots");
            for (SQLEntity resultSet : table.find()) {
                if(resultSet.getString("owner").equals(playerId)) {
                    String[] plotid = resultSet.getString("plotid").split(";");
                    plots.add(resultSet.getString("level") + ";" + plotid[0] + ";" + plotid[1]);
                }
            }
        }
        return plots;
    }

    @Override
    public Plot getNextFreePlot(String level) {
        List<Plot> plots = new ArrayList<>();
        SQLTable table = database.getTable("plots");
        for (SQLEntity resultSet : table.find()) {
            if(resultSet.getString("level").equals(level)) {
                String[] plotid = resultSet.getString("plotid").split(";");
                plots.add(new Plot(Integer.parseInt(plotid[0]), Integer.parseInt(plotid[1]), level));
            }
        }
        if (plots.size() == 0) return new Plot(0, 0, level);
        int lastX = 0;
        int lastZ = 0;

        for (Plot plot : plots) {
            int x = plot.getX() - lastX;
            int y = plot.getZ() - lastZ;
            int diff = Math.abs(x * y);
            if (diff < 4) {
                lastX = plot.getX();
                lastZ = plot.getZ();

                Plot find = new Plot(plot.getX() + 1, plot.getZ(), level);
                if (!hasOwner(find)) return find;
                find = new Plot(plot.getX(), plot.getZ() + 1, level);
                if (!hasOwner(find)) return find;
                find = new Plot(plot.getX() - 1, plot.getZ(), level);
                if (!hasOwner(find)) return find;
                find = new Plot(plot.getX(), plot.getZ() - 1, level);
                if (!hasOwner(find)) return find;
            }
        }
        return getNextFreePlot(level);
    }

    @Override
    public void setMergeCheck(Plot plot, Plot mergePlot) {
        List<String> mergeList = new ArrayList<>();
        for (Plot plots : getMergeCheck(plot)) {
            mergeList.add(plots.getX() + ";" + plots.getZ());
        }
        mergeList.add(mergePlot.getFullID());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("merge_check", new Gson().toJson(mergeList));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public List<Plot> getMergeCheck(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        List<Plot> list = new ArrayList<>();
        if(find != null) {
            if(find.getString("merge_check") == null) return new ArrayList<>();
            List<String> plotList = new Gson().fromJson(find.getString("merge_check"), List.class);
            for (String plots : plotList) {
                String[] ex = plots.split(";");
                list.add(new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName()));
            }
        }
        return list;
    }

    @Override
    public Plot getOriginPlot(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        if(find != null) {
            if(find.getString("merge") == null) return null;
            String[] ex = find.getString("merge").split(";");
            return new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName());
        }
        return null;
    }

    @Override
    public void setOriginPlot(Plot mergePlot, Plot originPlot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", mergePlot.getLevelName()).append("plotid", mergePlot.getFullID());
        SQLEntity updateEntity = new SQLEntity("merge", originPlot.getFullID());
        table.update(searchEntity, updateEntity);
    }


    @Override
    public void addMerge(Plot originPlot, Plot mergePlot) {
        List<String> mergeList = new ArrayList<>();
        for (Plot plots : getMerges(originPlot)) {
            mergeList.add(plots.getX() + ";" + plots.getZ());
        }
        mergeList.add(mergePlot.getFullID());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", originPlot.getLevelName()).append("plotid", originPlot.getFullID());
        SQLEntity updateEntity = new SQLEntity("merges", new Gson().toJson(mergeList));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public List<Plot> getMerges(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity, false, null);
        List<Plot> list = new ArrayList<>();
        if(find != null) {
            if(find.getString("merges") == null) return new ArrayList<>();
            List<String> plotList = new Gson().fromJson(find.getString("merges"), List.class);
            for (String plots : plotList) {
                String[] ex = plots.split(";");
                list.add(new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName()));
            }
        }
        return list;
    }

    @Override
    public void resetMerges(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("home", null).append("merge", null)
                .append("merges", null)
                .append("merge_check", null);
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void deleteMergeList(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("merges", null);
        table.update(searchEntity, updateEntity);
    }
}
