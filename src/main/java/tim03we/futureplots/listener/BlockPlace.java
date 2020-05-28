package tim03we.futureplots.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.events.PlotBlockEvent;
import tim03we.futureplots.events.PlotEvent;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

public class BlockPlace implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels.contains(player.getLevel().getName())) {
            Plot plot = FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation());
            new PlotEvent(new PlotBlockEvent(FuturePlots.getInstance(), event, plot));
            if(!player.isOp()) {
                if(plot != null) {
                    if(!plot.canInteract(player)) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
