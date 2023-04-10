package tim03we.futureplots;

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
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.StructureBlockUpdatePacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Config;
import tim03we.futureplots.commands.*;
import tim03we.futureplots.commands.sub.*;
import tim03we.futureplots.generator.PlotGenerator;
import tim03we.futureplots.handler.CommandHandler;
import tim03we.futureplots.listener.*;
import tim03we.futureplots.provider.*;
import tim03we.futureplots.provider.data.MongoDBProvider;
import tim03we.futureplots.provider.data.MySQLProvider;
import tim03we.futureplots.provider.data.SQLiteProvider;
import tim03we.futureplots.provider.data.YamlProvider;
import tim03we.futureplots.provider.economy.EconomyAPIProvider;
import tim03we.futureplots.provider.EconomyProvider;
import tim03we.futureplots.provider.economy.LlamaEconomyProvider;
import tim03we.futureplots.tasks.*;
import tim03we.futureplots.utils.*;
import tim03we.futureplots.utils.bstats.Metrics;
import tim03we.futureplots.utils.forms.FormAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuturePlots extends PluginBase {

    private final HashMap<String, Class<?>> providerClass = new HashMap<>();

    private static FuturePlots instance;
    public static EconomyProvider economyProvider;
    public static DataProvider provider;
    public static Config cmds;

    @Override
    public void onLoad() {
        instance = this;
        providerClass.put("yaml", YamlProvider.class);
        providerClass.put("mysql", MySQLProvider.class);
        providerClass.put("sqlite", SQLiteProvider.class);
        providerClass.put("mongodb", MongoDBProvider.class);
        registerGenerator();


        File folder = new File(getDataFolder() + "/worlds/");
        File[] files = folder.listFiles();
        if(files != null) {
            for(File file : files) {
                Settings.levels.add(file.getName().replace(".yml", ""));
            }
        }
        loadWorldsSettings();
    }

    @Override
    public void onEnable() {
        new File(getDataFolder() + "/worlds/").mkdirs();
        saveDefaultConfig();
        Settings.init();
        Language.init();
        saveResource("commands.yml");
        cmds = new Config(getDataFolder() + "/commands.yml", Config.YAML);
        checkVersion();
        cmds = new Config(getDataFolder() + "/commands.yml", Config.YAML);
        registerCommands();
        registerEvents();
        loadWorlds();

        if(Settings.formsUse) {
            FormAPI.load(this);
            getLogger().info("Forms are loaded..");
        }

        initProvider();

        if(Settings.metrics) {
            int pluginId = 16194;
            Metrics metrics = new Metrics(this, pluginId);
            getLogger().info("Metrics have been started.");
        }
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new BlockBurn(), this);
        pm.registerEvents(new BlockPiston(), this);
        pm.registerEvents(new BlockPlace(), this);
        pm.registerEvents(new EntityDamage(), this);
        pm.registerEvents(new EntityDamageByEntity(), this);
        pm.registerEvents(new EntityExplode(), this);
        pm.registerEvents(new EntityShootBow(), this);
        pm.registerEvents(new ItemFrameDropItem(), this);
        pm.registerEvents(new LiquidFlow(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerLogin(), this);
        pm.registerEvents(new PlayerMove(), this);
        pm.registerEvents(new StructureGrow(), this);
        getLogger().info("Listeners have been loaded.");
    }

    private void checkVersion() {
        getLogger().info("Check " + Settings.language + ".yml version..");
        if(!Language.getNoPrefix("version").equals("1.3.0")) {
            new File(getDataFolder() + "/lang/" + Settings.language + "_old.yml").delete();
            if(new File(getDataFolder() + "/lang/" + Settings.language + ".yml").renameTo(new File(getDataFolder() + "/lang/" + Settings.language + "_old.yml"))) {
                getLogger().critical("The version of the language configuration does not match. You will find the old file marked \"" + Settings.language + "_old.yml\" in the same language directory.");
                Language.init();
            }
        }
        getLogger().info("Check config.yml version..");
        if(!getConfig().getString("version").equals("1.3.3")) {
            new File(getDataFolder() + "/config_old.yml").delete();
            if(new File(getDataFolder() + "/config.yml").renameTo(new File(getDataFolder() + "/config_old.yml"))) {
                getLogger().critical("The version of the configuration does not match. You will find the old file marked \"config_old.yml\" in the same directory.");
                saveDefaultConfig();
            }
        }
        getLogger().info("Check commands.yml version..");
        if(!cmds.getString("version").equals("1.1.1")) {
            new File(getDataFolder() + "/commands_old.yml").delete();
            if(new File(getDataFolder() + "/commands.yml").renameTo(new File(getDataFolder() + "/commands_old.yml"))) {
                getLogger().critical("The version of the commands file does not match. You will find the old file marked \"commands_old.yml\" in the same directory.");
                saveResource("commands.yml");
            }
        }
    }

    @Override
    public void onDisable() {
        provider.save();
    }

    private void initProvider() {
        getLogger().info("Loading Provider..");
        Class<?> providerClass = this.providerClass.get(this.getConfig().getString("provider"));
        if (providerClass == null) {
            this.getLogger().critical("The specified provider could not be found.");
            Server.getInstance().getPluginManager().disablePlugin(Server.getInstance().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        try {
            provider = (DataProvider) providerClass.newInstance();

        } catch (InstantiationException | IllegalAccessException e) { this.getLogger().critical("The specified provider could not be found.");
            getServer().getPluginManager().disablePlugin(getServer().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        provider.connect();
        getLogger().info("Data provider was selected: " + Settings.provider);
        if(Settings.economyUse) {
            try {
                if(getServer().getPluginManager().getPlugin("EconomyAPI") != null) {
                    economyProvider = EconomyAPIProvider.class.newInstance();
                    getLogger().info("Data provider was selected: EconomyAPI");
                } else if(getServer().getPluginManager().getPlugin("LlamaEconomy") != null) {
                    economyProvider = LlamaEconomyProvider.class.newInstance();
                    getLogger().info("Data provider was selected: LlamaEconomy");
                } else {
                    Settings.economyUse = false;
                    getLogger().critical("A Economy provider could not be found.");
                    getLogger().critical("The Economy function has been deactivated.");
                }
            } catch (InstantiationException | IllegalAccessException e) {
                Settings.economyUse = false;
                getLogger().critical("A Economy provider could not be found.");
                getLogger().critical("The Economy function has been deactivated.");
            }
        }
    }

    private void registerCommands() {
        CommandHandler commandHandler = new CommandHandler();
        if(cmds.getBoolean("plot.clear.enable")) commandHandler.registerCommand(cmds.getString("plot.clear.name"), new ClearCommand(cmds.getString("plot.clear.name"), cmds.getString("plot.clear.description"), cmds.getString("plot.clear.usage")), cmds.getStringList("plot.clear.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.delete.enable")) commandHandler.registerCommand(cmds.getString("plot.delete.name"), new DeleteCommand(cmds.getString("plot.delete.name"), cmds.getString("plot.delete.description"), cmds.getString("plot.delete.usage")), cmds.getStringList("plot.delete.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.claim.enable")) commandHandler.registerCommand(cmds.getString("plot.claim.name"), new ClaimCommand(cmds.getString("plot.claim.name"), cmds.getString("plot.claim.description"), cmds.getString("plot.claim.usage")), cmds.getStringList("plot.claim.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.home.enable")) commandHandler.registerCommand(cmds.getString("plot.home.name"), new HomeCommand(cmds.getString("plot.home.name"), cmds.getString("plot.home.description"), cmds.getString("plot.home.usage")), cmds.getStringList("plot.home.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.homes.enable")) commandHandler.registerCommand(cmds.getString("plot.homes.name"), new HomesCommand(cmds.getString("plot.homes.name"), cmds.getString("plot.homes.description"), cmds.getString("plot.homes.usage")), cmds.getStringList("plot.homes.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.help.enable")) commandHandler.registerCommand(cmds.getString("plot.help.name"), new HelpCommand(cmds.getString("plot.help.name"), cmds.getString("plot.help.description"), cmds.getString("plot.help.usage")), cmds.getStringList("plot.help.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.generate.enable")) commandHandler.registerCommand(cmds.getString("plot.generate.name"), new GenerateCommand(cmds.getString("plot.generate.name"), cmds.getString("plot.generate.description"), cmds.getString("plot.generate.usage")), cmds.getStringList("plot.generate.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.auto.enable")) commandHandler.registerCommand(cmds.getString("plot.auto.name"), new AutoCommand(cmds.getString("plot.auto.name"), cmds.getString("plot.auto.description"), cmds.getString("plot.auto.usage")), cmds.getStringList("plot.auto.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.info.enable")) commandHandler.registerCommand(cmds.getString("plot.info.name"), new InfoCommand(cmds.getString("plot.info.name"), cmds.getString("plot.info.description"), cmds.getString("plot.info.usage")), cmds.getStringList("plot.info.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.deny.enable")) commandHandler.registerCommand(cmds.getString("plot.deny.name"), new DenyCommand(cmds.getString("plot.deny.name"), cmds.getString("plot.deny.description"), cmds.getString("plot.deny.usage")), cmds.getStringList("plot.deny.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.undeny.enable")) commandHandler.registerCommand(cmds.getString("plot.undeny.name"), new UnDenyCommand(cmds.getString("plot.undeny.name"), cmds.getString("plot.undeny.description"), cmds.getString("plot.undeny.usage")), cmds.getStringList("plot.undeny.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.addhelper.enable")) commandHandler.registerCommand(cmds.getString("plot.addhelper.name"), new AddHelperCommand(cmds.getString("plot.addhelper.name"), cmds.getString("plot.addhelper.description"), cmds.getString("plot.addhelper.usage")), cmds.getStringList("plot.addhelper.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.removehelper.enable")) commandHandler.registerCommand(cmds.getString("plot.removehelper.name"), new RemoveHelperCommand(cmds.getString("plot.removehelper.name"), cmds.getString("plot.removehelper.description"), cmds.getString("plot.removehelper.usage")), cmds.getStringList("plot.removehelper.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.addmember.enable")) commandHandler.registerCommand(cmds.getString("plot.addmember.name"), new AddMemberCommand(cmds.getString("plot.addmember.name"), cmds.getString("plot.addmember.description"), cmds.getString("plot.addmember.usage")), cmds.getStringList("plot.addmember.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.removemember.enable")) commandHandler.registerCommand(cmds.getString("plot.removemember.name"), new RemoveMemberCommand(cmds.getString("plot.removemember.name"), cmds.getString("plot.removemember.description"), cmds.getString("plot.removemember.usage")), cmds.getStringList("plot.removemember.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.dispose.enable")) commandHandler.registerCommand(cmds.getString("plot.dispose.name"), new DisposeCommand(cmds.getString("plot.dispose.name"), cmds.getString("plot.dispose.description"), cmds.getString("plot.dispose.usage")), cmds.getStringList("plot.dispose.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.kick.enable")) commandHandler.registerCommand(cmds.getString("plot.kick.name"), new KickCommand(cmds.getString("plot.kick.name"), cmds.getString("plot.kick.description"), cmds.getString("plot.kick.usage")), cmds.getStringList("plot.kick.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.setowner.enable")) commandHandler.registerCommand(cmds.getString("plot.setowner.name"), new SetOwnerCommand(cmds.getString("plot.setowner.name"), cmds.getString("plot.setowner.description"), cmds.getString("plot.setowner.usage")), cmds.getStringList("plot.setowner.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.sethome.enable")) commandHandler.registerCommand(cmds.getString("plot.sethome.name"), new SetHomeCommand(cmds.getString("plot.sethome.name"), cmds.getString("plot.sethome.description"), cmds.getString("plot.sethome.usage")), cmds.getStringList("plot.sethome.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.deletehome.enable")) commandHandler.registerCommand(cmds.getString("plot.deletehome.name"), new DeleteHomeCommand(cmds.getString("plot.deletehome.name"), cmds.getString("plot.deletehome.description"), cmds.getString("plot.deletehome.usage")), cmds.getStringList("plot.deletehome.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.erode.enable")) commandHandler.registerCommand(cmds.getString("plot.erode.name"), new ErodeCommand(cmds.getString("plot.erode.name"), cmds.getString("plot.erode.description"), cmds.getString("plot.erode.usage")), cmds.getStringList("plot.erode.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.merge.enable")) commandHandler.registerCommand(cmds.getString("plot.merge.name"), new MergeCommand(cmds.getString("plot.merge.name"), cmds.getString("plot.merge.description"), cmds.getString("plot.merge.usage")), cmds.getStringList("plot.merge.alias").toArray(new String[0]));
        if(cmds.getBoolean("plot.teleport.enable")) commandHandler.registerCommand(cmds.getString("plot.teleport.name"), new TeleportCommand(cmds.getString("plot.teleport.name"), cmds.getString("plot.teleport.description"), cmds.getString("plot.teleport.usage")), cmds.getStringList("plot.teleport.alias").toArray(new String[0]));

        FuturePlots.getInstance().getServer().getCommandMap().register(cmds.getString("plot.name"), new MainCommand());
        getLogger().info("All commands have been loaded...");
    }

    public static FuturePlots getInstance() {
        return instance;
    }


    private void registerGenerator() {
        Generator.addGenerator(PlotGenerator.class, "futureplots", Generator.TYPE_INFINITE);
    }

    private void loadWorlds() {
        for (String world : Settings.levels) {
            getServer().loadLevel(world);
        }
    }

    private void loadWorldsSettings() {
        for (String world : Settings.levels) {
            new PlotSettings(world);
        }
    }

    public void generateLevel(String levelName) {
        Settings.levels.add(levelName);
        new PlotSettings(levelName);
        Map<String, Object> options = new HashMap<>();
        options.put("preset", levelName);
        getServer().generateLevel(levelName, 0, Generator.getGenerator("futureplots"), options);
    }

    public void clearEntities(Plot plot) {
        for (Entity entity : getServer().getLevelByName(plot.getLevelName()).getEntities()) {
            if (!(entity instanceof Player)) {
                if (entity != null && entity.getLocation() != null) {
                    if (getPlotByPosition(entity.getLocation()).getX() == plot.getX() && getPlotByPosition(entity.getLocation()).getZ() == plot.getZ() && getPlotByPosition(entity.getLocation()).getLevelName().equals(plot.getLevelName())) {
                        entity.close();
                    }
                }
            }
        }
    }

    public void clearPlot(Plot plot) {
        //clearEntities(plot);
        getServer().getScheduler().scheduleDelayedTask(this, new PlotClearTask(plot), 1, true);
    }

    public void erodePlot(Plot plot) {
        clearEntities(plot);
        getServer().getScheduler().scheduleDelayedTask(this, new PlotErodeTask(plot), 1, true);
    }

    public boolean canClaim(Player player) {
        if (player.isOp()) return true;
        int max_plots = Settings.max_plots;
        for (String perm : player.getEffectivePermissions().keySet()) {
            if (perm.contains("futureplots.plot.")) {
                String max = perm.replace("futureplots.plot.", "");
                if (max.equalsIgnoreCase("unlimited")) {
                    return true;
                } else {
                    try {
                        int num = Integer.parseInt(max);
                        if (num > max_plots) max_plots = num;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        String levelName = Settings.levels.get(0);
        if(Settings.levels.size() > 0) {
            levelName = player.getLevel().getName();
            if(!Settings.levels.contains(levelName)) {
                getLogger().error("Error when claiming the plot. An error occurred while retrieving the world. Contact the owner of the plugin about this error.");
                return false;
            }
        }
        String playerId = player.getName();
        int plotCount = provider.getPlots(playerId, levelName).size();
        return plotCount < max_plots;
    }

    public Plot getPlot(Position position) {
        if(Settings.levels.contains(position.getLevel().getName())) {
            Plot plot = FuturePlots.getInstance().getPlotByPosition(position);
            if(plot == null) {
                Plot merge = FuturePlots.getInstance().isInMergeCheck(position);
                if(merge != null) {
                    plot = merge;
                }
            }
            if(plot != null && FuturePlots.provider.getOriginPlot(plot) != null && FuturePlots.provider.getMerges(plot).isEmpty()) {
                plot = FuturePlots.provider.getOriginPlot(plot);
            }
            return plot;
        }
        return null;
    }

    public Plot getPlot(String levelName, double x, double y, double z) {
        if(Settings.levels.contains(levelName)) {
            Position position = new Position(x, y, z, getServer().getLevelByName(levelName));
            Plot plot = FuturePlots.getInstance().getPlotByPosition(position);
            if(plot == null) {
                Plot merge = FuturePlots.getInstance().isInMergeCheck(position);
                if(merge != null) {
                    plot = merge;
                }
            }
            if(plot != null && FuturePlots.provider.getOriginPlot(plot) != null && FuturePlots.provider.getMerges(plot).isEmpty()) {
                plot = FuturePlots.provider.getOriginPlot(plot);
            }
            return plot;
        }
        return null;
    }

    public Position getPlotPosition(Plot plot) {
        String levelName = plot.getLevelName();
        int plotSize = PlotSettings.getPlotSize(levelName);
        int roadWidth = PlotSettings.getRoadWidth(levelName);
        int groundHeight = PlotSettings.getGroundHeight(levelName);
        int totalSize = plotSize + roadWidth;
        int x = totalSize * plot.getX();
        int z = totalSize * plot.getZ();
        Level level = getServer().getLevelByName(plot.getLevelName());
        return new Position(x, groundHeight, z, level);
    }

    public Position getPlotBorderPosition(Plot plot) {
        String levelName = plot.getLevelName();
        int plotSize = PlotSettings.getPlotSize(levelName);
        int roadWidth = PlotSettings.getRoadWidth(levelName);
        int groundHeight = PlotSettings.getGroundHeight(levelName);
        int totalSize = plotSize + roadWidth;
        int x = totalSize * plot.getX();
        int z = totalSize * plot.getZ();
        Level level = getServer().getLevelByName(plot.getLevelName());
        return new Position(x + Math.floor(plotSize / 2), groundHeight + 1.5, z - 1, level);
    }

    public Plot getPlotByPosition(Position position) {
        double x = position.x;
        double z = position.z;
        int X;
        int Z;
        double difX;
        double difZ;
        String levelName = position.getLevel().getName();
        int plotSize = PlotSettings.getPlotSize(levelName);
        int roadWidth = PlotSettings.getRoadWidth(levelName);
        int totalSize = plotSize + roadWidth;
        if(x >= 0) {
            X = (int) Math.floor(x / totalSize);
            difX = x % totalSize;
        }else{
            X = (int) Math.ceil((x - plotSize + 1) / totalSize);
            difX = Math.abs((x - plotSize + 1) % totalSize);
        }
        if(z >= 0) {
            Z = (int) Math.floor(z / totalSize);
            difZ = z % totalSize;
        } else {
            Z = (int) Math.ceil((z - plotSize + 1) / totalSize);
            difZ = Math.abs((z - plotSize + 1) % totalSize);
        }
        if((difX > plotSize - 1) || (difZ > plotSize - 1)) {
            return null;
        }
        return new Plot(X, Z, position.getLevel().getName());
    }

    public void mergePlots(Player player, Plot plot, BlockFace direction, String targetPlotPlayerId) {
        //String playerId = Utils.getPlayerId(player.getName());
        Plot nextPlot;
        int x = plot.getX();
        int z = plot.getZ();
        if (direction == BlockFace.NORTH) {
            z--;
        } else if (direction == BlockFace.SOUTH) {
            z++;
        } else if (direction == BlockFace.WEST) {
            x--;
        } else if (direction == BlockFace.EAST) {
            x++;
        }
        nextPlot = new Plot(x, z, plot.getLevelName());
        if (!provider.getOwner(nextPlot).equalsIgnoreCase(targetPlotPlayerId)) {
            player.sendMessage(Language.translate(true, "merge.not.a.owner"));
            return;
        }

        if (isMergeCheck(plot, nextPlot)) {
            player.sendMessage(Language.translate(true, "merge.already.merged"));
            return;
        }

        if (provider.getOriginPlot(nextPlot) == null && provider.getMerges(nextPlot).isEmpty() /* && provider.getOriginPlot(plot) == null && provider.getMerges(plot).size() == 0*/) { // Wenn Plot noch nie gemerged wurde
            if (provider.getOriginPlot(plot) == null) {
                provider.setOriginPlot(nextPlot, plot); // Merge Plot wird Origin Plot hinzugefügt
                provider.addMerge(plot, nextPlot); // Origin Plot bekommt nextPlot dazu
            } else {
                provider.setOriginPlot(nextPlot, provider.getOriginPlot(plot)); // Merge Plot wird Origin Plot hinzugefügt
                provider.addMerge(provider.getOriginPlot(plot), nextPlot); // Origin Plot bekommt nextPlot dazu
            }
        } else {
            boolean allone = true;
            Plot currentPlot = plot;
            Plot mergePlot = nextPlot;
            if(provider.getOriginPlot(currentPlot) != null && provider.getMerges(currentPlot).isEmpty()) {
                // Wenn das gegenüberliegende Plot teil eines Merges ist
                currentPlot = provider.getOriginPlot(currentPlot);
            }
            if(provider.getOriginPlot(mergePlot) != null && provider.getMerges(mergePlot).isEmpty()) {
                // Wenn das gegenüberliegende Plot teil eines Merges ist
                mergePlot = provider.getOriginPlot(mergePlot);
            }
            if(provider.getMerges(currentPlot).size() > 0 && provider.getMerges(mergePlot).size() > 0) {
                allone = false;
            }
            if(allone) {
                if (provider.getMerges(nextPlot).size() > 0) {
                    if (provider.getOriginPlot(plot) == null && provider.getMerges(plot).size() == 0) {
                        if (!plot.getFullID().equalsIgnoreCase(nextPlot.getFullID())) {
                            provider.setOriginPlot(plot, nextPlot);
                            provider.addMerge(nextPlot, plot);
                        }
                    }
                } else {
                    if (provider.getOriginPlot(plot) == null && provider.getMerges(plot).size() == 0) {
                        if (!plot.getFullID().equalsIgnoreCase(nextPlot.getFullID())) {
                            provider.setOriginPlot(plot, provider.getOriginPlot(nextPlot));
                            provider.addMerge(provider.getOriginPlot(nextPlot), plot);
                        }
                    }
                }
            } else {
                for (Plot merge : provider.getMerges(currentPlot)) {
                    provider.setOriginPlot(merge, mergePlot);
                    provider.addMerge(mergePlot, merge);
                }
                provider.setOriginPlot(currentPlot, mergePlot);
                provider.addMerge(mergePlot, currentPlot);
                // ?? provider.deleteMergeList(currentPlot);
            }
        }
        provider.setMergeCheck(plot, nextPlot);
        provider.setMergeCheck(nextPlot, plot);

        getServer().getScheduler().scheduleDelayedTask(new RoadFillTask(this, plot, nextPlot), 1);
        if (direction == BlockFace.NORTH) {
            Plot leftPlot = new Plot(plot.getX() - 1, plot.getZ(), plot.getLevelName());
            Plot leftUpPlot = new Plot(plot.getX() - 1, plot.getZ() - 1, plot.getLevelName());
            Plot upPlot = new Plot(plot.getX(), plot.getZ() - 1, plot.getLevelName());
            Plot rightPlot = new Plot(plot.getX() + 1, plot.getZ(), plot.getLevelName());
            Plot rightUpPlot = new Plot(plot.getX() + 1, plot.getZ() - 1, plot.getLevelName());
            if (isMergeCheck(plot, leftPlot) && isMergeCheck(leftPlot, leftUpPlot) && isMergeCheck(leftUpPlot, upPlot)
                    && provider.getOwner(leftPlot).equals(targetPlotPlayerId) && provider.getOwner(leftUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, plot, leftUpPlot, false, direction, 256), 5);
            }
            if (isMergeCheck(plot, rightPlot) && isMergeCheck(rightPlot, rightUpPlot) && isMergeCheck(rightUpPlot, upPlot)
                    && provider.getOwner(rightPlot).equals(targetPlotPlayerId) && provider.getOwner(rightUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, rightPlot, upPlot, false, direction, 256), 5);
            }
        } else if (direction == BlockFace.SOUTH) {
            Plot leftPlot = new Plot(plot.getX() + 1, plot.getZ(), plot.getLevelName());
            Plot leftUpPlot = new Plot(plot.getX() + 1, plot.getZ() + 1, plot.getLevelName());
            Plot upPlot = new Plot(plot.getX(), plot.getZ() + 1, plot.getLevelName());
            Plot rightPlot = new Plot(plot.getX() - 1, plot.getZ(), plot.getLevelName());
            Plot rightUpPlot = new Plot(plot.getX() - 1, plot.getZ() + 1, plot.getLevelName());
            if (isMergeCheck(plot, leftPlot) && isMergeCheck(leftPlot, leftUpPlot) && isMergeCheck(leftUpPlot, upPlot)
                    && provider.getOwner(leftPlot).equals(targetPlotPlayerId) && provider.getOwner(leftUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, leftUpPlot, plot, false, direction, 256), 5);
            }
            if (isMergeCheck(plot, rightPlot) && isMergeCheck(rightPlot, rightUpPlot) && isMergeCheck(rightUpPlot, upPlot)
                    && provider.getOwner(rightPlot).equals(targetPlotPlayerId) && provider.getOwner(rightUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, upPlot, rightPlot, false, direction, 256), 5);
            }
        } else if (direction == BlockFace.WEST) {
            Plot leftPlot = new Plot(plot.getX(), plot.getZ() + 1, plot.getLevelName());
            Plot leftUpPlot = new Plot(plot.getX() - 1, plot.getZ() + 1, plot.getLevelName());
            Plot upPlot = new Plot(plot.getX() - 1, plot.getZ(), plot.getLevelName());
            Plot rightPlot = new Plot(plot.getX(), plot.getZ() - 1, plot.getLevelName());
            Plot rightUpPlot = new Plot(plot.getX() - 1, plot.getZ() - 1, plot.getLevelName());
            if (isMergeCheck(plot, leftPlot) && isMergeCheck(leftPlot, leftUpPlot) && isMergeCheck(leftUpPlot, upPlot)
                    && provider.getOwner(leftPlot).equals(targetPlotPlayerId) && provider.getOwner(leftUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, leftPlot, upPlot, false, direction, 256), 5);
            }
            if (isMergeCheck(plot, rightPlot) && isMergeCheck(rightPlot, rightUpPlot) && isMergeCheck(rightUpPlot, upPlot)
                    && provider.getOwner(rightPlot).equals(targetPlotPlayerId) && provider.getOwner(rightUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, plot, rightUpPlot, false, direction, 256), 5);
            }
        } else if (direction == BlockFace.EAST) {
            Plot leftPlot = new Plot(plot.getX(), plot.getZ() - 1, plot.getLevelName());
            Plot leftUpPlot = new Plot(plot.getX() + 1, plot.getZ() - 1, plot.getLevelName());
            Plot upPlot = new Plot(plot.getX() + 1, plot.getZ(), plot.getLevelName());
            Plot rightPlot = new Plot(plot.getX(), plot.getZ() + 1, plot.getLevelName());
            Plot rightUpPlot = new Plot(plot.getX() + 1, plot.getZ() + 1, plot.getLevelName());
            if (isMergeCheck(plot, leftPlot) && isMergeCheck(leftPlot, leftUpPlot) && isMergeCheck(leftUpPlot, upPlot)
                    && provider.getOwner(leftPlot).equals(targetPlotPlayerId) && provider.getOwner(leftUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, upPlot, leftPlot, false, direction, 256), 5);
            }
            if (isMergeCheck(plot, rightPlot) && isMergeCheck(rightPlot, rightUpPlot) && isMergeCheck(rightUpPlot, upPlot)
                    && provider.getOwner(rightPlot).equals(targetPlotPlayerId) && provider.getOwner(rightUpPlot).equals(targetPlotPlayerId) && provider.getOwner(upPlot).equals(targetPlotPlayerId)) {
                getServer().getScheduler().scheduleDelayedTask(new RoadMiddleFillTask(this, rightUpPlot, plot, false, direction, 256), 5);
            }
        }
        player.sendMessage(Language.translate(true, "merge.merged", player.getDirection().getName()));
    }

    public boolean isMerge(Plot plot, Plot toMerge) {
        if(provider.getOriginPlot(plot) == null) {
            for (Plot plot1 : provider.getMerges(plot)) {
                if(plot1.getFullID().equalsIgnoreCase(toMerge.getFullID())) {
                    return true;
                }
            }
        }
        if(provider.getOriginPlot(toMerge) == null) {
            for (Plot plot1 : provider.getMerges(toMerge)) {
                if(plot1.getFullID().equalsIgnoreCase(plot.getFullID())) {
                    return true;
                }
            }
        }
        if (provider.getOriginPlot(plot) != null && provider.getOriginPlot(toMerge) != null && provider.getOriginPlot(plot).getFullID().equalsIgnoreCase(provider.getOriginPlot(toMerge).getFullID())) {
            return true;
        }
        return false;
    }

    public boolean isMerge(Plot plot) {
        return provider.getMerges(plot).size() > 0 || provider.getOriginPlot(plot) != null;
    }

    public boolean isMergeCheck(Plot plot1, Plot plot2) {
        List<Plot> plots1 = provider.getMergeCheck(plot1);
        List<Plot> plots2 = provider.getMergeCheck(plot2);
        for (Plot mergePlot : plots1) {
            if(mergePlot.getX() == plot2.getX() && mergePlot.getZ() == plot2.getZ()) {
                return true;
            }
        }
        for (Plot mergePlot : plots2) {
            if(mergePlot.getX() == plot1.getX() && mergePlot.getZ() == plot1.getZ()) {
                return true;
            }
        }
        return false;
    }

    public Plot getMergeByBlockFace(Plot plot, BlockFace direction) {
        Plot mergePlot = null;
        if(direction == BlockFace.NORTH) {
            mergePlot = new Plot(plot.getX(), plot.getZ() - 1, plot.getLevelName());
        } else if(direction == BlockFace.SOUTH) {
            mergePlot = new Plot(plot.getX(), plot.getZ() + 1, plot.getLevelName());
        } else if(direction == BlockFace.WEST) {
            mergePlot = new Plot(plot.getX() - 1, plot.getZ(), plot.getLevelName());
        } else if(direction == BlockFace.EAST) {
            mergePlot = new Plot(plot.getX() + 1, plot.getZ(), plot.getLevelName());
        }
        return mergePlot;
    }

    public void resetMerges(Plot plot, boolean reset) {
        String levelName = plot.getLevelName();
        List<Plot> merges = new ArrayList<>();
        if(provider.getOriginPlot(plot) == null && !provider.getMerges(plot).isEmpty()) {
            merges = provider.getMerges(plot);
        } else {
            merges = provider.getMerges(provider.getOriginPlot(plot));
            plot = provider.getOriginPlot(plot);
        }
        BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (Plot merge : merges) {
            for (BlockFace face : faces) {
                if(isMerge(merge, getMergeByBlockFace(merge, face))) {
                    getServer().getScheduler().scheduleDelayedTask(new RoadResetTask(this, merge, getMergeByBlockFace(merge, face), reset), 1);
                }
            }
            clearPlot(merge);
        }
        Plot finalPlot = plot;
        getServer().getScheduler().scheduleDelayedTask(() -> {
            clearPlot(finalPlot);
            if(reset) {
                finalPlot.changeBorder(PlotSettings.getWallBlockUnClaimed(levelName));
            } else finalPlot.changeBorder(PlotSettings.getWallBlockClaimed(levelName));
        }, 5);
        merges.forEach((merge) -> provider.resetMerges(merge));
        provider.resetMerges(plot);
        if(reset) merges.forEach((merge) -> provider.deletePlot(merge));
        if(reset) FuturePlots.provider.deletePlot(plot);
    }

    public Plot isInMerge(Player player) {
        return isInMerge(player, player.getPosition());
    }

    public Plot isInMerge(Player player, Block block) {
        return isInMerge(player, block.getLocation());
    }

    public Plot isInMerge(Player player, Position position) {
        String playerId = player.getName();
        String levelName = position.getLevel().getName();
        boolean checkNext = true;
        Plot inPlot = null;
        Position newPos = position;
        int roadWidth = PlotSettings.getRoadWidth(levelName);
        Plot plot1 = FuturePlots.getInstance().getPlotByPosition(newPos.add(roadWidth, 0, 0));
        Plot plot2 = FuturePlots.getInstance().getPlotByPosition(newPos.add(-roadWidth, 0, 0));
        if(plot1 != null && plot2 != null &&
                FuturePlots.provider.getOwner(plot1).equals(playerId) &&
                FuturePlots.provider.getOwner(plot2).equals(playerId) &&
                FuturePlots.getInstance().isMergeCheck(plot1, plot2)) { // Check X side
            checkNext = false;
            inPlot = plot1;
        }
        if(checkNext) {
            newPos = position;
            plot1 = FuturePlots.getInstance().getPlotByPosition(newPos.add(0, 0, roadWidth));
            plot2 = FuturePlots.getInstance().getPlotByPosition(newPos.add(0, 0, -roadWidth));
            if(plot1 != null && plot2 != null &&
                    FuturePlots.provider.getOwner(plot1).equals(playerId) &&
                    FuturePlots.provider.getOwner(plot2).equals(playerId) &&
                    FuturePlots.getInstance().isMergeCheck(plot1, plot2)) { // Check Z side
                checkNext = false;
                inPlot = plot1;
            }
        }
        if(checkNext) { // SOUTH WEST to NORTH EAST
            newPos = position;
            plot1 = FuturePlots.getInstance().getPlotByPosition(newPos.add(-roadWidth, 0, roadWidth));
            plot2 = FuturePlots.getInstance().getPlotByPosition(newPos.add(roadWidth, 0, -roadWidth));
            if(plot1 != null && plot2 != null &&
                    FuturePlots.provider.getOwner(plot1).equals(playerId) &&
                    FuturePlots.provider.getOwner(plot2).equals(playerId) &&
                    FuturePlots.getInstance().isMergeCheck(plot1, FuturePlots.getInstance().getMergeByBlockFace(plot1, BlockFace.NORTH)) &&
                    FuturePlots.getInstance().isMergeCheck(plot2, FuturePlots.getInstance().getMergeByBlockFace(plot2, BlockFace.SOUTH)) &&
                    FuturePlots.getInstance().isMergeCheck(plot1, FuturePlots.getInstance().getMergeByBlockFace(plot1, BlockFace.EAST)) &&
                    FuturePlots.getInstance().isMergeCheck(plot2, FuturePlots.getInstance().getMergeByBlockFace(plot2, BlockFace.WEST)) &&
                    FuturePlots.getInstance().isMerge(plot1, plot2)) {
                inPlot = plot1;
            }
        }
        return inPlot;
    }

    public Plot isInMergeCheck(Position position) {
        String levelName = position.getLevel().getName();
        boolean checkNext = true;
        Plot inPlot = null;
        Position newPos = position;
        int roadWidth = PlotSettings.getRoadWidth(levelName);
        Plot plot1 = FuturePlots.getInstance().getPlotByPosition(newPos.add(roadWidth, 0, 0));
        Plot plot2 = FuturePlots.getInstance().getPlotByPosition(newPos.add(-roadWidth, 0, 0));
        if(plot1 != null && plot2 != null &&
                FuturePlots.getInstance().isMergeCheck(plot1, plot2)) { // Check X side
            checkNext = false;
            inPlot = plot1;
        }
        if(checkNext) {
            newPos = position;
            plot1 = FuturePlots.getInstance().getPlotByPosition(newPos.add(0, 0, roadWidth));
            plot2 = FuturePlots.getInstance().getPlotByPosition(newPos.add(0, 0, -roadWidth));
            if(plot1 != null && plot2 != null &&
                    FuturePlots.getInstance().isMergeCheck(plot1, plot2)) { // Check Z side
                checkNext = false;
                inPlot = plot1;
            }
        }
        if(checkNext) { // SOUTH WEST to NORTH EAST
            newPos = position;
            plot1 = FuturePlots.getInstance().getPlotByPosition(newPos.add(-roadWidth, 0, roadWidth));
            plot2 = FuturePlots.getInstance().getPlotByPosition(newPos.add(roadWidth, 0, -roadWidth));
            if(plot1 != null && plot2 != null &&
                    FuturePlots.getInstance().isMergeCheck(plot1, FuturePlots.getInstance().getMergeByBlockFace(plot1, BlockFace.NORTH)) &&
                    FuturePlots.getInstance().isMergeCheck(plot2, FuturePlots.getInstance().getMergeByBlockFace(plot2, BlockFace.SOUTH)) &&
                    FuturePlots.getInstance().isMergeCheck(plot1, FuturePlots.getInstance().getMergeByBlockFace(plot1, BlockFace.EAST)) &&
                    FuturePlots.getInstance().isMergeCheck(plot2, FuturePlots.getInstance().getMergeByBlockFace(plot2, BlockFace.WEST)) &&
                    FuturePlots.getInstance().isMerge(plot1, plot2)) {
                inPlot = plot1;
            }
        }
        return inPlot;
    }
}
