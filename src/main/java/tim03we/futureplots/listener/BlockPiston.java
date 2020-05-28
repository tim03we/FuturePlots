package tim03we.futureplots.listener;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPistonEvent;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

import java.util.List;

public class BlockPiston implements Listener {

    @EventHandler
    public void onPiston(BlockPistonEvent event) {
        if(Settings.levels.contains(event.getBlock().getLevel().getName())) {
            List<Block> blocks = event.getBlocks();
            for (Block block : blocks) {
                Plot plot = FuturePlots.getInstance().getPlotByPosition(block.getLocation());
                if(plot == null) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
