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

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import tim03we.futureplots.commands.*;
import tim03we.futureplots.generator.PlotGenerator;
import tim03we.futureplots.handler.CommandHandler;
import tim03we.futureplots.tasks.PlotClearTask;
import tim03we.futureplots.utils.Language;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotSettings;
import tim03we.futureplots.utils.Settings;

import java.io.File;

public class FuturePlots extends PluginBase {

    private static FuturePlots instance;

    @Override
    public void onEnable() {
        new File(getDataFolder() + "/worlds/").mkdirs();
        instance = this;
        saveDefaultConfig();
        registerGenerator();
        registerCommands();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Settings.init();
        Language.init();
        loadWorlds();
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
        /* ToDo */
        //commandHandler.registerCommand("flag", new FlagCommand("flag", " , "/plot flag <flag> [value]"), new String[]{});
        //commandHandler.registerCommand("addmember", new AddMember("addmember", "", "/plot addmember <name>"), new String[]{});
        //commandHandler.registerCommand("removemember", new RemoveMember("removehome", "", "/plot removemember <name>"), new String[]{"rmmember"});
        //commandHandler.registerCommand("SetHome", new SetHomeCommand("sethome", "", "/plot sethome"), new String[]{});
        //commandHandler.registerCommand("deny", new DenyCommand("deny", "", "/plot deny"), new String[]{});
        //commandHandler.registerCommand("undeny", new UnDenyCommand("undeny", "", "/plot undeny"), new String[]{});
        /* ToDo */
        commandHandler.registerCommand("addhelper", new AddHelper("addhelper", "Run the command", "/plot addhelper <name>"), new String[]{"trust"});
        commandHandler.registerCommand("removehelper", new RemoveHelper("removehelper", "Run the command", "/plot removehelper <name>"), new String[]{"rmhelper", "untrust"});
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

    public void generateLevel(String levelName) {
        Settings.levels.add(levelName);
        new PlotSettings(levelName).saveDefault();
        getServer().generateLevel(levelName, 0, Generator.getGenerator("futureplots"));
    }

    public boolean isPlot(Position position) {
        return getPlotByPosition(position) != null;
    }

    public void clearPlot(Plot plot) {
        getServer().getScheduler().scheduleDelayedTask(this, new PlotClearTask(plot), 1, true);
    }

    public Position getPlotPosition(Plot plot) {
        int plotSize = Settings.plotSize;
        int roadWidth = Settings.roadWidth;
        int totalSize = plotSize + roadWidth;
        int x = totalSize * plot.getX();
        int z = totalSize * plot.getZ();
        Level level = getServer().getLevelByName(plot.getLevelName());
        return new Position(x, Settings.groundHeight, z, level);
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
}
