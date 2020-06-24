package tim03we.futureplots.utils;

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
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.tasks.PlotSetBorderTask;

public class Plot {

    private int x;
    private int z;
    private String levelName;

    public Plot(int x, int z, String levelName) {
        this.x = x;
        this.z = z;
        this.levelName = levelName;
    }

    private Plot getPlot(int x, int z, String levelname) {
        return new Plot(x, z, levelname);
    }

    private Plot getPlot() {
        return new Plot(x, z, levelName);
    }

    public String getFullID() {
        return this.x + ";" + this.z;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public String getLevelName() {
        return this.levelName;
    }

    public Position getPosition() {
        return FuturePlots.getInstance().getPlotPosition(getPlot(x, z, levelName));
    }

    public Position getBorderPosition() {
        return FuturePlots.getInstance().getPlotBorderPosition(getPlot(x, z, levelName));
    }

    public void changeBorder(Block block) {
        Server.getInstance().getScheduler().scheduleDelayedTask(FuturePlots.getInstance(), new PlotSetBorderTask(getPlot(x, z, levelName), block), 1, true);
    }

    public boolean canByPass(Player player) {
        return new PlotPlayer(player).canByPass();
    }

    public boolean canInteract(Player player) {
        if(FuturePlots.provider.hasOwner(getPlot())) {
            if(FuturePlots.provider.getOwner(getPlot()).equals(player.getName())) {
                return true;
            } else if(FuturePlots.provider.isHelper(player.getName(), getPlot())) {
                return true;
            } else {
                if(FuturePlots.provider.isMember(player.getName(), getPlot())) {
                    Player target = Server.getInstance().getPlayer(FuturePlots.provider.getOwner(getPlot()));
                    return target != null;
                }
            }
        }
        return false;
    }
}
