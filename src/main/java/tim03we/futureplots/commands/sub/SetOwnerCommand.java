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
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.commands.BaseCommand;
import tim03we.futureplots.utils.*;

public class SetOwnerCommand extends BaseCommand {

    public SetOwnerCommand(String name, String description, String usage) {
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
            if(args.length > 1) {
                if(!plot.canByPass(player)) {
                    player.sendMessage(translate(true, "not.a.owner"));
                    return;
                }
                Player target = Server.getInstance().getPlayerExact(args[1]);
                String targetName = target == null ? args[1].toLowerCase() : target.getName();
                String targetPlayerId = args[1];
                if(targetPlayerId == null) {
                    player.sendMessage(translate(true, "player.not.found"));
                    return;
                }

                if(player.isOp()) {
                    if(!FuturePlots.provider.hasOwner(plot)) {
                        player.sendMessage(translate(true, "not.a.owner"));
                        return;
                    }
                }

                if(!player.isOp()) {
                    if(target == null) {
                        player.sendMessage(translate(true, "plot.setowner.target.not.online"));
                        return;
                    }
                }

                if(target != null) {
                    if(!player.isOp()) {
                        if(!FuturePlots.getInstance().canClaim(target)) {
                            player.sendMessage(translate(true, "plot.setowner.target.max"));
                            return;
                        }
                    }
                    target.sendMessage(translate(true, "plot.setowner.target", plot.getFullID()));
                }

                FuturePlots.provider.setOwner(targetPlayerId, plot);
                for (Plot mergePlot : FuturePlots.provider.getMerges(plot)) {
                    FuturePlots.provider.setOwner(targetPlayerId, mergePlot);
                }

                player.sendMessage(translate(true, "plot.setowner", targetName));
            } else {
                sender.sendMessage(getUsage());
            }
        }
    }
}
