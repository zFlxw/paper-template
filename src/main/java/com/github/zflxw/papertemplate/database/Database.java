package com.github.zflxw.papertemplate.database;


import com.github.zflxw.papertemplate.YourPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

/**
 * Credits to: DreamTexX
 * This system is based on a code snippet, which he sent to me.
 */
public class Database {
    private final String username = YourPlugin.getInstance().getConfiguration().getString("mysql.username");
    private final String password = YourPlugin.getInstance().getConfiguration().getString("mysql.password");
    private final String database = YourPlugin.getInstance().getConfiguration().getString("mysql.database");
    private final String host = YourPlugin.getInstance().getConfiguration().getString("mysql.host");
    private final int port = YourPlugin.getInstance().getConfiguration().getInt("mysql.port");

    private Connection connection;

    /**
     * this method established a connection to the database. Based on your configuration settings, the database will be either
     * mysql or sqlite.
     */
    public void connect() {
        try {
            if (YourPlugin.getInstance().getConfiguration().getBoolean("mysql.enabled")) {
                this.connection = DriverManager.getConnection("jdbc:mysql://%s:%s/%s".formatted(host, port, database), username, password);

                YourPlugin.getInstance().log(Level.INFO, "MySQL connection successfully established.");
            } else {
                File databaseFile = new File(YourPlugin.getInstance().getDataFolder(), "database.db");

                if (!databaseFile.exists()) {
                    databaseFile.createNewFile();
                }

                this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getPath());

                YourPlugin.getInstance().log(Level.INFO, "SQLite connection successfully established.");
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            YourPlugin.getInstance().log(Level.SEVERE, "MySQL connection cannot be established. Please check your database!");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * this method closes the connection to the database, most likely on plugin shutdown.
     */
    public void disconnect() {
        try {
            if (!this.connection.isClosed()) {
                this.connection.close();
                YourPlugin.getInstance().log(Level.INFO, "MySQL connection closed.");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * a transaction is a request to the database. The database will handle this request
     * this system is based on {@link PreparedStatement}, you should be familiar with those
     * @param sql the sql statement to perform
     * @param objects the parameters to replace in the sql statement.
     * @return true, if the transaction was successfully. False otherwise
     */
    public boolean transaction(String sql, Object... objects) {
        return this.transaction(statement(sql, objects));
    }

    /**
     * a transaction is a request to the database. The database will handle this request
     * this system is based on {@link PreparedStatement}, you should be familiar with those
     * @param sql the sql statements to perform
     * @return true, if the transaction was successfully. False otherwise
     */
    public boolean transaction(String... sql) {
        try {
            PreparedStatement[] statements = new PreparedStatement[sql.length];

            for (int i = 0; i < sql.length; i++) {
                statements[i] = this.connection.prepareStatement(sql[i]);
            }

            return this.transaction(statements);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    /**
     * a transaction is a request to the database. The database will handle this request
     * this system is based on {@link PreparedStatement}, you should be familiar with those
     * @param statements the prepared statements to perform
     * @return true, if the transaction was successfully. False otherwise
     */
    public boolean transaction(PreparedStatement... statements) {
        try {
            if (this.connection.isClosed()) {
                YourPlugin.getInstance().log(Level.WARNING, "Cannot establish MySQL connection. Trying to reconnect...");
                this.connect();
                return false;
            }
            this.connection.setAutoCommit(false);
            for (PreparedStatement statement : statements) {
                if (statement != null) {
                    statement.setQueryTimeout(30);
                    statement.execute();
                    statement.close();
                } else {
                    YourPlugin.getInstance().log(Level.WARNING, "Found an invalid statement. Ignoring it.");
                }
            }
            this.connection.commit();
            return true;
        } catch (SQLException exception) {
            try {
                this.connection.rollback();
                YourPlugin.getInstance().log(Level.WARNING, "Transaction failed. Connection rollback successful");
            } catch (SQLException ex) {
                YourPlugin.getInstance().log(Level.WARNING, "Transaction and rollback failed.");
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * this method creates a prepared statement for you
     * @param sql the sql statement to perform
     * @param objects the params to replace in the statement
     * @return a prepared statement with the given params
     */
    public PreparedStatement statement(String sql, Object... objects) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }

            return statement;
        } catch (SQLException exception) {
            if (exception.getSQLState() != null && exception.getSQLState().startsWith("08")) {
                YourPlugin.getInstance().log(Level.WARNING, "Connection failed. Trying to reconnect...");
                this.connect();
            } else {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * query a prepared statement to the server.
     * @param statement the statement to query
     * @return a result set of the query
     */
    public ResultSet query(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (SQLException exception) {
            if (exception.getSQLState().startsWith("08")) {
                YourPlugin.getInstance().log(Level.WARNING, "Connection failed. Trying to reconnect...");
                this.connect();
            } else {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * query a prepared statement to the server.
     * @param sql the sql statement to query
     * @param objects the params to replace in the statement
     * @return a result set of the query
     */
    public ResultSet query(String sql, Object... objects) {
        try {
            return this.statement(sql, objects).executeQuery();
        } catch (SQLException exception) {
            if (exception.getSQLState() != null && exception.getSQLState().startsWith("08")) {
                YourPlugin.getInstance().log(Level.WARNING, "Connection failed. Trying to reconnect...");
                this.connect();
            } else {
                exception.printStackTrace();
            }
        }

        return null;
    }

    /**
     * get the connection
     * @return the connection
     */
    public Connection getConnection() {
        return this.connection;
    }
}
