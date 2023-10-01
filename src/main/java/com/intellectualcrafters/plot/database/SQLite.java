package com.intellectualcrafters.plot.database;

import com.intellectualcrafters.plot.PS;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Connects to and uses a SQLite database.
 */
public class SQLite extends Database {

    private final String dbLocation;
    private Connection connection;

    /**
     * Creates a new SQLite instance
     *
     * @param dbLocation Location of the Database (Must end in .db)
     */
    public SQLite(File dbLocation) {
        this.dbLocation = dbLocation.getAbsolutePath();
    }

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            return this.connection;
        }
        if (!PS.get().IMP.getDirectory().exists()) {
            PS.get().IMP.getDirectory().mkdirs();
        }
        File file = new File(this.dbLocation);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
                PS.debug("&cUnable to create database!");
            }
        }
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbLocation);
        return this.connection;
    }

    @Override
    public boolean checkConnection() throws SQLException {
        return (this.connection != null) && !this.connection.isClosed();
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean closeConnection() throws SQLException {
        if (this.connection == null) {
            return false;
        }
        this.connection.close();
        this.connection = null;
        return true;
    }

    @Override
    public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            openConnection();
        }
        try (Statement statement = this.connection.createStatement()) {
            return statement.executeQuery(query);
        }
    }

    @Override
    public int updateSQL(String query) throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            openConnection();
        }
        try (Statement statement = this.connection.createStatement()) {
            return statement.executeUpdate(query);
        }
    }

    @Override
    public Connection forceConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbLocation);
        return this.connection;
    }
}
