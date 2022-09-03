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

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import org.bson.Document;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.provider.DataProvider;
import tim03we.futureplots.provider.mongodb.MongoAPI;
import tim03we.futureplots.provider.sql.SQLEntity;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoDBProvider implements DataProvider {

    private String plotsColl = "plots";

    @Override
    public void connect() {
        Config config = FuturePlots.getInstance().getConfig();
        MongoAPI.load(config.getString("mongodb.uri"), config.getString("mongodb.database"));
        checkData();
    }

    private void checkData() {
        /*FuturePlots.getInstance().getLogger().warning("[XUID] Start checking for missing XUIDs... During this check, no players can enter the server.");
        int missingXuid = 0;

        SQLTable table = database.getTable("plots");
        for (SQLEntity sqlEntity : table.find()) {
            String owner = sqlEntity.getString("owner");
            if(!Utils.isLong(owner) && owner.length() != 16 && !owner.equals("none")) {
                RequestXUID requestXUID = new RequestXUID(owner);
                String xuid = requestXUID.sendAndGetXuid();
                table.update(sqlEntity, new SQLEntity("owner", xuid));
                missingXuid++;
                FuturePlots.getInstance().getLogger().info("[XUID] " + owner + " has been converted to an XUID..");
                FuturePlots.xuidProvider.updateEntry(owner, xuid);
            }
            List<String> newList = new Gson().fromJson(sqlEntity.getString("helpers"), List.class);
            for (String key : newList) {
                 if(!Utils.isLong(key) && key.length() != 16 && !owner.equals("none")) {
                     RequestXUID requestXUID = new RequestXUID(key);
                     String xuid = requestXUID.sendAndGetXuid();
                     if(xuid != null) {
                         newList.remove(key);
                         newList.add(xuid);
                         missingXuid++;
                         FuturePlots.getInstance().getLogger().info("[XUID] " + key + " has been converted to an XUID..");
                         FuturePlots.xuidProvider.updateEntry(key, xuid);
                     }
                 }
            }
            table.update(new SQLEntity("level", sqlEntity.getString("level")).append("plotid", sqlEntity.getString("plotid")), new SQLEntity("helpers", new Gson().toJson(newList)));

            newList = new Gson().fromJson(sqlEntity.getString("members"), List.class);
            for (String key : newList) {
                if(!Utils.isLong(key) && key.length() != 16 && !owner.equals("none")) {
                    RequestXUID requestXUID = new RequestXUID(key);
                    String xuid = requestXUID.sendAndGetXuid();
                    if(xuid != null) {
                        newList.remove(key);
                        newList.add(xuid);
                        missingXuid++;
                        FuturePlots.getInstance().getLogger().info("[XUID] " + key + " has been converted to an XUID..");
                        FuturePlots.xuidProvider.updateEntry(key, xuid);
                    }
                }
            }
            table.update(new SQLEntity("level", sqlEntity.getString("level")).append("plotid", sqlEntity.getString("plotid")), new SQLEntity("members", new Gson().toJson(newList)));

            newList = new Gson().fromJson(sqlEntity.getString("denied"), List.class);
            for (String key : newList) {
                if(!Utils.isLong(key) && key.length() != 16 && !owner.equals("none")) {
                    RequestXUID requestXUID = new RequestXUID(key);
                    String xuid = requestXUID.sendAndGetXuid();
                    if(xuid != null) {
                        newList.remove(key);
                        newList.add(xuid);
                        missingXuid++;
                        FuturePlots.getInstance().getLogger().info("[XUID] " + key + " has been converted to an XUID..");
                        FuturePlots.xuidProvider.updateEntry(key, xuid);
                    }
                }
            }
            table.update(new SQLEntity("level", sqlEntity.getString("level")).append("plotid", sqlEntity.getString("plotid")), new SQLEntity("denied", new Gson().toJson(newList)));
        }
        FuturePlots.getInstance().getLogger().warning("[XUID] " + missingXuid + " names were converted to XUID. The server can now be accessed.");
        */
        Settings.joinServer = true;
    }

    @Override
    public void save() {
    }

    @Override
    public boolean claimPlot(String playerId, Plot plot) {
        CompletableFuture.runAsync(() -> {
            Document insertDoc = new Document("level", plot.getLevelName());
            insertDoc.append("plotid", plot.getFullID());
            if(getMerges(plot).size() > 0) {
                MongoAPI.changeValue(plotsColl, new Document("level", plot.getLevelName()).append("plotid", plot.getFullID()), "owner", playerId);
            } else {
                insertDoc.append("owner", playerId);
                insertDoc.append("helpers", new ArrayList<>());
                insertDoc.append("members", new ArrayList<>());
                insertDoc.append("denied", new ArrayList<>());
                insertDoc.append("flags", new ArrayList<>());
                insertDoc.append("home", null);
                insertDoc.append("merge", null);
                insertDoc.append("merges", new ArrayList<>());
                insertDoc.append("merge_check", new ArrayList<>());
                MongoAPI.mongoDatabase.getCollection(plotsColl).insertOne(insertDoc);
            }
        });
        return true;
    }

    @Override
    public void deletePlot(Plot plot) {
        CompletableFuture.runAsync(() -> {
            Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(new Document("level", plot.getLevelName()).append("plotid", plot.getFullID())).first();
            if(find != null) {
                MongoAPI.mongoDatabase.getCollection(plotsColl).deleteOne(find);
            }
        });
    }

    @Override
    public void disposePlot(Plot plot) {
        Document find = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, find, "owner", "none");
        MongoAPI.changeValue(plotsColl, find, "helpers", new ArrayList<>());
        MongoAPI.changeValue(plotsColl, find, "members",  new ArrayList<>());
        MongoAPI.changeValue(plotsColl, find, "denied",  new ArrayList<>());
        MongoAPI.changeValue(plotsColl, find, "flags",  new ArrayList<>());
        MongoAPI.changeValue(plotsColl, find, "home", "");
    }

    @Override
    public boolean isHelper(String playerId, Plot plot) {
        return getHelpers(plot) != null && getHelpers(plot).contains(playerId);
    }

    @Override
    public boolean isDenied(String playerId, Plot plot) {
        return getDenied(plot) != null && getDenied(plot).contains(playerId);
    }

    @Override
    public boolean isMember(String playerId, Plot plot) {
        return getMembers(plot) != null && getMembers(plot).contains(playerId);
    }

    @Override
    public boolean hasOwner(Plot plot) {
        return !getOwner(plot).equals("none");
    }

    @Override
    public void setOwner(String playerId, Plot plot) {
        CompletableFuture.runAsync(() -> {
            MongoAPI.changeValue(plotsColl, new Document("level", plot.getLevelName())
                    .append("plotid", plot.getFullID()), "owner", playerId);
        });
    }

    @Override
    public String getOwner(Plot plot) {
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(new Document("level", plot.getLevelName())
                .append("plotid", plot.getFullID())).first();
        if(find == null) {
            return "none";
        }
        return find.getString("owner");
    }

    @Override
    public List<String> getHelpers(Plot plot) {
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(new Document("level", plot.getLevelName())
                .append("plotid", plot.getFullID())).first();
        if(find == null) {
            return null;
        }
        return find.getList("helpers", String.class);
    }

    @Override
    public List<String> getMembers(Plot plot) {
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(new Document("level", plot.getLevelName())
                .append("plotid", plot.getFullID())).first();
        if(find == null) {
            return null;
        }
        return find.getList("members", String.class);
    }

    @Override
    public List<String> getDenied(Plot plot) {
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(new Document("level", plot.getLevelName())
                .append("plotid", plot.getFullID())).first();
        if(find == null) {
            return null;
        }
        return find.getList("denied", String.class);
    }

    @Override
    public void addHelper(String playerId, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.add(playerId.toLowerCase());
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "helpers", helpers);
    }

    @Override
    public void addMember(String playerId, Plot plot) {
        List<String> members = getMembers(plot);
        members.add(playerId.toLowerCase());
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "members", members);
    }

    @Override
    public void addDenied(String playerId, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.add(playerId.toLowerCase());
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "denied", denied);
    }

    @Override
    public void removeHelper(String playerId, Plot plot) {
        List<String> helpers = getHelpers(plot);
        helpers.remove(playerId.toLowerCase());
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "helpers", helpers);
    }

    @Override
    public void removeMember(String playerId, Plot plot) {
        List<String> members = getMembers(plot);
        members.remove(playerId.toLowerCase());
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "members", members);
    }

    @Override
    public void removeDenied(String playerId, Plot plot) {
        List<String> denied = getDenied(plot);
        denied.remove(playerId.toLowerCase());
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "denied", denied);
    }

    @Override
    public void setHome(Plot plot, Location location) {
        String locationString = location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "home", locationString);
    }

    @Override
    public void deleteHome(Plot plot) {
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(search).first();
        if(find != null) {
            MongoAPI.mongoDatabase.getCollection(plotsColl).deleteOne(find);
        }
        MongoAPI.changeValue(plotsColl, search, "home", null);
    }

    @Override
    public Location getHome(Plot plot) {
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(search).first();
        if(find != null && find.getString("home") != null) {
            String locationString = find.getString("home");
            if(locationString != null) {
                String[] ex = find.getString("home").split(":");
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
        List<Plot> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : getPlots(playerId, level)) {
                String[] ex = plot.split(";");
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }

        } else {
            for (String plot : getPlots(playerId, null)) {
                String[] ex = plot.split(";");
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }
        }
        if((i - 1) >= plots.size()) return null;
        if(plots.size() > 0) {
            Plot plot = plots.get(i - 1);
            return plot;
        }
        return null;
    }

    @Override
    public List<String> getPlots(String playerId, Object level) {
        List<String> plots = new ArrayList<>();
        if(level != null) {
            for (Document find : MongoAPI.mongoDatabase.getCollection(plotsColl).find()) {
                if(find.getString("level").equals(level) && find.getString("owner").equals(playerId)) {
                    String[] plotid = find.getString("plotid").split(";");
                    plots.add(level + ";" + plotid[0] + ";" + plotid[1]);
                }
            }
        } else {
            for (Document find : MongoAPI.mongoDatabase.getCollection(plotsColl).find()) {
                if(find.getString("owner").equals(playerId)) {
                    String[] plotid = find.getString("plotid").split(";");
                    plots.add(find.getString("level") + ";" + plotid[0] + ";" + plotid[1]);
                }
            }
        }
        return plots;
    }

    @Override
    public Plot getNextFreePlot(String level) {
        List<Plot> plots = new ArrayList<>();
        for (Document find : MongoAPI.mongoDatabase.getCollection(plotsColl).find()) {
            if(find.getString("level").equals(level)) {
                String[] plotid = find.getString("plotid").split(";");
                plots.add(new Plot(Integer.parseInt(plotid[0]), Integer.parseInt(plotid[1]), level));
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
        mergeList.add(mergePlot.getFullID());
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "merge_check", mergeList);
    }

    @Override
    public List<Plot> getMergeCheck(Plot plot) {
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(search).first();
        List<Plot> list = new ArrayList<>();
        if(find != null) {
            List<String> plotList = find.getList("merge_check", String.class);
            for (String plots : plotList) {
                String[] ex = plots.split(";");
                list.add(new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName()));
            }
        }
        return list;
    }

    @Override
    public Plot getOriginPlot(Plot plot) {
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(search).first();
        if(find != null) {
            if(find.getString("merge") == null) return null;
            String[] ex = find.getString("merge").split(";");
            return new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName());
        }
        return null;
    }

    @Override
    public void setOriginPlot(Plot mergePlot, Plot originPlot) {
        Document search = new Document("level", mergePlot.getLevelName()).append("plotid", mergePlot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "merge", originPlot.getFullID());
    }


    @Override
    public void addMerge(Plot originPlot, Plot mergePlot) {
        List<String> mergeList = new ArrayList<>();
        for (Plot plots : getMerges(originPlot)) {
            mergeList.add(plots.getX() + ";" + plots.getZ());
        }
        mergeList.add(mergePlot.getFullID());
        Document search = new Document("level", originPlot.getLevelName()).append("plotid", originPlot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "merges", mergeList);
    }

    @Override
    public List<Plot> getMerges(Plot plot) {
        Document find = MongoAPI.mongoDatabase.getCollection(plotsColl).find(new Document("level", plot.getLevelName())
                .append("plotid", plot.getFullID())).first();
        List<Plot> list = new ArrayList<>();
        if(find != null) {
            List<String> plotList = find.getList("merges", String.class);
            for (String plots : plotList) {
                String[] ex = plots.split(";");
                list.add(new Plot(Integer.parseInt(ex[0]), Integer.parseInt(ex[1]), plot.getLevelName()));
            }
        }
        return list;
    }

    @Override
    public void resetMerges(Plot plot) {
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "home", null);
        MongoAPI.changeValue(plotsColl, search, "merge", null);
        MongoAPI.changeValue(plotsColl, search, "merges", new ArrayList<>());
        MongoAPI.changeValue(plotsColl, search, "merge_check", new ArrayList<>());
    }

    @Override
    public void deleteMergeList(Plot plot) {
        Document search = new Document("level", plot.getLevelName()).append("plotid", plot.getFullID());
        MongoAPI.changeValue(plotsColl, search, "merges", new ArrayList<>());
    }
}
