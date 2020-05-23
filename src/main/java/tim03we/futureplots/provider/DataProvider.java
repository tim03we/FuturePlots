package tim03we.futureplots.provider;

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

import tim03we.futureplots.utils.Plot;

import java.util.ArrayList;

public interface DataProvider {

    void saveAll();

    void claimPlot(String username, Plot plot);

    void deletePlot(Plot plot);

    boolean isHelper(String username, Plot plot);

    void addHelper(String username, Plot plot);

    void removeHelper(String username, Plot plot);

    String getHelpers(Plot plot);

    boolean isMember(String username, Plot plot);

    void addMember(String username, Plot plot);

    void removeMember(String username, Plot plot);

    String getMembers(Plot plot);

    String getDenied(Plot plot);

    boolean isDenied(String username, Plot plot);

    void addDenied(String username, Plot plot);

    void removeDenied(String username, Plot plot);

    boolean isOwner(String username, Plot plot);

    boolean hasOwner(Plot plot);

    boolean hasHome(String username, int homenNumber);

    boolean hasHomeInLevel(String username, int homenNumber, String levelName);

    ArrayList<String> getHomes(String username, String world);

    ArrayList<String> getHomes(String username);

    Plot getPlot(String username, int homeNumber);

    Plot getPlotFromNumber(String username, int homeNumber, String levelName);

    String getPlotName(Plot plot);

    Plot getNextFreePlot(Plot plot);
}
