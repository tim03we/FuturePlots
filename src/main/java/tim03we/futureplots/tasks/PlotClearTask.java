package tim03we.futureplots.tasks;

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

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotSettings;

public class PlotClearTask extends Task {

    private Plot plot;
    private Level level;
    private int height;
    private int plotSize;
    private Block bottomBlock;
    private Block plotFillBlock;
    private Block plotFloorBlock;
    private Position plotBeginPos;
    private int xMax;
    private int zMax;
    private Vector3 pos;

    public PlotClearTask(Plot plot) {
        this.plot = plot;
        this.plotBeginPos = FuturePlots.getInstance().getPlotPosition(plot);
        this.level = plotBeginPos.getLevel();
        this.plotSize = new PlotSettings(plot.getLevelName()).getPlotSize();
        this.xMax = (int) (plotBeginPos.x + plotSize);
        this.zMax = (int) (plotBeginPos.z + plotSize);
        this.height = new PlotSettings(plot.getLevelName()).getGroundHeight();
        this.bottomBlock = Block.get(new PlotSettings(plot.getLevelName()).getBottomBlock());
        this.plotFillBlock = Block.get(new PlotSettings(plot.getLevelName()).getPlotFillBlock());
        this.plotFloorBlock = Block.get(new PlotSettings(plot.getLevelName()).getPlotFloorBlock());
        this.pos = new Position(plotBeginPos.x, 0, plotBeginPos.z, Server.getInstance().getLevelByName(plot.getLevelName()));
    }

    @Override
    public void onRun(int i) {
        try {
            Block block;
            while (pos.x < xMax) {
                while (pos.z < zMax) {
                    while (pos.y < 256) {
                        if (pos.y == 0) {
                            block = bottomBlock;
                        } else if (pos.y < height) {
                            block = plotFillBlock;
                        } else if (pos.y == height) {
                            block = plotFloorBlock;
                        } else {
                            block = Block.get(0);
                        }
                        level.setBlock(pos, block);
                        pos.y++;
                    }
                    pos.y = 0;
                    pos.z++;
                }
                pos.z = plotBeginPos.z;
                pos.x++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
