package tim03we.futureplots.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityShootBowEvent;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotPlayer;
import tim03we.futureplots.utils.Settings;

public class EntityShootBow implements Listener {

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            if(Settings.levels.contains(entity.getLevel().getName())) {
                if(!((Player) entity).isOp()) {
                    Plot plot = new PlotPlayer((Player) entity).getPlot();
                    if(plot == null || !plot.canInteract((Player) entity)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
