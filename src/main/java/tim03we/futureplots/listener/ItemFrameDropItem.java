package tim03we.futureplots.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

public class ItemFrameDropItem implements Listener {

    @EventHandler
    public void onItemFrameDropItem(ItemFrameDropItemEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels.contains(player.getLevel().getName())) {
            Plot plot = FuturePlots.getInstance().getPlot(event.getItemFrame().getLocation());
            if(!player.isOp()) {
                if(plot == null) {
                    event.setCancelled(true);
                    return;
                }
                if(!plot.canInteract(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
