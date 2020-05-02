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

    public int getClaimPrice() {
        return getConfig().getInt("settings.price.claim");
    }

    public int getClearPrice() {
        return getConfig().getInt("settings.price.clear");
    }

    public int getDeletePrice() {
        return getConfig().getInt("settings.price.delete");
    }

    public void initWorld() {
        Config worldConfig = getConfig();
        if(!worldConfig.exists("settings.wallBlock")) worldConfig.set("settings.wallBlock", Settings.wallBlock);
        if(!worldConfig.exists("settings.roadBlock")) worldConfig.set("settings.roadBlock", Settings.roadBlock);
        if(!worldConfig.exists("settings.roadWidth")) worldConfig.set("settings.roadWidth", Settings.roadWidth);
        if(!worldConfig.exists("settings.groundHeight")) worldConfig.set("settings.groundHeight", Settings.groundHeight);
        if(!worldConfig.exists("settings.plotSize")) worldConfig.set("settings.plotSize", Settings.plotSize);
        if(!worldConfig.exists("settings.bottomBlock")) worldConfig.set("settings.bottomBlock", Settings.bottomBlock);
        if(!worldConfig.exists("settings.plotFloorBlock")) worldConfig.set("settings.plotFloorBlock", Settings.plotFloorBlock);
        if(!worldConfig.exists("settings.plotFillBlock")) worldConfig.set("settings.plotFillBlock", Settings.plotFillBlock);
        if(!worldConfig.exists("settings.price.claim")) worldConfig.set("settings.price.claim", Settings.claim_price);
        if(!worldConfig.exists("settings.price.clear")) worldConfig.set("settings.price.clear", Settings.clear_price);
        if(!worldConfig.exists("settings.price.delete")) worldConfig.set("settings.price.delete", Settings.delete_price);
        worldConfig.save();
    }
}
