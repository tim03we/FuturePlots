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

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

import java.util.ArrayList;
import java.util.List;

public class YamlProvider implements DataProvider {

    private final Config yaml = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);

    @Override
    public void connect() {
        if(Settings.use_auto_save) {
            runAutoSaveTask();
        }
    }

    private void runAutoSaveTask() {
        Server.getInstance().getScheduler().scheduleRepeatingTask(FuturePlots.getInstance(), () -> {
            yaml.save();
        }, (Settings.auto_save_interval * 20), true);
    }

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
        return !getOwner(plot).equals("none");
    }

    @Override
    public void setOwner(String name, Plot plot) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner", name);
    }

    @Override
    public String getOwner(Plot plot) {
        if(yaml.exists(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ())) {
            return yaml.getString(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner");
        }
        return "none";
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
    public void setHome(Plot plot, Location location) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home", location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch());
    }

    @Override
    public void deleteHome(Plot plot) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home", "");
    }

    @Override
    public Location getHome(Plot plot) {
        if(yaml.exists(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home")) {
            String locationString = yaml.getString(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home");
            if(!locationString.equals("")) {
                String[] ex = yaml.getString(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home").split(":");
                return new Location(Double.parseDouble(ex[0]), Double.parseDouble(ex[1]), Double.parseDouble(ex[2]), Double.parseDouble(ex[3]), Double.parseDouble(ex[4]), Server.getInstance().getLevelByName(plot.getLevelName()));
            }
        }
        return null;
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
        if((i - 1) >= plots.size()) return null;
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
        List<Plot> plots = new ArrayList<>();
        for (String list : yaml.getAll().keySet()) {
            String[] ex = list.split(";");
            if(ex[0].equals(level)) {
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }
        }
        if (plots.size() == 0) return new Plot(0, 0, level);
        int lastX = 0;
        int lastZ = 0;

        for (Plot plot : plots) {
            int x = plot.getX() - lastX;
            int y = plot.getZ() - lastZ;
            int diff = Math.abs(x * y);
            if (diff < 4) {
                lastX = plot.getX();
                lastZ = plot.getZ();

                Plot find = new Plot(plot.getX() + 1, plot.getZ(), level);
                if (!hasOwner(find)) return find;
                find = new Plot(plot.getX(), plot.getZ() + 1, level);
                if (!hasOwner(find)) return find;
                find = new Plot(plot.getX() - 1, plot.getZ(), level);
                if (!hasOwner(find)) return find;
                find = new Plot(plot.getX(), plot.getZ() - 1, level);
                if (!hasOwner(find)) return find;
            }
        }
        return getNextFreePlot(level);
    }
}
