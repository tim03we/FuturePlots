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

public class ErodeCommand extends BaseCommand {

    public ErodeCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Plot plot = new PlotPlayer(player).getPlot();
            if(plot == null) {
                player.sendMessage(translate(true, "not.in.plot"));
                return;
            }
            if(!plot.canByPass(player)) {
                player.sendMessage(translate(true, "not.a.owner"));
                return;
            }
            String levelName = plot.getLevelName();
            if(!player.isOp()) {
                if(Settings.interaction_confirmation && args.length < 2) {
                    player.sendMessage(translate(true, "interaction.confirmation", getUsage()));
                    return;
                } else {
                    if(!args[1].equalsIgnoreCase("confirm")) {
                        player.sendMessage(translate(true, "interaction.confirmation", getUsage()));
                        return;
                    }
                }
            }

            if(Settings.economyUse && Settings.economyWorlds.contains(levelName)) {
                if(!new PlotPlayer(player).bypassEco()) {
                    if((FuturePlots.economyProvider.getMoney(player.getName()) - PlotSettings.getErodePrice(levelName)) >= 0) {
                        FuturePlots.economyProvider.reduceMoney(player.getName(), PlotSettings.getErodePrice(levelName));
                    } else {
                        player.sendMessage(translate(true, "economy.no.money"));
                        return;
                    }
                }
            }
            player.teleport(plot.getBorderPosition());
            FuturePlots.getInstance().erodePlot(plot);
            player.sendMessage(translate(true, "plot.erode"));
        }
    }
}
