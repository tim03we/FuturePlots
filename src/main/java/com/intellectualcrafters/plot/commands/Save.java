package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.jnbt.CompoundTag;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.*;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.SchematicHandler;
import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.general.commands.CommandDeclaration;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@CommandDeclaration(command = "save",
        aliases = {"backup"},
        description = "Save your plot",
        category = CommandCategory.SCHEMATIC,
        requiredType = RequiredType.NONE,
        permission = "plots.save")
public class Save extends SubCommand {

    @Override
    public boolean onCommand(final PlotPlayer player, String[] args) {
        String world = player.getLocation().getWorld();
        if (!PS.get().hasPlotArea(world)) {
            return !sendMessage(player, C.NOT_IN_PLOT_WORLD);
        }
        final Plot plot = player.getCurrentPlot();
        if (plot == null) {
            return !sendMessage(player, C.NOT_IN_PLOT);
        }
        if (!plot.hasOwner()) {
            MainUtil.sendMessage(player, C.PLOT_UNOWNED);
            return false;
        }
        if (!plot.isOwner(player.getUUID()) && !Permissions.hasPermission(player, C.PERMISSION_ADMIN_COMMAND_SAVE)) {
            MainUtil.sendMessage(player, C.NO_PLOT_PERMS);
            return false;
        }
        if (plot.getRunning() > 0) {
            MainUtil.sendMessage(player, C.WAIT_FOR_TIMER);
            return false;
        }
        plot.addRunning();
        SchematicHandler.manager.getCompoundTag(plot, new RunnableVal<CompoundTag>() {
            @Override
            public void run(final CompoundTag value) {
                TaskManager.runTaskAsync(new Runnable() {
                    @Override
                    public void run() {
                        String time = (System.currentTimeMillis() / 1000) + "";
                        Location[] corners = plot.getCorners();
                        corners[0].setY(0);
                        corners[1].setY(255);
                        int size = (corners[1].getX() - corners[0].getX()) + 1;
                        PlotId id = plot.getId();
                        String world = plot.getArea().toString().replaceAll(";", "-").replaceAll("[^A-Za-z0-9]", "");
                        final String file = time + '_' + world + '_' + id.x + '_' + id.y + '_' + size;
                        UUID uuid = player.getUUID();
                        SchematicHandler.manager.upload(value, uuid, file, new RunnableVal<URL>() {
                            @Override
                            public void run(URL url) {
                                plot.removeRunning();
                                if (url == null) {
                                    MainUtil.sendMessage(player, C.SAVE_FAILED);
                                    return;
                                }
                                MainUtil.sendMessage(player, C.SAVE_SUCCESS);
                                List<String> schematics = player.getMeta("plot_schematics");
                                if (schematics != null) {
                                    schematics.add(file + ".schematic");
                                }
                            }
                        });
                    }
                });
            }
        });
        return true;
    }
}
