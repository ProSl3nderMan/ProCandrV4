package me.prosl3nderman.procandrv4;

import me.prosl3nderman.procandrv4.Database.StatsTable;
import me.prosl3nderman.procandrv4.Jobs.*;
import me.prosl3nderman.procandrv4.shop.MainShopMenu;
import me.prosl3nderman.procandrv4.shop.PotionsManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.inventivetalent.glow.GlowAPI;

import java.util.*;
import java.util.logging.Level;

public class Game {

    private String map;
    private List<String> players;
    private List<String> cops;
    private List<Block> doors;
    private List<String> joinCopsList;
    private Boolean copsRecruiting; //If true, /cops adds players to the list in which they are picked from randomly to become a cop..
    private HashMap<String, Integer> timer = new HashMap<>(); //timer for multiple things, like the beginning 30 second copTimer.
    private HashMap<String, Location> spawnpoint = new HashMap<>(); //string = playername ; location = spawnpoint
    private BossBar bb = null;
    private List<String> playersSpottedByCamera;
    private HashMap<String, Job> job = new HashMap<>(); //String = player name; String = job title.
    private BukkitTask openCellTimer;
    private Boolean openCellActive = false;
    private HashMap<String, ItemStack> potionEquiped = new HashMap<>(); //String = player name; ItemStack = potionConsumed/Used
    private String firstJanitor = "null"; //first person to complete janitor.
    private String firstCook = "null"; //first person to complete cook.
    private String firstLaundryman = "null"; //first person to complete laundryman.
    private String firstLibrarian = "null"; //first person to complete librarian
    private Boolean copsTask;
    private Boolean leversUsed;
    private String designatedJob;
    private HashMap<String, BukkitTask> timers = new HashMap<>();

    public Game(String mapp) {
        map = mapp;

        players = new ArrayList<>();
        cops = new ArrayList<>();
        doors = new ArrayList<>();
        joinCopsList = new ArrayList<>();
        playersSpottedByCamera = new ArrayList<>();
        copsRecruiting = true;
        copsTask = false;
        leversUsed = false;

        fillDoors();
        startCopTimer();
        updateJoinSign();

        ProCandrV4.plugin.games.put(map, this);
        Bukkit.getLogger().log(Level.FINE, "[ProCandr] New game started using map " + map + "!");

        openCellTimer = new BukkitRunnable() {
            public void run() {
                sendMessageToGame(ChatColor.GOLD + "Do " + ChatColor.RED + "/open cell " + ChatColor.GOLD + " to open the cells quickly! (2.5 secconds)");
                openCellActive = true;
                new BukkitRunnable() {
                    public void run() {
                        openCellActive = false;
                    }
                }.runTaskLater(ProCandrV4.plugin, 70L);
            }
        }.runTaskLater(ProCandrV4.plugin, 6000L);
    }

    public Game(String mapp, Boolean onEnable) {
        map = mapp;

        players = new ArrayList<>();
        cops = new ArrayList<>();

        updateJoinSign();
        closeDoors();

        spawnDirtAndGarbage();
    }

    public void endGameNoPlayers() {
        ProCandrV4.plugin.games.remove(map);
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Closing game " + ChatColor.LIGHT_PURPLE + map + ChatColor.DARK_PURPLE + " because of no players...");
        closeDoors();

        if (openCellTimer.isCancelled() == false)
            openCellTimer.cancel();
        for (String keyTimer : timers.keySet()) {
            timers.get(keyTimer).cancel();
        }

        players.clear();
        cops.clear();
        updateJoinSign();
    }

    public void modEndGame(Player mod) {
        List<Player> players = new ArrayList<>();
        if (openCellTimer.isCancelled() == false)
            openCellTimer.cancel();
        for (String ps : this.players) {
            Player p = Bukkit.getPlayer(ps);
            p.setPlayerListName(p.getName());
            players.add(p);

            p.setLevel(0);
            p.setFoodLevel(20);
            ProCandrV4.plugin.game.remove(p.getName());
            spawnpoint.remove(p.getName());
            if (job.containsKey(p.getName()))
                job.get(p.getName()).resetAndRemoveJob();

            ProCandrV4.plugin.clearInventory(p);
            if (hadUsedPotion(p)) {
                PotionsManager PM = new PotionsManager();
                PM.equipPotion(p, getUsedPotion(p));
                removeUsedPotion(p);
            }
            new MainShopMenu().giveItem(p);
            p.teleport(ProCandrV4.plugin.getSpawn());
        }
        joinCopsList.clear();

        String title = ChatColor.DARK_PURPLE + "Mod " + ChatColor.WHITE + mod.getName() + ChatColor.DARK_PURPLE + " ended the game!";
        Bukkit.broadcastMessage(title);
        for (String ps : this.players) {
            Player p = Bukkit.getPlayer(ps);
            p.sendTitle(title, "", 0, 100, 40);
        }
        closeDoors();
        ProCandrV4.plugin.games.remove(map);
        for (String keyTimer : timers.keySet()) {
            timers.get(keyTimer).cancel();
        }

        this.players.clear();
        cops.clear();
        updateJoinSign();
    }

