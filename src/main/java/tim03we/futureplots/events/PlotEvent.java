package tim03we.futureplots.events;

import cn.nukkit.Server;
import cn.nukkit.event.Event;

public class PlotEvent {

    public PlotEvent(Event event) {
        Server.getInstance().getPluginManager().callEvent(event);
    }
}
