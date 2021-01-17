package me.prosl3nderman.procandrv4.Database;

import me.prosl3nderman.procandrv4.ItemsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemsTable {

    private Connection connection;
    private Statement statement;
    private String host, database, username, password, table;
    private int port;

    //uuid, items, potions.
    //items = lhelm, lchestplate, lleggings, lboots, cchestplate, cleggings, thelm, wpic, wshovel, spic, sshovel
    //potions = jpot1, jpot2, jsplash, sspot1, sspot2, spsplash, hpot1, hpot2, hsplash, slsplash1, slsplash2, psplash1, psplash2

    public ItemsTable() {
        host = "172.17.0.1";
        port = 3306;
        database = "candr";
        username = "procandr";
        password = "pr0C@ndr15";
        table = "items";
    }

    public void onJoin(Player p, String uuid) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    //CODE STARTS HERE
                    statement = connection.createStatement();
                    ResultSet entry = statement.executeQuery("SELECT * FROM "+table+" WHERE PlayersUUID='" + uuid + "';");
                    if (entry.next()) {
                        String itemsString = entry.getString("items");
                        String potionsString = entry.getString("potions");
                        ItemsConfig IC = new ItemsConfig();
                        if (!itemsString.equalsIgnoreCase("null")) {
                            List<String> itemsList = getList(itemsString);
                            IC.getConfig().set(uuid + ".items", itemsList);
                        }
                        if (!itemsString.equalsIgnoreCase("null")) {
                            List<String> potionsList = getList(potionsString);
                            IC.getConfig().set(uuid + ".potions", potionsList);
                        }
                        IC.srConfig();
                    }

                    //CODE ENDS HERE

                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    private List<String> getList(String objectsString) {
        List<String> objectsList = new ArrayList<>();
        if (!objectsString.contains(";"))
            objectsList.add(objectsString);
        else {
            for (String s : objectsString.split(";"))
                objectsList.add(s);
        }
        return objectsList;

    }

    public void addItem(String uuid, String item) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();
                    ResultSet entry = statement.executeQuery("SELECT items FROM " + table + " WHERE PlayersUUID='" + uuid + "';");
                    if (!entry.next()) {
                        statement.executeUpdate("INSERT INTO "+table+" (PlayersUUID, items, potions) VALUES ('" + uuid + "', 'null', 'null');");

                        statement = connection.createStatement();
                        entry = statement.executeQuery("SELECT items FROM " + table + " WHERE PlayersUUID='" + uuid + "';");
                        entry.next();
                    }
                    String items = entry.getString("items");
                    if (items.equalsIgnoreCase("null"))
                        items = item;
                    else
                        items = items + ";" + item;

                    statement.executeUpdate("UPDATE " + table + " SET items='" + items + "' WHERE PlayersUUID='" + uuid + "';");

                    ItemsConfig IC = new ItemsConfig();
                    List<String> itemsList = getList(items);
                    IC.getConfig().set(uuid + ".items", itemsList);
                    IC.srConfig();
                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void addPotion(String uuid, String potion) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();
                    ResultSet entry = statement.executeQuery("SELECT potions FROM " + table + " WHERE PlayersUUID='" + uuid + "';");
                    if (!entry.next()) {
                        statement.executeUpdate("INSERT INTO " + table + " (PlayersUUID, items, potions) VALUES ('" + uuid + "', 'null', 'null');");

                        statement = connection.createStatement();
                        entry = statement.executeQuery("SELECT potions FROM " + table + " WHERE PlayersUUID='" + uuid + "';");
                        entry.next();
                    }

                    String potions = entry.getString("potions");
                    if (potions.equalsIgnoreCase("null"))
                        potions = potion;
                    else
                        potions = potions + ";" + potion;

                    statement.executeUpdate("UPDATE " + table + " SET potions='" + potions + "' WHERE PlayersUUID='" + uuid + "';");

                    ItemsConfig IC = new ItemsConfig();
                    List<String> potionsList = getList(potions);
                    IC.getConfig().set(uuid + ".potions", potionsList);
                    IC.srConfig();
                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }



    private void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database + "?useSSL=false", this.username, this.password);
        }
    }
}
