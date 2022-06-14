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
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotSettings;

public class RoadMiddleFillTask extends Task {

    private FuturePlots plugin;
    private Plot start;
    private Plot end;
    private Level level;
    private int height;
    private Position plotBeginPos;
    private int xMax;
    private int zMax;
    private Block roadBlock, groundBlock, bottomBlock;
    private int maxBlocksPerTick;
    private Vector3 pos;
    private boolean fillCorner;
    private BlockFace cornerDirection;
    private Position startPos;
    private Position endPos;

    public RoadMiddleFillTask(FuturePlots plugin, Plot start, Plot end, boolean fillCorner, BlockFace cornerDirection, int maxBlocksPerTick) {
        this.plugin = plugin;
        this.start = start;
        this.end = end;
        this.fillCorner = fillCorner;
        this.cornerDirection = cornerDirection;

        String levelName = start.getLevelName();
        int plotSize = PlotSettings.getPlotSize(levelName);
        int roadWidth = PlotSettings.getRoadWidth(levelName);

        this.startPos = plugin.getPlotPosition(start);
        this.endPos = plugin.getPlotPosition(end).add(plotSize - 1, 0, plotSize - 1);

        this.xMax = endPos.getFloorX();
        this.zMax = endPos.getFloorZ();
        this.plotBeginPos = plugin.getPlotPosition(start);
        this.level = plotBeginPos.getLevel();

        this.height = PlotSettings.getGroundHeight(levelName);
        this.roadBlock = PlotSettings.getPlotFloorBlock(levelName);
        this.groundBlock = PlotSettings.getPlotFillBlock(levelName);
        this.bottomBlock = PlotSettings.getBottomBlock(levelName);

        this.maxBlocksPerTick = 256;
        this.pos = new Vector3(this.plotBeginPos.x, 0, this.plotBeginPos.z);
    }

    @Override
    public void onRun(int i) {
        while (this.pos.x > this.xMax) {
            while (this.pos.z > this.zMax) {
                while (this.pos.y < 255) {
                    Block block;
                    if(this.pos.y == 0) {
                        block = this.bottomBlock;
                    } else if(this.pos.y < this.height) {
                        block = this.groundBlock;
                    } else if(this.pos.y == this.height) {
                        block = this.roadBlock;
                    } else {
                        block = Block.get(BlockID.AIR);
                    }

                    this.level.setBlock(this.pos, block, false);
                    this.pos.y++;
                }
                this.pos.y = 0;
                this.pos.z--;
            }
            this.pos.z = this.plotBeginPos.z;
            this.pos.x--;
        }
    }
}
