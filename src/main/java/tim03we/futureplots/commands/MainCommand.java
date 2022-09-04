package tim03we.futureplots.commands;

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
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.handler.CommandHandler;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotPlayer;
import tim03we.futureplots.utils.Settings;
import tim03we.futureplots.utils.forms.data.FormWindows;

import java.util.ArrayList;
import java.util.HashMap;

public class MainCommand extends Command {

    public MainCommand() {
        super(FuturePlots.cmds.getString("plot.name"), FuturePlots.cmds.getString("plot.description"), FuturePlots.cmds.getString("plot.usage"));
        setAliases(FuturePlots.cmds.getStringList("plot.alias").toArray(new String[0]));
        commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter(FuturePlots.cmds.getString("plot.parameters.subcommand"), false, convertToStringArray(CommandHandler.commmands, CommandHandler.aliases)),
                new CommandParameter(FuturePlots.cmds.getString("plot.parameters.player"), CommandParamType.TARGET, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!testPermission(sender)) {
            return false;
        }
        if(args.length > 0) {
            if(!CommandHandler.runCmd(sender, args[0], args)) {
                sender.sendMessage(getUsage());
            }
        } else {
            if(Settings.formsUse) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    PlotPlayer plotPlayer = new PlotPlayer(player);
                    Plot plot = plotPlayer.getPlot();
                    if(plot == null) {
                        FormWindows.openNoPlotForm(player);
                    } else {
                        if(FuturePlots.provider.hasOwner(plot)) {
                            FormWindows.openPlotForm(player, plot);
                        } else {
                            FormWindows.openNoPlotForm(player);
                        }
                    }
                }
            } else {
                sender.sendMessage(getUsage());
            }
        }
        return false;
    }

    private String[] convertToStringArray(HashMap<String, BaseCommand> map, HashMap<String, BaseCommand> map2) {
        ArrayList<String> commands = new ArrayList<>();
        for (String command : map.keySet()) {
            commands.add(command);
        }
        for (String alias : map2.keySet()) {
            commands.add(alias);
        }
        String[] array = new String[commands.size()];
        for(int j = 0; j < commands.size(); j++){
            array[j] = commands.get(j);
        }
        return array;
    }
}
