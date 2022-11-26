package tim03we.futureplots.commands.sub;

import cn.nukkit.IPlayer;
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

public class TeleportCommand extends BaseCommand {

    public TeleportCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            // String playerId = Utils.getPlayerId(player.getName());
            DataProvider provider = FuturePlots.provider;
            if (args.length < 2) {
                sender.sendMessage(this.getUsage());
                return;
            }

            IPlayer target = FuturePlots.getInstance().getServer().getOfflinePlayer(args[1]);
            if (target == null) {
                sender.sendMessage(translate(true, "player.not.found"));
                return;
            }

            String targetId = Utils.getPlayerId(args[1]);

            int homeNumber = 1;
            if(provider.getPlots(targetId, null).size() == 0) {
                sender.sendMessage(translate(true, "has.no.plot"));
                return;
            }
            String levelName = Settings.levels.size() == 1 ? Settings.levels.get(0) : player.getLevel().getName();
            if(args.length > 2) {
                if(isInteger(args[2])) {
                    homeNumber = Integer.parseInt(args[2]);
                } else {
                    player.sendMessage(translate(true, "has.no.plot.number", args[2]));
                    return;
                }
            }
            if(!Settings.levels.contains(player.getLevel().getName())) {
                if(Settings.levels.size() > 1) {
                    if(args.length > 3) {
                        if(Settings.levels.contains(args[3])) {
                            levelName = args[3];
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
            if(provider.getPlots(targetId, levelName).size() == 0) {
                player.sendMessage(Language.translate(true, "has.no.plot"));
                return;
            }
            Plot plot = provider.getPlot(targetId, homeNumber, levelName);
            if(plot == null) {
                player.sendMessage(translate(true, "has.no.plot.number", args[2]));
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
