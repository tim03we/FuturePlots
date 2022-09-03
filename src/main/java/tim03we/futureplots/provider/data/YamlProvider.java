package tim03we.futureplots.provider.data;

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
import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.provider.DataProvider;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;
import tim03we.futureplots.utils.Utils;
import tim03we.futureplots.utils.xuid.web.RequestXUID;

import java.util.ArrayList;
import java.util.List;

public class YamlProvider implements DataProvider {

    private final Config yaml = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);

    @Override
    public void connect() {
        if(Settings.use_auto_save) {
            runAutoSaveTask();
        }
        checkData();
    }

    private void runAutoSaveTask() {
        Server.getInstance().getScheduler().scheduleRepeatingTask(FuturePlots.getInstance(), () -> {
            yaml.save();
        }, (Settings.auto_save_interval * 20), true);
    }

    private void checkData() {
        for (String key : yaml.getAll().keySet()) {
            if(!yaml.exists(key + ".owner")) yaml.set(key + ".owner", "");
            if(!yaml.exists(key + ".helpers")) yaml.set(key + ".helpers", new ArrayList<String>());
            if(!yaml.exists(key + ".members")) yaml.set(key + ".members", new ArrayList<String>());
            if(!yaml.exists(key + ".denied")) yaml.set(key + ".denied", new ArrayList<String>());
            if(!yaml.exists(key + ".flags")) yaml.set(key + ".flags", new ArrayList<String>());
            if(!yaml.exists(key + ".home")) yaml.set(key + ".home", "");
            if(!yaml.exists(key + ".merge")) yaml.set(key + ".merge", "");
            if(!yaml.exists(key + ".merges")) yaml.set(key + ".merges", new ArrayList<String>());
            if(!yaml.exists(key + ".merge_check")) yaml.set(key + ".merge_check", new ArrayList<String>());
        }

        FuturePlots.getInstance().getLogger().warning("[XUID] Start checking for missing XUIDs... During this check, no players can enter the server.");
        int missingXuid = 0;
        for (String key : yaml.getAll().keySet()) {
            String owner = yaml.getString(key + ".owner");
            if(!Utils.isLong(owner) && owner.length() != 16 && !owner.equals("none")) {
                RequestXUID requestXUID = new RequestXUID(owner);
                String xuid = requestXUID.sendAndGetXuid();
                yaml.set(key + ".owner", xuid);
                missingXuid++;
                FuturePlots.getInstance().getLogger().info("[XUID] " + owner + " has been converted to an XUID..");
                FuturePlots.xuidProvider.updateEntry(owner, xuid);
            }
            List<String> newList = yaml.getStringList(key + ".helpers");
            for (String key2 : newList) {
                if(!Utils.isLong(key2) && key2.length() != 16 && !owner.equals("none")) {
                    RequestXUID requestXUID = new RequestXUID(owner);
                    String xuid = requestXUID.sendAndGetXuid();
                    if(xuid != null) {
                        newList.remove(key2);
                        newList.add(xuid);
                        missingXuid++;
                        FuturePlots.getInstance().getLogger().info("[XUID] " + key2 + " has been converted to an XUID..");
                        FuturePlots.xuidProvider.updateEntry(key2, xuid);
                    }
                }
            }
            yaml.set(key + ".helpers", newList);

            newList = yaml.getStringList(key + ".members");
            for (String key2 : newList) {
                if(!Utils.isLong(key2) && key2.length() != 16 && !owner.equals("none")) {
                    RequestXUID requestXUID = new RequestXUID(owner);
                    String xuid = requestXUID.sendAndGetXuid();
                    if(xuid != null) {
                        newList.remove(key2);
                        newList.add(xuid);
                        missingXuid++;
                        FuturePlots.getInstance().getLogger().info("[XUID] " + key2 + " has been converted to an XUID..");
                        FuturePlots.xuidProvider.updateEntry(key2, xuid);
                    }
                }
            }
            yaml.set(key + ".members", newList);

            newList = yaml.getStringList(key + ".denied");
            for (String key2 : newList) {
                if(!Utils.isLong(key2) && key2.length() != 16 && !owner.equals("none")) {
                    RequestXUID requestXUID = new RequestXUID(owner);
                    String xuid = requestXUID.sendAndGetXuid();
                    if(xuid != null) {
                        newList.remove(key2);
                        newList.add(xuid);
                        missingXuid++;
                        FuturePlots.getInstance().getLogger().info("[XUID] " + key2 + " has been converted to an XUID..");
                        FuturePlots.xuidProvider.updateEntry(key2, xuid);
                    }
                }
            }
            yaml.set(key + ".denied", newList);
        }
        FuturePlots.getInstance().getLogger().warning("[XUID] " + missingXuid + " names were converted to XUID. The server can now be accessed.");
        Settings.joinServer = true;
    }

    @Override
    public void save() {
        yaml.save();
    }

    @Override
    public void checkPlayer(Player player) {

    }

    @Override
    public boolean claimPlot(String playerId, Plot plot) {
        if(getMerges(plot).size() > 0) {
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner", playerId);
        } else {
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner", playerId);
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers", new ArrayList<String>());
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members", new ArrayList<String>());
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".denied", new ArrayList<String>());
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".flags", new ArrayList<String>());
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home", "");
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge", "");
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merges", new ArrayList<String>());
            yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge_check", new ArrayList<String>());
        }
        return true;
    }

    @Override
    public void deletePlot(Plot plot) {
        yaml.remove(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ());
    }

    @Override
    public void disposePlot(Plot plot) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner", "none");
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".denied", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".flags", new ArrayList<String>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home", "");
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
    public boolean isHelper(String playerId, Plot plot) {
        return getHelpers(plot).contains(playerId);
    }

    @Override
    public boolean isDenied(String playerId, Plot plot) {
        return getDenied(plot).contains(playerId);
    }

    @Override
    public boolean isMember(String playerId, Plot plot) {
        return getMembers(plot).contains(playerId);
    }

    @Override
    public boolean hasOwner(Plot plot) {
        return !getOwner(plot).equals("none");
    }

    @Override
    public void setOwner(String playerId, Plot plot) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner", playerId);
    }

    @Override
    public String getOwner(Plot plot) {
        if(yaml.exists(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ())) {
            String owner = yaml.getString(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".owner");
            return owner;
        }
        return "none";
    }

    @Override
    public void addHelper(String playerId, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.add(playerId);
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers", helpers);
    }

    @Override
    public void addMember(String playerId, Plot plot) {
        List<String> members = getMembers(plot);
        members.add(playerId);
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members", members);
    }

    @Override
    public void addDenied(String playerId, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.add(playerId);
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".denied", denied);
    }

    @Override
    public void removeMember(String playerId, Plot plot) {
        List<String> members = getMembers(plot);
        members.remove(playerId);
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".members", members);
    }

    @Override
    public void removeHelper(String playerId, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.remove(playerId);
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".helpers", helpers);
    }

    @Override
    public void removeDenied(String playerId, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.remove(playerId);
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
    public Plot getPlot(String playerId, Object number, Object level) {
        int i = 1;
        if(number != null && (int) number > 0) {
            i = (int) number;
        }
        ArrayList<String> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : yaml.getAll().keySet()) {
                String[] ex = plot.split(";");
                if(ex[0].equals(level) && yaml.getString(plot + ".owner").equals(playerId)) plots.add(plot);
            }
        } else {
            for (String plot : yaml.getAll().keySet()) {
                if(yaml.getString(plot + ".owner").equals(playerId)) plots.add(plot);
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
    public List<String> getPlots(String playerId, Object level) {
        List<String> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : yaml.getAll().keySet()) {
                String[] ex = plot.split(";");
                if(ex[0].equals(level) && yaml.getString(plot + ".owner").equals(playerId)) plots.add(plot);
            }
        } else {
            for (String plot : yaml.getAll().keySet()) {
                if(yaml.getString(plot + ".owner").equals(playerId)) plots.add(plot);
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

    @Override
    public void setMergeCheck(Plot plot, Plot mergePlot) {
        List<String> mergeList = new ArrayList<>();
        for (Plot plots : getMergeCheck(plot)) {
            mergeList.add(plots.getX() + ";" + plots.getZ());
        }
        mergeList.add(mergePlot.getX() + ";" + mergePlot.getZ());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge_check", mergeList);
    }

    @Override
    public List<Plot> getMergeCheck(Plot plot) {
        List<Plot> plots = new ArrayList<>();
        for (String plotId : yaml.getStringList(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge_check")) {
            String[] ex = plotId.split(";");
            plots.add(new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName()));
        }
        return plots;
    }

    @Override
    public Plot getOriginPlot(Plot plot) {
        String originString = yaml.getString(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge");
        if(originString.isEmpty()) return null;
        String[] ex = originString.split(";");
        return new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName());
    }

    @Override
    public void setOriginPlot(Plot mergePlot, Plot originPlot) {
        yaml.set(mergePlot.getLevelName() + ";" + mergePlot.getX() + ";" + mergePlot.getZ() + ".merge", originPlot.getFullID());
    }


    @Override
    public void addMerge(Plot originPlot, Plot mergePlot) {
        List<String> mergeList = new ArrayList<>();
        for (Plot plots : getMerges(originPlot)) {
            mergeList.add(plots.getX() + ";" + plots.getZ());
        }
        mergeList.add(mergePlot.getX() + ";" + mergePlot.getZ());
        yaml.set(originPlot.getLevelName() + ";" + originPlot.getX() + ";" + originPlot.getZ() + ".merges", mergeList);
    }

    @Override
    public List<Plot> getMerges(Plot plot) {
        List<Plot> plots = new ArrayList<>();
        for (String plotId : yaml.getStringList(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merges")) {
            String[] ex = plotId.split(";");
            plots.add(new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName()));
        }
        return plots;
    }

    @Override
    public void resetMerges(Plot plot) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".home", "");
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge", "");
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merges", new ArrayList<>());
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merge_check", new ArrayList<>());
    }

    @Override
    public void deleteMergeList(Plot plot) {
        yaml.set(plot.getLevelName() + ";" + plot.getX() + ";" + plot.getZ() + ".merges", new ArrayList<>());
    }
}
