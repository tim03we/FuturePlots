package com.intellectualcrafters.plot.util;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.general.commands.CommandCaller;

import java.util.HashMap;

/**
 * The Permissions class handles checking user permissions.<br>
 *  - This will respect * nodes and plots.admin and can be used to check permission ranges (e.g. plots.plot.5)<br>
 *  - Checking the PlotPlayer class directly will not take the above into account<br> 
 */
public class Permissions {

    public static boolean hasPermission(PlotPlayer player, C caption, boolean notify) {
        return hasPermission(player, caption.s(), notify);
    }

    /**
     * Check if a player has a permission (C class helps keep track of permissions).
     * @param player
     * @param caption
     * @return
     */
    public static boolean hasPermission(PlotPlayer player, C caption) {
        return hasPermission(player, caption.s());
    }
    
    /**
     * Check if a {@link PlotPlayer} has a permission.
     * @param player
     * @param permission
     * @return
     */
    public static boolean hasPermission(PlotPlayer player, String permission) {
        if (!Settings.Enabled_Components.PERMISSION_CACHE) {
            return hasPermission((CommandCaller) player, permission);
        }
        HashMap<String, Boolean> map = player.getMeta("perm");
        if (map != null) {
            Boolean result = map.get(permission);
            if (result != null) {
                return result;
            }
        } else {
            map = new HashMap<>();
            player.setMeta("perm", map);
        }
        boolean result = hasPermission((CommandCaller) player, permission);
        map.put(permission, result);
        return result;
    }
    
    /**
     * Check if a {@code CommandCaller} has a permission.
     * @param caller
     * @param permission
     * @return
     */
    public static boolean hasPermission(CommandCaller caller, String permission) {
        if (caller.hasPermission(permission)) {
            return true;
        } else if (caller.isPermissionSet(permission)) {
            return false;
        }
        if (caller.hasPermission(C.PERMISSION_ADMIN.s())) {
            return true;
        }
        permission = permission.toLowerCase().replaceAll("^[^a-z|0-9|\\.|_|-]", "");
        String[] nodes = permission.split("\\.");
        StringBuilder n = new StringBuilder();
        for (int i = 0; i <= (nodes.length - 1); i++) {
            n.append(nodes[i] + ".");
            String combined = n + C.PERMISSION_STAR.s();
            if (!permission.equals(combined)) {
                if (caller.hasPermission(combined)) {
                    return true;
                } else if (caller.isPermissionSet(combined)) {
                    return false;
                }
            }
        }
        return false;
    }
    
    /**
     * Check if a PlotPlayer has a permission, and optionally send the no permission message if applicable.
     * @param player
     * @param permission
     * @param notify
     * @return 
     */
    public static boolean hasPermission(PlotPlayer player, String permission, boolean notify) {
        if (!hasPermission(player, permission)) {
            if (notify) {
                MainUtil.sendMessage(player, C.NO_PERMISSION_EVENT, permission);
            }
            return false;
        }
        return true;
    }

    public static int hasPermissionRange(PlotPlayer player, C perm, int range) {
        return hasPermissionRange(player, perm.s(), range);
    }

    /**
     * Check the the highest permission a PlotPlayer has within a specified range.<br>
     *  - Excessively high values will lag<br>
     *  - The default range that is checked is {@link Settings.Limit#MAX_PLOTS}<br>
     * @param player
     * @param stub The permission stub to check e.g. for `plots.plot.#` the stub is `plots.plot`
     * @param range The range to check
     * @return The highest permission they have within that range
     */
    public static int hasPermissionRange(PlotPlayer player, String stub, int range) {
        return player.hasPermissionRange(stub, range);
    }
}
