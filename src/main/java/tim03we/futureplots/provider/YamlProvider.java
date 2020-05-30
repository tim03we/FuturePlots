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

import cn.nukkit.utils.Config;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;

import java.util.ArrayList;
import java.util.List;

public class YamlProvider implements DataProvider {

    private final Config yaml = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);

    @Override
    public void save() {
        yaml.save();
    }

    @Override
    public void claimPlot(String username, Plot plot) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner", username);
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".denied", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".flags", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge", new ArrayList<String>());
    }

    @Override
    public void deletePlot(Plot plot) {
        yaml.remove(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ());
    }

    @Override
    public List<String> getHelpers(Plot plot) {
        return yaml.getStringList(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers");
    }

    @Override
    public List<String> getMembers(Plot plot) {
        return yaml.getStringList(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members");
    }

    @Override
    public List<String> getDenied(Plot plot) {
        return yaml.getStringList(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".denied");
    }

    @Override
    public boolean isHelper(String name, Plot plot) {
        return getHelpers(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean isDenied(String name, Plot plot) {
        return getDenied(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean isMember(String name, Plot plot) {
        return getMembers(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean hasOwner(Plot plot) {
        return getOwner(plot) != null;
    }

    @Override
    public String getOwner(Plot plot) {
        if(yaml.exists(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ())) {
            return yaml.getString(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner");
        }
        return null;
    }

    @Override
    public void addHelper(String name, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.add(name.toLowerCase());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers", helpers);
    }

    @Override
    public void addMember(String name, Plot plot) {
        List<String> members = getMembers(plot);
        members.add(name.toLowerCase());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members", members);
    }

    @Override
    public void addDenied(String name, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.add(name.toLowerCase());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".denied", denied);
    }

    @Override
    public void removeMember(String name, Plot plot) {
        List<String> members = getMembers(plot);
        members.remove(name.toLowerCase());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members", members);
    }

    @Override
    public void removeHelper(String name, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.remove(name.toLowerCase());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers", helpers);
    }

    @Override
    public void removeDenied(String name, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.remove(name.toLowerCase());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".denied", denied);
    }

    @Override
    public Plot getPlot(String name, Object number, Object level) {
        int i = 1;
        if(number != null && (int) number > 0) {
            i = (int) number;
        }
        ArrayList<String> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : yaml.getAll().keySet()) {
                String[] ex = plot.split(";");
                if(ex[0].equals(level) && yaml.getString(plot + ".owner").equals(name)) plots.add(plot);
            }
        } else {
            for (String plot : yaml.getAll().keySet()) {
                if(yaml.getString(plot + ".owner").equals(name)) plots.add(plot);
            }
        }
        if(plots.size() > 0) {
            String[] ex = plots.get(i - 1).split(";");
            return new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]);
        }
        return null;
    }

    @Override
    public List<String> getPlots(String name, Object level) {
        List<String> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : yaml.getAll().keySet()) {
                String[] ex = plot.split(";");
                if(ex[0].equals(level) && yaml.getString(plot + ".owner").equals(name)) plots.add(plot);
            }
        } else {
            for (String plot : yaml.getAll().keySet()) {
                if(yaml.getString(plot + ".owner").equals(name)) plots.add(plot);
            }
        }
        return plots;
    }

    @Override
    public Plot getNextFreePlot(String level) {
        int limitXZ = 0;
        for(int i = 0; limitXZ <= 0 || i < limitXZ; i++) {
            ArrayList<String> existing = new ArrayList<>();
            for (String list : yaml.getAll().keySet()) {
                String[] ex = list.split(";");
                if(Math.abs(Integer.parseInt(ex[1])) == i && Math.abs(Integer.parseInt(ex[2])) <= i) {
                    existing.add(list);
                } else if(Math.abs(Integer.parseInt(ex[2])) == i && Math.abs(Integer.parseInt(ex[1])) <= i) {
                    existing.add(list);
                }
            }
            if(existing.size() == Math.max(1, 8 * i)) {
                continue;
            }
            if(FuturePlots.getInstance().findEmptyPlotSquared(0, i, existing) != null) {
                String[] ex = FuturePlots.getInstance().findEmptyPlotSquared(0, i, existing).split(";");
                return new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), level);
            }
            for (int a = 1; a < i; a++) {
                if(FuturePlots.getInstance().findEmptyPlotSquared(a, i, existing) != null) {
                    String[] ex = FuturePlots.getInstance().findEmptyPlotSquared(a, i, existing).split(";");
                    return new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), level);
                }
            }
            if(FuturePlots.getInstance().findEmptyPlotSquared(i, i, existing) != null) {
                String[] ex = FuturePlots.getInstance().findEmptyPlotSquared(i, i, existing).split(";");
                return new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), level);
            }
        }
        return null;
    }
}
