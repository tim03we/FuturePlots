package tim03we.futureplots.utils.forms;

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
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import tim03we.futureplots.utils.forms.custom.CustomForm;
import tim03we.futureplots.utils.forms.modal.ModalForm;
import tim03we.futureplots.utils.forms.simple.SimpleForm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FormHandler {

    public static HashMap<String, SimpleForm> simplePending = new HashMap<>();
    public static HashMap<String, ModalForm> modalPending = new HashMap<>();
    public static HashMap<String, CustomForm> customPending = new HashMap<>();

    public static void handleSimple(Player player, FormWindowSimple form) {
        if (simplePending.containsKey(player.getName())) {
            SimpleForm sform = simplePending.get(player.getName());
            simplePending.remove(player.getName());

            if (form.getResponse() == null) {
                sform.setClosed(player);
                return;
            }

            ElementButton clickedButton = form.getResponse().getClickedButton();

            for (Map.Entry<ElementButton, Consumer<Player>> map : sform.getButtons().entrySet()) {
                if (map.getKey().getText().equalsIgnoreCase(clickedButton.getText())) {
                    if (map.getValue() != null) map.getValue().accept(player);
                    break;
                }
            }

            sform.setSubmitted(player, form.getResponse());
        }
    }

    public static void handleModal(Player player, FormWindowModal form) {
        if (modalPending.containsKey(player.getName())) {
            ModalForm mform = modalPending.get(player.getName());
            modalPending.remove(player.getName());

            if (form.getResponse() == null) {
                mform.setClosed(player);
                return;
            }

            String clickedButton = form.getResponse().getClickedButtonText();
            if (clickedButton.equalsIgnoreCase(mform.getYes())) mform.setYes(player);
            if (clickedButton.equalsIgnoreCase(mform.getNo())) mform.setNo(player);

            mform.setSubmitted(player, form.getResponse());
        }
    }

    public static void handleCustom(Player player, FormWindowCustom form) {
        if (customPending.containsKey(player.getName())) {
            CustomForm cform = customPending.get(player.getName());
            customPending.remove(player.getName());

            if (form.getResponse() == null) {
                cform.setClosed(player);
                return;
            }

            cform.setSubmitted(player, form.getResponse());
        }
    }

}
