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

public class BorderCorrectionTask extends Task {

    private FuturePlots plugin;
    private Plot start;
    private Level level;
    private int height;
    private Block plotWallBlock;
    private Position plotBeginPos;
    private int xMax;
    private int zMax;
    private BlockFace direction;
    private Block roadBlock;
    private Block groundBlock;
    private Block bottomBlock;
    private Plot end;
    private Vector3 pos;

    public BorderCorrectionTask(FuturePlots plugin, Plot start, Plot end) {
        this.plugin = plugin;
        this.start = start;
        this.end = end;

        this.plotBeginPos = plugin.getPlotPosition(start);
        this.level = plotBeginPos.getLevel();

        String levelName = start.getLevelName();
        int plotSize = PlotSettings.getPlotSize(levelName);
        int roadWidth = PlotSettings.getRoadWidth(levelName);
        this.height = PlotSettings.getGroundHeight(levelName);
        this.plotWallBlock = PlotSettings.getWallBlockClaimed(levelName);
        this.roadBlock = PlotSettings.getRoadBlock(levelName);
        this.groundBlock = PlotSettings.getPlotFillBlock(levelName);
        this.bottomBlock = PlotSettings.getBottomBlock(levelName);

        if((start.getZ() - end.getZ()) == 1) {
            this.plotBeginPos = this.plotBeginPos.subtract(0, 0, roadWidth);
            this.xMax = this.plotBeginPos.getFloorX() + plotSize;
            this.zMax = this.plotBeginPos.getFloorZ() + roadWidth;
            this.direction = BlockFace.NORTH;
        } else if((start.getX() - end.getX()) == -1) {
            this.plotBeginPos = this.plotBeginPos.add(plotSize, 0, 0);
            this.xMax = this.plotBeginPos.getFloorX() + roadWidth;
            this.zMax = this.plotBeginPos.getFloorZ() + plotSize;
            this.direction = BlockFace.EAST;
        } else if((start.getZ() - end.getZ()) == -1) {
            this.plotBeginPos = this.plotBeginPos.add(0, 0, plotSize);
            this.xMax = this.plotBeginPos.getFloorX() + plotSize;
            this.zMax = this.plotBeginPos.getFloorZ() + roadWidth;
            this.direction = BlockFace.SOUTH;
        } else if((start.getX() - end.getX()) == 1) {
            this.plotBeginPos = this.plotBeginPos.subtract(roadWidth, 0,0);
            this.xMax = this.plotBeginPos.getFloorX() + roadWidth;
            this.zMax = this.plotBeginPos.getFloorZ() + plotSize;
            this.direction = BlockFace.WEST;
        }
        this.pos = new Vector3(this.plotBeginPos.x, 0, this.plotBeginPos.z);
    }

    @Override
    public void onRun(int i) {
        if(this.direction == BlockFace.NORTH || this.direction == BlockFace.SOUTH) {
            while(this.pos.z < this.zMax) {
                while(this.pos.y < 255) {
                    Block block;
                    if(this.pos.y > this.height + 1) {
                        block = Block.get(BlockID.AIR);
                    } else if(this.pos.y == this.height + 1) {
                        //block = this.plotWallBlock;
                        block = this.plotWallBlock;
                    } else if(this.pos.y == this.height) {
                        block = this.roadBlock;
                    } else if(this.pos.y == 0) {
                        block = this.bottomBlock;
                    } else {
                        block = this.groundBlock;
                    }
                    this.level.setBlock(new Vector3(this.pos.x - 1, this.pos.y, this.pos.z), block, false);
                    this.level.setBlock(new Vector3(this.xMax, this.pos.y, this.pos.z), block, false);
                    this.pos.y++;
                }
                this.pos.y = 0;
                this.pos.z++;
            }
        } else {
            while (this.pos.x < this.xMax) {
                while (this.pos.y < 255) {
                    Block block;
                    if(this.pos.y > this.height + 1) {
                        block = Block.get(BlockID.AIR);
                    } else if(this.pos.y == this.height + 1) {
                        block = this.plotWallBlock;
                    } else if(this.pos.y == this.height) {
                        block = this.roadBlock;
                    } else if(this.pos.y == 0) {
                        block = this.bottomBlock;
                    } else {
                        block = this.groundBlock;
                    }
                    this.level.setBlock(new Vector3(this.pos.x, this.pos.y, this.pos.z - 1), block, false);
                    this.level.setBlock(new Vector3(this.pos.x, this.pos.y, this.zMax), block, false);
                    this.pos.y++;
                }
                this.pos.y = 0;
                this.pos.x++;
            }
        }
    }
}
