package tim03we.futureplots.tasks;

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
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotSettings;

import java.util.concurrent.CompletableFuture;

public class PlotSetBorderTask extends Task {

    private Level level;
    private int height;
    private Block plotWallBlock;
    private Position plotBeginPos;
    private double xMax, zMax;

    public PlotSetBorderTask(Plot plot, Block block) {
        PlotSettings plotSettings = new PlotSettings(plot.getLevelName());
        this.plotBeginPos = FuturePlots.getInstance().getPlotPosition(plot);
        this.level = plotBeginPos.getLevel();
        this.plotBeginPos = plotBeginPos.subtract(1,0,1);
        int plotSize = plotSettings.getPlotSize();
        this.xMax = plotBeginPos.x + plotSize + 1;
        this.zMax = plotBeginPos.z + plotSize + 1;
        this.height = plotSettings.getGroundHeight();
        this.plotWallBlock = block;
    }

    @Override
    public void onRun(int i) {
        CompletableFuture.runAsync(() -> {
            try {
                double x;
                double z;

                for (x = plotBeginPos.x; x <= xMax; x++) {
                    level.setBlock(new Vector3(x, height + 1, plotBeginPos.z), plotWallBlock);
                    level.setBlock(new Vector3(x, height + 1, zMax), plotWallBlock);
                }
                for (z = plotBeginPos.z; z <= zMax; z++) {
                    level.setBlock(new Vector3(plotBeginPos.x, height + 1, z), plotWallBlock);
                    level.setBlock(new Vector3(xMax, height + 1, z), plotWallBlock);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
