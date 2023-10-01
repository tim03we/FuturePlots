package com.intellectualcrafters.plot.database;

import com.intellectualcrafters.plot.util.StringMan;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class StmtMod<T> {

    public abstract String getCreateMySQL(int size);

    public String getCreateMySQL(int size, String query, int params) {
        StringBuilder statement = new StringBuilder(query);
        for (int i = 0; i < size - 1; i++) {
            statement.append('(').append(StringMan.repeat(",?", params).substring(1)).append("),");
        }
        statement.append('(').append(StringMan.repeat(",?", params).substring(1)).append(')');
        return statement.toString();
    }

    public String getCreateSQLite(int size, String query, int params) {
        StringBuilder statement = new StringBuilder(query);
        String modParams = StringMan.repeat(",?", params).substring(1);
        for (int i = 0; i < size - 1; i++) {
            statement.append("UNION SELECT ").append(modParams).append(' ');
        }
        return statement.toString();
    }

    public abstract String getCreateSQLite(int size);

    public abstract String getCreateSQL();

    public abstract void setMySQL(PreparedStatement stmt, int i, T obj) throws SQLException;

    public abstract void setSQLite(PreparedStatement stmt, int i, T obj) throws SQLException;

    public abstract void setSQL(PreparedStatement stmt, T obj) throws SQLException;
}
