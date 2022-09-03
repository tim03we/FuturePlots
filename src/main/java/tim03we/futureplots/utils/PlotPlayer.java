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
import cn.nukkit.level.Position;
import tim03we.futureplots.FuturePlots;

public class PlotPlayer {

    private Player player;

    public PlotPlayer(Player player) {
        this.player = player;
    }

    public Plot getPlot() {
        if(Settings.levels.contains(player.getLevel().getName())) {
            Plot plot = FuturePlots.getInstance().getPlotByPosition(player.getPosition());
            if(plot == null) {
                Plot merge = FuturePlots.getInstance().isInMergeCheck(player.getPosition());
                if(merge != null) {
                    plot = merge;
                }
            }
            if(plot != null && FuturePlots.provider.getOriginPlot(plot) != null && FuturePlots.provider.getMerges(plot).isEmpty()) {
                plot = FuturePlots.provider.getOriginPlot(plot);
            }
            return plot;
        }
        return null;
    }

    public boolean canByPass() {
        return (FuturePlots.provider.getOwner(getPlot()) != null && FuturePlots.provider.getOwner(getPlot()).equals(Utils.getPlayerId(player.getName()))) || player.isOp() || player.hasPermission("plot.bypass");
    }

    public boolean bypassEco() {
        return player.isOp() || player.hasPermission("futureplots.economy.bypass");
    }
}
