package tim03we.futureplots.utils.forms.data;

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
import cn.nukkit.form.element.ElementButton;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.handler.CommandHandler;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;
import tim03we.futureplots.utils.Utils;
import tim03we.futureplots.utils.forms.simple.SimpleForm;

import java.util.function.Consumer;

public class FormWindows {

    public static void openNoPlotForm(Player player) {
        SimpleForm.Builder form = new SimpleForm.Builder("Plot Hilfe", "Du befindest dich auf keinem Plot. Hier kannst du auswählen, ob für dich automatisch ein freies Plot gesucht wird.\n\nUm ein Plot verwalten zu können, musst du dich auf einem Plot befinden.\n\n");
        form.addButton(new ElementButton("Suche Plot"), (player1 -> {
            CommandHandler.runCmd(player, "claim", new String[]{});
        }));
        form.build().send(player);
    }

    public static void openPlotForm(Player player, Plot plot) {
        String ownerName = Utils.getPlayerName(FuturePlots.provider.getOwner(plot));
        boolean owner = false;
        if(ownerName.equalsIgnoreCase(player.getName())) {
            owner = true;
        }

        SimpleForm.Builder form = new SimpleForm.Builder("Plot " + plot.getFullID(), "Inhaber: " + ownerName + "\n\n");

        form.addButton(new ElementButton("Helfer"));
        form.addButton(new ElementButton("Member"));
        form.addButton(new ElementButton("Blockiert"));
        if(owner) {
            if(CommandHandler.getCommand("clear") != null) form.addButton(new ElementButton("Plot\n§eLEEREN"), (player1 -> {
                if(Settings.interaction_confirmation) {
                    confirm(player, cb -> {
                        if(cb) {
                            CommandHandler.runCmd(player, "clear", new String[]{"confirm"});
                        }
                    });
                } else {
                    CommandHandler.runCmd(player, "clear", new String[]{});
                }
            }));
            if(CommandHandler.getCommand("dispose") != null) form.addButton(new ElementButton("Plot\n§6FREIGEBEN"), (player1 -> {
                if(Settings.interaction_confirmation) {
                    confirm(player, cb -> {
                        if(cb) {
                            CommandHandler.runCmd(player, "dispose", new String[]{"confirm"});
                        }
                    });
                } else {
                    CommandHandler.runCmd(player, "dispose", new String[]{});
                }
            }));
            if(CommandHandler.getCommand("delete") != null) form.addButton(new ElementButton("Plot\n§cLÖSCHEN"), player1 -> {
                if(Settings.interaction_confirmation) {
                    confirm(player, cb -> {
                        if(cb) {
                            CommandHandler.runCmd(player, "delete", new String[]{"confirm"});
                        }
                    });
                } else {
                    CommandHandler.runCmd(player, "delete", new String[]{});
                }
            });
        }
    }

    private static void confirm(Player player, Consumer<Boolean> callback) {
        SimpleForm.Builder form = new SimpleForm.Builder("Confirmation", "Bist du sicher, das du diese Aktion ausführen möchtest? Diese Aktion kann nicht mehr Rückgängig gemacht werden.\n\nSolltest du damit einverstanden sein, somit bestätige dies.\n\n");
        form.addButton(new ElementButton("Verstanden!"), (player1 -> {
            callback.accept(true);
        }));
        form.onClose(player1 -> {
            callback.accept(false);
        });
        form.build().send(player);
    }
}
