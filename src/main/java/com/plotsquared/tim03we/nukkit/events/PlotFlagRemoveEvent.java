package com.plotsquared.tim03we.nukkit.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.object.Plot;

/**
 * Called when a flag is removed from a plot
 *
 */
public class PlotFlagRemoveEvent extends PlotEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Flag flag;
    private boolean cancelled;

    /**
     * PlotFlagRemoveEvent: Called when a flag is removed from a plot
     *
     * @param flag Flag that was removed
     * @param plot Plot from which the flag was removed
     */
    public PlotFlagRemoveEvent(Flag flag, Plot plot) {
        super(plot);
        this.flag = flag;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the flag involved
     *
     * @return Flag
     */
    public Flag getFlag() {
        return this.flag;
    }

    @Override
    public final boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public final void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
