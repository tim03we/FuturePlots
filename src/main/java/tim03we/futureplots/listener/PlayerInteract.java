package tim03we.futureplots.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDragonEgg;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.ItemEdible;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotPlayer;
import tim03we.futureplots.utils.Settings;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels.contains(player.getLevel().getName())) {
            if(!player.isOp()) {
                Plot plot = FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation());
                if(event.getItem() instanceof ItemEdible && event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
                    return;
                }
                if(plot != null) {
                    if(!plot.canInteract(player)) {
                        if(event.getItem().canBeActivated() || event.getBlock().canBeActivated()) {
                            event.setCancelled(true);
                        } else {
                            plot = new PlotPlayer(player).getPlot();
                            if(plot == null || !plot.canInteract(player)) {
                                event.setCancelled(true);
                            }
                        }
                    } else {
                        if(event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                            if(event.getBlock() instanceof BlockDragonEgg) {
                                event.setCancelled(true);
                            }
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
