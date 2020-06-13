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
