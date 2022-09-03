package tim03we.futureplots.utils.forms.custom;

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
import cn.nukkit.form.element.Element;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import tim03we.futureplots.utils.forms.FormAPI;
import tim03we.futureplots.utils.forms.FormHandler;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CustomForm {

    private final String title;
    private final ArrayList<Element> elements;
    private final Consumer<Player> closeCallback;
    private final BiConsumer<Player, FormResponseCustom> submitCallback;

    public CustomForm(Builder b) {
        this.title = b.title;
        this.elements = b.elements;
        this.closeCallback = b.closeCallback;
        this.submitCallback = b.submitCallback;
    }


    public void send(Player player) {
        FormWindowCustom form = new FormWindowCustom(title);
        elements.forEach(form::addElement);

        FormHandler.customPending.put(player.getName(), this);
        player.showFormWindow(form);

        Server.getInstance().getScheduler().scheduleDelayedTask(FormAPI.instance, () -> player.sendExperience(player.getExperience()), 20);
    }

    public void setClosed(Player player) {
        if (closeCallback == null) return;
        closeCallback.accept(player);
    }

    public void setSubmitted(Player player, FormResponseCustom form) {
        submitCallback.accept(player, form);
    }

    public static class Builder {

        private String title;
        private ArrayList<Element> elements = new ArrayList<>();
        private Consumer<Player> closeCallback;
        private BiConsumer<Player, FormResponseCustom> submitCallback;

        public Builder(String title) {
            this.title = title;
        }

        public Builder addElement(Element element) {
            elements.add(element);
            return this;
        }

        public Builder setTitle(String s) {
            title = s;
            return this;
        }

        public Builder onClose(Consumer<Player> cb) {
            this.closeCallback = cb;
            return this;
        }

        public Builder onSubmit(BiConsumer<Player, FormResponseCustom> cb) {
            this.submitCallback = cb;
            return this;
        }


        public CustomForm build() {
            return new CustomForm(this);
        }

    }

}
