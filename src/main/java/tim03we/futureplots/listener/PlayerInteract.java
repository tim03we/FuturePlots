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
                        if(event.getAction() == PlayerInteractEvent.Action.PHYSICAL) {
                            event.setCancelled(true);
                            return;
                        }
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
