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
import tim03we.futureplots.Plot;
import tim03we.futureplots.Settings;

import java.util.ArrayList;

public class YAMLProvider {

    public void claimPlot(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        config.set(plot.getX() + "#" + plot.getZ() + ".owner", username);
        config.set(plot.getX() + "#" + plot.getZ() + ".helpers", new ArrayList<String>());
        config.set(plot.getX() + "#" + plot.getZ() + ".denied", new ArrayList<String>());
        config.set(plot.getX() + "#" + plot.getZ() + ".flags", new ArrayList<String>());
        config.save();
    }

    public void deletePlot(Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        config.remove(plot.getX() + "#" + plot.getZ());
        config.save();
    }

    public String getHelpers(Plot plot) {
        StringBuilder sb = new StringBuilder();
        for (String list : new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML).getStringList(plot.getX() + "#" + plot.getZ() + ".helpers")) {
            sb.append(list + ", ");
        }
        return sb.toString();
    }

    public String getDenied(Plot plot) {
        StringBuilder sb = new StringBuilder();
        for (String list : new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML).getStringList(plot.getX() + "#" + plot.getZ() + ".denied")) {
            sb.append(list + ", ");
        }
        return sb.toString();    }

    public boolean isHelper(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        for (String list : config.getStringList(plot.getX() + "#" + plot.getZ() + ".helpers")) {
            if(list.toLowerCase().equals(username.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void addHelper(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        ArrayList<String> helpers = new ArrayList<>();
        for (String list : config.getStringList(plot.getX() + "#" + plot.getZ() + ".helpers")) {
            helpers.add(list);
        }
        helpers.add(username.toLowerCase());
        config.set(plot.getX() + "#" + plot.getZ() + ".helpers", helpers);
        config.save();
    }

    public void removeHelper(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        ArrayList<String> helpers = new ArrayList<>();
        for (String list : config.getStringList(plot.getX() + "#" + plot.getZ() + ".helpers")) {
            helpers.add(list);
        }
        helpers.remove(username.toLowerCase());
        config.set(plot.getX() + "#" + plot.getZ() + ".helpers", helpers);
        config.save();
    }

    public boolean isDenied(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        for (String list : config.getStringList(plot.getX() + "#" + plot.getZ() + ".denied")) {
            if(list.toLowerCase().equals(username.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void addDenied(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        ArrayList<String> denied = new ArrayList<>();
        for (String list : config.getStringList(plot.getX() + "#" + plot.getZ() + ".denied")) {
            denied.add(list);
        }
        denied.add(username.toLowerCase());
        config.set(plot.getX() + "#" + plot.getZ() + ".denied", denied);
        config.save();
    }

    public void removeDenied(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        ArrayList<String> denied = new ArrayList<>();
        for (String list : config.getStringList(plot.getX() + "#" + plot.getZ() + ".denied")) {
            denied.add(list);
        }
        denied.remove(username.toLowerCase());
        config.set(plot.getX() + "#" + plot.getZ() + ".denied", denied);
        config.save();
    }

    public boolean isOwner(String username, Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        return config.getString(plot.getX() + "#" + plot.getZ() + ".owner").equals(username);
    }

    public boolean hasOwner(Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        return config.exists(plot.getX() + "#" + plot.getZ());
    }

    public boolean hasHome(String username, int homeNumber) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        ArrayList<String> homes = new ArrayList<>();
        for (String list : config.getAll().keySet()) {
            if(config.getString(list + ".owner").equals(username)) {
                homes.add(list);
            }
        }
        if (homes.get(homeNumber - 1) != null) {
            return true;
        }
        return false;
    }

    public String getPlotId(String username, int homeNumber) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        ArrayList<String> homes = new ArrayList<>();
        for (String list : config.getAll().keySet()) {
            if(config.getString(list + ".owner").equals(username)) {
                homes.add(list);
            }
        }
        if(homes.size() > 0) {
            return homes.get(homeNumber - 1);
        }
        return null;
    }

    public ArrayList<String> getHomes(String username) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        ArrayList<String> homes = new ArrayList<>();
        for (String list : config.getAll().keySet()) {
            if(config.getString(list + ".owner").equals(username)) {
                homes.add(list);
            }
        }
        return homes;
    }

    public String getPlotName(Plot plot) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        return config.getString(plot.getX() + "#" + plot.getZ() + ".owner");
    }

    public Plot getNextFreePlot(int limitXZ) {
        Config config = new Config(FuturePlots.getInstance().getDataFolder() + "/plots.yml", Config.YAML);
        for(int i = 0; limitXZ <= 0 || i < limitXZ; i++) {
            ArrayList<String> existing = new ArrayList<>();
            for (String list : config.getAll().keySet()) {
                String[] ex = list.split("#");
                if(Math.abs(Integer.parseInt(ex[0])) == i && Math.abs(Integer.parseInt(ex[1])) <= i) {
                    existing.add(list);
                } else if(Math.abs(Integer.parseInt(ex[1])) == i && Math.abs(Integer.parseInt(ex[0])) <= i) {
                    existing.add(list);
                }
            }
            if(existing.size() == Math.max(1, 8 * i)) {
                continue;
            }

            if(new Provider().findEmptyPlotSquared(0, i, existing) != null) {
                String[] ex = new Provider().findEmptyPlotSquared(0, i, existing).split("#");
                Plot plot = new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), Settings.levelName);
                return plot;
            }
            for (int a = 1; a < i; a++) {
                if(new Provider().findEmptyPlotSquared(a, i, existing) != null) {
                    String[] ex = new Provider().findEmptyPlotSquared(a, i, existing).split("#");
                    Plot plot = new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), Settings.levelName);
                    return plot;
                }
            }
            if(new Provider().findEmptyPlotSquared(i, i, existing) != null) {
                String[] ex = new Provider().findEmptyPlotSquared(i, i, existing).split("#");
                Plot plot = new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), Settings.levelName);
                return plot;
            }
        }
        return null;
    }

    /*public function getNextFreePlot(string $levelName, int $limitXZ = 0) : ?plot {
        $plotsArr = $this->yaml->get("plots", []);
        for($i = 0; $limitXZ <= 0 or $i < $limitXZ; $i++) {
            $existing = [];
            foreach($plotsArr as $id => $data) {
                if($data["level"] === $levelName) {
                    if(abs($data["x"]) === $i and abs($data["z"]) <= $i) {
                        $existing[] = [$data["x"], $data["z"]];
                    }elseif(abs($data["z"]) === $i and abs($data["x"]) <= $i) {
                        $existing[] = [$data["x"], $data["z"]];
                    }
                }
            }
            $plots = [];
            foreach($existing as $arr) {
                $plots[$arr[0]][$arr[1]] = true;
            }
            if(count($plots) === max(1, 8 * $i)) {
                continue;
            }
            if($ret = self::findEmptyPlotSquared(0, $i, $plots)) {
                list($X, $Z) = $ret;
                $plot = new Plot($levelName, $X, $Z);
                $this->cachePlot($plot);
                return $plot;
            }
            for($a = 1; $a < $i; $a++) {
                if($ret = self::findEmptyPlotSquared($a, $i, $plots)) {
                    list($X, $Z) = $ret;
                    $plot = new Plot($levelName, $X, $Z);
                    $this->cachePlot($plot);
                    return $plot;
                }
            }
            if($ret = self::findEmptyPlotSquared($i, $i, $plots)) {
                list($X, $Z) = $ret;
                $plot = new Plot($levelName, $X, $Z);
                $this->cachePlot($plot);
                return $plot;
            }
        }
        return null;
    }*/
}
