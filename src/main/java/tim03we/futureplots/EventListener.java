package tim03we.futureplots;

/*
 * This software is distributed under "GNU General Public License v3.0".
 * This license allows you to use it and/or modify it but you are not at
 * all allowed to sell this plugin at any cost. If found doing so the
 * necessary action required would be taken.
 *
 * GunGame is distributed in the hope that it will be useful,
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
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.ItemEdible;
import tim03we.futureplots.utils.Language;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

public class EventListener extends Language implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels != null && Settings.levels.contains(player.getLevel().getName())) {
            Plot plot = FuturePlots.getInstance().getPlotByPosition(event.getTo());
            Plot plotFrom = FuturePlots.getInstance().getPlotByPosition(event.getFrom());
            if(plot != null && plotFrom == null) {
                if(Settings.popup) {
                    if(FuturePlots.provider.hasOwner(plot)) {
                        player.sendPopup(translate(false, "popup-owned-plot", plot.getX() + ";" + plot.getZ(), FuturePlots.provider.getPlotName(plot)));
                    } else {
                        player.sendPopup(translate(false, "popup-free-plot", plot.getX() + ";" + plot.getZ()));
                    }
                }
            } else if(plotFrom != null && plot == null) {
                //
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels.contains(player.getLevel().getName())) {
            if(!player.isOp()) {
                if(FuturePlots.getInstance().isPlot(event.getBlock().getLocation())) {
                    if(!FuturePlots.provider.isOwner(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation())) && !FuturePlots.provider.isHelper(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation()))) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels.contains(player.getLevel().getName())) {
            if(!player.isOp()) {
                if(FuturePlots.getInstance().isPlot(event.getBlock().getLocation())) {
                    if(!FuturePlots.provider.isOwner(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation())) && !FuturePlots.provider.isHelper(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation()))) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(Settings.levels.contains(player.getLevel().getName())) {
            if(!player.isOp()) {
                if(event.getItem() instanceof ItemEdible) {
                    if(event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                        if(FuturePlots.getInstance().isPlot(event.getBlock().getLocation())) {
                            if(!FuturePlots.provider.isOwner(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation())) && !FuturePlots.provider.isHelper(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation()))) {
                                event.setCancelled(true);
                                return;
                            }
                        } else {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                if(FuturePlots.getInstance().isPlot(event.getBlock().getLocation())) {
                    if(!FuturePlots.provider.isOwner(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation())) && !FuturePlots.provider.isHelper(player.getName(), FuturePlots.getInstance().getPlotByPosition(event.getBlock().getLocation()))) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
