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
import com.google.gson.Gson;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.SQLHelper;
import tim03we.futureplots.utils.Settings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SQLiteProvider implements DataProvider {

    private static Connection connection;
    private static Statement statement;

    @Override
    public void connect() {
        if(Server.getInstance().getPluginManager().getPlugin("DbLib") == null) {
            Server.getInstance().getLogger().error("[FuturePlots] The plugin DbLib could not be found.");
            Server.getInstance().getPluginManager().disablePlugin(Server.getInstance().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + FuturePlots.getInstance().getDataFolder().getPath() + "/plots.db");
            connection.setAutoCommit(false);
            checkAndRun();
            Server.getInstance().getLogger().info("[FuturePlots] Connection to SQLite database successful.");
        } catch (SQLException | ClassNotFoundException ex) {
            Server.getInstance().getLogger().error("[FuturePlots] No connection to the database could be established.");
            ex.printStackTrace();
        }
    }

    private void checkAndRun() {
        CompletableFuture.runAsync(() -> {
            String[] array = new String[]{"home VARCHAR(999)"};
            if (connection != null) {
                try {
                    PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS plots(level VARCHAR(255), plotid VARCHAR(255), owner VARCHAR(255), helpers VARCHAR(999), members VARCHAR(999), denied VARCHAR(999), flags VARCHAR(999), mmerge VARCHAR(255), merges VARCHAR(999));");
                    statement.executeUpdate();
                    statement.close();

                    for (String s : array) {
                        String[] ex = s.split(" ");
                        if(!new SQLHelper(connection).columnExists(ex[0])) {
                            statement = connection.prepareStatement("ALTER TABLE plots ADD COLUMN " + s + ";");
                            statement.executeUpdate();
                            statement.close();
                            connection.commit();
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
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO plots (level, plotid, owner) VALUES (?, ?, ?)");
            statement.setObject(1, plot.getLevelName());
            statement.setObject(2, plot.getFullID());
            statement.setObject(3, name);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public void deletePlot(Plot plot) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM plots WHERE level = ? AND plotid = ?;");
            statement.setString(1, plot.getLevelName());
            statement.setString(2, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
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
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE plots SET owner = ? WHERE level = ? AND plotid = ?;");
            statement.setObject(1, name);
            statement.setObject(2, plot.getLevelName());
            statement.setObject(3, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public String getOwner(Plot plot) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE level = ? AND plotid = ?");
            statement.setString(1, plot.getLevelName());
            statement.setString(2, plot.getFullID());
            ResultSet result = statement.executeQuery();
            result.next();
            connection.commit();

            return result.getString("owner");
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
        return "none";
    }

    @Override
    public List<String> getHelpers(Plot plot) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE level = ? AND plotid = ?");
            statement.setString(1, plot.getLevelName());
            statement.setString(2, plot.getFullID());
            ResultSet result = statement.executeQuery();
            result.next();
            connection.commit();
            if(result.getString("helpers") == null) return new ArrayList<>();
            return new Gson().fromJson(result.getString("helpers"), List.class);
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getMembers(Plot plot) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE level = ? AND plotid = ?");
            statement.setString(1, plot.getLevelName());
            statement.setString(2, plot.getFullID());
            ResultSet result = statement.executeQuery();
            result.next();
            connection.commit();
            if(result.getString("members") == null) return new ArrayList<>();
            return new Gson().fromJson(result.getString("members"), List.class);
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getDenied(Plot plot) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE level = ? AND plotid = ?");
            statement.setString(1, plot.getLevelName());
            statement.setString(2, plot.getFullID());
            ResultSet result = statement.executeQuery();
            result.next();
            connection.commit();
            if(result.getString("denied") == null) return new ArrayList<>();
            return new Gson().fromJson(result.getString("denied"), List.class);
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addHelper(String name, Plot plot) {
        try {
            List<String> helpers = getHelpers(plot);
            helpers.add(name.toLowerCase());
            PreparedStatement statement = connection.prepareStatement("UPDATE plots SET helpers = ? WHERE level = ? AND plotid = ?;");
            statement.setString(1, new Gson().toJson(helpers));
            statement.setString(2, plot.getLevelName());
            statement.setString(3, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public void addMember(String name, Plot plot) {
        try {
            List<String> members = getMembers(plot);
            members.add(name.toLowerCase());
            PreparedStatement statement = connection.prepareStatement("UPDATE plots SET members = ? WHERE level = ? AND plotid = ?;");
            statement.setString(1, new Gson().toJson(members));
            statement.setString(2, plot.getLevelName());
            statement.setString(3, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public void addDenied(String name, Plot plot) {
        try {
            List<String> denied = getDenied(plot);
            denied.add(name.toLowerCase());
            PreparedStatement statement = connection.prepareStatement("UPDATE plots SET denied = ? WHERE level = ? AND plotid = ?;");
            statement.setString(1, new Gson().toJson(denied));
            statement.setString(2, plot.getLevelName());
            statement.setString(3, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public void removeHelper(String name, Plot plot) {
        try {
            List<String> helpers = getHelpers(plot);
            helpers.remove(name.toLowerCase());
            PreparedStatement statement = connection.prepareStatement("UPDATE plots SET helpers = ? WHERE level = ? AND plotid = ?;");
            statement.setString(1, new Gson().toJson(helpers));
            statement.setString(2, plot.getLevelName());
            statement.setString(3, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public void removeMember(String name, Plot plot) {
        try {
            List<String> members = getMembers(plot);
            members.remove(name.toLowerCase());
            PreparedStatement statement = connection.prepareStatement("UPDATE plots SET members = ? WHERE level = ? AND plotid = ?;");
            statement.setString(1, new Gson().toJson(members));
            statement.setString(2, plot.getLevelName());
            statement.setString(3, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public void removeDenied(String name, Plot plot) {
        try {
            List<String> denied = getDenied(plot);
            denied.remove(name.toLowerCase());
            PreparedStatement statement = connection.prepareStatement("UPDATE plots SET denied = ? WHERE level = ? AND plotid = ?;");
            statement.setString(1, new Gson().toJson(denied));
            statement.setString(2, plot.getLevelName());
            statement.setString(3, plot.getFullID());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
    }

    @Override
    public void setHome(Plot plot, Location location) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE plots SET home = ? WHERE level = ? AND plotid = ?;");
                statement.setString(1, location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch());
                statement.setString(2, plot.getLevelName());
                statement.setString(3, plot.getFullID());
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                if(Settings.debug) e.printStackTrace();
            }
        });
    }

    @Override
    public void deleteHome(Plot plot) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE plots SET home = ? WHERE level = ? AND plotid = ?;");
                statement.setString(1, null);
                statement.setString(2, plot.getLevelName());
                statement.setString(3, plot.getFullID());
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                if(Settings.debug) e.printStackTrace();
            }
        });
    }

    @Override
    public Location getHome(Plot plot) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots WHERE level = ? AND plotid = ?");
            statement.setString(1, plot.getLevelName());
            statement.setString(2, plot.getFullID());
            ResultSet result = statement.executeQuery();
            result.next();
            connection.commit();
            String locationString = result.getString("home");
            if(locationString != null) {
                String[] ex = result.getString("home").split(":");
                return new Location(Double.parseDouble(ex[0]), Double.parseDouble(ex[1]), Double.parseDouble(ex[2]), Double.parseDouble(ex[3]), Double.parseDouble(ex[4]), Server.getInstance().getLevelByName(plot.getLevelName()));
            }
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
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
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM plots;");
                connection.commit();
                while(resultSet.next()) {
                    if(resultSet.getString("level").equals(level) && resultSet.getString("owner").equals(name)) {
                        String[] plotid = resultSet.getString("plotid").split(";");
                        plots.add(level + ";" + plotid[0] + ";" + plotid[1]);
                    }
                }
            } catch (SQLException e) {
                if(Settings.debug) e.printStackTrace();
            }
        } else {
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM plots;");
                connection.commit();
                while(resultSet.next()) {
                    if(resultSet.getString("owner").equals(name)) {
                        String[] plotid = resultSet.getString("plotid").split(";");
                        plots.add(resultSet.getString("level") + ";" + plotid[0] + ";" + plotid[1]);
                    }
                }
            } catch (SQLException e) {
                if(Settings.debug) e.printStackTrace();
            }
        }
        return plots;
    }

    @Override
    public Plot getNextFreePlot(String level) {
        List<Plot> plots = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM plots;");
            connection.commit();
            while(resultSet.next()) {
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
        } catch (SQLException e) {
            if(Settings.debug) e.printStackTrace();
        }
        return null;
    }
}
