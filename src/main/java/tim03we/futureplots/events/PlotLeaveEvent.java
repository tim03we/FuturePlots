package tim03we.futureplots.events;

import cn.nukkit.Player;
import cn.nukkit.event.plugin.PluginEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;

public class PlotLeaveEvent extends PluginEvent {

    private Plot plot;
    private Player player;

    public PlotLeaveEvent(FuturePlots plugin, Plot plot, Player player) {
        super(plugin);
        this.plot = plot;
        this.player = player;
    }

    public Plot getPlot() {
        return plot;
    }

    public Player getPlayer() {
        return player;
    }
}
