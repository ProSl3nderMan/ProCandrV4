package me.prosl3nderman.procandrv4.Database;

import me.prosl3nderman.procandrv4.ActiveConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class StatsTable {

    private Connection connection;
    private Statement statement;
    private String host, database, username, password, table;
    private int port;
    private String THEJOINDATE, THELASTJOIN, THEHOURS,THEVOTES, THEVOTEPOINTS;

    public StatsTable() {
        host = "172.17.0.1";
        port = 3306;
        database = "candr";
        username = "procandr";
        password = "pr0C@ndr15";
        table = "stats";
    }

    public void newPlayer(String uuid, String name) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    //CODE STARTS HERE
                    ResultSet entry = statement.executeQuery("SELECT joinDate FROM "+table+" WHERE PlayersUUID='" + uuid + "';");

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String joinDate = dtf.format(now);
                    if (!entry.next())
                        statement.executeUpdate("INSERT INTO "+table+" (PlayersUUID, IGN, joinDate, lastLogin, hours) VALUES ('" + uuid + "', '" + name + "', '" + joinDate + "', '" + joinDate + "', '0');");
                    else
                        statement.executeUpdate("UPDATE "+table+" SET joinDate = '" + joinDate + "' WHERE PlayersUUID = '" + uuid + "';");
                    //CODE ENDS HERE

                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void onLeave(Player p, String uuid) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    //CODE STARTS HERE

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String lastLogin = dtf.format(now);
                    statement.executeUpdate("UPDATE "+table+" SET lastLogin = '" + lastLogin + "' WHERE PlayersUUID = '" + uuid + "';");

                    Double hours;
                    ActiveConfig ac = new ActiveConfig();
                    if (ac.getConfig().contains(uuid + ".joinTime")) {
                        LocalDateTime joinTime = LocalDateTime.parse(ac.getConfig().getString(uuid + ".joinTime"), dtf);
                        Duration dur = Duration.between(joinTime, now);
                        hours = Double.parseDouble(Long.toString(ChronoUnit.SECONDS.between(joinTime, now))) / 60 / 60;
                        ac.getConfig().set(uuid + ".joinTime", null);
                        ac.srConfig();
                    } else
                        hours = Double.parseDouble("0");

                    statement = connection.createStatement();
                    ResultSet entry = statement.executeQuery("SELECT hours FROM "+table+" WHERE PlayersUUID='" + uuid + "';");
                    if (entry.next()) {
                        hours = Double.parseDouble(entry.getString("hours")) + hours;
                        statement.executeUpdate("UPDATE " + table + " SET hours = '" + hours + "' WHERE PlayersUUID = '" + uuid + "';");
                    }
                    ProCandrV4.plugin.checkHours(hours,uuid, p);
                    //CODE ENDS HERE

                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void onJoin(Player p, String uuid) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    //CODE STARTS HERE
                    statement = connection.createStatement();
                    ResultSet entry = statement.executeQuery("SELECT IGN FROM "+table+" WHERE PlayersUUID='" + uuid + "';");
                    if (entry.next())
                        statement.executeUpdate("UPDATE "+table+" SET IGN = '" + p.getName() + "' WHERE PlayersUUID = '" + uuid + "';");
                    else {
                        newPlayer(uuid, p.getName());
                        return;
                    }

                    //CODE ENDS HERE

                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void onEnable() {
        try {
            openConnection();
            statement = connection.createStatement();

            //CODE STARTS HERE

            for (Player p : Bukkit.getOnlinePlayers()) {
                String uuid = p.getUniqueId().toString();

                openConnection();
                statement = connection.createStatement();

                //CODE STARTS HERE
                statement = connection.createStatement();
                ResultSet entry = statement.executeQuery("SELECT IGN FROM "+table+" WHERE PlayersUUID='" + uuid + "';");
                if (entry.next())
                    statement.executeUpdate("UPDATE "+table+" SET IGN = '" + p.getName() + "' WHERE PlayersUUID = '" + uuid + "';");
                else {
                    newPlayer(uuid, p.getName());
                    return;
                }
            }
            //CODE ENDS HERE

        } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public void onDisable() {
        try {
            openConnection();
            statement = connection.createStatement();

            //CODE STARTS HERE

            for (Player p : Bukkit.getOnlinePlayers()) {
                String uuid = p.getUniqueId().toString();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String lastLogin = dtf.format(now);
                statement.executeUpdate("UPDATE " + table + " SET lastLogin = '" + lastLogin + "' WHERE PlayersUUID = '" + uuid + "';");

                Double hours;
                ActiveConfig ac = new ActiveConfig();
                if (ac.getConfig().contains(uuid + ".joinTime")) {
                    LocalDateTime joinTime = LocalDateTime.parse(ac.getConfig().getString(uuid + ".joinTime"), dtf);
                    Duration dur = Duration.between(joinTime, now);
                    hours = Double.parseDouble(Long.toString(ChronoUnit.SECONDS.between(joinTime, now))) / 60 / 60;
                    ac.getConfig().set(uuid + ".joinTime", null);
                    ac.srConfig();
                } else
                    hours = Double.parseDouble("0");

                statement = connection.createStatement();
                ResultSet entry = statement.executeQuery("SELECT hours FROM " + table + " WHERE PlayersUUID='" + uuid + "';");
                if (entry.next()) {
                    hours = Double.parseDouble(entry.getString("hours")) + hours;
                    statement.executeUpdate("UPDATE " + table + " SET hours = '" + hours + "' WHERE PlayersUUID = '" + uuid + "';");
                }
            }
            //CODE ENDS HERE

        } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public void giveGameEndPoints(List<Player> players, List<Player> escapees) {
        new BukkitRunnable() {
            public void run() {
                try {
                    for (Player p : players) {
                        openConnection();
                        statement = connection.createStatement();
                        String uuid = p.getUniqueId().toString();
                        ResultSet entry = statement.executeQuery("SELECT * FROM " + table + " WHERE PlayersUUID='" + uuid + "';");
                        if (entry.next()) {
                            int games = entry.getInt("games");
                            if (!escapees.contains(p))
                                statement.executeUpdate("UPDATE " + table + " SET games='" + (games + 1) + "' WHERE PlayersUUID='" + uuid + "';");
                            else {
                                int escapes = entry.getInt("escapes");
                                statement.executeUpdate("UPDATE " + table + " SET escapes='" + (escapes + 1) + "', games='" + (games + 1) + "' WHERE PlayersUUID='" + uuid + "';");
                            }
                        }
                    }
                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void showStats(Player p, String IGN) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    //CODE STARTS HERE
                    ResultSet entry = statement.executeQuery("SELECT * FROM "+table+" WHERE IGN='" + IGN + "';");
                    if (entry.next()) {
                        THEJOINDATE = entry.getString("joinDate");
                        THELASTJOIN = entry.getString("lastLogin");
                        THEHOURS = entry.getString("hours");
                        int escapes = entry.getInt("escapes");
                        int games = entry.getInt("games");

                        p.sendMessage(ChatColor.LIGHT_PURPLE + "------ " + ChatColor.DARK_PURPLE + IGN + ChatColor.LIGHT_PURPLE + " ------");
                        p.sendMessage(ChatColor.DARK_PURPLE + "Games: " + ChatColor.LIGHT_PURPLE + games);
                        p.sendMessage(ChatColor.DARK_PURPLE + "Escapes: " + ChatColor.LIGHT_PURPLE + escapes);
                        p.sendMessage(ChatColor.DARK_PURPLE + "Hours: " + ChatColor.LIGHT_PURPLE + THEHOURS);
                        p.sendMessage(ChatColor.DARK_PURPLE + "Join Date: " + ChatColor.LIGHT_PURPLE + THEJOINDATE);
                        p.sendMessage(ChatColor.DARK_PURPLE + "Last Login: " + ChatColor.LIGHT_PURPLE + THELASTJOIN);
                        p.sendMessage(ChatColor.LIGHT_PURPLE  + "Do /top <games|escapes|hours> [page number(1 default)] for top stats.");
                    } else
                        p.sendMessage(ChatColor.RED + "The player " + ChatColor.WHITE + IGN + ChatColor.RED + " does not exist!");
                    //CODE ENDS HERE

                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void getTopHours(Player p, Integer pageNumber) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    ResultSet entry = statement.executeQuery("SELECT * FROM "+table+" ORDER BY hours DESC limit " + (((pageNumber*10)-9) -1) + ",10;");
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "------ " + ChatColor.DARK_PURPLE + "Top Hours" + ChatColor.LIGHT_PURPLE + " ------");
                    for (int i = ((pageNumber*10) -9); i < ((pageNumber*10) +1); i++) {
                        if (entry.next())
                            p.sendMessage(ChatColor.LIGHT_PURPLE + "(" + i + "). " + ChatColor.DARK_PURPLE + entry.getString("IGN") + ChatColor.DARK_PURPLE + " : " + ChatColor.LIGHT_PURPLE
                                    + entry.getString("hours") + " hours");
                    }
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "------ " + ChatColor.DARK_PURPLE + "Page " + pageNumber + ", do /top hours " + (pageNumber + 1) + " for next page" + ChatColor.LIGHT_PURPLE + " ------");
                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void getTopGames(Player p, Integer pageNumber) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    ResultSet entry = statement.executeQuery("SELECT * FROM "+table+" ORDER BY games DESC limit " + (((pageNumber*10)-9) -1) + ",10;");
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "------ " + ChatColor.DARK_PURPLE + "Top Games" + ChatColor.LIGHT_PURPLE + " ------");
                    for (int i = ((pageNumber*10) -9); i < ((pageNumber*10) +1); i++) {
                        if (entry.next())
                            p.sendMessage(ChatColor.LIGHT_PURPLE + "(" + i + "). " + ChatColor.DARK_PURPLE + entry.getString("IGN") + ChatColor.DARK_PURPLE + " : " + ChatColor.LIGHT_PURPLE
                                    + entry.getString("games") + " games");
                    }
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "------ " + ChatColor.DARK_PURPLE + "Page " + pageNumber + ", do /top games " + (pageNumber + 1) + " for next page" + ChatColor.LIGHT_PURPLE + " ------");
                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public void getTopEscapes(Player p, Integer pageNumber) {
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    ResultSet entry = statement.executeQuery("SELECT * FROM "+table+" ORDER BY escapes DESC limit " + (((pageNumber*10)-9) -1) + ",10;");
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "------ " + ChatColor.DARK_PURPLE + "Top Escapes" + ChatColor.LIGHT_PURPLE + " ------");
                    for (int i = ((pageNumber*10) -9); i < ((pageNumber*10) +1); i++) {
                        if (entry.next())
                            p.sendMessage(ChatColor.LIGHT_PURPLE + "(" + i + "). " + ChatColor.DARK_PURPLE + entry.getString("IGN") + ChatColor.DARK_PURPLE + " : " + ChatColor.LIGHT_PURPLE
                                    + entry.getString("escapes") + " escapes");
                    }
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "------ " + ChatColor.DARK_PURPLE + "Page " + pageNumber + ", do /top escapes " + (pageNumber + 1) + " for next page" + ChatColor.LIGHT_PURPLE + " ------");
                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
    }

    public String getLastOn(String IGN) { //if LASTJOIN=false then the player does not exist.
        THELASTJOIN = "false";
        new BukkitRunnable() {
            public void run() {
                try {
                    openConnection();
                    statement = connection.createStatement();

                    //CODE STARTS HERE
                    ResultSet entry = statement.executeQuery("SELECT * FROM "+table+" WHERE IGN='" + IGN + "';");
                    if (entry.next())
                        THELASTJOIN = entry.getString("lastLogin");
                    Bukkit.broadcastMessage(THELASTJOIN);
                    return;
                    //CODE ENDS HERE

                } catch (SQLException e) { e.printStackTrace(); } catch (ClassNotFoundException e) { e.printStackTrace(); }
            }
        }.runTaskAsynchronously(ProCandrV4.plugin);
        return THELASTJOIN;
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
