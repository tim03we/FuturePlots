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
import tim03we.futureplots.commands.BaseCommand;

public class FlagCommand extends BaseCommand {

    public static String[] flags = new String[]{"pvp"};

    public FlagCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(sender instanceof Player) {
            if(args.length > 1) {
                if(flagExists(args[1])) {
                    //
                } else {
                    sender.sendMessage("");
                }
            }
        }
    }

    private boolean flagExists(String flag) {
        for (String s : flags) {
            if(s.equals(flag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
