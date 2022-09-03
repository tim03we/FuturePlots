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

import java.util.HashMap;

public class SQLEntity {

    private HashMap<String, Object> dataMap = new HashMap<>();

    public SQLEntity(String key, Object data) {
        this.dataMap.put(key, data);
    }

    public SQLEntity(HashMap<String, Object> map) {
        this.dataMap.putAll(map);
    }

    public HashMap<String, Object> getDataMap() {
        return dataMap;
    }

    public SQLEntity append(String str, Object obj) {
        this.dataMap.put(str, obj);
        return this;
    }

    public boolean isEmpty() {
        return this.dataMap.isEmpty();
    }

    public void clear() {
        this.dataMap.clear();
    }

    public String getString(String str) {
        return (String) this.dataMap.get(str);
    }

    public Integer getInteger(String str) {
        return (Integer) this.dataMap.get(str);
    }

    public Double getDouble(String str) {
        return (Double) this.dataMap.get(str);
    }

    public Float getFloat(String str) {
        return (Float) this.dataMap.get(str);
    }

    public Long getLong(String str) {
        return (Long) this.dataMap.get(str);
    }

    public Boolean getBoolean(String str) {
        return (Boolean) this.dataMap.get(str);
    }

    public Object getObject(String str) {
        return this.dataMap.get(str);
    }
}
