package tim03we.futureplots.utils;

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
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Settings;

public class PlotSettings {

    private String levelName;

    public PlotSettings(String levelName) {
        this.levelName = levelName;
    }

    public Config getConfig() {
        return new Config(FuturePlots.getInstance().getDataFolder() + "/worlds/" + levelName + ".yml", Config.YAML);
    }

    public Level getLevel() {
        return Server.getInstance().getLevelByName(levelName);
    }

    public int getWallBlock() {
        return getConfig().getInt("settings.wallBlock");
    }

    public int getRoadBlock() {
        return getConfig().getInt("settings.roadBlock");
    }

    public int getRoadWidth() {
        return getConfig().getInt("settings.roadWidth");
    }

    public int getGroundHeight() {
        return getConfig().getInt("settings.groundHeight");
    }

    public int getPlotSize() {
        return getConfig().getInt("settings.plotSize");
    }

    public int getBottomBlock() {
        return getConfig().getInt("settings.bottomBlock");
    }

    public int getPlotFloorBlock() {
        return getConfig().getInt("settings.plotFloorBlock");
    }

    public int getPlotFillBlock() {
        return getConfig().getInt("settings.plotFillBlock");
    }

    public void saveDefault() {
        Config worldConfig = getConfig();
        worldConfig.set("settings.wallBlock", Settings.wallBlock);
        worldConfig.set("settings.roadBlock", Settings.roadBlock);
        worldConfig.set("settings.roadWidth", Settings.roadWidth);
        worldConfig.set("settings.groundHeight", Settings.groundHeight);
        worldConfig.set("settings.plotSize", Settings.plotSize);
        worldConfig.set("settings.bottomBlock", Settings.bottomBlock);
        worldConfig.set("settings.plotFloorBlock", Settings.plotFloorBlock);
        worldConfig.set("settings.plotFillBlock", Settings.plotFillBlock);
        worldConfig.save();
    }
}
