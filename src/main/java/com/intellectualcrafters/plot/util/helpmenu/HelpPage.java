package com.intellectualcrafters.plot.util.helpmenu;

import com.intellectualcrafters.plot.commands.CommandCategory;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.StringMan;

import java.util.ArrayList;
import java.util.List;

public class HelpPage {

    private final List<HelpObject> helpObjects;
    private final String header;

    public HelpPage(CommandCategory category, int currentPage, int maxPages) {
        this.helpObjects = new ArrayList<>();
        this.header = C.HELP_PAGE_HEADER.s().replace("%category%", category == null ? "ALL" : category.toString())
                .replace("%current%", (currentPage + 1) + "").replace("%max%", (maxPages + 1) + "");
    }

    public void render(PlotPlayer player) {
        if (this.helpObjects.size() < 1) {
            MainUtil.sendMessage(player, C.NOT_VALID_NUMBER, "(0)");
        } else {
            String message = C.HELP_HEADER.s() + "\n" + this.header + "\n" + StringMan.join(this.helpObjects, "\n") + "\n" + C.HELP_FOOTER.s();
            MainUtil.sendMessage(player, message, false);
        }
    }

    public void addHelpItem(HelpObject object) {
        this.helpObjects.add(object);
    }
}
