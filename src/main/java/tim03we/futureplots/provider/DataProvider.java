package tim03we.futureplots.provider;

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

import cn.nukkit.level.Location;
import tim03we.futureplots.utils.Plot;

import java.util.List;

public interface DataProvider {

    void connect();

    void save();

    void claimPlot(String name, Plot plot);

    void deletePlot(Plot plot);

    boolean isHelper(String name, Plot plot);

    boolean isMember(String name, Plot plot);

    boolean isDenied(String name, Plot plot);

    boolean hasOwner(Plot plot);

    void setOwner(String name, Plot plot);

    String getOwner(Plot plot);

    List<String> getHelpers(Plot plot);

    List<String> getMembers(Plot plot);

    List<String> getDenied(Plot plot);

    void addHelper(String name, Plot plot);

    void addMember(String name, Plot plot);

    void addDenied(String name, Plot plot);

    void removeHelper(String name, Plot plot);

    void removeMember(String name, Plot plot);

    void removeDenied(String name, Plot plot);

    void setHome(Plot plot, Location location);

    void deleteHome(Plot plot);

    Location getHome(Plot plot);

    Plot getPlot(String name, Object number, Object level);

    List<String> getPlots(String name, Object level);

    Plot getNextFreePlot(String level);
}
