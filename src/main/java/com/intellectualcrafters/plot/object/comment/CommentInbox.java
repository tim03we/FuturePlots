package com.intellectualcrafters.plot.object.comment;

import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.Permissions;

import java.util.List;

public abstract class CommentInbox {
    
    @Override
    public abstract String toString();

    public boolean canRead(Plot plot, PlotPlayer player) {
        if (Permissions.hasPermission(player, "plots.inbox.read." + toString(), true)) {
            if (plot.isOwner(player.getUUID()) || Permissions.hasPermission(player, "plots.inbox.read." + toString() + ".other", true)) {
                return true;
            }
        }
        return false;
    }

    public boolean canWrite(Plot plot, PlotPlayer player) {
        if (plot == null) {
            return Permissions.hasPermission(player, "plots.inbox.write." + toString(), true);
        }
        return Permissions.hasPermission(player, "plots.inbox.write." + toString(), true) && (plot.isOwner(player.getUUID()) || Permissions
                .hasPermission(player, "plots.inbox.write." + toString() + ".other", true));
    }

    public boolean canModify(Plot plot, PlotPlayer player) {
        if (Permissions.hasPermission(player, "plots.inbox.modify." + toString(), true)) {
            if (plot.isOwner(player.getUUID()) || Permissions.hasPermission(player, "plots.inbox.modify." + toString() + ".other", true)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     *
     * <br>
     * The `whenDone` parameter should be executed when it's done fetching the comments.
     * The value should be set to List of comments
     *
     * @param plot
     * @param whenDone
     * @return
     */
    public abstract boolean getComments(Plot plot, RunnableVal<List<PlotComment>> whenDone);

    public abstract boolean addComment(Plot plot, PlotComment comment);

    public void removeComment(Plot plot, PlotComment comment) {
        DBFunc.removeComment(plot, comment);
    }

    public void clearInbox(Plot plot) {
        DBFunc.clearInbox(plot, toString());
    }
}
