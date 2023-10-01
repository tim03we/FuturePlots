package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.configuration.ConfigurationSection;
import com.intellectualcrafters.configuration.MemorySection;
import com.intellectualcrafters.configuration.file.YamlConfiguration;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.MainUtil;
import com.plotsquared.general.commands.CommandDeclaration;

import java.io.IOException;
import java.util.Objects;

@CommandDeclaration(command = "reload",
        aliases = "rl",
        permission = "plots.admin.command.reload",
        description = "Reload translations and world settings",
        usage = "/plot reload",
        category = CommandCategory.ADMINISTRATION)
public class Reload extends SubCommand {

    @Override
    public boolean onCommand(PlotPlayer player, String[] args) {
        try {
            // The following won't affect world generation, as that has to be
            // loaded during startup unfortunately.
            PS.get().setupConfigs();
            C.load(PS.get().translationFile);
            PS.get().foreachPlotArea(new RunnableVal<PlotArea>() {
                @Override
                public void run(PlotArea area) {
                    ConfigurationSection worldSection = PS.get().worlds.getConfigurationSection("worlds." + area.worldname);
                    if (worldSection == null) {
                        return;
                    }
                    if (area.TYPE != 2 || !worldSection.contains("areas")) {
                        area.saveConfiguration(worldSection);
                        area.loadDefaultConfiguration(worldSection);
                    } else {
                        ConfigurationSection areaSection =
                                worldSection.getConfigurationSection("areas." + area.id + "-" + area.getMin() + "-" + area.getMax());
                        YamlConfiguration clone = new YamlConfiguration();
                        for (String key : areaSection.getKeys(true)) {
                            if (areaSection.get(key) instanceof MemorySection) {
                                continue;
                            }
                            if (!clone.contains(key)) {
                                clone.set(key, areaSection.get(key));
                            }
                        }
                        for (String key : worldSection.getKeys(true)) {
                            if (worldSection.get(key) instanceof MemorySection) {
                                continue;
                            }
                            if (!key.startsWith("areas") && !clone.contains(key)) {
                                clone.set(key, worldSection.get(key));
                            }
                        }
                        area.saveConfiguration(clone);
                        // netSections is the combination of
                        for (String key : clone.getKeys(true)) {
                            if (clone.get(key) instanceof MemorySection) {
                                continue;
                            }
                            if (!worldSection.contains(key)) {
                                worldSection.set(key, clone.get(key));
                            } else {
                                Object value = worldSection.get(key);
                                if (Objects.equals(value, clone.get(key))) {
                                    areaSection.set(key, clone.get(key));
                                }
                            }
                        }
                        area.loadDefaultConfiguration(clone);
                    }
                }
            });
            PS.get().worlds.save(PS.get().worldsFile);
            MainUtil.sendMessage(player, C.RELOADED_CONFIGS);
        } catch (IOException e) {
            e.printStackTrace();
            MainUtil.sendMessage(player, C.RELOAD_FAILED);
        }
        return true;
    }
}
