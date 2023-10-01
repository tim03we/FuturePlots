package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.ConfigurationNode;
import com.intellectualcrafters.plot.generator.GeneratorWrapper;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.SetupObject;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.SetupUtils;
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.WorldUtil;
import com.plotsquared.general.commands.CommandDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

@CommandDeclaration(
        command = "setup",
        permission = "plots.admin.command.setup",
        description = "Setup wizard for plot worlds",
        usage = "/plot setup",
        aliases = {"create"},
        category = CommandCategory.ADMINISTRATION)
public class Setup extends SubCommand {

    public void displayGenerators(PlotPlayer player) {
        StringBuilder message = new StringBuilder();
        message.append("&6What generator do you want?");
        for (Entry<String, GeneratorWrapper<?>> entry : SetupUtils.generators.entrySet()) {
            if (entry.getKey().equals(PS.imp().getPluginName())) {
                message.append("\n&8 - &2").append(entry.getKey()).append(" (Default Generator)");
            } else if (entry.getValue().isFull()) {
                message.append("\n&8 - &7").append(entry.getKey()).append(" (Plot Generator)");
            } else {
                message.append("\n&8 - &7").append(entry.getKey()).append(" (Unknown structure)");
            }
        }
        MainUtil.sendMessage(player, message.toString());
    }