    public void endGameEnginesDone() {
        List<Player> escapees = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        if (openCellTimer.isCancelled() == false)
            openCellTimer.cancel();
        for (String ps : this.players) {
            Player p = Bukkit.getPlayer(ps);
            p.setPlayerListName(p.getName());
            if (isInsideEscapeRegion(p.getLocation()) && playerIsCop(p) == false)
                escapees.add(p);
            players.add(p);

            ProCandrV4.econ.depositPlayer(p, 10);
            p.sendMessage(ChatColor.GREEN + "$10 has been added to your account for playing!");
            p.setLevel(0);
            p.setFoodLevel(20);
            ProCandrV4.plugin.game.remove(p.getName());
            spawnpoint.remove(p.getName());
            if (job.containsKey(p.getName()))
                job.get(p.getName()).resetAndRemoveJob();

            if (cops.contains(p.getName())) {
                ProCandrV4.econ.depositPlayer(p, 10);
                p.sendMessage(ChatColor.GREEN + "$10 has been added to your account for being a cop!");
            }
            if (joinCopsList.contains(p.getName()))
                joinCopsList.remove(p.getName());
            ProCandrV4.plugin.clearInventory(p);
            if (hadUsedPotion(p)) {
                PotionsManager PM = new PotionsManager();
                PM.equipPotion(p, getUsedPotion(p));
                removeUsedPotion(p);
            }
            new MainShopMenu().giveItem(p);
            p.teleport(ProCandrV4.plugin.getSpawn());
        }
        StatsTable CD = new StatsTable();
        CD.giveGameEndPoints(players, escapees);
        String winners = "";
        int counter = 0;
        for (Player p : escapees) {
            if (counter < 4)
                winners = winners + ChatColor.DARK_PURPLE + ", " + ChatColor.DARK_RED + p.getName();
            counter = counter + 1;
            ProCandrV4.econ.depositPlayer(p, 20);
            p.sendMessage(ChatColor.GREEN + "$20 has been added to your account for winning!");
        }
        winners = winners.replaceFirst(ChatColor.DARK_PURPLE + ", ", "");
        String title = ChatColor.DARK_PURPLE + "Robbers escaped the cops!";
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Congratulations to " + ChatColor.DARK_RED + winners + ChatColor.DARK_PURPLE + " for winning the round on " + ChatColor.DARK_RED + map + ChatColor.DARK_PURPLE + "!");
        for (String ps : this.players) {
            Player p = Bukkit.getPlayer(ps);
            p.sendTitle(title, ChatColor.DARK_PURPLE + "Congratulations to the escapees for winning the round!", 0, 100, 40);
        }
        for (String keyTimer : timers.keySet()) {
            timers.get(keyTimer).cancel();
        }
        closeDoors();
        ProCandrV4.plugin.games.remove(map);

        this.players.clear();
        cops.clear();
        updateJoinSign();
    }

    private void givePlayerJob(Player p) {
        List<String> jobs = new ArrayList<>();
        jobs.add("janitor");
        jobs.add("cook");
        jobs.add("librarian");
        jobs.add("laundryman");

        String job = getRandomString(jobs);

        if (job.equalsIgnoreCase("janitor"))
            new Janitor(p).setupJob();
        else if (job.equalsIgnoreCase("cook"))
            new Cook(p).setupJob();
        else if (job.equalsIgnoreCase("librarian"))
            new Librarian(p).setupJob();
        else if (job.equalsIgnoreCase("laundryman"))
            new Laundryman(p).setupJob();
    }

    public String getRandomString(List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    private boolean isInsideEscapeRegion(Location loc) {
        MapsConfig MC = new MapsConfig(map);
        Location l1 = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("escapeRegionBlock1"),false);
        Location l2 = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("escapeRegionBlock2"),false);

        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

