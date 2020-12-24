package tim03we.futureplots.provider.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SQLTable {

    private SQLConnection instance;

    private String table;

    public SQLTable(String table, SQLDatabase database, SQLConnection instance) {
        this.table = table;
        database.set();
        this.instance = instance;
    }

    public void update(String where, Object equals, SQLEntity newDocument) {
        try {
            StringBuilder updateBuilder = new StringBuilder();
            for (String update : newDocument.getDataMap().keySet()) {
                updateBuilder.append(update).append(" = ?, ");
            }
            String updateString = updateBuilder.toString().substring(0, (updateBuilder.toString().length() - 2));
            PreparedStatement statement = instance.connection.prepareStatement("UPDATE " + this.table + " SET " + updateString + " WHERE " + where + " = ?;");
            int i = 1;
            for (Object value : newDocument.getDataMap().values()) {
                statement.setObject(i, value);
                i++;
            }
            statement.setObject(i, equals);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(SQLEntity whereEntity, SQLEntity newDocument) {
        try {
            StringBuilder updateBuilder = new StringBuilder();
            for (String update : newDocument.getDataMap().keySet()) {
                updateBuilder.append(update).append(" = ?, ");
            }
            StringBuilder whereBuilder = new StringBuilder();
            for (String where : whereEntity.getDataMap().keySet()) {
                whereBuilder.append(where).append(" = ? AND ");
            }
            String whereString = whereBuilder.toString().substring(0, (whereBuilder.toString().length() - 5));
            String updateString = updateBuilder.toString().substring(0, (updateBuilder.toString().length() - 2));
            PreparedStatement statement = instance.connection.prepareStatement("UPDATE " + this.table + " SET " + updateString + " WHERE " + whereString + ";");
            int i = 1;
            for (Object value : newDocument.getDataMap().values()) {
                statement.setObject(i, value);
                i++;
            }
            for (Object value : whereEntity.getDataMap().values()) {
                statement.setObject(i, value);
                i++;
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<SQLEntity> find() {
        try {
            PreparedStatement statement = instance.connection.prepareStatement("SELECT * FROM " + this.table + ";");
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData meta = resultSet.getMetaData();

            Set<SQLEntity> sqlDocuments = new HashSet<>();
            while (resultSet.next()) {
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    map.put(meta.getColumnName(i), resultSet.getObject(i));
                }
                sqlDocuments.add(new SQLEntity(map));
            }
            return sqlDocuments;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SQLEntity find(SQLEntity sqlDocument) {
        try {
            StringBuilder whereBuilder = new StringBuilder();
            for (String where : sqlDocument.getDataMap().keySet()) {
                whereBuilder.append(where).append(" = ? AND ");
            }
            String whereString = whereBuilder.toString().substring(0, (whereBuilder.toString().length() - 5));
            PreparedStatement statement = instance.connection.prepareStatement("SELECT * FROM " + this.table + " WHERE " + whereString + ";");
            int whereInt = 1;
            for (Object value : sqlDocument.getDataMap().values()) {
                statement.setObject(whereInt, value);
                whereInt++;
            }
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData meta = resultSet.getMetaData();

            while (resultSet.next()) {
                HashMap<String, Object> map = new HashMap<>();
                for (String key : sqlDocument.getDataMap().keySet()) {
                    if(sqlDocument.getDataMap().get(key).equals(resultSet.getObject(key))) {
                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                            map.put(meta.getColumnName(i), resultSet.getObject(i));
                        }
                        return new SQLEntity(map);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(SQLEntity sqlDocument) {
        try {
            StringBuilder whereBuilder = new StringBuilder();
            for (String where : sqlDocument.getDataMap().keySet()) {
                whereBuilder.append(where).append(" = ? AND ");
            }
            String whereString = whereBuilder.toString().substring(0, (whereBuilder.toString().length() - 5));
            PreparedStatement statement = instance.connection.prepareStatement("DELETE FROM " + this.table + " WHERE " + whereString + ";");
            int i = 1;
            for (String where : sqlDocument.getDataMap().keySet()) {
                statement.setObject(i, sqlDocument.getDataMap().get(where));
                i++;
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(SQLEntity sqlDocument) {
        try {
            StringBuilder columnBuilder = new StringBuilder();
            StringBuilder valuesBuilder = new StringBuilder();
            for (String insert : sqlDocument.getDataMap().keySet()) {
                columnBuilder.append(insert).append(", ");
                valuesBuilder.append("?").append(", ");
            }
            String columnString = columnBuilder.toString().substring(0, (columnBuilder.toString().length() - 2));
            String valuesString = valuesBuilder.toString().substring(0, (valuesBuilder.toString().length() - 2));
            PreparedStatement statement = instance.connection.prepareStatement("INSERT INTO " + table + " (" + columnString + ") VALUES (" + valuesString + ");");
            int i = 1;
            for (Object value : sqlDocument.getDataMap().values()) {
                statement.setObject(i, value);
                i++;
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean columnExists(String column) {
        boolean exists;
        try {
            instance.connection.prepareStatement("SELECT " + column + " FROM '" + table + "';");
            instance.connection.commit();
            exists = true;
        } catch (SQLException e) {
            exists = false;
        }
        return exists;
    }
}