    @Override
    public boolean onCommand(PlotPlayer player, String[] args) {
        // going through setup
        SetupObject object = player.getMeta("setup");
        if (object == null) {
            object = new SetupObject();
            player.setMeta("setup", object);
            SetupUtils.manager.updateGenerators();
            sendMessage(player, C.SETUP_INIT);
            displayGenerators(player);
            return false;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("cancel")) {
                player.deleteMeta("setup");
                MainUtil.sendMessage(player, "&aCancelled setup");
                return false;
            }
            if (args[0].equalsIgnoreCase("back")) {
                if (object.setup_index > 0) {
                    object.setup_index--;
                    ConfigurationNode node = object.step[object.setup_index];
                    sendMessage(player, C.SETUP_STEP, object.setup_index + 1, node.getDescription(), node.getType().getType(),
                            String.valueOf(node.getDefaultValue()));
                    return false;
                } else if (object.current > 0) {
                    object.current--;
                }
            }
        }
        int index = object.current;
        switch (index) {
            case 0:  // choose generator
                if (args.length != 1 || !SetupUtils.generators.containsKey(args[0])) {
                    String prefix = "\n&8 - &7";
                    MainUtil.sendMessage(player, "&cYou must choose a generator!" + prefix + StringMan.join(SetupUtils.generators.keySet(), prefix)
                            .replaceAll(PS.imp().getPluginName(), "&2" + PS.imp().getPluginName()));
                    sendMessage(player, C.SETUP_INIT);
                    return false;
                }
                object.setupGenerator = args[0];
                object.current++;
                String partial = "\n&8 - &7PARTIAL&8 - &7Vanilla with clusters of plots";
                MainUtil.sendMessage(player, "&6What world type do you want?\n&8 - &2DEFAULT&8 - &7Standard plot generation"
                        + "\n&8 - &7AUGMENTED&8 - &7Plot generation with terrain" + partial);
                break;
            case 1:  // choose world type
                List<String> allTypes = Arrays.asList("default", "augmented", "partial");
                List<String> allDesc = Arrays.asList("Standard plot generation", "Plot generation with vanilla terrain",
                        "Vanilla with clusters of plots");
                ArrayList<String> types = new ArrayList<>();
                if (SetupUtils.generators.get(object.setupGenerator).isFull()) {
                    types.add("default");
                }
                types.add("augmented");
                types.add("partial");
                if (args.length != 1 || !types.contains(args[0].toLowerCase())) {
                    MainUtil.sendMessage(player, "&cYou must choose a world type!");
                    for (String type : types) {
                        int i = allTypes.indexOf(type);
                        if (type.equals("default")) {
                            MainUtil.sendMessage(player, "&8 - &2" + type + " &8-&7 " + allDesc.get(i));
                        } else {
                            MainUtil.sendMessage(player, "&8 - &7" + type + " &8-&7 " + allDesc.get(i));
                        }
                    }
                    return false;
                }
                object.type = allTypes.indexOf(args[0].toLowerCase());
                GeneratorWrapper<?> gen = SetupUtils.generators.get(object.setupGenerator);
                if (object.type == 0) {
                    object.current = 6;
                    if (object.step == null) {
                        object.plotManager = object.setupGenerator;
                        object.step = SetupUtils.generators.get(object.plotManager).getPlotGenerator()
                                .getNewPlotArea("CheckingPlotSquaredGenerator", null, null, null).getSettingNodes();
                        SetupUtils.generators.get(object.plotManager).getPlotGenerator().processSetup(object);
                    }
                    if (object.step.length == 0) {
                        MainUtil.sendMessage(player, "&6What do you want your world to be called?");
                        object.setup_index = 0;
                        return true;
                    }
                    ConfigurationNode step = object.step[object.setup_index];
                    sendMessage(player, C.SETUP_STEP, object.setup_index + 1, step.getDescription(), step.getType().getType(),
                            String.valueOf(step.getDefaultValue()));
                } else {
                    if (gen.isFull()) {
                        object.plotManager = object.setupGenerator;
                        object.setupGenerator = null;
                        object.step = SetupUtils.generators.get(object.plotManager).getPlotGenerator()
                                .getNewPlotArea("CheckingPlotSquaredGenerator", null, null, null).getSettingNodes();
                        SetupUtils.generators.get(object.plotManager).getPlotGenerator().processSetup(object);
                    } else {
                        object.plotManager = PS.imp().getPluginName();
                        MainUtil.sendMessage(player, "&c[WARNING] The specified generator does not identify as BukkitPlotGenerator");
                        MainUtil.sendMessage(player, "&7 - You may need to manually configure the other plugin");
                        object.step = SetupUtils.generators.get(object.plotManager).getPlotGenerator()
                                .getNewPlotArea("CheckingPlotSquaredGenerator", null, null, null).getSettingNodes();
                    }
                    if (object.type == 2) {
                        MainUtil.sendMessage(player, "What would you like this area called?");
                        object.current++;
                    } else {
                        MainUtil.sendMessage(player, "&6What terrain would you like in plots?"
                                + "\n&8 - &2NONE&8 - &7No terrain at all"
                                + "\n&8 - &7ORE&8 - &7Just some ore veins and trees"
                                + "\n&8 - &7ROAD&8 - &7Terrain separated by roads"
                                + "\n&8 - &7ALL&8 - &7Entirely vanilla generation");
                        object.current = 5;
                    }
                }
                break;
            case 2:  // area id
                if (!StringMan.isAlphanumericUnd(args[0])) {
                    MainUtil.sendMessage(player, "&cThe area id must be alphanumerical!");
                    return false;
                }
                for (PlotArea area : PS.get().getPlotAreas()) {
                    if (area.id != null && area.id.equalsIgnoreCase(args[0])) {
                        MainUtil.sendMessage(player, "&cYou must choose an area id that is not in use!");
                        return false;
                    }
                }
                object.id = args[0];
                object.current++;
                MainUtil.sendMessage(player, "&6What should be the minimum Plot Id?");
                break;
            case 3:  // min
                object.min = PlotId.fromString(args[0]);
                if (object.min == null) {
                    MainUtil.sendMessage(player, "&cYou must choose a valid minimum PlotId!");
                    return false;
                }
                object.current++;
                MainUtil.sendMessage(player, "&6What should be the maximum Plot Id?");
                break;
            case 4:
                // max
                PlotId id = PlotId.fromString(args[0]);
                if (id == null) {
                    MainUtil.sendMessage(player, "&cYou must choose a valid maximum PlotId!");
                    return false;
                }
                if (id.x <= object.min.x || id.y <= object.min.y) {
                    MainUtil.sendMessage(player, "&cThe max PlotId must be greater than the minimum!");
                    return false;
                }
                object.max = id;
                object.current++;
                MainUtil.sendMessage(player, "&6What terrain would you like in plots?"
                        + "\n&8 - &2NONE&8 - &7No terrain at all"
                        + "\n&8 - &7ORE&8 - &7Just some ore veins and trees"
                        + "\n&8 - &7ROAD&8 - &7Terrain separated by roads"
                        + "\n&8 - &7ALL&8 - &7Entirely vanilla generation");
                break;
            case 5: { // Choose terrain
                List<String> terrain = Arrays.asList("none", "ore", "road", "all");
                if (args.length != 1 || !terrain.contains(args[0].toLowerCase())) {
                    MainUtil.sendMessage(player, "&cYou must choose the terrain!"
                            + "\n&8 - &2NONE&8 - &7No terrain at all"
                            + "\n&8 - &7ORE&8 - &7Just some ore veins and trees"
                            + "\n&8 - &7ROAD&8 - &7Terrain separated by roads"
                            + "\n&8 - &7ALL&8 - &7Entirely vanilla generation");
                    return false;
                }
                object.terrain = terrain.indexOf(args[0].toLowerCase());
                object.current++;
                if (object.step == null) {
                    object.step = SetupUtils.generators.get(object.plotManager).getPlotGenerator()
                            .getNewPlotArea("CheckingPlotSquaredGenerator", null, null, null).getSettingNodes();
                }
                ConfigurationNode step = object.step[object.setup_index];
                sendMessage(player, C.SETUP_STEP, object.setup_index + 1, step.getDescription(), step.getType().getType(),
                        String.valueOf(step.getDefaultValue()));
                break;
            }
            case 6:  // world setup
                if (object.setup_index == object.step.length) {
                    MainUtil.sendMessage(player, "&6What do you want your world to be called?");
                    object.setup_index = 0;
                    object.current++;
                    return true;
                }
                ConfigurationNode step = object.step[object.setup_index];
                if (args.length < 1) {
                    sendMessage(player, C.SETUP_STEP, object.setup_index + 1, step.getDescription(), step.getType().getType(),
                            String.valueOf(step.getDefaultValue()));
                    return false;
                }
                boolean valid = step.isValid(args[0]);
                if (valid) {
                    sendMessage(player, C.SETUP_VALID_ARG, step.getConstant(), args[0]);
                    step.setValue(args[0]);
                    object.setup_index++;
                    if (object.setup_index == object.step.length) {
                        onCommand(player, args);
                        return false;
                    }
                    step = object.step[object.setup_index];
                    sendMessage(player, C.SETUP_STEP, object.setup_index + 1, step.getDescription(), step.getType().getType(),
                            String.valueOf(step.getDefaultValue()));
                    return false;
                } else {
                    sendMessage(player, C.SETUP_INVALID_ARG, args[0], step.getConstant());
                    sendMessage(player, C.SETUP_STEP, object.setup_index + 1, step.getDescription(), step.getType().getType(),
                            String.valueOf(step.getDefaultValue()));
                    return false;
                }
            case 7:
                if (args.length != 1) {
                    MainUtil.sendMessage(player, "&cYou need to choose a world name!");
                    return false;
                }
                if (WorldUtil.IMP.isWorld(args[0])) {
                    if (PS.get().hasPlotArea(args[0])) {
                        MainUtil.sendMessage(player, "&cThat world name is already taken!");
                        return false;
                    }
                    MainUtil.sendMessage(player,
                            "&cThe world you specified already exists. After restarting, new terrain will use " + PS.imp().getPluginName() + ", however you may need to "
                                    + "reset the world for it to generate correctly!");
                }
                object.world = args[0];
                player.deleteMeta("setup");
                String world;
                if (object.setupManager == null) {
                    world = SetupUtils.manager.setupWorld(object);
                } else {
                    world = object.setupManager.setupWorld(object);
                }
                try {
                    player.teleport(WorldUtil.IMP.getSpawn(world));
                } catch (Exception e) {
                    player.sendMessage("&cAn error occurred. See console for more information");
                    e.printStackTrace();
                }
                sendMessage(player, C.SETUP_FINISHED, object.world);
        }
        return false;
    }
}
