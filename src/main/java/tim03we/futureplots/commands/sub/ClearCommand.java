package tim03we.futureplots.commands.sub;

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
import cn.nukkit.command.CommandSender;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.commands.BaseCommand;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotPlayer;
import tim03we.futureplots.utils.PlotSettings;
import tim03we.futureplots.utils.Settings;

public class ClearCommand extends BaseCommand {

    public ClearCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Plot plot = new PlotPlayer((Player) sender).getPlot();
            if(plot != null) {
                if(plot.canByPass((Player) sender)) {
                    String levelName = player.getLevel().getName();
                    if(Settings.economy) {
                        if(!new PlotPlayer((Player) sender).bypassEco()) {
                            if((FuturePlots.economyProvider.getMoney(sender.getName()) - PlotSettings.getClearPrice(levelName)) >= 0) {
                                FuturePlots.economyProvider.reduceMoney(sender.getName(), PlotSettings.getClearPrice(levelName));
                            } else {
                                sender.sendMessage(translate(true, "economy.no.money"));
                                return;
                            }
                        }
                    }
                    if(FuturePlots.getInstance().isMerge(plot)) {
                        FuturePlots.getInstance().resetMerges(plot, false);
                    }
                    ((Player) sender).teleport(plot.getBorderPosition());
                    FuturePlots.getInstance().clearPlot(plot);
                    sender.sendMessage(translate(true, "plot.clear"));
                } else {
                    sender.sendMessage(translate(true, "not.a.owner"));
                }
            } else {
                sender.sendMessage(translate(true, "not.in.plot"));
            }
        }
    }
}
