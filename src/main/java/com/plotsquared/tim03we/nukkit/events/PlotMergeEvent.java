package com.plotsquared.tim03we.nukkit.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import com.intellectualcrafters.plot.object.Plot;

public class PlotMergeEvent extends PlotEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final int dir;
    private final int max;
    private final Level world;
    private boolean cancelled;

    /**
     * PlotMergeEvent: Called when plots are merged
     *
     * @param world World in which the event occurred
     * @param plot  Plot that was merged
     * @param dir   The direction of the merge
     * @param max   Max merge size
     */
    public PlotMergeEvent(Level world, Plot plot, final int dir, final int max) {
        super(plot);
        this.world = world;
        this.dir = dir;
        this.max = max;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Level getLevel() {
        return this.world;
    }

    public int getDir() {
        return this.dir;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
