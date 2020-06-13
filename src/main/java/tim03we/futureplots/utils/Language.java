package tim03we.futureplots.utils;

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

import cn.nukkit.utils.Config;
import tim03we.futureplots.FuturePlots;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Language {

    public static HashMap<String, String> messages = new HashMap<>();
    public static String prefix;

    public static void init() {
        messages.clear();
        new File(FuturePlots.getInstance().getDataFolder() + "/lang/").mkdirs();
        FuturePlots.getInstance().saveResource("lang/" + Settings.language + ".yml");
        Config m = new Config(FuturePlots.getInstance().getDataFolder() + "/lang/" + Settings.language + ".yml");
        for (Map.Entry<String, Object> map : m.getAll().entrySet()) {
            String key = map.getKey();
            if (map.getValue() instanceof String) {
                String val = (String) map.getValue();
                messages.put(key, val);
            }
        }
        prefix = m.getString("prefix");
    }

    public static String translate(boolean prefix, String key, Object... replacements) {
        String message;
        if(prefix) {
            message = get(key);
        } else {
            message = getNoPrefix(key);
        }
        if(replacements == null) {
            return message;
        }
        int i = 1;
        for (Object replacement : replacements) {
            message = message.replace("%" + i, String.valueOf(replacement));
            i++;
        }
        return message;
    }

    public static String get(String key) {
        return prefix.replace("&", "§") + messages.getOrDefault(key, "null").replace("&", "§");
    }

    public static String getNoPrefix(String key) {
        return messages.getOrDefault(key, "null").replace("&", "§");
    }
}
