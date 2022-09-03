package tim03we.futureplots.provider.sql;

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

import java.util.ArrayList;
import java.util.List;

public class SQLUtil {

    public static String convertToString(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : stringList) {
            stringBuilder.append(s).append(",");
        }
        if(stringBuilder.toString().isEmpty()) return stringBuilder.toString(); // list size = 0
        return stringBuilder.toString().substring(0, (stringBuilder.toString().length() - 1));
    }

    public static List<String> convertToList(String stringList) {
        List<String> list = new ArrayList<>();
        for (String s : stringList.split(",")) {
            if(!s.isEmpty()) list.add(s);
        }
        return list;
    }
}
