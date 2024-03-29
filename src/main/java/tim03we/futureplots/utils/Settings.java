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

import cn.nukkit.utils.Config;
import tim03we.futureplots.FuturePlots;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    public static ArrayList<String> levels = new ArrayList<>();
    public static String wallBlockClaimed;
    public static String wallBlockUnClaimed;
    public static String roadBlock;
    public static int roadWidth;
    public static int groundHeight;
    public static int plotSize;
    public static String bottomBlock;
    public static String plotFloorBlock;
    public static String plotFillBlock;
    public static int delete_price;
    public static int clear_price;
    public static int claim_price;
    public static int dispose_price;
    public static int erode_price;
    public static int merge_price;

    public static String provider;
    public static String language;
    public static int max_plots;
    public static boolean popup;
    public static boolean debug;
    public static boolean economyUse;
    public static List<String> economyWorlds;
    public static boolean claim_tp;
    public static boolean interaction_confirmation;
    public static boolean formsUse;
    public static boolean metrics;
    public static String xuidWebUrl;

    public static boolean websiteActive = false;
    public static boolean joinServer = false;

    public static boolean use_auto_save;
    public static int auto_save_interval;

    public static boolean damage_from_players;
    public static boolean damage_from_entitys;
    public static boolean damage_fall;

    public static void init() {
        Config config = FuturePlots.getInstance().getConfig();

        wallBlockClaimed = config.getString("default-settings.wall.claimed");
        wallBlockUnClaimed = config.getString("default-settings.wall.unclaimed");
        roadBlock = config.getString("default-settings.roadBlock");
        roadWidth = config.getInt("default-settings.roadWidth");
        groundHeight = config.getInt("default-settings.groundHeight");
        plotSize = config.getInt("default-settings.plotSize");
        bottomBlock = config.getString("default-settings.bottomBlock");
        plotFloorBlock = config.getString("default-settings.plotFloorBlock");
        plotFillBlock = config.getString("default-settings.plotFillBlock");
        provider = config.getString("provider").toLowerCase();
        language = config.getString("lang").toLowerCase();
        max_plots = config.getInt("max-plots");
        metrics = config.getBoolean("metrics");
        xuidWebUrl = config.getString("xuid.url");
        popup = config.getBoolean("show-popup");
        debug = config.getBoolean("debug");
        claim_tp = config.getBoolean("teleport-on-claim");
        formsUse = config.getBoolean("forms");
        interaction_confirmation = config.getBoolean("confirmations");
        economyUse = config.getBoolean("economy.enable");
        economyWorlds = config.getStringList("economy.worlds");
        claim_price = config.getInt("default-settings.price.claim");
        clear_price = config.getInt("default-settings.price.clear");
        delete_price = config.getInt("default-settings.price.delete");
        dispose_price = config.getInt("default-settings.price.dispose");
        erode_price = config.getInt("default-settings.price.erode");
        merge_price = config.getInt("default-settings.price.merge");
        use_auto_save = config.getBoolean("auto-save.use");
        auto_save_interval = config.getInt("auto-save.interval");
        damage_from_players = config.getBoolean("damage.player");
        damage_from_entitys = config.getBoolean("damage.entity");
        damage_fall = config.getBoolean("damage.fall");
    }

}
