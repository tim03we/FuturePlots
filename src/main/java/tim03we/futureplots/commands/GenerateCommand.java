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

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.utils.Config;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.PlotSettings;
import tim03we.futureplots.utils.Settings;

import java.util.HashMap;
import java.util.Map;

public class GenerateCommand extends BaseCommand {

    public GenerateCommand(String name, String description, String usage) {
        super(name, description, usage);
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(args.length > 1) {
            Config worldConfig = new PlotSettings(args[1]).getConfig();
            worldConfig.set("settings.wallBlock", Settings.wallBlock);
            worldConfig.set("settings.roadBlock", Settings.roadBlock);
            worldConfig.set("settings.roadWidth", Settings.roadWidth);
            worldConfig.set("settings.groundHeight", Settings.groundHeight);
            worldConfig.set("settings.plotSize", Settings.plotSize);
            worldConfig.set("settings.bottomBlock", Settings.bottomBlock);
            worldConfig.set("settings.plotFloorBlock", Settings.plotFloorBlock);
            worldConfig.set("settings.plotFillBlock", Settings.plotFillBlock);
            worldConfig.save();
            Settings.levels.add(args[1]);
            Map<String, Object> options = new HashMap<>();
            options.put("preset", args[1]);
            FuturePlots.getInstance().generateLevel(args[1]);
            Server.getInstance().generateLevel(args[1], 0, Generator.getGenerator("futureplots"), options);
        }
    }
}
