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
