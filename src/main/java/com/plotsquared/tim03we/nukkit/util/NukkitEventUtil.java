package com.plotsquared.tim03we.nukkit.util;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.object.*;
import com.intellectualcrafters.plot.util.EventUtil;
import com.plotsquared.tim03we.nukkit.NukkitMain;
import com.plotsquared.tim03we.nukkit.events.*;
import com.plotsquared.tim03we.nukkit.object.NukkitPlayer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class NukkitEventUtil extends EventUtil {

    private final NukkitMain plugin;

    public NukkitEventUtil(NukkitMain plugin) {
        this.plugin = plugin;
    }

    public Player getPlayer(PlotPlayer player) {
        if (player instanceof NukkitPlayer) {
            return ((NukkitPlayer) player).player;
        }
        return null;
    }

    public boolean callEvent(Event event) {
        plugin.getServer().getPluginManager().callEvent(event);
        return !(event instanceof Cancellable) || !event.isCancelled();
    }

    @Override
    public boolean callClaim(PlotPlayer player, Plot plot, boolean auto) {
        return callEvent(new PlayerClaimPlotEvent(getPlayer(player), plot, auto));
    }

    @Override
    public boolean callTeleport(PlotPlayer player, Location from, Plot plot) {
        return callEvent(new PlayerTeleportToPlotEvent(getPlayer(player), from, plot));
    }

    @Override
    public boolean callComponentSet(Plot plot, String component) {
        return callEvent(new PlotComponentSetEvent(plot, component));
    }

    @Override
    public boolean callClear(Plot plot) {
        return callEvent(new PlotClearEvent(plot));
    }

    @Override
    public boolean callDelete(Plot plot) {
        return callEvent(new PlotDeleteEvent(plot));
    }

    @Override
    public boolean callFlagAdd(Flag flag, Plot plot) {
        return callEvent(new PlotFlagAddEvent(flag, plot));
    }

    @Override
    public boolean callFlagRemove(Flag<?> flag, Plot plot, Object value) {
        return callEvent(new PlotFlagRemoveEvent(flag, plot));
    }

    @Override
    public boolean callMerge(Plot plot, int dir, int max) {
        return callEvent(new PlotMergeEvent(NukkitUtil.getWorld(plot.getWorldName()), plot, dir, max));
    }

    @Override
    public boolean callAutoMerge(Plot plot, ArrayList<PlotId> plots) {
        return callEvent(new PlotAutoMergeEvent(NukkitUtil.getWorld(plot.getWorldName()), plot, plots));
    }

    @Override
    public boolean callUnlink(PlotArea area, ArrayList<PlotId> plots) {
        return callEvent(new PlotUnlinkEvent(NukkitUtil.getWorld(area.worldname), area, plots));
    }

    @Override
    public void callEntry(PlotPlayer player, Plot plot) {
        callEvent(new PlayerEnterPlotEvent(getPlayer(player), plot));
    }

    @Override
    public void callLeave(PlotPlayer player, Plot plot) {
        callEvent(new PlayerLeavePlotEvent(getPlayer(player), plot));
    }

    @Override
    public void callDenied(PlotPlayer initiator, Plot plot, UUID player, boolean added) {
        callEvent(new PlayerPlotDeniedEvent(getPlayer(initiator), plot, player, added));
    }

    @Override
    public void callTrusted(PlotPlayer initiator, Plot plot, UUID player, boolean added) {
        callEvent(new PlayerPlotTrustedEvent(getPlayer(initiator), plot, player, added));
    }

    @Override
    public void callMember(PlotPlayer initiator, Plot plot, UUID player, boolean added) {
        callEvent(new PlayerPlotHelperEvent(getPlayer(initiator), plot, player, added));
    }

    @Override
    public boolean callOwnerChange(PlotPlayer initiator, Plot plot, UUID oldOwner, UUID newOwner, boolean hasOldOwner) {
        return callEvent(new PlotChangeOwnerEvent(getPlayer(initiator), plot, oldOwner, newOwner, hasOldOwner));
    }

    @Override
    public boolean callFlagRemove(Flag flag, Object object, PlotCluster cluster) {
        return callEvent(new ClusterFlagRemoveEvent(flag, cluster));
    }

    @Override
    @Nullable
    public Rating callRating(PlotPlayer player, Plot plot, Rating rating) {
        PlotRateEvent event = new PlotRateEvent(player, rating, plot);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return null;
        }
        return event.getRating();
    }

}