        return loc.getX() >= x1 && loc.getX() <= x2 && loc.getY() >= y1 && loc.getY() <= y2 && loc.getZ() >= z1 && loc.getZ() <= z2;
    }

    public void startEngines(Player engineStarter) {
        bb = Bukkit.createBossBar("Escape engines starting in 30 seconds!", BarColor.GREEN, BarStyle.SOLID);
        for (String ps : players) {
            Player p = Bukkit.getPlayer(ps);
            bb.addPlayer(p);
            p.sendTitle(ChatColor.DARK_GREEN + "A robber has started the escape engines!", ChatColor.GREEN + "Robbers have 30 seconds to get in the escape vehicle to escape.", 0, 100, 40);
            p.sendMessage(ChatColor.DARK_RED + engineStarter.getName() + ChatColor.DARK_GREEN + " has started the escape engines! Robbers have 30 seconds to get in the escape vehicle to escape.");
        }
        timer.put(map + "engineTimer", 30);
        new BukkitRunnable() {
            public void run() {
                if (players.isEmpty() || !timer.containsKey(map + "engineTimer")) {
                    cancel();
                    bb.removeAll();
                    bb = null;
                    return;
                }
                bb.setTitle("Escape engines starting in " + timer.get(map + "engineTimer") + " seconds!");
                if (timer.get(map + "engineTimer") == 0) {
                    cancel();
                    timer.remove(map + "engineTimer");
                    bb.removeAll();
                    bb = null;
                    ProCandrV4.plugin.getServer().getScheduler().scheduleSyncDelayedTask(ProCandrV4.plugin, new Runnable() {
                        @Override
                        public void run() {
                            endGameEnginesDone();
                        }
                    });
                    return;
                }
                timer.put(map + "engineTimer",timer.get(map + "engineTimer")-1);
            }
        }.runTaskTimer(ProCandrV4.plugin, 20L, 20L);
    }

    public Boolean engineIsOn() {
        if (timer.containsKey(map + "engineTimer"))
            return true;
        return false;
    }

    public Boolean isEngineSign(Location loc) {
        if (new MapsConfig(map).getConfig().getString("engineSign").equalsIgnoreCase(ProCandrV4.plugin.getStringFLocation(loc, false)))
            return true;
        return false;
    }

    public Boolean playerIsCop(Player p) {
        if (cops.contains(p.getName()))
            return true;
        return false;
    }

    public void addToCopsList(Player p) {
        if (copsRecruiting) {
            if (!joinCopsList.contains(p.getName())) {
                joinCopsList.add(p.getName());
                p.sendMessage(ChatColor.AQUA + "Your name was added to the list to be cop this round.");
            } else
                p.sendMessage(ChatColor.RED + "You were already added to the list! Wait for the timer to reach " + ChatColor.WHITE + "0" + ChatColor.RED + " at the top.");
        } else {
            if (cops.isEmpty() || (cops.size() == 1 && players.size() >= 8))
                gearUpCop(p);
            else if (cops.size() == 2)
                p.sendMessage(ChatColor.RED + "The cop team is full! Keep an eye on chat for when a cop member leaves.");
            else
                p.sendMessage(ChatColor.RED + "When there are 8 or more players, there can become another cop!");
        }
    }

    private void startCopTimer() {
        timer.put(map + "copTimer",30);
        bb = Bukkit.createBossBar("Cop selecting process commencing in 30 seconds.", BarColor.GREEN, BarStyle.SOLID);
        new BukkitRunnable() {
            public void run() {
                if (players.isEmpty()) {
                    cancel();
                    bb.removeAll();
                    bb = null;
                    return;
                }
                bb.setTitle("Cop selecting process commencing in " + timer.get(map + "copTimer") + " seconds.");
                if (timer.get(map + "copTimer") == 0) {
                    cancel();
                    timer.remove(map + "copTimer");
                    bb.removeAll();
                    bb = null;
                    copsRecruiting = false;
                    if (players.isEmpty() && ProCandrV4.plugin.games.containsKey(map)) { //end game and delete the object in games.
                        ProCandrV4.plugin.getServer().getScheduler().scheduleSyncDelayedTask(ProCandrV4.plugin, new Runnable() {
                            @Override
                            public void run() {
                                endGameNoPlayers();
                            }
                        });
                    } else if (joinCopsList.isEmpty()) //no one wanted to become a cop, announce it and tell them how to become a cop.
                        sendMessageToGame(ChatColor.AQUA + "" + ChatColor.BOLD + "No one wanted to become a cop, so there is no cop yet! Do " + ChatColor.DARK_AQUA + ChatColor.BOLD + "/cops" + ChatColor.AQUA
                                + ChatColor.BOLD +" to become a cop!");
                    else { //pick a cop and set them up.
                        ProCandrV4.plugin.getServer().getScheduler().scheduleSyncDelayedTask(ProCandrV4.plugin, new Runnable() {
                            @Override
                            public void run() {
                                pickACop();
                            }
                        });
                    }
                    return;
                }
                timer.put(map + "copTimer",timer.get(map + "copTimer")-1);
            }
        }.runTaskTimerAsynchronously(ProCandrV4.plugin, 20L, 20L);
    }

    private void pickACop() {
        Random ran = new Random();
        Player cop1 = Bukkit.getPlayer(joinCopsList.get(ran.nextInt(joinCopsList.size())));
        Player cop2 = null;
        if (players.size() >= 8 && joinCopsList.size() > 1) {
            cop2 = Bukkit.getPlayer(joinCopsList.get(ran.nextInt(joinCopsList.size())));
            while (cop1 == cop2)
                cop2 = Bukkit.getPlayer(joinCopsList.get(ran.nextInt(joinCopsList.size())));
        }
        gearUpCop(cop1);
        if (cop2 != null)
            gearUpCop(cop2);
        joinCopsList.clear();
    }

    private void gearUpCop(Player cop) {
        if (job.containsKey(cop.getName()))
            job.get(cop.getName()).resetAndRemoveJob();
        cop.setLevel(0);
        cop.setFoodLevel(20);
        cops.add(cop.getName());
        ProCandrV4.plugin.clearInventory(cop);

        MapsConfig MC = new MapsConfig(map);
        Location loc = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("copspawnloc"), true);
        spawnpoint.put(cop.getName(), loc);
        cop.teleport(loc);
        cop.sendMessage(ChatColor.DARK_GREEN + "You are now a cop! Don't let them escape!");

        ItemMeta meta;
        ItemStack helm = new ItemStack(Material.DIAMOND_HELMET, 1);
        helm.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        helm.addEnchantment(Enchantment.DURABILITY, 3);
        meta = helm.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Cop's Helmet");
        helm.setItemMeta(meta);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        chestplate.addEnchantment(Enchantment.DURABILITY, 3);
        meta = chestplate.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Cop's Chestplate");
        chestplate.setItemMeta(meta);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        leggings.addEnchantment(Enchantment.DURABILITY, 3);
        meta = leggings.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Cop's Leggings");
        leggings.setItemMeta(meta);
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS, 1);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        boots.addEnchantment(Enchantment.DURABILITY, 3);
        meta = boots.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Cop's Boots");
        boots.setItemMeta(meta);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
        meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Punishment");
        sword.setItemMeta(meta);
        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        meta = bow.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Taser");
        bow.setItemMeta(meta);
        ItemStack stick = new ItemStack(Material.STICK, 1);
        stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
        meta = stick.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Baton");
        stick.setItemMeta(meta);
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 20);

        cop.getInventory().addItem(new ItemStack[] { sword });
        cop.getInventory().addItem(new ItemStack[] { stick });
        cop.getInventory().addItem(new ItemStack[] { bow });
        cop.getInventory().setItem(35, arrow);
        cop.getInventory().addItem(new ItemStack[] { food });

        if (cop.getInventory().getHelmet() != null)
            cop.getInventory().addItem(cop.getInventory().getHelmet());
        if (cop.getInventory().getChestplate() != null)
            cop.getInventory().addItem(cop.getInventory().getChestplate());
        if (cop.getInventory().getLeggings() != null)
            cop.getInventory().addItem(cop.getInventory().getLeggings());
        if (cop.getInventory().getBoots() != null)
            cop.getInventory().addItem(cop.getInventory().getBoots());

        cop.getInventory().setHelmet(helm);
        cop.getInventory().setChestplate(chestplate);
        cop.getInventory().setLeggings(leggings);
        cop.getInventory().setBoots(boots);
        cop.setPlayerListName(ChatColor.BLUE + cop.getName());

        sendMessageToGame(ChatColor.DARK_RED + cop.getName() + ChatColor.DARK_GREEN + ", is now the new cop! Let the game begin!!");
        sendMessageToGame(ChatColor.DARK_BLUE + "Please listen to the cops orders! Do not abuse of anything and have fun!");

        updateJoinSign();

        /*new BukkitRunnable() {
            public void run() {
                giveCopsTask();
            }
        }.runTaskLater(ProCandrV4.plugin, 100L);
        */
    }

    private void giveCopsTask() {
        if (cops.size() == 0)
            return;
        if (copsTask)
            return;
        copsTask = true;
        Random ran = new Random();
        int randomNum = ran.nextInt(3) + 1;

        if (randomNum == 1) {
            janitorTask();
            designatedJob = "janitor";
        } else if (randomNum == 2) {
            cookTask();
            designatedJob = "cook";
        } else if (randomNum == 3) {
            laundrymanTask();
            designatedJob = "laundryman";
        } else if (randomNum == 4) {
            librarianTask();
            designatedJob = "librarian";
        }

        BukkitTask copWarning = new BukkitRunnable() {
            public void run() {
                timers.remove("copWarning");
                if (leversUsed == false) {
                    sendMessageToCops(ChatColor.RED + "" + ChatColor.BOLD + "WARNING: You must let prisoners out for this task, failure to do so will result in all cells being forced open! You have 30 seconds.");
                    BukkitTask copWarningOpenCells = new BukkitRunnable() {
                        public void run() {
                            timers.remove("copWarningOpenCells");
                            if (leversUsed == false)
                                openCellCommand(null, true);
                        }
                    }.runTaskLater(ProCandrV4.plugin, 600L);
                    timers.put("copWarningOpenCells", copWarningOpenCells);
                }
            }
        }.runTaskLater(ProCandrV4.plugin, 600L);
        timers.put("copWarning", copWarning);
    }

    private void janitorTask() {
        FileConfiguration cfg = ProCandrV4.plugin.getConfig();
        sendMessageToCops(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.janitorCops.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.janitorCops.secondLine")));
        for (String cops : this.cops) {
            Player cop = Bukkit.getPlayer(cops);
            cop.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.janitorCops.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.janitorCops.secondLine")), 0, 100, 40);
        }
        for (String ps : players) {
            if (!cops.contains(ps)) {
                Player p = Bukkit.getPlayer(ps);
                new Janitor(p).setupJob();
            }
        }
    }

    private void cookTask() {
        FileConfiguration cfg = ProCandrV4.plugin.getConfig();
        sendMessageToCops(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.cookCops.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.cookCops.secondLine")));
        for (String cops : this.cops) {
            Player cop = Bukkit.getPlayer(cops);
            cop.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.cookCops.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.cookCops.secondLine")), 0, 100, 40);
        }
        for (String ps : players) {
            if (!cops.contains(ps)) {
                Player p = Bukkit.getPlayer(ps);
                new Cook(p).setupJob();
            }
        }
    }

    private void laundrymanTask() {
        FileConfiguration cfg = ProCandrV4.plugin.getConfig();
        sendMessageToCops(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.laundrymanCops.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.laundrymanCops.secondLine")));
        for (String cops : this.cops) {
            Player cop = Bukkit.getPlayer(cops);
            cop.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.laundrymanCops.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.laundrymanCops.secondLine")), 0, 100, 40);
        }
        for (String ps : players) {
            if (!cops.contains(ps)) {
                Player p = Bukkit.getPlayer(ps);
                new Laundryman(p).setupJob();
            }
        }
    }

    private void librarianTask() {
        FileConfiguration cfg = ProCandrV4.plugin.getConfig();
        sendMessageToCops(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.librarianCops.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.librarianCops.secondLine")));
        for (String cops : this.cops) {
            Player cop = Bukkit.getPlayer(cops);
            cop.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.librarianCops.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.librarianCops.secondLine")), 0, 100, 40);
        }
        for (String ps : players) {
            if (!cops.contains(ps)) {
                Player p = Bukkit.getPlayer(ps);
                new Laundryman(p).setupJob();
            }
        }
    }

    private void sendMessageToGame(String message) {
        for (String ps : players) {
            Player p = Bukkit.getPlayer(ps);
            p.sendMessage(message);
        }
    }

    private void sendMessageToCops(String message) {
        for (String ps : cops) {
            Player p = Bukkit.getPlayer(ps);
            p.sendMessage(message);
        }
    }

    public void addPlayer(Player p) {
        addPlayerPrivate(p, "random");
    }

    public void addPlayer(Player p, String job) {
        addPlayerPrivate(p, job);
    }

    private void addPlayerPrivate(Player p, String job) {
        ProCandrV4.plugin.clearInventory(p);
        p.setLevel(0);
        players.add(p.getName());
        ProCandrV4.plugin.game.put(p.getName(), map);
        if (bb != null)
            bb.addPlayer(p);

        Location cell = getPlayerCell();
        spawnpoint.put(p.getName(), cell);
        p.teleport(cell);

        ItemStack food = new ItemStack(Material.COOKED_BEEF, 10);
        p.getInventory().addItem(food);

        if (job.equalsIgnoreCase("random"))
            givePlayerJob(p);
        else if (job.equalsIgnoreCase("janitor"))
            new Janitor(p).setupJob();
        else if (job.equalsIgnoreCase("cook"))
            new Cook(p).setupJob();
        else if (job.equalsIgnoreCase("librarian"))
            new Librarian(p).setupJob();
        else if (job.equalsIgnoreCase("laundryman"))
            new Laundryman(p).setupJob();
        else
            givePlayerJob(p);


        p.sendMessage("");
        if (copsRecruiting) {
            p.sendTitle(ChatColor.DARK_GREEN + "Welcome to the map " + map + "!", ChatColor.GREEN + "Do /cops to become a cop this round!", 0, 100, 40);
            p.sendMessage(ChatColor.DARK_GREEN + "Welcome to the map " + map + "! Do " + ChatColor.GREEN + "/cops" + ChatColor.DARK_GREEN + " to become a cop this round!");
        } else {
            p.sendTitle(ChatColor.DARK_GREEN + "Welcome to the map " + map + "!", ChatColor.GREEN + "If there are no cops, do /cops to become one for this round!", 0, 100, 40);
            p.sendMessage(ChatColor.DARK_GREEN + "Welcome to the map " + map + "! If there are no cops, do " + ChatColor.GREEN + "/cops" + ChatColor.DARK_GREEN + " to become one for this round!");
        }
        updateJoinSign();
        p.setGameMode(GameMode.ADVENTURE);

        if (copsTask) {
            if (designatedJob == null)
                return;
            if (designatedJob.equalsIgnoreCase("janitor"))
                new Janitor(p).setupJob();
            else if (designatedJob.equalsIgnoreCase("cook"))
                new Cook(p).setupJob();
            else if (designatedJob.equalsIgnoreCase("librarian"))
                new Librarian(p).setupJob();
            else if (designatedJob.equalsIgnoreCase("laundryman"))
                new Laundryman(p).setupJob();
        }
    }

    public void removePlayer(Player p) {
        p.setPlayerListName(p.getName());
        players.remove(p.getName());
        ProCandrV4.plugin.game.remove(p.getName());
        spawnpoint.remove(p.getName());
        if (job.containsKey(p.getName()))
            job.get(p.getName()).resetAndRemoveJob();
        if (bb != null)
            bb.removePlayer(p);

        if (cops.contains(p.getName())) {
            cops.remove(p.getName());
            p.setPlayerListName(p.getName());
            if (!players.isEmpty())
                sendMessageToGame(ChatColor.AQUA + "The cop " + ChatColor.DARK_AQUA + p.getName() + ChatColor.AQUA + " has left the game! Do " + ChatColor.DARK_AQUA + "/cops" + ChatColor.AQUA + " to become a cop.");
        }
        if (joinCopsList.contains(p.getName()))
            joinCopsList.remove(p.getName());

        if (players.isEmpty()) {
            endGameNoPlayers();
        }

        if (p.isOnline()) {
            ProCandrV4.plugin.clearInventory(p);
            if (hadUsedPotion(p)) {
                PotionsManager PM = new PotionsManager();
                PM.equipPotion(p, getUsedPotion(p));
                removeUsedPotion(p);
            }
            new MainShopMenu().giveItem(p);
            p.setLevel(0);
            p.setFoodLevel(20);
            p.teleport(ProCandrV4.plugin.getSpawn());
            p.sendTitle(ChatColor.DARK_GREEN + "Thanks for playing!", ChatColor.GREEN + "See ya next round.", 0, 60, 40);
            p.sendMessage(ChatColor.DARK_GREEN + "Thanks for playing! See ya next round.");
        }
        updateJoinSign();
    }

    public void kickPlayer(Player p, Player mod, String reason) {
        p.setPlayerListName(p.getName());
        players.remove(p.getName());
        ProCandrV4.plugin.game.remove(p.getName());
        spawnpoint.remove(p.getName());
        if (job.containsKey(p.getName()))
            job.get(p.getName()).resetAndRemoveJob();
        if (bb != null)
            bb.removePlayer(p);

        if (cops.contains(p.getName())) {
            cops.remove(p.getName());
            p.setPlayerListName(p.getName());
            if (!players.isEmpty())
                sendMessageToGame(ChatColor.AQUA + "The cop " + ChatColor.DARK_AQUA + p.getName() + ChatColor.AQUA + " has left the game! Do " + ChatColor.DARK_AQUA + "/cops" + ChatColor.AQUA + " to become a cop.");
        }
        if (joinCopsList.contains(p.getName()))
            joinCopsList.remove(p.getName());

        if (players.isEmpty())
            endGameNoPlayers();

        if (p.isOnline()) {
            ProCandrV4.plugin.clearInventory(p);
            if (hadUsedPotion(p)) {
                PotionsManager PM = new PotionsManager();
                PM.equipPotion(p, getUsedPotion(p));
                removeUsedPotion(p);
            }
            new MainShopMenu().giveItem(p);
            p.setLevel(0);
            p.setFoodLevel(20);
            p.teleport(ProCandrV4.plugin.getSpawn());
            p.sendTitle(ChatColor.RED + "You have been kicked by " + ChatColor.WHITE + mod.getName() + ChatColor.RED + "!", ChatColor.GOLD + reason, 0, 60, 40);
            p.sendMessage(ChatColor.RED + "You have been kicked by " + ChatColor.WHITE + mod.getName() + ChatColor.RED + "! You were kicked for '" + ChatColor.GOLD + reason + ChatColor.RED + "'.");
        }
        updateJoinSign();
    }

    private Location getPlayerCell() {
        MapsConfig MC = new MapsConfig(map);

        Random randomM = new Random();
        String cellString = MC.getConfig().getStringList("cells.spawnpoints").get(randomM.nextInt(MC.getConfig().getStringList("cells.spawnpoints").size()));

        return ProCandrV4.plugin.getLocationFString(cellString, true);
    }

    public Boolean isFull() {
        if (players.size() >= 16)
            return true;
        return false;
    }

    public void openDoors() {
        leversDown();
        sendBlockUpdates();
    }

    public void closeDoors() {
        leversUp();
        sendBlockUpdates();
    }

    private void leversUp() {
        MapsConfig MC = new MapsConfig(map);
        for (String locInString : MC.getConfig().getStringList("levers")) {
            Location loc = ProCandrV4.plugin.getLocationFString(locInString, false);
            BlockData d = loc.getBlock().getBlockData();
            if (!(d instanceof Powerable)) {
                Bukkit.getLogger().log(Level.WARNING, "[ProCandr] Error, the lever at " + loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + " is not a lever?");
            } else {
                ((Powerable)d).setPowered(false);
                loc.getBlock().setBlockData(d);
            }
        }
    }

    private void leversDown() {
        MapsConfig MC = new MapsConfig(map);
        for (String locInString : MC.getConfig().getStringList("levers")) {
            Location loc = ProCandrV4.plugin.getLocationFString(locInString, false);
            BlockData d = loc.getBlock().getBlockData();
            if (!(d instanceof Powerable)) {
                Bukkit.getLogger().log(Level.WARNING, "[ProCandr] Error, the lever at " + loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + " is not a lever?");
            } else {
                ((Powerable)d).setPowered(true);
                loc.getBlock().setBlockData(d);
            }
        }
    }

    public void sendBlockUpdates() {
        MapsConfig MC = new MapsConfig(map);
        for (String locInString : MC.getConfig().getStringList("bupdates")) {
            Location loc = ProCandrV4.plugin.getLocationFString(locInString, false);
            loc.getBlock().setType(Material.MOVING_PISTON);
            loc.getBlock().setType(Material.AIR);
        }
    }

    private void fillDoors() {
        MapsConfig MC = new MapsConfig(map);
        for (String locInString : MC.getConfig().getStringList("cells.doors")) {
            Location loc = ProCandrV4.plugin.getLocationFString(locInString, false);
            doors.add(loc.getBlock());
        }
    }

    public boolean isAlreadySpottedByCamera(Player p) {
        if (playersSpottedByCamera.contains(p.getName()))
            return true;
        return false;
    }

    public boolean isPlayerInCameraView(Player p) {
        MapsConfig MC = new MapsConfig(map);
        for (String cameraNum : MC.getConfig().getConfigurationSection("cameras").getKeys(false)) {
            Location block1 = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("cameras." + cameraNum + ".regionBlock1"), false);
            Location block2 = ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("cameras." + cameraNum + ".regionBlock2"), false);
            if (isInsideRegion(p.getLocation(), block1, block2))
                return true;
        }
        return false;
    }

    public void cameraSpottedPlayer(Player p) {
        playersSpottedByCamera.add(p.getName());
        List<Player> copsP = new ArrayList<>();
        for (String cop : cops)
            copsP.add(Bukkit.getPlayer(cop));
        GlowAPI.setGlowing(p, GlowAPI.Color.YELLOW, copsP);
        new BukkitRunnable() {
            public void run() {
                if (isPlayerInCameraView(p) == false) {
                    cancel();
                    GlowAPI.setGlowing(p, false, copsP);
                    playersSpottedByCamera.remove(p.getName());
                }
            }
        }.runTaskTimer(ProCandrV4.plugin, 40L, 40L);
    }

    private boolean isInsideRegion(Location loc, Location l1, Location l2) {
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

        return loc.getX() >= x1 && loc.getX() <= x2 && loc.getY() >= y1 && loc.getY() <= y2 && loc.getZ() >= z1 && loc.getZ() <= z2;
    }

    public void updateJoinSign() {
        MapsConfig MC = new MapsConfig(map);
        if (!MC.getConfig().contains("joinSign")) {
            Bukkit.getLogger().log(Level.WARNING, "[ProCandrV4] The map " + map + " does not have a join sign! Set the join sign with the '/procandr tool " + map + "' command.");
            return;
        }
        Sign joinSign = (Sign) ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("joinSign"), false).getBlock().getState();
        joinSign.setLine(0, ChatColor.BLACK + "[" + ChatColor.DARK_GREEN + "CANDR" + ChatColor.BLACK + "]");
        joinSign.setLine(1, ChatColor.DARK_GREEN + "Map: " + ChatColor.BLACK + map);
        joinSign.setLine(2, ChatColor.DARK_GREEN + "Players: " + ChatColor.BLACK + players.size() + ChatColor.DARK_GREEN + "/" + ChatColor.BLACK + "16");
        joinSign.setLine(3, ChatColor.DARK_GREEN + "Cops: " + ChatColor.BLACK + cops.size() + ChatColor.DARK_GREEN + "/" + ChatColor.BLACK + "2");
        joinSign.update();
    }

    public void staffAccess(Player p, String loc) {
        MapsConfig MC = new MapsConfig(map);
        if (!MC.getConfig().contains("staffAccess." + loc))
            return;
        p.teleport(ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("staffAccess." + loc), true));
    }

    public MapsConfig getMapfig() {
        return new MapsConfig(map);
    }

    public Job getJob(Player p) {
        if (job.containsKey(p.getName()))
            return job.get(p.getName());
        return null;
    }

    public void setJob(Player p, Job jobInstance) {
        job.put(p.getName(), jobInstance);
    }

    public void removeJob(Player p) {
        if (!job.containsKey(p.getName()))
            return;
        job.remove(p.getName());
    }

    public Location getSpawn(Player p) {
        return spawnpoint.get(p.getName());
    }

    public boolean doorIsAJobDoor(Location location) {
        MapsConfig MC = getMapfig();
        for (String doorName : MC.getConfig().getConfigurationSection("jobDoors").getKeys(false)) {
            String door = MC.getConfig().getString("jobDoors." + doorName + ".door");
            String loc1 = ProCandrV4.plugin.getStringFLocation(location, false);
            String loc2 = ProCandrV4.plugin.getStringFLocation(location.clone().add(0,1,0), false);
            if (door.equalsIgnoreCase(loc1) || door.equalsIgnoreCase(loc2))
                return true;
        }
        return false;
    }

    public boolean keyIsTheRightKey(Location location, String displayName) {
        MapsConfig MC = getMapfig();
        for (String doorName : MC.getConfig().getConfigurationSection("jobDoors").getKeys(false)) {
            String door = MC.getConfig().getString("jobDoors." + doorName + ".door");
            String loc1 = ProCandrV4.plugin.getStringFLocation(location, false);
            String loc2 = ProCandrV4.plugin.getStringFLocation(location.clone().add(0,1,0), false);
            if (door.equalsIgnoreCase(loc1) || door.equalsIgnoreCase(loc2)) {
                if (doorName.equalsIgnoreCase("janitor") && ChatColor.stripColor(displayName).contains("Yellow"))
                    return true;
                if (doorName.equalsIgnoreCase("librarian") && ChatColor.stripColor(displayName).contains("Red"))
                    return true;
                if (doorName.equalsIgnoreCase("cook") && ChatColor.stripColor(displayName).contains("Pink"))
                    return true;
                if (doorName.equalsIgnoreCase("laundryman") && ChatColor.stripColor(displayName).contains("Green"))
                    return true;
            }
        }
        return false;
    }

    public void teleportThroughDoor(Location location, Player p) {
        MapsConfig MC = getMapfig();
        for (String doorName : MC.getConfig().getConfigurationSection("jobDoors").getKeys(false)) {
            String door = MC.getConfig().getString("jobDoors." + doorName + ".door");
            String loc1 = ProCandrV4.plugin.getStringFLocation(location, false);
            String loc2 = ProCandrV4.plugin.getStringFLocation(location.clone().add(0,1,0), false);
            if (door.equalsIgnoreCase(loc1) || door.equalsIgnoreCase(loc2))
                p.teleport(ProCandrV4.plugin.getLocationFString(MC.getConfig().getString("jobDoors." + doorName + ".destination"), true));
        }
    }

    public void openCellCommand(Player p) {
        if (openCellActive != true) {
            p.sendMessage(ChatColor.RED + "You cannot do " + ChatColor.WHITE + "/open cell" + ChatColor.RED + " at this time! Please wait till it prompts you to do " +  ChatColor.WHITE + "/open cell" +
                    ChatColor.RED + " in chat.");
            return;
        }
        openCellActive = false;
        sendMessageToGame(ChatColor.GOLD + "" + ChatColor.BOLD + "Cells have been opened, riot!!!");
        openDoors();
    }

    public void openCellCommand(Player p, Boolean modOpenCell) {
        openCellActive = false;
        sendMessageToGame(ChatColor.GOLD + "" + ChatColor.BOLD + "Cells have been opened, riot!!!");
        openDoors();
    }

    public void playerUsedPotion(Player p, ItemStack potion) {
        potionEquiped.put(p.getName(), potion);
    }

    public ItemStack getUsedPotion(Player p) {
        return potionEquiped.get(p.getName());
    }

    public Boolean hadUsedPotion(Player p) {
        return potionEquiped.containsKey(p.getName());
    }

    public void removeUsedPotion(Player p) {
        potionEquiped.remove(p.getName());
    }

    private void spawnDirtAndGarbage() {
        for (String locString : getDirtAndGarbageSpots()) {
            Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
            if (new Random().nextInt(10) > 5)
                loc.getBlock().setType(Material.GRASS);
            else
                loc.getBlock().setType(Material.DEAD_BUSH);
        }
    }
    private List<String> getDirtAndGarbageSpots() {
        if (new MapsConfig(map).getConfig().contains("dirtAndGarbageSpots"))
            return new MapsConfig(map).getConfig().getStringList("dirtAndGarbageSpots");
        return new ArrayList<>();
    }

    public void setFirstJanitor(Player p) {
        firstJanitor = p.getName();
        for (String cops : cops) {
            Player cop = Bukkit.getPlayer(cops);
            ProCandrV4.econ.depositPlayer(cop, 10);
            cop.sendMessage(ChatColor.GREEN + "$10 has been added to your account for getting the prison cleaned.");
        }
    }

    public Boolean someoneCompletedJanitor() {
        if (firstJanitor.equalsIgnoreCase("null"))
            return false;
        if (Bukkit.getPlayer(firstJanitor) == null)
            return false;
        if (Bukkit.getPlayer(firstJanitor).isOnline())
            return true;
        return false;
    }

    public void setFirstCook(Player p) {
        firstCook = p.getName();
        for (String cops : cops) {
            Player cop = Bukkit.getPlayer(cops);
            ProCandrV4.econ.depositPlayer(cop, 10);
            cop.sendMessage(ChatColor.GREEN + "$10 has been added to your account for getting the prison's food cooked.");
        }
    }

    public Boolean someoneCompletedCook() {
        if (firstCook.equalsIgnoreCase("null"))
            return false;
        if (Bukkit.getPlayer(firstCook) == null)
            return false;
        if (Bukkit.getPlayer(firstCook).isOnline())
            return true;
        return false;
    }

    public void setFirstLaundryman(Player p) {
        firstLaundryman = p.getName();
        for (String cops : cops) {
            Player cop = Bukkit.getPlayer(cops);
            ProCandrV4.econ.depositPlayer(cop, 10);
            cop.sendMessage(ChatColor.GREEN + "$10 has been added to your account for getting the prison's bedsheets cleaned.");
        }
    }

    public Boolean someoneCompletedLaundryman() {
        if (firstLaundryman.equalsIgnoreCase("null"))
            return false;
        if (Bukkit.getPlayer(firstLaundryman) == null)
            return false;
        if (Bukkit.getPlayer(firstLaundryman).isOnline())
            return true;
        return false;
    }

    public void setFirstLibrarian(Player p) {
        firstLibrarian = p.getName();
        for (String cops : cops) {
            Player cop = Bukkit.getPlayer(cops);
            ProCandrV4.econ.depositPlayer(cop, 10);
            cop.sendMessage(ChatColor.GREEN + "$10 has been added to your account for getting the prison's books sorted.");
        }
    }

    public Boolean someoneCompletedLibrarian() {
        if (firstLibrarian.equalsIgnoreCase("null"))
            return false;
        if (Bukkit.getPlayer(firstLibrarian) == null)
            return false;
        if (Bukkit.getPlayer(firstLibrarian).isOnline())
            return true;
        return false;
    }

    public List<String> getCops() {
        return cops;
    }

    public void setLeversUsed(Boolean state) {
        leversUsed = state;
    }

    public Boolean copsTaskActive() {
        return copsTask;
    }

    public Integer getPlayerSize() {
        return players.size();
    }
}
