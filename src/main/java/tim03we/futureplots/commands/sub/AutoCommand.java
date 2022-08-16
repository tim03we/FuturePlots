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

import static tim03we.futureplots.utils.Settings.max_plots;

public class AutoCommand extends BaseCommand {

    public AutoCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(Settings.levels.size() == 0) {
                sender.sendMessage(translate(true, "no.plotworld"));
                return;
            }
            String levelName = player.getLevel().getName();
            if(Settings.levels.size() > 1) {
                if(!Settings.levels.contains(levelName)) {
                    sender.sendMessage(translate(true, "not.in.world"));
                    return;
                }
            }
            if(FuturePlots.getInstance().canClaim(player)) {
                if(Settings.economy) {
                    if(!new PlotPlayer((Player) sender).bypassEco()) {
                        if((FuturePlots.economyProvider.getMoney(sender.getName()) - PlotSettings.getClaimPrice(levelName)) >= 0) {
                            FuturePlots.economyProvider.reduceMoney(sender.getName(), PlotSettings.getClaimPrice(levelName));
                        } else {
                            sender.sendMessage(translate(true, "economy.no.money"));
                            return;
                        }
                    }
                }
                Plot plot = FuturePlots.provider.getNextFreePlot(levelName);
                plot.changeBorder(PlotSettings.getWallBlockClaimed(levelName));
                FuturePlots.provider.claimPlot(sender.getName(), plot);
                ((Player) sender).teleport(plot.getBorderPosition());
                sender.sendMessage(translate(true, "plot.claim"));
            } else {
                sender.sendMessage(translate(true, "plot.max"));
            }
        }
    }
}
