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
import cn.nukkit.math.BlockFace;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.commands.BaseCommand;
import tim03we.futureplots.utils.*;

public class MergeCommand extends BaseCommand {

    public MergeCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.hasPermission("futureplots.command.merge")) {
                player.sendMessage(Language.translate(true, "no.permission"));
                return;
            }
            String levelName = player.getLevel().getName();
            if(Settings.economyUse && Settings.economyWorlds.contains(levelName)) {
                if(!new PlotPlayer(player).bypassEco()) {
                    if((FuturePlots.economyProvider.getMoney(player.getName()) - PlotSettings.getMergePrice(levelName)) >= 0) {
                        FuturePlots.economyProvider.reduceMoney(player.getName(), PlotSettings.getMergePrice(levelName));
                    } else {
                        player.sendMessage(translate(true, "economy.no.money"));
                        return;
                    }
                }
            }
            Plot plot = FuturePlots.getInstance().getPlotByPosition(player.getPosition());
            if(plot == null) {
                player.sendMessage(Language.translate(true, "not.in.plot"));
                return;
            }
            if(!plot.canByPass(player)) {
                player.sendMessage(Language.translate(true, "not.a.owner"));
                return;
            }
            String targetPlotPlayerId = FuturePlots.provider.getOwner(plot);
            BlockFace direction = player.getDirection();
            FuturePlots.getInstance().mergePlots(player, plot, direction, targetPlotPlayerId);
        }
    }
}
