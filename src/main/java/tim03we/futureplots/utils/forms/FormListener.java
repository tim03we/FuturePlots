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

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;

public class FormListener implements Listener {

    @EventHandler
    public void onForm(PlayerFormRespondedEvent event) {
        if (event.getWindow() instanceof FormWindowSimple) FormHandler.handleSimple(event.getPlayer(), (FormWindowSimple) event.getWindow());
        if (event.getWindow() instanceof FormWindowModal) FormHandler.handleModal(event.getPlayer(), (FormWindowModal) event.getWindow());
        if (event.getWindow() instanceof FormWindowCustom) FormHandler.handleCustom(event.getPlayer(), (FormWindowCustom) event.getWindow());
    }
}
