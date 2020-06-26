package tim03we.futureplots.listener;

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

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.events.PlotEnterEvent;
import tim03we.futureplots.events.PlotEvent;
import tim03we.futureplots.events.PlotLeaveEvent;
import tim03we.futureplots.utils.Language;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

public class PlayerMove extends Language implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels != null && Settings.levels.contains(player.getLevel().getName())) {
            Plot plot = FuturePlots.getInstance().getPlotByPosition(event.getTo());
            Plot plotFrom = FuturePlots.getInstance().getPlotByPosition(event.getFrom());
            if(plot != null && plotFrom == null) {
                if(FuturePlots.provider.isDenied(player.getName(), plot) && !FuturePlots.provider.getOwner(plot).equals(player.getName()) && !FuturePlots.provider.isHelper(player.getName(), plot)) {
                    event.setCancelled(true);
                } else {
                    new PlotEvent(new PlotEnterEvent(FuturePlots.getInstance(), plot, player));
                    if(Settings.popup) {
                        if(FuturePlots.provider.hasOwner(plot)) {
                            player.sendPopup(translate(false, "plot.enter.owned", plot.getX() + ";" + plot.getZ(), FuturePlots.provider.getOwner(plot)));
                        } else {
                            player.sendPopup(translate(false, "plot.enter.free", plot.getX() + ";" + plot.getZ()));
                        }
                    }
                }
            } else if(plotFrom != null && plot == null) {
                new PlotEvent(new PlotLeaveEvent(FuturePlots.getInstance(), plotFrom, player));
            }
        }
    }
}
