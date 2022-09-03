package tim03we.futureplots.utils.xuid.web;

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

import tim03we.futureplots.utils.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Web {

    public static boolean online() {
        try {
            URL url = new URL("http://" + Settings.xuidWebUrl + "/xuid/index.php");
            Scanner sc = new Scanner(url.openStream());
            StringBuffer sb = new StringBuffer();
            while(sc.hasNext()) {
                sb.append(sc.next());
            }
            String result = sb.toString();
            result = result.replaceAll("<[^>]*>", "");
            if(result.equalsIgnoreCase("OK")) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}
