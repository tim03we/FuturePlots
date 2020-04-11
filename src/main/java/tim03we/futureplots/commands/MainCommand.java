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

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import tim03we.futureplots.handler.CommandHandler;

public class MainCommand extends Command {

    public MainCommand() {
        super("plot", "FuturePlots Command", "/plots <sub-command>");
        setAliases(new String[]{"plots", "p"});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!testPermission(sender)) {
            return false;
        }
        if(args.length > 0) {
            /*StringBuilder sb = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                if (i > 1) sb.append(" ");
                sb.append(args[i]);
            }*/
            if(CommandHandler.commmands.get(args[0]) != null ) {
                CommandHandler.commmands.get(args[0]).execute(sender, args[0], args);
            } else if (CommandHandler.aliases.get(args[0]) != null){
                CommandHandler.aliases.get(args[0]).execute(sender, args[0], args);
            } else {
                sender.sendMessage("Befehl existiert nicht.");
            }
        } else {
            sender.sendMessage(getUsage());
        }
        return false;
    }
}
