package tim03we.futureplots.provider;

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
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.provider.sql.SQLConnection;
import tim03we.futureplots.provider.sql.SQLDatabase;
import tim03we.futureplots.provider.sql.SQLEntity;
import tim03we.futureplots.provider.sql.SQLTable;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MySQLProvider implements DataProvider {

    private SQLDatabase database;

    @Override
    public void connect() {
        if(Server.getInstance().getPluginManager().getPlugin("DbLib") == null) {
            Server.getInstance().getLogger().error("[FuturePlots] The plugin DbLib could not be found.");
            Server.getInstance().getPluginManager().disablePlugin(Server.getInstance().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        Config config = FuturePlots.getInstance().getConfig();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            SQLConnection sqlConnection = new SQLConnection(
                    config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.user"),
                    config.getString("mysql.password")
            );
            database = sqlConnection.getDatabase(config.getString("mysql.database"));
            checkAndRun();
            Server.getInstance().getLogger().info("[FuturePlots] Connection to MySQL database successful.");
        } catch (ClassNotFoundException ex) {
            Server.getInstance().getLogger().error("[FuturePlots] No connection to the database could be established.");
            ex.printStackTrace();
        }
    }

    private void checkAndRun() {
        CompletableFuture.runAsync(() -> {
            SQLTable table = database.getTable("plots");
            String[] array = new String[]{"home VARCHAR(999)"};
            if (database.getConnection() != null) {
                try {
                    PreparedStatement statement = database.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS plots(level VARCHAR(255), plotid VARCHAR(255), owner VARCHAR(255), helpers VARCHAR(999), members VARCHAR(999), denied VARCHAR(999), flags VARCHAR(999), mmerge VARCHAR(255), merges VARCHAR(999));");
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
    }

    @Override
    public void save() {
    }

    @Override
    public void claimPlot(String name, Plot plot) {
        CompletableFuture.runAsync(() -> {
            SQLTable table = database.getTable("plots");
            SQLEntity insertEntity = new SQLEntity("level", plot.getLevelName());
            insertEntity.append("plotid", plot.getFullID());
            insertEntity.append("owner", name);
            table.insert(insertEntity);
        });
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
    public boolean isHelper(String name, Plot plot) {
        return getHelpers(plot) != null && getHelpers(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean isDenied(String name, Plot plot) {
        return getDenied(plot) != null && getDenied(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean isMember(String name, Plot plot) {
        return getMembers(plot) != null && getMembers(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean hasOwner(Plot plot) {
        return !getOwner(plot).equals("none");
    }

    @Override
    public void setOwner(String name, Plot plot) {
        CompletableFuture.runAsync(() -> {
            SQLTable table = database.getTable("plots");
            SQLEntity updateEntity = new SQLEntity("owner", name);
            table.update(new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID()), updateEntity);
        });
    }

    @Override
    public String getOwner(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity);
        if(find != null) {
            return find.getString("owner");
        }
        return "none";
    }

    @Override
    public List<String> getHelpers(Plot plot) {
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity find = table.find(searchEntity);
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
        SQLEntity find = table.find(searchEntity);
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
        SQLEntity find = table.find(searchEntity);
        if(find != null) {
            if(find.getString("denied") == null) return new ArrayList<>();
            return new Gson().fromJson(find.getString("denied"), List.class);
        }
        return null;
    }

    @Override
    public void addHelper(String name, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.add(name.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("helpers", new Gson().toJson(helpers));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void addMember(String name, Plot plot) {
        List<String> members = getMembers(plot);
        members.add(name.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("members", new Gson().toJson(members));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void addDenied(String name, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.add(name.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("denied", new Gson().toJson(denied));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void removeHelper(String name, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.remove(name.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("helpers", new Gson().toJson(helpers));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void removeMember(String name, Plot plot) {
        List<String> members = getMembers(plot);
        members.remove(name.toLowerCase());
        SQLTable table = database.getTable("plots");
        SQLEntity searchEntity = new SQLEntity("level", plot.getLevelName()).append("plotid", plot.getFullID());
        SQLEntity updateEntity = new SQLEntity("members", new Gson().toJson(members));
        table.update(searchEntity, updateEntity);
    }

    @Override
    public void removeDenied(String name, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.remove(name.toLowerCase());
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
        SQLEntity find = table.find(searchEntity);
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
    public Plot getPlot(String name, Object number, Object level) {
        int i = 1;
        if(number != null && (int) number > 0) {
            i = (int) number;
        }
        List<Plot> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : getPlots(name, level)) {
                String[] ex = plot.split(";");
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }

        } else {
            for (String plot : getPlots(name, null)) {
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
    public List<String> getPlots(String name, Object level) {
        List<String> plots = new ArrayList<>();
        if(level != null) {
            SQLTable table = database.getTable("plots");
            for (SQLEntity resultSet : table.find()) {
                if(resultSet.getString("level").equals(level)) {
                    String[] plotid = resultSet.getString("plotid").split(";");
                    plots.add(level + ";" + plotid[0] + ";" + plotid[1]);
                }
            }
        } else {
            SQLTable table = database.getTable("plots");
            for (SQLEntity resultSet : table.find()) {
                String[] plotid = resultSet.getString("plotid").split(";");
                plots.add(resultSet.getString("level") + ";" + plotid[0] + ";" + plotid[1]);
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
}
