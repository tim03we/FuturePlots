package com.intellectualcrafters.plot.util;

import com.google.common.collect.BiMap;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.*;
import com.intellectualcrafters.plot.uuid.UUIDWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class UUIDHandler {

    public static UUIDHandlerImplementation implementation;

    public static void add(StringWrapper name, UUID uuid) {
        implementation.add(name, uuid);
    }

    /**
     * Get the map containing all names/uuids.
     *
     * @return map with names + uuids
     *
     * @see BiMap
     */
    public static BiMap<StringWrapper, UUID> getUuidMap() {
        return implementation.getUUIDMap();
    }

    /**
     * Check if a uuid is cached
     *
     * @param uuid to check
     *
     * @return true of the uuid is cached
     *
     * @see BiMap#containsValue(Object)
     */
    public static boolean uuidExists(UUID uuid) {
        return implementation.uuidExists(uuid);
    }

    /**
     * Check if a name is cached
     *
     * @param name to check
     *
     * @return true of the name is cached
     *
     * @see BiMap#containsKey(Object)
     */
    public static boolean nameExists(StringWrapper name) {
        return implementation.nameExists(name);
    }

    public static HashSet<UUID> getAllUUIDS() {
        final HashSet<UUID> uuids = new HashSet<>();
        PS.get().foreachPlotRaw(new RunnableVal<Plot>() {
            @Override
            public void run(Plot plot) {
                if (plot.hasOwner()) {
                    uuids.add(plot.owner);
                    uuids.addAll(plot.getTrusted());
                    uuids.addAll(plot.getMembers());
                    uuids.addAll(plot.getDenied());
                }
            }
        });
        return uuids;
    }

    public static UUIDWrapper getUUIDWrapper() {
        return implementation.getUUIDWrapper();
    }

    public static void setUUIDWrapper(UUIDWrapper wrapper) {
        implementation.setUUIDWrapper(wrapper);
    }

    public static void startCaching(Runnable whenDone) {
        implementation.startCaching(whenDone);
    }

    public static void cache(BiMap<StringWrapper, UUID> toAdd) {
        implementation.add(toAdd);
    }

    public static UUID getUUID(PlotPlayer player) {
        if (implementation == null) {
            return null;
        }
        return implementation.getUUID(player);
    }

    public static UUID getUUID(OfflinePlotPlayer player) {
        if (implementation == null) {
            return null;
        }
        return implementation.getUUID(player);
    }

    public static String getName(UUID uuid) {
        if (implementation == null) {
            return null;
        }
        return implementation.getName(uuid);
    }

    public static PlotPlayer getPlayer(UUID uuid) {
        if (implementation == null) {
            return null;
        }
        return check(implementation.getPlayer(uuid));
    }

    public static PlotPlayer getPlayer(String name) {
        if (implementation == null) {
            return null;
        }
        return check(implementation.getPlayer(name));
    }

    private static PlotPlayer check(PlotPlayer plr) {
        if (plr != null && !plr.isOnline()) {
            UUIDHandler.getPlayers().remove(plr.getName());
            PS.get().IMP.unregister(plr);
            plr = null;
        }
        return plr;
    }

    public static UUID getUUIDFromString(String nameOrUUIDString) {
        if (implementation == null) {
            return null;
        }
        if (nameOrUUIDString.length() > 16) {
            try {
                return UUID.fromString(nameOrUUIDString);
            } catch (IllegalArgumentException  e) {
                return null;
            }
        }
        return UUIDHandler.getUUID(nameOrUUIDString, null);
    }

    public static UUID getUUID(String name, RunnableVal<UUID> ifFetch) {
        if (implementation == null) {
            return null;
        }
        return implementation.getUUID(name, ifFetch);
    }

    public static UUID getCachedUUID(String name, RunnableVal<UUID> ifFetch) {
        if (implementation == null) {
            return null;
        }
        return implementation.getUUIDMap().get(new StringWrapper(name));
    }

    public static Map<String, PlotPlayer> getPlayers() {
        if (implementation == null) {
            return new HashMap<>();
        }
        return implementation.getPlayers();
    }

    public static void handleShutdown() {
        if (implementation == null) {
            return;
        }
        implementation.handleShutdown();
    }
}
