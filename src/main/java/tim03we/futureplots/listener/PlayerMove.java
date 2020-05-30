package tim03we.futureplots.listener;

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
                        if(FuturePlots.provider.getOwner(plot) != null) {
                            player.sendPopup(translate(false, "plot.enter.owned", plot.getX() + ";" + plot.getZ(), FuturePlots.provider.getOwner(plot)));
                        } else {
                            player.sendPopup(translate(false, "plot.enter.free", plot.getX() + ";" + plot.getZ()));
                        }
                    }
                }
            } else if(plotFrom != null && plot == null) {
                new PlotEvent(new PlotLeaveEvent(FuturePlots.getInstance(), plot, player));
            }
        }
    }
}
