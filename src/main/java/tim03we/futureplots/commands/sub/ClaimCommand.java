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
import cn.nukkit.level.Position;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.commands.BaseCommand;
import tim03we.futureplots.utils.*;

import static tim03we.futureplots.utils.Settings.plotSize;

public class ClaimCommand extends BaseCommand {

    public ClaimCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            PlotPlayer plotPlayer = new PlotPlayer(player);
            Plot plot = plotPlayer.getPlot();

            if(plot == null) {
                player.sendMessage(translate(true, "not.in.plot"));
                return;
            }
            if(!FuturePlots.getInstance().canClaim(player)) {
                player.sendMessage(translate(true, "plot.max"));
                return;
            }
            if (FuturePlots.provider.hasOwner(plot)) {
                player.sendMessage(translate(true, "plot.claim.already"));
                return;
            }
            String levelName = player.getLevel().getName();
            if(Settings.economyUse && Settings.economyWorlds.contains(levelName)) {
                if(!plotPlayer.bypassEco()) {
                    if((FuturePlots.economyProvider.getMoney(player.getName()) - PlotSettings.getClaimPrice(levelName)) >= 0) {
                        FuturePlots.economyProvider.reduceMoney(player.getName(), PlotSettings.getClaimPrice(levelName));
                    } else {
                        player.sendMessage(translate(true, "economy.no.money"));
                        return;
                    }
                }
            }
            String playerId = player.getName();
            plot.changeBorder(PlotSettings.getWallBlockClaimed(levelName));
            FuturePlots.provider.claimPlot(playerId, plot);
            if(Settings.claim_tp) {
                player.teleport(new Position(plot.getPosition().x += Math.floor(plotSize / 2), plot.getPosition().y += 1.5, plot.getPosition().z -= 1,  plot.getPosition().getLevel()));
            }
            player.sendMessage(translate(true, "plot.claim"));
        }
    }
}
