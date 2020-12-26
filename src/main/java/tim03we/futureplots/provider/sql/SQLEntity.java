package tim03we.futureplots.provider.sql;

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
