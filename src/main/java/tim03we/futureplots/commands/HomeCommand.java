package tim03we.futureplots.commands;

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
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;
import tim03we.futureplots.utils.World;

public class HomeCommand extends BaseCommand {

    public HomeCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            int homeNumber = 1;
            if(args.length > 1) {
                try { homeNumber = Integer.parseInt(args[1]);
                } catch (NumberFormatException numberFormatException) { sender.sendMessage(translate(true, "has-no-plot-num", args[1])); return; }
            }
            String[] ex;
            if(FuturePlots.provider.hasHome(sender.getName(), homeNumber)) {
                ex = FuturePlots.provider.getPlotId(sender.getName(), homeNumber).split(";");
            } else {
                sender.sendMessage(translate(true, "has-no-plot", null));
                return;
            }
            Position plotPos;
            if(Settings.levels.size() > 1) {
                if(args.length > 2) {
                    if(!new World(args[2]).exists()) {
                        sender.sendMessage(translate(true, "world-not-exists", null));
                        return;
                    } else {
                        try {
                            if(FuturePlots.provider.hasHomeInLevel(sender.getName(), homeNumber, args[2])) {
                                ex = FuturePlots.provider.getPlotId(sender.getName(), homeNumber, args[2]).split(";");
                                plotPos = FuturePlots.getInstance().getPlotBorderPosition(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), args[2]));
                            } else {
                                sender.sendMessage(translate(true, "has-no-plot-inWorld", null));
                                return;
                            }
                        } catch (IndexOutOfBoundsException e) { sender.sendMessage(translate(true, "has-no-plot-inWorld", null)); return; }
                    }
                } else {
                    sender.sendMessage(translate(true, "worldName-required", null));
                    return;
                }
            } else plotPos = FuturePlots.getInstance().getPlotBorderPosition(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            try {
                ((Player) sender).teleport(plotPos);
                sender.sendMessage(translate(true, "plot-tp", null));
            } catch (IndexOutOfBoundsException exception) {
                sender.sendMessage(translate(true, "has-no-plot-num", args[1]));
            }
        }
    }
}
