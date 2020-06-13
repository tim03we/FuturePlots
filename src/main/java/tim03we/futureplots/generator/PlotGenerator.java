package tim03we.futureplots.generator;

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

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.impl.plains.PlainsBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import tim03we.futureplots.utils.PlotSettings;

import java.util.HashMap;
import java.util.Map;

public class PlotGenerator extends Generator {

    private Map<String, Object> options;
    protected Level level;
    protected Block roadBlock;
    protected Block bottomBlock;
    protected Block plotFillBlock;
    protected Block plotFloorBlock;
    protected Block wallBlock;
    protected int roadWidth;
    protected int groundHeight;
    protected int plotSize;
    static int PLOT = 0;
    static int ROAD = 1;
    static int WALL = 2;

    private final String NAME = "futureplots";
    private ChunkManager chunkManager;


    public PlotGenerator(Map<String, Object> options) {
        this.options = options;
        try {
            level = Server.getInstance().getLevelByName((String) options.get("preset"));
            PlotSettings plotSettings = new PlotSettings((String) options.get("preset"));
            roadBlock = plotSettings.getRoadBlock();
            wallBlock = plotSettings.getWallBlockUnClaimed();
            plotFloorBlock = plotSettings.getPlotFloorBlock();
            plotFillBlock = plotSettings.getPlotFillBlock();
            bottomBlock = plotSettings.getBottomBlock();
            roadWidth = plotSettings.getRoadWidth();
            plotSize = plotSettings.getPlotSize();
            groundHeight = plotSettings.getGroundHeight();
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | NumberFormatException e) {
            Server.getInstance().getLogger().critical("Your world configuration " + options.get("preset") + ".yml is incorrect, check it or the server will not start properly. An example of the config, if it does not match your previous one, can be found at \"https://github.com/tim03we/FuturePlots/wiki/World-Config-Example\".");
            Server.getInstance().shutdown();
        }
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
        this.chunkManager = chunkManager;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        HashMap<Integer, Integer> shape = getShape(chunkX << 4, chunkZ << 4);
        level.loadChunk(chunkX << 4, chunkZ << 4);
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        int bottomBlockId = bottomBlock.getId();
        int bottomBlockMeta = bottomBlock.getDamage();
        int plotFillBlockId = plotFillBlock.getId();
        int plotFillBlockMeta = plotFillBlock.getDamage();
        int plotFloorBlockId = plotFloorBlock.getId();
        int plotFloorBlockMeta = plotFloorBlock.getDamage();
        int roadBlockId = roadBlock.getId();
        int roadBlockMeta = roadBlock.getDamage();
        int wallBlockId = wallBlock.getId();
        int wallBlockMeta = wallBlock.getDamage();
        int groundH = groundHeight;
        for (int Z = 0; Z < 16; ++Z) {
            for(int X = 0; X < 16; ++X) {
                chunk.setBiome(X, Z, new PlainsBiome());
                chunk.setBlock(X, 0, Z, bottomBlockId, bottomBlockMeta);
                for(int y = 1; y < groundH; ++y) {
                    chunk.setBlock(X, y, Z, plotFillBlockId, plotFillBlockMeta);
                }
                int type = shape.get((Z << 4) | X);
                if(type == PLOT) {
                    chunk.setBlock(X, groundH, Z, plotFloorBlockId, plotFloorBlockMeta);
                } else if(type == ROAD) {
                    chunk.setBlock(X, groundH, Z, roadBlockId, roadBlockMeta);
                } else {
                    chunk.setBlock(X, groundH, Z, roadBlockId, roadBlockMeta);
                    chunk.setBlock(X, groundH + 1, Z, wallBlockId, wallBlockMeta);
                }
            }
        }
        chunk.setX(chunkX);
        chunk.setZ(chunkZ);
        chunk.setGenerated();
        level.setChunk(chunkX, chunkZ, chunk);
    }

    @Override
    public void populateChunk(int i, int i1) {
    }

    @Override
    public Map<String, Object> getSettings() {
        return this.options;
    }

    @Override
    public String getName() {
        return "futureplots";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, groundHeight + 1, 0);
    }

    @Override
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public HashMap<Integer, Integer> getShape(int x, int z) {
        int totalSize = plotSize + roadWidth;
        int X;
        int Z;
        if(x >= 0) {
            X = x % totalSize;
        } else {
            X = totalSize - Math.abs(x % totalSize);
        }
        if(z >= 0) {
            Z = z % totalSize;
        } else {
            Z = totalSize - Math.abs(z % totalSize);
        }
        HashMap<Integer, Integer> shape = new HashMap<>();
        int typeZ;
        int typeX;
        int type;
        int startX = X;
        for (z = 0; z < 16; z++, Z++) {
            if(Z == totalSize) {
                Z = 0;
            }
            if(Z < plotSize) {
                typeZ = PLOT;
            } else if(Z == plotSize || Z == (totalSize - 1)) {
                typeZ = WALL;
            } else {
                typeZ = ROAD;
            }
            for (x = 0, X = startX; x < 16; x++, X++) {
                if(X == totalSize) {
                    X = 0;
                }
                if(X < plotSize) {
                    typeX = PLOT;
                } else if(X == plotSize || X == (totalSize - 1)) {
                    typeX = WALL;
                } else {
                    typeX = ROAD;
                }
                if(typeX == typeZ) {
                    type = typeX;
                } else if(typeX == PLOT) {
                    type = typeZ;
                } else if(typeZ == PLOT) {
                    type = typeX;
                } else {
                    type = ROAD;
                }
                shape.put((z << 4) | x, type);
            }
        }
        return shape;
    }
}
