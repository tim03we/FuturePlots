package tim03we.futureplots.commands;

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
import tim03we.futureplots.utils.Language;

public class BaseCommand extends Language {

    private final String name;
    private final String description;
    private final String usage;

    public BaseCommand(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    public void execute(CommandSender sender, String command, String[] args) {
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public String getUsage() {
        return this.usage;
    }
}
