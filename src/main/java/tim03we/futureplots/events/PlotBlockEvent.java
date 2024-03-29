package tim03we.futureplots.events;

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

import cn.nukkit.event.Event;
import cn.nukkit.event.plugin.PluginEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Types;

public class PlotBlockEvent extends PluginEvent {

    private Event event;
    private Plot plot;
    private Types type;

    public PlotBlockEvent(FuturePlots plugin, Event event, Plot plot, Types type) {
        super(plugin);
        this.event = event;
        this.plot = plot;
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public Plot getPlot() {
        return plot;
    }

    public Types getType() {
        return type;
    }
}
