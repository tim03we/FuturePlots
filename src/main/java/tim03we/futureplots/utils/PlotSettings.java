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

import java.util.HashMap;

public class PlotSettings {

    public static HashMap<String, HashMap<String, Object>> settings = new HashMap<>();

    private String levelName;

    public PlotSettings(String levelName) {
        this.levelName = levelName;
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/worlds/" + levelName + ".yml", Config.YAML);

        HashMap<String, Object> levelSettings = new HashMap<>();

        // WallBlockClaimed
        String[] ex1 = config.getString("settings.wall.claimed").split(":");
        levelSettings.put("wall.claimed", Block.get(Integer.parseInt(ex1[0]), Integer.parseInt(ex1[1])));

        // WallBlockUnClaimed
        String[] ex2 = config.getString("settings.wall.unclaimed").split(":");
        levelSettings.put("wall.unclaimed", Block.get(Integer.parseInt(ex2[0]), Integer.parseInt(ex2[1])));

        // RoadBlock
        String[] ex3 = config.getString("settings.roadBlock").split(":");
        levelSettings.put("roadBlock", Block.get(Integer.parseInt(ex3[0]), Integer.parseInt(ex3[1])));

        // BottomBlock
        String[] ex4 = config.getString("settings.bottomBlock").split(":");
        levelSettings.put("bottomBlock", Block.get(Integer.parseInt(ex4[0]), Integer.parseInt(ex4[1])));

        // PlotFloorBlock
        String[] ex5 = config.getString("settings.plotFloorBlock").split(":");
        levelSettings.put("plotFloorBlock", Block.get(Integer.parseInt(ex5[0]), Integer.parseInt(ex5[1])));

        // PlotFillBlock
        String[] ex6 = config.getString("settings.plotFillBlock").split(":");
        levelSettings.put("plotFillBlock", Block.get(Integer.parseInt(ex6[0]), Integer.parseInt(ex6[1])));

        // RoadWidth
        levelSettings.put("roadWidth", config.getInt("settings.roadWidth"));

        // GroundHeight
        levelSettings.put("groundHeight", config.getInt("settings.groundHeight"));

        // PlotSize
        levelSettings.put("plotSize", config.getInt("settings.plotSize"));

        // Price Claim
        levelSettings.put("price.claim", config.getInt("settings.price.claim"));

        // Price Clear
        levelSettings.put("price.clear", config.getInt("settings.price.clear"));

        // Price Delete
        levelSettings.put("price.delete", config.getInt("settings.price.delete"));

        // Price Dispose
        levelSettings.put("price.dispose", config.getInt("settings.price.dispose"));

        // Price Erode
        levelSettings.put("price.erode", config.getInt("settings.price.erode"));

        // Price Merge
        levelSettings.put("price.merge", config.getInt("settings.price.merge"));

        PlotSettings.settings.put(levelName, levelSettings);


        if(!config.exists("settings.wall.unclaimed")) config.set("settings.wall.unclaimed", Settings.wallBlockUnClaimed);
        if(!config.exists("settings.wall.claimed")) config.set("settings.wall.claimed", Settings.wallBlockClaimed);
        if(!config.exists("settings.roadBlock")) config.set("settings.roadBlock", Settings.roadBlock);
        if(!config.exists("settings.roadWidth")) config.set("settings.roadWidth", Settings.roadWidth);
        if(!config.exists("settings.groundHeight")) config.set("settings.groundHeight", Settings.groundHeight);
        if(!config.exists("settings.plotSize")) config.set("settings.plotSize", Settings.plotSize);
        if(!config.exists("settings.bottomBlock")) config.set("settings.bottomBlock", Settings.bottomBlock);
        if(!config.exists("settings.plotFloorBlock")) config.set("settings.plotFloorBlock", Settings.plotFloorBlock);
        if(!config.exists("settings.plotFillBlock")) config.set("settings.plotFillBlock", Settings.plotFillBlock);
        if(!config.exists("settings.price.claim")) config.set("settings.price.claim", Settings.claim_price);
        if(!config.exists("settings.price.clear")) config.set("settings.price.clear", Settings.clear_price);
        if(!config.exists("settings.price.delete")) config.set("settings.price.delete", Settings.delete_price);
        if(!config.exists("settings.price.dispose")) config.set("settings.price.dispose", Settings.dispose_price);
        if(!config.exists("settings.price.erode")) config.set("settings.price.erode", Settings.erode_price);
        if(!config.exists("settings.price.merge")) config.set("settings.price.merge", Settings.merge_price);
        config.save();
    }

    private Config getConfig() {
        return new Config(FuturePlots.getInstance().getDataFolder() + "/worlds/" + levelName + ".yml", Config.YAML);
    }


    public static int getRoadWidth(String levelName) {
        return (int) settings.get(levelName).get("roadWidth");
    }

    public static int getGroundHeight(String levelName) {
        return (int) settings.get(levelName).get("groundHeight");
    }

    public static int getPlotSize(String levelName) {
        return (int) settings.get(levelName).get("plotSize");
    }

    public static int getClaimPrice(String levelName) {
        return (int) settings.get(levelName).get("price.claim");
    }

    public static int getClearPrice(String levelName) {
        return (int) settings.get(levelName).get("price.clear");
    }

    public static int getDeletePrice(String levelName) {
        return (int) settings.get(levelName).get("price.delete");
    }

    public static int getDisposePrice(String levelName) {
        return (int) settings.get(levelName).get("price.dispose");
    }

    public static int getErodePrice(String levelName) {
        return (int) settings.get(levelName).get("price.erode");
    }

    public static int getMergePrice(String levelName) {
        return (int) settings.get(levelName).get("price.merge");
    }

    public static Block getWallBlockClaimed(String levelName) {
        return (Block) settings.get(levelName).get("wall.claimed");
    }

    public static Block getWallBlockUnClaimed(String levelName) {
        return (Block) settings.get(levelName).get("wall.unclaimed");
    }

    public static Block getRoadBlock(String levelName) {
        return (Block) settings.get(levelName).get("roadBlock");
    }

    public static Block getBottomBlock(String levelName) {
        return (Block) settings.get(levelName).get("bottomBlock");
    }

    public static Block getPlotFloorBlock(String levelName) {
        return (Block) settings.get(levelName).get("plotFloorBlock");
    }

    public static Block getPlotFillBlock(String levelName) {
        return (Block) settings.get(levelName).get("plotFillBlock");
    }

}
