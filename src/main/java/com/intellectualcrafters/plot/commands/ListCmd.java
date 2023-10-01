package com.intellectualcrafters.plot.commands;

import com.google.common.base.Optional;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.PS.SortType;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.*;
import com.intellectualcrafters.plot.util.*;
import com.intellectualcrafters.plot.util.expiry.ExpireManager;
import com.plotsquared.general.commands.CommandDeclaration;

import java.util.*;
import java.util.Map.Entry;

@CommandDeclaration(
        command = "list",
        aliases = {"l", "find", "search"},
        description = "List plots",
        permission = "plots.list",
        category = CommandCategory.INFO,
        usage = "/plot list <forsale|mine|shared|world|top|all|unowned|unknown|player|world|done|fuzzy <search...>> [#]")
public class ListCmd extends SubCommand {

    private String[] getArgumentList(PlotPlayer player) {
        List<String> args = new ArrayList<>();
        if (EconHandler.getEconHandler() != null && Permissions.hasPermission(player, C.PERMISSION_LIST_FORSALE)) {
            args.add("forsale");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_MINE)) {
            args.add("mine");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_SHARED)) {
            args.add("shared");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_WORLD)) {
            args.add("world");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_TOP)) {
            args.add("top");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_ALL)) {
            args.add("all");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_UNOWNED)) {
            args.add("unowned");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_UNKNOWN)) {
            args.add("unknown");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_PLAYER)) {
            args.add("<player>");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_WORLD)) {
            args.add("<world>");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_DONE)) {
            args.add("done");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_EXPIRED)) {
            args.add("expired");
        }
        if (Permissions.hasPermission(player, C.PERMISSION_LIST_FUZZY)) {
            args.add("fuzzy <search...>");
        }
        return args.toArray(new String[args.size()]);
    }

    public void noArgs(PlotPlayer player) {
        MainUtil.sendMessage(player, C.SUBCOMMAND_SET_OPTIONS_HEADER.s() + Arrays.toString(getArgumentList(player)));
    }

    @Override
    public boolean onCommand(PlotPlayer player, String[] args) {
        if (args.length < 1) {
            noArgs(player);
            return false;
        }
        int page = 0;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[args.length - 1]);
                --page;
                if (page < 0) {
                    page = 0;
                }
            } catch (NumberFormatException ignored) {
                page = -1;
            }
        }

        List<Plot> plots = null;

        String world = player.getLocation().getWorld();
        PlotArea area = player.getApplicablePlotArea();
        String arg = args[0].toLowerCase();
        boolean sort = true;
        switch (arg) {
            case "mine":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_MINE)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_MINE);
                    return false;
                }
                sort = false;
                plots = PS.get().sortPlotsByTemp(PS.get().getBasePlots(player));
                break;
            case "shared":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_SHARED)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_SHARED);
                    return false;
                }
                plots = new ArrayList<>();
                for (Plot plot : PS.get().getPlots()) {
                    if (plot.getTrusted().contains(player.getUUID()) || plot.getMembers().contains(player.getUUID())) {
                        plots.add(plot);
                    }
                }
                break;
            case "world":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_WORLD)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_WORLD);
                    return false;
                }
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_WORLD_NAME.f(world))) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_WORLD_NAME.f(world));
                    return false;
                }
                plots = new ArrayList<>(PS.get().getPlots(world));
                break;
            case "expired":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_EXPIRED)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_EXPIRED);
                    return false;
                }
                plots = ExpireManager.IMP == null ? new ArrayList<Plot>() : new ArrayList<>(ExpireManager.IMP.getPendingExpired());
                break;
            case "area":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_AREA)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_AREA);
                    return false;
                }
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_WORLD_NAME.f(world))) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_WORLD_NAME.f(world));
                    return false;
                }
                plots = area == null ? new ArrayList<Plot>() : new ArrayList<>(area.getPlots());
                break;
            case "all":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_ALL)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_ALL);
                    return false;
                }
                plots = new ArrayList<>(PS.get().getPlots());
                break;
            case "done":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_DONE)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_DONE);
                    return false;
                }
                plots = new ArrayList<>();
                for (Plot plot : PS.get().getPlots()) {
                    Optional<String> flag = plot.getFlag(Flags.DONE);
                    if (!flag.isPresent()) {
                        continue;
                    }
                    plots.add(plot);
                }
                Collections.sort(plots, new Comparator<Plot>() {
                    @Override
                    public int compare(Plot a, Plot b) {
                        String va = "" + a.getFlags().get(Flags.DONE);
                        String vb = "" + b.getFlags().get(Flags.DONE);
                        if (MathMan.isInteger(va)) {
                            if (MathMan.isInteger(vb)) {
                                return Integer.parseInt(vb) - Integer.parseInt(va);
                            }
                            return -1;
                        }
                        return 1;
                    }
                });
                sort = false;
                break;
            case "top":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_TOP)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_TOP);
                    return false;
                }
                plots = new ArrayList<>(PS.get().getPlots());
                Collections.sort(plots, new Comparator<Plot>() {
                    @Override
                    public int compare(Plot p1, Plot p2) {
                        double v1 = 0;
                        int p1s = p1.getSettings().getRatings().size();
                        int p2s = p2.getRatings().size();
                        if (!p1.getSettings().getRatings().isEmpty()) {
                            for (Entry<UUID, Rating> entry : p1.getRatings().entrySet()) {
                                double av = entry.getValue().getAverageRating();
                                v1 += av * av;
                            }
                            v1 /= p1s;
                            v1 += p1s;
                        }
                        double v2 = 0;
                        if (!p2.getSettings().getRatings().isEmpty()) {
                            for (Entry<UUID, Rating> entry : p2.getRatings().entrySet()) {
                                double av = entry.getValue().getAverageRating();
                                v2 += av * av;
                            }
                            v2 /= p2s;
                            v2 += p2s;
                        }
                        if (v2 == v1 && v2 != 0) {
                            return p2s - p1s;
                        }
                        return (int) Math.signum(v2 - v1);
                    }
                });
                sort = false;
                break;
            case "forsale":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_FORSALE)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_FORSALE);
                    return false;
                }
                if (EconHandler.getEconHandler() == null) {
                    break;
                }
                plots = new ArrayList<>();
                for (Plot plot : PS.get().getPlots()) {
                    Optional<Double> price = plot.getFlag(Flags.PRICE);
                    if (price.isPresent()) {
                        plots.add(plot);
                    }
                }
                break;
            case "unowned":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_UNOWNED)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_UNOWNED);
                    return false;
                }
                plots = new ArrayList<>();
                for (Plot plot : PS.get().getPlots()) {
                    if (plot.owner == null) {
                        plots.add(plot);
                    }
                }
                break;
            case "unknown":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_UNKNOWN)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_UNKNOWN);
                    return false;
                }
                plots = new ArrayList<>();
                for (Plot plot : PS.get().getPlots()) {
                    if (plot.owner == null) {
                        continue;
                    }
                    if (UUIDHandler.getName(plot.owner) == null) {
                        plots.add(plot);
                    }
                }
                break;
            case "fuzzy":
                if (!Permissions.hasPermission(player, C.PERMISSION_LIST_FUZZY)) {
                    MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_FUZZY);
                    return false;
                }
                if (args.length < (page == -1 ? 2 : 3)) {
                    C.COMMAND_SYNTAX.send(player, "/plot list fuzzy <search...> [#]");
                    return false;
                }
                String term;
                if (MathMan.isInteger(args[args.length - 1])) {
                    term = StringMan.join(Arrays.copyOfRange(args, 1, args.length - 1), " ");
                } else {
                    term = StringMan.join(Arrays.copyOfRange(args, 1, args.length), " ");
                }
                plots = MainUtil.getPlotsBySearch(term);
                sort = false;
                break;
            default:
                if (PS.get().hasPlotArea(args[0])) {
                    if (!Permissions.hasPermission(player, C.PERMISSION_LIST_WORLD)) {
                        MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_WORLD);
                        return false;
                    }
                    if (!Permissions.hasPermission(player, C.PERMISSION_LIST_WORLD_NAME.f(args[0]))) {
                        MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_WORLD_NAME.f(args[0]));
                        return false;
                    }
                    plots = new ArrayList<>(PS.get().getPlots(args[0]));
                    break;
                }
                UUID uuid = UUIDHandler.getUUID(args[0], null);
                if (uuid == null) {
                    try {
                        uuid = UUID.fromString(args[0]);
                    } catch (Exception ignored) {}
                }
                if (uuid != null) {
                    if (!Permissions.hasPermission(player, C.PERMISSION_LIST_PLAYER)) {
                        MainUtil.sendMessage(player, C.NO_PERMISSION, C.PERMISSION_LIST_PLAYER);
                        return false;
                    }
                    sort = false;
                    plots = PS.get().sortPlotsByTemp(PS.get().getPlots(uuid));
                    break;
                }
        }

        if (plots == null) {
            sendMessage(player, C.DID_YOU_MEAN, new StringComparison<>(args[0], new String[]{"mine", "shared", "world", "all"}).getBestMatch());
            return false;
        }

        if (plots.isEmpty()) {
            MainUtil.sendMessage(player, C.FOUND_NO_PLOTS);
            return false;
        }
        displayPlots(player, plots, 12, page, area, args, sort);
        return true;
    }

    public void displayPlots(final PlotPlayer player, List<Plot> plots, int pageSize, int page, PlotArea area,
            String[] args, boolean sort) {
        // Header
        Iterator<Plot> iterator = plots.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isBasePlot()) {
                iterator.remove();
            }
        }
        if (sort) {
            plots = PS.get().sortPlots(plots, SortType.CREATION_DATE, area);
        }
        this.paginate(player, plots, pageSize, page, new RunnableVal3<Integer, Plot, PlotMessage>() {
            @Override
            public void run(Integer i, Plot plot, PlotMessage message) {
                String color;
                if (plot.owner == null) {
                    color = "$3";
                } else if (plot.isOwner(player.getUUID())) {
                    color = "$1";
                } else if (plot.isAdded(player.getUUID())) {
                    color = "$4";
                } else if (plot.isDenied(player.getUUID())) {
                    color = "$2";
                } else {
                    color = "$1";
                }
                PlotMessage trusted =
                        new PlotMessage().text(C.color(C.PLOT_INFO_TRUSTED.s().replaceAll("%trusted%", MainUtil.getPlayerList(plot.getTrusted()))))
                                .color("$1");
                PlotMessage members =
                        new PlotMessage().text(C.color(C.PLOT_INFO_MEMBERS.s().replaceAll("%members%", MainUtil.getPlayerList(plot.getMembers()))))
                                .color("$1");
                String strFlags = StringMan.join(plot.getFlags().values(), ",");
                if (strFlags.isEmpty()) {
                    strFlags = C.NONE.s();
                }
                PlotMessage flags = new PlotMessage().text(C.color(C.PLOT_INFO_FLAGS.s().replaceAll("%flags%", strFlags))).color("$1");
                message.text("[").color("$3").text(i + "").command("/plot visit " + plot.getArea() + ";" + plot.getId())
                        .tooltip("/plot visit " + plot.getArea() + ";" + plot.getId()).color("$1")
                        .text("]")
                        .color("$3").text(" " + plot.toString()).tooltip(trusted, members, flags)
                        .command("/plot info " + plot.getArea() + ";" + plot.getId()).color(color).text(" - ").color("$2");
                String prefix = "";
                for (UUID uuid : plot.getOwners()) {
                    String name = UUIDHandler.getName(uuid);
                    if (name == null) {
                        message = message.text(prefix).color("$4").text("unknown").color("$2").tooltip(uuid.toString()).suggest(uuid.toString());
                    } else {
                        PlotPlayer pp = UUIDHandler.getPlayer(uuid);
                        if (pp != null) {
                            message = message.text(prefix).color("$4").text(name).color("$1").tooltip(new PlotMessage("Online").color("$4"));
                        } else {
                            message = message.text(prefix).color("$4").text(name).color("$1").tooltip(new PlotMessage("Offline").color("$3"));
                        }
                    }
                    prefix = ", ";
                }
            }
        }, "/plot list " + args[0], C.PLOT_LIST_HEADER_PAGED.s());
    }

}
