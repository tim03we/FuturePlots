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
import tim03we.futureplots.provider.DataProvider;
import tim03we.futureplots.utils.Language;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;
import tim03we.futureplots.utils.Utils;

public class HomeCommand extends BaseCommand {

    public HomeCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String playerId = player.getName();
            DataProvider provider = FuturePlots.provider;
            int homeNumber = 1;
            if(provider.getPlots(playerId, null).size() == 0) {
                sender.sendMessage(translate(true, "has.no.plot"));
                return;
            }
            String levelName = Settings.levels.size() == 1 ? Settings.levels.get(0) : player.getLevel().getName();
            if(args.length > 1) {
                if(isInteger(args[1])) {
                    homeNumber = Integer.parseInt(args[1]);
                } else {
                    player.sendMessage(Language.translate(true, "has.no.plot.number", args[1]));
                    return;
                }
            }
            if(!Settings.levels.contains(player.getLevel().getName())) {
                if(Settings.levels.size() > 1) {
                    if(args.length > 2) {
                        if(Settings.levels.contains(args[2])) {
                            levelName = args[2];
                        } else {
                            player.sendMessage(translate(true, "plot.world.not.exists"));
                            return;
                        }
                    } else {
                        player.sendMessage(translate(true, "plot.world.required"));
                        return;
                    }
                }
            }
            if(provider.getPlots(playerId, levelName).size() == 0) {
                player.sendMessage(Language.translate(true, "has.no.plot"));
                return;
            }
            Plot plot = provider.getPlot(playerId, homeNumber, levelName);
            if(plot == null) {
                player.sendMessage(translate(true, "has.no.plot.number", args[1]));
                return;
            }
            if(FuturePlots.provider.getOriginPlot(plot) != null && FuturePlots.provider.getMerges(plot).isEmpty()) {
                plot = FuturePlots.provider.getOriginPlot(plot);
            }
            Position position = plot.getBorderPosition();
            if(provider.getHome(plot) != null) {
                position = provider.getHome(plot);
            }
            player.teleport(position);
        }
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
