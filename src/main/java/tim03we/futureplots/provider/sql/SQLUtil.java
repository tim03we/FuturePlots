package tim03we.futureplots.provider.sql;

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
