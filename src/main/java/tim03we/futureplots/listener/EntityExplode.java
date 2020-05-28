package tim03we.futureplots.listener;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityExplodeEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

import java.util.ArrayList;

public class EntityExplode implements Listener {

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if(event.isCancelled()) return;
        if(Settings.levels.contains(entity.getLevel().getName())) {
            ArrayList<Block> allowedBlocks = new ArrayList<>();
            for (Block block : event.getBlockList()) {
                Plot plot = FuturePlots.getInstance().getPlotByPosition(block.getLocation());
                if(plot != null) {
                    allowedBlocks.add(block);
                }
            }
            event.setBlockList(allowedBlocks);
        }
    }
}
