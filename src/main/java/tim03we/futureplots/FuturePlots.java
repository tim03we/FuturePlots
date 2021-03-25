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
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Config;
import tim03we.futureplots.commands.*;
import tim03we.futureplots.commands.sub.*;
import tim03we.futureplots.generator.PlotGenerator;
import tim03we.futureplots.handler.CommandHandler;
import tim03we.futureplots.listener.*;
import tim03we.futureplots.provider.*;
import tim03we.futureplots.tasks.PlotClearTask;
import tim03we.futureplots.tasks.PlotErodeTask;
import tim03we.futureplots.utils.Language;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotSettings;
import tim03we.futureplots.utils.Settings;

import java.io.File;
import java.util.HashMap;
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
        registerGenerator();
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
        initProvider();
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockBreak(), this);
        pm.registerEvents(new BlockBurn(), this);
        pm.registerEvents(new BlockPiston(), this);
        pm.registerEvents(new BlockPlace(), this);
        pm.registerEvents(new EntityExplode(), this);
        pm.registerEvents(new EntityShootBow(), this);
        pm.registerEvents(new ItemFrameDropItem(), this);
        pm.registerEvents(new LiquidFlow(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new PlayerMove(), this);
    }

    private void checkVersion() {
        if(!Language.getNoPrefix("version").equals("1.2.7")) {
            new File(getDataFolder() + "/lang/" + Settings.language + "_old.yml").delete();
            if(new File(getDataFolder() + "/lang/" + Settings.language + ".yml").renameTo(new File(getDataFolder() + "/lang/" + Settings.language + "_old.yml"))) {
                getLogger().critical("The version of the language configuration does not match. You will find the old file marked \"" + Settings.language + "_old.yml\" in the same language directory.");
                Language.init();
            }
        }
        if(!getConfig().getString("version").equals("1.2.2")) {
            new File(getDataFolder() + "/config_old.yml").delete();
            if(new File(getDataFolder() + "/config.yml").renameTo(new File(getDataFolder() + "/config_old.yml"))) {
                getLogger().critical("The version of the configuration does not match. You will find the old file marked \"config_old.yml\" in the same directory.");
                saveDefaultConfig();
            }
        }
        if(!cmds.getString("version").equals("1.0.3")) {
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
        Class<?> providerClass = this.providerClass.get(this.getConfig().getString("provider"));
        if (providerClass == null) {
            this.getLogger().critical("The specified provider could not be found.");
            Server.getInstance().getPluginManager().disablePlugin(Server.getInstance().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        try { provider = (DataProvider) providerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) { this.getLogger().critical("The specified provider could not be found.");
            getServer().getPluginManager().disablePlugin(getServer().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
        provider.connect();
        if(Settings.economy) {
            try {
                if(getServer().getPluginManager().getPlugin("EconomyAPI") != null) {
                    economyProvider = EconomySProvider.class.newInstance();
                    getLogger().warning("Economy provider was set to EconomyS.");
                } else {
                    Settings.economy = false;
                    getLogger().critical("A Economy provider could not be found.");
                    getLogger().critical("The Economy function has been deactivated.");
                }
            } catch (InstantiationException | IllegalAccessException e) {
                Settings.economy = false;
                getLogger().critical("A Economy provider could not be found.");
                getLogger().critical("The Economy function has been deactivated.");
            }
        }
    }

    private void registerCommands() {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.registerCommand(cmds.getString("plot.clear.name"), new ClearCommand(cmds.getString("plot.clear.name"), cmds.getString("plot.clear.description"), cmds.getString("plot.clear.usage")), cmds.getStringList("plot.clear.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.delete.name"), new DeleteCommand(cmds.getString("plot.delete.name"), cmds.getString("plot.delete.description"), cmds.getString("plot.delete.usage")), cmds.getStringList("plot.delete.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.claim.name"), new ClaimCommand(cmds.getString("plot.claim.name"), cmds.getString("plot.claim.description"), cmds.getString("plot.claim.usage")), cmds.getStringList("plot.claim.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.home.name"), new HomeCommand(cmds.getString("plot.home.name"), cmds.getString("plot.home.description"), cmds.getString("plot.home.usage")), cmds.getStringList("plot.home.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.homes.name"), new HomesCommand(cmds.getString("plot.homes.name"), cmds.getString("plot.homes.description"), cmds.getString("plot.homes.usage")), cmds.getStringList("plot.homes.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.help.name"), new HelpCommand(cmds.getString("plot.help.name"), cmds.getString("plot.help.description"), cmds.getString("plot.help.usage")), cmds.getStringList("plot.help.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.generate.name"), new GenerateCommand(cmds.getString("plot.generate.name"), cmds.getString("plot.generate.description"), cmds.getString("plot.generate.usage")), cmds.getStringList("plot.generate.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.auto.name"), new AutoCommand(cmds.getString("plot.auto.name"), cmds.getString("plot.auto.description"), cmds.getString("plot.auto.usage")), cmds.getStringList("plot.auto.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.info.name"), new InfoCommand(cmds.getString("plot.info.name"), cmds.getString("plot.info.description"), cmds.getString("plot.info.usage")), cmds.getStringList("plot.info.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.deny.name"), new DenyCommand(cmds.getString("plot.deny.name"), cmds.getString("plot.deny.description"), cmds.getString("plot.deny.usage")), cmds.getStringList("plot.deny.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.undeny.name"), new UnDenyCommand(cmds.getString("plot.undeny.name"), cmds.getString("plot.undeny.description"), cmds.getString("plot.undeny.usage")), cmds.getStringList("plot.undeny.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.addhelper.name"), new AddHelperCommand(cmds.getString("plot.addhelper.name"), cmds.getString("plot.addhelper.description"), cmds.getString("plot.addhelper.usage")), cmds.getStringList("plot.addhelper.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.removehelper.name"), new RemoveHelperCommand(cmds.getString("plot.removehelper.name"), cmds.getString("plot.removehelper.description"), cmds.getString("plot.removehelper.usage")), cmds.getStringList("plot.removehelper.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.addmember.name"), new AddMemberCommand(cmds.getString("plot.addmember.name"), cmds.getString("plot.addmember.description"), cmds.getString("plot.addmember.usage")), cmds.getStringList("plot.addmember.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.removemember.name"), new RemoveMemberCommand(cmds.getString("plot.removemember.name"), cmds.getString("plot.removemember.description"), cmds.getString("plot.removemember.usage")), cmds.getStringList("plot.removemember.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.dispose.name"), new DisposeCommand(cmds.getString("plot.dispose.name"), cmds.getString("plot.dispose.description"), cmds.getString("plot.dispose.usage")), cmds.getStringList("plot.dispose.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.kick.name"), new KickCommand(cmds.getString("plot.kick.name"), cmds.getString("plot.kick.description"), cmds.getString("plot.kick.usage")), cmds.getStringList("plot.kick.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.setowner.name"), new SetOwnerCommand(cmds.getString("plot.setowner.name"), cmds.getString("plot.setowner.description"), cmds.getString("plot.setowner.usage")), cmds.getStringList("plot.setowner.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.sethome.name"), new SetHomeCommand(cmds.getString("plot.sethome.name"), cmds.getString("plot.sethome.description"), cmds.getString("plot.sethome.usage")), cmds.getStringList("plot.sethome.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.deletehome.name"), new DeleteHomeCommand(cmds.getString("plot.deletehome.name"), cmds.getString("plot.deletehome.description"), cmds.getString("plot.deletehome.usage")), cmds.getStringList("plot.deletehome.alias").toArray(new String[0]));
        commandHandler.registerCommand(cmds.getString("plot.erode.name"), new ErodeCommand(cmds.getString("plot.erode.name"), cmds.getString("plot.erode.description"), cmds.getString("plot.erode.usage")), cmds.getStringList("plot.erode.alias").toArray(new String[0]));
        FuturePlots.getInstance().getServer().getCommandMap().register(cmds.getString("plot.name"), new MainCommand());
    }

    public static FuturePlots getInstance() {
        return instance;
    }


    private void registerGenerator() {
        Generator.addGenerator(PlotGenerator.class, "futureplots", Generator.TYPE_INFINITE);
    }

    private void loadWorlds() {
        for (String world : Settings.levels) {
            new PlotSettings(world).initWorld();
            getServer().loadLevel(world);
        }
    }

    public void generateLevel(String levelName) {
        Settings.levels.add(levelName);
        new PlotSettings(levelName).initWorld();
        Map<String, Object> options = new HashMap<>();
        options.put("preset", levelName);
        getServer().generateLevel(levelName, 0, Generator.getGenerator("futureplots"), options);
    }

    public void clearEntities(Plot plot) {
        for (Entity entity : getServer().getLevelByName(plot.getLevelName()).getEntities()) {
            if (!(entity instanceof Player)) {
                if (entity != null) {
                    if (getPlotByPosition(entity.getLocation()).getX() == plot.getX() && getPlotByPosition(entity.getLocation()).getZ() == plot.getZ() && getPlotByPosition(entity.getLocation()).getLevelName().equals(plot.getLevelName())) {
                        entity.close();
                    }
                }
            }
        }
    }

    public void clearPlot(Plot plot) {
        clearEntities(plot);
        getServer().getScheduler().scheduleDelayedTask(this, new PlotClearTask(plot), 1, true);
    }

    public void erodePlot(Plot plot) {
        clearEntities(plot);
        getServer().getScheduler().scheduleDelayedTask(this, new PlotErodeTask(plot), 1, true);
    }

    public int claimAvailable(Player player) {
        if (player.isOp()) return -1;
        int max_plots = Settings.max_plots;
        for (String perm : player.getEffectivePermissions().keySet()) {
            if (perm.contains("futureplots.plot.")) {
                String max = perm.replace("futureplots.plot.", "");
                if (max.equalsIgnoreCase("unlimited")) {
                    return -1;
                } else {
                    try {
                        int num = Integer.parseInt(max);
                        if (num > max_plots) max_plots = num;
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return max_plots;
    }

    public Position getPlotPosition(Plot plot) {
        int plotSize = new PlotSettings(plot.getLevelName()).getPlotSize();
        int roadWidth = new PlotSettings(plot.getLevelName()).getRoadWidth();
        int groundHeight = new PlotSettings(plot.getLevelName()).getGroundHeight();
        int totalSize = plotSize + roadWidth;
        int x = totalSize * plot.getX();
        int z = totalSize * plot.getZ();
        Level level = getServer().getLevelByName(plot.getLevelName());
        return new Position(x, groundHeight, z, level);
    }

    public Position getPlotBorderPosition(Plot plot) {
        int plotSize = new PlotSettings(plot.getLevelName()).getPlotSize();
        int roadWidth = new PlotSettings(plot.getLevelName()).getRoadWidth();
        int groundHeight = new PlotSettings(plot.getLevelName()).getGroundHeight();
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
        int plotSize = new PlotSettings(position.getLevel().getName()).getPlotSize();
        int roadWidth = new PlotSettings(position.getLevel().getName()).getRoadWidth();
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
}
