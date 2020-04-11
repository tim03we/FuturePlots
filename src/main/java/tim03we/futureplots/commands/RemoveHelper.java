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
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.Plot;
import tim03we.futureplots.provider.Provider;

public class RemoveHelper extends BaseCommand {

    public RemoveHelper(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            if(FuturePlots.getInstance().isInPlot((Player) sender)) {
                Plot plot = FuturePlots.getInstance().getPlotByPosition(((Player) sender).getPosition());
                if(new Provider().isOwner(sender.getName(), plot)) {
                    if (args.length > 1) {
                        if (new Provider().isHelper(args[1], plot)) {
                            new Provider().removeHelper(args[1], plot);
                            sender.sendMessage(translate(true, "helper-removed", args[1]));
                        } else {
                            sender.sendMessage(translate(true, "no-helper", null));
                        }
                    } else {
                        sender.sendMessage(getUsage());
                    }
                } else {
                    sender.sendMessage(translate(true, "not-a-owner", null));
                }
            } else {
                sender.sendMessage(translate(true, "not-in-plot", null));
            }
        }
    }
}
