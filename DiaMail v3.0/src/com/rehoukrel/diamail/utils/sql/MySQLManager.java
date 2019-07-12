package com.rehoukrel.diamail.utils.sql;

import com.rehoukrel.diamail.utils.language.Placeholder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQLManager {

    private JavaPlugin plugin;

    private Connection connection;
    private String username = "root", password = "password", host = "localhost", database = "MyDatabase";
    private int port = 3306;

    private Statement statement;

    public MySQLManager(JavaPlugin plugin, String username, String password, String database, String host, int port){
        this(plugin, username, password, database);
        this.host = host;
        this.port = port;
    }

    public MySQLManager(JavaPlugin plugin, String username, String password, String database){
        this.plugin = plugin;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public void openConnection(){

        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                plugin.getLogger().info("Connecting to MySQL...");
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + getDatabase(), this.username, this.password);
                statement = connection.createStatement();
            }
            plugin.getLogger().info("Connected to MySQL :D");
        }catch (SQLException e){
            plugin.getLogger().warning("Cannot connect to MySQL :(");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // SQL Statement Action
    //------------ RESULT SET --------------
    public ResultSet getResultSet(String table, String selectData, String condition){
        StringBuilder sql = new StringBuilder("SELECT " + selectData + " FROM " + table);
        if (condition.length() > 0){
            sql.append(" WHERE " + condition);
        }
        try {
            return statement.executeQuery(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //--------------- GET ------------------
    public List<Object> getData(String table, String selectData, String condition){
        List<Object> r = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT " + selectData + " FROM " + table);
        if (condition.length() > 0){
            sql.append(" WHERE " + condition);
        }
        try {
            ResultSet resultSet = statement.executeQuery(sql.toString());
            while(resultSet.next()){
                System.out.println(resultSet.getObject(selectData));
                Object o = resultSet.getObject(selectData);
                r.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    public List<HashMap<String, Object>> getDataMap(String table, String selectData, String condition, String... dataPath){
        List<HashMap<String, Object>> r = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT " + selectData + " FROM " + table);
        if (condition.length() > 0){
            sql.append(" WHERE " + condition);
        }
        try {
            ResultSet resultSet = statement.executeQuery(sql.toString());
            while(resultSet.next()){
                HashMap<String, Object> map = new HashMap<>();
                for (String s : dataPath) {
                    Object o = resultSet.getObject(s);
                    map.put(s, o);
                }
                r.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }
    //---------------------------------------

    //-------------- INSERT -----------------
    public void insertSingleData(String table, HashMap<String, Object> data){
        try {
            if (getConnection() != null && !getConnection().isClosed()){
                StringBuilder raw = new StringBuilder("INSERT INTO " + table + "(%path%) VALUES (%value%)"), path = new StringBuilder(), value = new StringBuilder();
                int count = 1;
                for (String s : data.keySet()){
                    Object obj = data.get(s);
                    if (!(obj instanceof Number)){
                        obj = "'" + obj.toString() + "'";
                    }

                    if (data.size() > 1){
                        if (count < data.size()){
                            path.append(s + ", ");
                            value.append(obj + ", ");
                        }else{
                            path.append(s);
                            value.append(obj);
                            break;
                        }
                        count++;
                    }else{
                        path.append(s);
                        value.append(obj);
                        break;
                    }
                }
                String sql = raw.toString();
                sql = sql.replace("%path%", path.toString());
                sql = sql.replace("%value%", value.toString());
                try {
                    statement.executeUpdate(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //-------------- UPDATE -----------------
    public void updateData(String table, String requirement, HashMap<String, Object> data){
        StringBuilder raw = new StringBuilder("UPDATE " + table + " SET %result%"),result = new StringBuilder();
        if (requirement != null && requirement.length() > 0){
            raw.append(" WHERE " + requirement);
        }
        String comma = "";
        for (String key : data.keySet()){
            result.append(comma);
            result.append(key + "=" + (data.get(key) instanceof Number ? data.get(key) : "'" + data.get(key) + "'"));
            comma = ", ";
        }
        String sql = raw.toString().replace("%result%", result.toString());
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //---------------------------------------

    //-------------- GENERAL ----------------

    public ResultSet getData(String sqlCommand){
        try {
            return statement.executeQuery(sqlCommand);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean runCommand(String sqlCommand){
        try {
            statement.execute(sqlCommand);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //---------------------------------------

    // Checker
    public boolean isItemStack(Object obj){
        return obj instanceof ItemStack;
    }

    public boolean isTableExists(String table) throws SQLException {
        DatabaseMetaData dbm = getConnection().getMetaData();
        ResultSet result = dbm.getTables(null, null, table, null);
        return result.next();
    }
    //-----------


    // Getter
    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }
}
