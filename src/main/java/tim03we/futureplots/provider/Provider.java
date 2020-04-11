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

import tim03we.futureplots.Plot;
import tim03we.futureplots.Settings;

import java.util.ArrayList;

public class Provider {

    public void claimPlot(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            new YAMLProvider().claimPlot(username, plot);
        }
    }

    public void deletePlot(Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            new YAMLProvider().deletePlot(plot);
        }
    }

    public boolean isHelper(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().isHelper(username, plot);
        }
        return false;
    }

    public void addHelper(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            new YAMLProvider().addHelper(username, plot);
        }
    }

    public void removeHelper(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            new YAMLProvider().removeHelper(username, plot);
        }
    }

    public String getHelpers(Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().getHelpers(plot);
        }
        return null;
    }

    public String getDenied(Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().getDenied(plot);
        }
        return null;
    }

    public boolean isDenied(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().isHelper(username, plot);
        }
        return false;
    }

    public void addDenied(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            new YAMLProvider().addHelper(username, plot);
        }
    }

    public void removeDenied(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            new YAMLProvider().removeHelper(username, plot);
        }
    }

    public boolean isOwner(String username, Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().isOwner(username, plot);
        }
        return false;
    }

    public boolean hasOwner(Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().hasOwner(plot);
        }
        return false;
    }

    public boolean hasHome(String username, int homenNumber) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().hasHome(username, homenNumber);
        }
        return false;
    }

    public ArrayList<String> getHomes(String username) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().getHomes(username);
        }
        return null;
    }

    public String getPlotId(String username, int homeNumber) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().getPlotId(username, homeNumber);
        }
        return null;
    }

    public String getPlotName(Plot plot) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().getPlotName(plot);
        }
        return null;
    }

    public Plot getNextFreePlot(int limitXZ) {
        if ("yaml".equals(Settings.provider)) {
            return new YAMLProvider().getNextFreePlot(0);
        }
        return null;
    }

    public String findEmptyPlotSquared(int a, int b, ArrayList<String> plots) {
        if (!plots.contains(a + "#" + b)) return a + "#" + b;
        if(!plots.contains(b + "#" + a)) return b + "#" + a;
        if(a != 0) {
            if(!plots.contains(-a + "#" + b)) return -a + "#" + b;
            if(!plots.contains(b + "#" + -a)) return b + "#" + -a;
        }
        if(b != 0) {
            if(!plots.contains(-b + "#" + a)) return -b + "#" + a;
            if(!plots.contains(a + "#" + -b)) return a + "#" + -b;
        }
        if(a == 0 | b == 0) {
            if(!plots.contains(-a + "#" + -b)) return -a + "#" + -b;
            if(!plots.contains(-b + "#" + -a)) return -b + "#" + -a;
        }
        return null;
    }
}
