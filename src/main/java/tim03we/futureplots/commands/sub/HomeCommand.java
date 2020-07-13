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
import tim03we.futureplots.provider.DataProvider;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

public class HomeCommand extends BaseCommand {

    public HomeCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            DataProvider provider = FuturePlots.provider;
            int homeNumber = 1;
            if(provider.getPlots(sender.getName(), null).size() == 0) {
                sender.sendMessage(translate(true, "has.no.plot"));
                return;
            }
            if(Settings.levels.size() > 1) {
                if(args.length > 2) {
                    if(isInteger(args[1])) {
                        if(Settings.levels.contains(args[2])) {
                            if(provider.getPlots(sender.getName(), args[2]).size() > 0) {
                                Plot plot = provider.getPlot(sender.getName(), Integer.parseInt(args[1]), args[2]);
                                if(plot != null) {
                                    if(provider.getHome(plot) != null) {
                                        ((Player) sender).teleport(provider.getHome(plot));
                                    } else {
                                        ((Player) sender).teleport(plot.getBorderPosition());
                                    }
                                    sender.sendMessage(translate(true, "plot.teleport"));
                                } else {
                                    sender.sendMessage(translate(true, "has.no.plot.world"));
                                    return;
                                }
                            } else {
                                sender.sendMessage(translate(true, "has.no.plot.world"));
                                return;
                            }
                        } else {
                            sender.sendMessage(translate(true, "plot.world.required"));
                            return;
                        }
                    } else {
                        sender.sendMessage(translate(true, "plot.world.required"));
                        return;
                    }
                } else {
                    sender.sendMessage(translate(true, "plot.world.required"));
                    return;
                }
                return;
            }
            if(args.length > 1) {
                if(isInteger(args[1])) {
                    if(provider.getPlots(sender.getName(), Settings.levels.get(0)).size() > 0) {
                        Plot plot = provider.getPlot(sender.getName(), Integer.parseInt(args[1]), Settings.levels.get(0));
                        if(plot != null) {
                            if(provider.getHome(plot) != null) {
                                ((Player) sender).teleport(provider.getHome(plot));
                            } else {
                                ((Player) sender).teleport(plot.getBorderPosition());
                            }
                            sender.sendMessage(translate(true, "plot.teleport"));
                        } else {
                            sender.sendMessage(translate(true, "has.no.plot.number", args[1]));
                        }
                    } else {
                        sender.sendMessage(translate(true, "has.no.plot"));
                    }
                }
            } else {
                Plot plot = provider.getPlot(sender.getName(), homeNumber, null);
                if(provider.getHome(plot) != null) {
                    ((Player) sender).teleport(provider.getHome(plot));
                } else {
                    ((Player) sender).teleport(plot.getBorderPosition());
                }
                sender.sendMessage(translate(true, "plot.teleport"));
            }
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
