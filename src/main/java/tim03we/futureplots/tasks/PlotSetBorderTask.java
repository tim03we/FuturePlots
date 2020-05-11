package tim03we.futureplots.tasks;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.PlotSettings;

import java.util.concurrent.CompletableFuture;

public class PlotSetBoarderTask extends Task {

    private Level level;
    private int height;
    private Block plotWallBlock;
    private Position plotBeginPos;
    private double xMax, zMax;

    public PlotSetBoarderTask (Plot plot, Block $block) {
        this.plotBeginPos = FuturePlots.getInstance().getPlotPosition(plot);
        this.level = plotBeginPos.getLevel();
        this.plotBeginPos = plotBeginPos.subtract(1,0,1);
        int plotSize = new PlotSettings(level.getName()).getPlotSize();
        this.xMax = plotBeginPos.x + plotSize + 1;
        this.zMax = plotBeginPos.z + plotSize + 1;
        this.height = new PlotSettings(level.getName()).getGroundHeight();
        this.plotWallBlock = $block;
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
