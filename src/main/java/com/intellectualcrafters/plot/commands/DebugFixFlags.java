package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.WorldUtil;
import com.plotsquared.general.commands.Argument;
import com.plotsquared.general.commands.CommandDeclaration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@CommandDeclaration(
        command = "debugfixflags",
        usage = "/plot debugfixflags <world>",
        permission = "plots.debugfixflags",
        description = "Attempt to fix all flags for a world",
        requiredType = RequiredType.CONSOLE,
        category = CommandCategory.DEBUG)
public class DebugFixFlags extends SubCommand {

    public DebugFixFlags() {
        super(Argument.String);
    }

    @Override
    public boolean onCommand(PlotPlayer player, String[] args) {
        PlotArea area = PS.get().getPlotAreaByString(args[0]);
        if (area == null || !WorldUtil.IMP.isWorld(area.worldname)) {
            MainUtil.sendMessage(player, C.NOT_VALID_PLOT_WORLD, args[0]);
            return false;
        }
        MainUtil.sendMessage(player, "&8--- &6Starting task &8 ---");
        for (Plot plot : area.getPlots()) {
            HashMap<Flag<?>, Object> flags = plot.getFlags();
            Iterator<Entry<Flag<?>, Object>> i = flags.entrySet().iterator();
            boolean changed = false;
            while (i.hasNext()) {
                if (i.next().getKey() == null) {
                    changed = true;
                    i.remove();
                }
            }
            if (changed) {
                DBFunc.setFlags(plot, plot.getFlags());
            }
        }
        MainUtil.sendMessage(player, "&aDone!");
        return true;
    }
}
