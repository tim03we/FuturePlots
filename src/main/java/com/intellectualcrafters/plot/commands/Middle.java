package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.general.commands.CommandDeclaration;

/**
 * @author manuelgu, altered by Citymonstret
 */
@CommandDeclaration(
        command = "middle",
        aliases = {"center", "centre"},
        description = "Teleports you to the center of the plot",
        usage = "/plot middle",
        category = CommandCategory.TELEPORT,
        requiredType = RequiredType.NONE)
public class Middle extends SubCommand {

    @Override
    public boolean onCommand(PlotPlayer player, String[] arguments) {
        Location location = player.getLocation();
        Plot plot = location.getPlot();
        if (plot == null) {
            return sendMessage(player, C.NOT_IN_PLOT);
        }
        if (!plot.hasOwner()) {
            return sendMessage(player, C.PLOT_UNOWNED);
        }
        player.teleport(plot.getCenter());
        return true;
    }
}
