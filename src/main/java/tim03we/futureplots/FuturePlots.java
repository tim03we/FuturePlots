package tim03we.futureplots;

/*
 * This software is distributed under "GNU General Public License v3.0".
 * This license allows you to use it and/or modify it but you are not at
 * all allowed to sell this plugin at any cost. If found doing so the
 * necessary action required would be taken.
 *
 * GunGame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License v3.0 for more details.
 *
 * You should have received a copy of the GNU General Public License v3.0
 * along with this program. If not, see
 * <https://opensource.org/licenses/GPL-3.0>.
 */

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import tim03we.futureplots.commands.*;
import tim03we.futureplots.generator.PlotGenerator;
import tim03we.futureplots.handler.CommandHandler;
import tim03we.futureplots.provider.DataProvider;
import tim03we.futureplots.provider.EconomyProvider;
import tim03we.futureplots.provider.EconomySProvider;
import tim03we.futureplots.provider.YamlProvider;
import tim03we.futureplots.tasks.PlotClearTask;
import tim03we.futureplots.utils.Language;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotSettings;
import tim03we.futureplots.utils.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FuturePlots extends PluginBase {

    private final HashMap<String, Class<?>> providerClass = new HashMap<>();

    private static FuturePlots instance;
    public static EconomyProvider economyProvider;
    public static DataProvider provider;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        providerClass.put("yaml", YamlProvider.class);
        registerGenerator();
    }

    @Override
    public void onEnable() {
        new File(getDataFolder() + "/worlds/").mkdirs();
        registerCommands();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Settings.init();
        Language.init();
        loadWorlds();
        initProvider();
        checkVersion();
    }

    private void checkVersion() {
        if(!Language.getNoPrefix("version").equals("1.1.0")) {
            new File(getDataFolder() + "/lang/" + Settings.language + "_old.yml").delete();
            if(new File(getDataFolder() + "/lang/" + Settings.language + ".yml").renameTo(new File(getDataFolder() + "/lang/" + Settings.language + "_old.yml"))) {
                getLogger().critical("The version of the language configuration does not match. You will find the old file marked \"" + Settings.language + "_old.yml\" in the same language directory.");
                Language.init();
            }
        }
        if(!getConfig().getString("version").equals("1.1.0")) {
            new File(getDataFolder() + "/config_old.yml").delete();
            if(new File(getDataFolder() + "/config.yml").renameTo(new File(getDataFolder() + "/config_old.yml"))) {
                getLogger().critical("The version of the configuration does not match. You will find the old file marked \"config_old.yml\" in the same directory.");
                saveDefaultConfig();
            }
        }
    }

    @Override
    public void onDisable() {
        provider.saveAll();
    }

    private void initProvider() {
        Class<?> providerClass = this.providerClass.get((this.getConfig().get(Settings.provider, "yaml")).toLowerCase());
        if (providerClass == null) { this.getLogger().critical("The specified provider could not be found."); }
        try { this.provider = (DataProvider) providerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) { this.getLogger().critical("The specified provider could not be found.");
            getServer().getPluginManager().disablePlugin(getServer().getPluginManager().getPlugin("FuturePlots"));
            return;
        }
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
        commandHandler.registerCommand("clear", new ClearCommand("clear", "Run the command", "/plot clear"), new String[]{"reset"});
        commandHandler.registerCommand("delete", new DeleteCommand("delete", "Run the command", "/plot delete"), new String[]{"del"});
        commandHandler.registerCommand("claim", new ClaimCommand("claim", "Run the command", "/plot claim"), new String[]{"c"});
        commandHandler.registerCommand("home", new HomeCommand("home", "Run the command", "/plot home"), new String[]{"h"});
        commandHandler.registerCommand("homes", new HomesCommand("homes", "Run the command", "/plot homes"), new String[]{});
        commandHandler.registerCommand("help", new HelpCommand("help", "Run the command", "/plot help"), new String[]{});
        commandHandler.registerCommand("generate", new GenerateCommand("generate", "Run the command", "/plot generate"), new String[]{"gen"});
        commandHandler.registerCommand("auto", new AutoCommand("auto", "Run the command", "/plot auto"), new String[]{"a"});
        commandHandler.registerCommand("info", new InfoCommand("info", "Run the command", "/plot info"), new String[]{"i"});
        commandHandler.registerCommand("deny", new DenyCommand("deny", "", "/plot deny <name>"), new String[]{});
        commandHandler.registerCommand("undeny", new UnDenyCommand("undeny", "", "/plot undeny <name>"), new String[]{});
        commandHandler.registerCommand("addhelper", new AddHelper("addhelper", "Run the command", "/plot addhelper <name>"), new String[]{"trust"});
        commandHandler.registerCommand("removehelper", new RemoveHelper("removehelper", "Run the command", "/plot removehelper <name>"), new String[]{"rmhelper", "untrust"});
        /* ToDo */
        //commandHandler.registerCommand("flag", new FlagCommand("flag", " , "/plot flag <flag> [value]"), new String[]{});
        //commandHandler.registerCommand("addmember", new AddMember("addmember", "", "/plot addmember <name>"), new String[]{});
        //commandHandler.registerCommand("removemember", new RemoveMember("removehome", "", "/plot removemember <name>"), new String[]{"rmmember"});
        /* ToDo */
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

    public boolean isPlot(Position position) {
        return getPlotByPosition(position) != null;
    }

    public void clearPlot(Plot plot) {
        getServer().getScheduler().scheduleDelayedTask(this, new PlotClearTask(plot), 1, true);
    }

    public int claimAvailable(Player player) {
        if (player.isOp()) return -1;
        int max_plots = Settings.max_plots;
        for (Map.Entry<String, Boolean> perm : player.addAttachment(instance).getPermissions().entrySet()) {
            if (perm.getValue()) {
                if (perm.getKey().contains("futureplots.plot.")) {
                    String max = perm.getKey().replace("futureplots.plot.", "");
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
        }
        return max_plots;
    }

    public Position getPlotPosition(Plot plot) {
        int plotSize = new PlotSettings(plot.getLevelName()).getPlotSize();
        int roadWidth = new PlotSettings(plot.getLevelName()).getRoadWidth();
        int totalSize = plotSize + roadWidth;
        int x = totalSize * plot.getX();
        int z = totalSize * plot.getZ();
        Level level = getServer().getLevelByName(plot.getLevelName());
        return new Position(x, Settings.groundHeight, z, level);
    }

    public Position getPlotBorderPosition(Plot plot) {
        int plotSize = new PlotSettings(plot.getLevelName()).getPlotSize();
        int roadWidth = new PlotSettings(plot.getLevelName()).getRoadWidth();
        int totalSize = plotSize + roadWidth;
        int x = totalSize * plot.getX();
        int z = totalSize * plot.getZ();
        Level level = getServer().getLevelByName(plot.getLevelName());
        return new Position(x += Math.floor(new PlotSettings(plot.getLevelName()).getPlotSize() / 2), Settings.groundHeight += 1.5, z -= 1, level);
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
        }else {
            Z = (int) Math.ceil((z - plotSize + 1) / totalSize);
            difZ = Math.abs((z - plotSize + 1) % totalSize);
        }
        if((difX > plotSize - 1) || (difZ > plotSize - 1)) {
            return null;
        }
        return new Plot(X, Z, position.getLevel().getName());
    }

    public String findEmptyPlotSquared(int a, int b, ArrayList<String> plots) {
        if (!plots.contains(a + ";" + b)) return a + ";" + b;
        if(!plots.contains(b + ";" + a)) return b + ";" + a;
        if(a != 0) {
            if(!plots.contains(-a + ";" + b)) return -a + ";" + b;
            if(!plots.contains(b + ";" + -a)) return b + ";" + -a;
        }
        if(b != 0) {
            if(!plots.contains(-b + ";" + a)) return -b + ";" + a;
            if(!plots.contains(a + ";" + -b)) return a + ";" + -b;
        }
        if(a == 0 | b == 0) {
            if(!plots.contains(-a + ";" + -b)) return -a + ";" + -b;
            if(!plots.contains(-b + ";" + -a)) return -b + ";" + -a;
        }
        return null;
    }
}
