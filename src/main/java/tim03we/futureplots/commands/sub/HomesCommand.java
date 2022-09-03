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
import tim03we.futureplots.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomesCommand extends BaseCommand {

    public HomesCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String playerId = Utils.getPlayerId(player.getName());
            if(FuturePlots.provider.getPlots(playerId, null).size() == 0) {
                player.sendMessage(translate(true, "has.no.plot"));
                return;
            }
            List<String> show = new ArrayList<>();
            sender.sendMessage(translate(true, "plot.homes.title"));
            for (String home : FuturePlots.provider.getPlots(playerId, null)) {
                String[] ex = home.split(";");
                Plot plot = new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]);
                if(FuturePlots.provider.getOriginPlot(plot) != null && FuturePlots.provider.getMerges(plot).isEmpty()) {
                    plot = FuturePlots.provider.getOriginPlot(plot);
                }
                if(!show.contains(plot.getFullID())) {
                    show.add(plot.getFullID());
                    player.sendMessage(translate(false, "plot.homes.text", plot.getLevelName(), plot.getFullID()));
                }
            }
        }
    }
}
