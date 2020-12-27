package tim03we.futureplots.utils;

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

import cn.nukkit.Server;
import cn.nukkit.block.Block;
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

    public Block getWallBlockClaimed() {
        String[] ex = getConfig().getString("settings.wall.claimed").split(":");
        return Block.get(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]));
    }

    public Block getWallBlockUnClaimed() {
        String[] ex = getConfig().getString("settings.wall.unclaimed").split(":");
        return Block.get(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]));
    }

    public Block getRoadBlock() {
        String[] ex = getConfig().getString("settings.roadBlock").split(":");
        return Block.get(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]));
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

    public Block getBottomBlock() {
        String[] ex = getConfig().getString("settings.bottomBlock").split(":");
        return Block.get(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]));
    }

    public Block getPlotFloorBlock() {
        String[] ex = getConfig().getString("settings.plotFloorBlock").split(":");
        return Block.get(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]));
    }

    public Block getPlotFillBlock() {
        String[] ex = getConfig().getString("settings.plotFillBlock").split(":");
        return Block.get(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]));
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

    public int getDisposePrice() {
        return getConfig().getInt("settings.price.dispose");
    }

    public int getErodePrice() {
        return getConfig().getInt("settings.price.erode");
    }

    public void initWorld() {
        Config worldConfig = getConfig();
        if(!worldConfig.exists("settings.wall.unclaimed")) worldConfig.set("settings.wall.unclaimed", Settings.wallBlockUnClaimed);
        if(!worldConfig.exists("settings.wall.claimed")) worldConfig.set("settings.wall.claimed", Settings.wallBlockClaimed);
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
        if(!worldConfig.exists("settings.price.dispose")) worldConfig.set("settings.price.dispose", Settings.dispose_price);
        if(!worldConfig.exists("settings.price.erode")) worldConfig.set("settings.price.erode", Settings.erode_price);
        worldConfig.save();
    }
}
