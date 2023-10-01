package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.database.MySQL;
import com.intellectualcrafters.plot.database.SQLManager;
import com.intellectualcrafters.plot.database.SQLite;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.worlds.SinglePlotArea;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.general.commands.CommandDeclaration;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@CommandDeclaration(
        command = "database",
        aliases = {"convert"},
        category = CommandCategory.ADMINISTRATION,
        permission = "plots.database",
        description = "Convert/Backup Storage",
        requiredType = RequiredType.CONSOLE,
        usage = "/plot database [area] <sqlite|mysql|import>")
public class Database extends SubCommand {

    public static void insertPlots(final SQLManager manager, final List<Plot> plots, final PlotPlayer player) {
        TaskManager.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Plot> ps = new ArrayList<>();
                    for (Plot p : plots) {
                        ps.add(p);
                    }
                    MainUtil.sendMessage(player, "&6Starting...");
                    manager.createPlotsAndData(ps, new Runnable() {
                        @Override
                        public void run() {
                            MainUtil.sendMessage(player, "&6Database conversion finished!");
                            manager.close();
                        }
                    });
                } catch (Exception e) {
                    MainUtil.sendMessage(player, "Failed to insert plot objects, see stacktrace for info");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCommand(final PlotPlayer player, String[] args) {
        if (args.length < 1) {
            MainUtil.sendMessage(player, "/plot database [area] <sqlite|mysql|import>");
            return false;
        }
        List<Plot> plots;
        PlotArea area = PS.get().getPlotAreaByString(args[0]);
        if (area != null) {
            plots = PS.get().sortPlotsByTemp(area.getPlots());
            args = Arrays.copyOfRange(args, 1, args.length);
        } else {
            plots = PS.get().sortPlotsByTemp(PS.get().getPlots());
        }
        if (args.length < 1) {
            MainUtil.sendMessage(player, "/plot database [world] <sqlite|mysql|import>");
            MainUtil.sendMessage(player, "[arg] indicates an optional argument");
            return false;
        }
        try {
            com.intellectualcrafters.plot.database.Database implementation;
            String prefix = "";
            switch (args[0].toLowerCase()) {
                case "import":
                    if (args.length < 2) {
                        MainUtil.sendMessage(player, "/plot database import <sqlite file> [prefix]");
                        return false;
                    }
                    File file = MainUtil.getFile(PS.get().IMP.getDirectory(), args[1].endsWith(".db") ? args[1] : args[1] + ".db");
                    if (!file.exists()) {
                        MainUtil.sendMessage(player, "&6Database does not exist: " + file);
                        return false;
                    }
                    MainUtil.sendMessage(player, "&6Starting...");
                    implementation = new SQLite(file);
                    SQLManager manager = new SQLManager(implementation, args.length == 3 ? args[2] : "", true);
                    HashMap<String, HashMap<PlotId, Plot>> map = manager.getPlots();
                    plots = new ArrayList<>();
                    for (Entry<String, HashMap<PlotId, Plot>> entry : map.entrySet()) {
                        String areaname = entry.getKey();
                        PlotArea pa = PS.get().getPlotAreaByString(areaname);
                        if (pa != null) {
                            for (Entry<PlotId, Plot> entry2 : entry.getValue().entrySet()) {
                                Plot plot = entry2.getValue();
                                if (pa.getOwnedPlotAbs(plot.getId()) != null) {
                                    if (pa instanceof SinglePlotArea) {
                                        Plot newPlot = pa.getNextFreePlot(null, plot.getId());
                                        if (newPlot != null) {
                                            PlotId newId = newPlot.getId();
                                            PlotId id = plot.getId();
                                            File worldFile = new File(PS.imp().getWorldContainer(), id.toCommaSeparatedString());
                                            if (worldFile.exists()) {
                                                File newFile = new File(PS.imp().getWorldContainer(), newId.toCommaSeparatedString());
                                                worldFile.renameTo(newFile);
                                            }
                                            id.x = newId.x;
                                            id.y = newId.y;
                                            id.recalculateHash();
                                            plot.setArea(pa);
                                            plots.add(plot);
                                            continue;
                                        }
                                    }
                                    MainUtil.sendMessage(player, "Skipping duplicate plot: " + plot + " | id=" + plot.temp);
                                    continue;
                                }
                                plot.setArea(pa);
                                plots.add(plot);
                            }
                        } else {
                            HashMap<PlotId, Plot> plotmap = PS.get().plots_tmp.get(areaname);
                            if (plotmap == null) {
                                plotmap = new HashMap<>();
                                PS.get().plots_tmp.put(areaname, plotmap);
                            }
                            plotmap.putAll(entry.getValue());
                        }
                    }
                    DBFunc.createPlotsAndData(plots, new Runnable() {
                        @Override
                        public void run() {
                            MainUtil.sendMessage(player, "&6Database conversion finished!");
                        }
                    });
                    return true;
                case "mysql":
                    if (args.length < 6) {
                        return MainUtil.sendMessage(player, "/plot database mysql [host] [port] [username] [password] [database] {prefix}");
                    }
                    String host = args[1];
                    String port = args[2];
                    String username = args[3];
                    String password = args[4];
                    String database = args[5];
                    if (args.length > 6) {
                        prefix = args[6];
                    }
                    implementation = new MySQL(host, port, database, username, password);
                    break;
                case "sqlite":
                    if (args.length < 2) {
                        return MainUtil.sendMessage(player, "/plot database sqlite [file]");
                    }
                    File sqliteFile = MainUtil.getFile(PS.get().IMP.getDirectory(), args[1] + ".db");
                    implementation = new SQLite(sqliteFile);
                    break;
                default:
                    return MainUtil.sendMessage(player, "/plot database [sqlite/mysql]");
            }
            try {
                SQLManager manager = new SQLManager(implementation, prefix, true);
                Database.insertPlots(manager, plots, player);
                return true;
            } catch (ClassNotFoundException | SQLException e) {
                MainUtil.sendMessage(player, "$1Failed to save plots, read stacktrace for info");
                MainUtil.sendMessage(player, "&d==== Here is an ugly stacktrace, if you are interested in those things ===");
                e.printStackTrace();
                MainUtil.sendMessage(player, "&d==== End of stacktrace ====");
                MainUtil.sendMessage(player, "$1Please make sure you are using the correct arguments!");
                return false;
            }
        } catch (ClassNotFoundException | SQLException e) {
            MainUtil.sendMessage(player, "$1Failed to open connection, read stacktrace for info");
            MainUtil.sendMessage(player, "&d==== Here is an ugly stacktrace, if you are interested in those things ===");
            e.printStackTrace();
            MainUtil.sendMessage(player, "&d==== End of stacktrace ====");
            MainUtil.sendMessage(player, "$1Please make sure you are using the correct arguments!");
            return false;
        }
    }
}
