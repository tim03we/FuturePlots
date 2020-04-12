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
import tim03we.futureplots.utils.PlotSettings;

public class HomeCommand extends BaseCommand {

    public HomeCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            int homeNumber = 1;
            if(args.length > 1) {
                homeNumber = Integer.parseInt(args[1]);
            }
            try {
                if(FuturePlots.provider.hasHome(sender.getName(), homeNumber)) {
                    String[] ex = FuturePlots.provider.getPlotId(sender.getName(), homeNumber).split(";");
                    Position pos = FuturePlots.getInstance().getPlotPosition(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ((Player) sender).getLevel().getName()));
                    ((Player) sender).teleport(new Position(pos.x += Math.floor(new PlotSettings(((Player) sender).getLevel().getName()).getPlotSize() / 2), pos.y += 1.5, pos.z -= 1,  pos.getLevel()));
                    sender.sendMessage(translate(true, "plot-tp", null));
                } else {
                    sender.sendMessage(translate(true, "has-no-plot", null));
                }
            } catch (IndexOutOfBoundsException ex) {
                sender.sendMessage(translate(true, "has-no-plot-num", args[1]));
            }
        }
    }
}
