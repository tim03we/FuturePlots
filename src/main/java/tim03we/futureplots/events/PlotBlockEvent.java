package tim03we.futureplots.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.plugin.PluginEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;

public class PlotBlockEvent extends PluginEvent {

    private Event event;
    private Plot plot;

    public PlotBlockEvent(FuturePlots plugin, Event event, Plot plot) {
        super(plugin);
        this.event = event;
        this.plot = plot;
    }

    public Event getEvent() {
        return event;
    }

    public Plot getPlot() {
        return plot;
    }
}
