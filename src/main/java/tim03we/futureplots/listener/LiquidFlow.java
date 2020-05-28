package tim03we.futureplots.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.LiquidFlowEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

public class LiquidFlow implements Listener {

    @EventHandler
    public void onLiquid(LiquidFlowEvent event) {
        if (event.isCancelled()) return;
        if(Settings.levels.contains(event.getBlock().getLevel().getName())) {
            Plot plot = FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation());
            if(plot == null) {
                event.setCancelled(true);
            }
        }
    }
}
