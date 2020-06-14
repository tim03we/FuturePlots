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

import cn.nukkit.command.CommandSender;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.commands.BaseCommand;
import tim03we.futureplots.handler.CommandHandler;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends BaseCommand {

    public HelpCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        List<String> commands = new ArrayList<>();
        for (String cmd : CommandHandler.commmands.keySet()) {
            commands.add(cmd);
        }

        int maxPages = commands.size() / 5;
        int page;

        try {
            if (args.length > 1) {
                int tPage = Integer.parseInt(args[1]) - 1;

                page = tPage;
                if (tPage > maxPages) page = maxPages;
            } else page = 0;
        } catch (Exception ex) { /* ignored */ return; }

        sender.sendMessage(translate(false, "plot.help.title"));

        int startFromIndex = page * 5;
        for (int i = 0; i < 5; i++) {
            int at = startFromIndex + i;
            if (commands.size() - 1 >= at) {
                sender.sendMessage(translate(false, "plot.help.text", commands.get(at), FuturePlots.cmds.getString("plot." + commands.get(at) + ".description")));
            }
        }
        sender.sendMessage(translate(false, "plot.help.page", (page + 1), (maxPages + 1)));
    }
}
