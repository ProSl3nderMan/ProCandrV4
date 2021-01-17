package me.prosl3nderman.procandrv4;

import me.prosl3nderman.procandrv4.Commands.*;
import me.prosl3nderman.procandrv4.Database.ItemsTable;
import me.prosl3nderman.procandrv4.Database.StatsTable;
import me.prosl3nderman.procandrv4.Events.*;
import me.prosl3nderman.procandrv4.shop.ArmoryShopMenu;
import me.prosl3nderman.procandrv4.shop.MainShopMenu;
import me.prosl3nderman.procandrv4.shop.PotionryShopMenu;
import me.prosl3nderman.procandrv4.shop.WeaponryShopMenu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ProCandrV4 extends JavaPlugin {

    public static ProCandrV4 plugin;
    public HashMap<String,Game> games = new HashMap<>();
    public HashMap<String,String> game = new HashMap<>(); //String = player name ; String = map name
    public static Economy econ = null;
    public HashMap<String, ItemStack> heads = new HashMap<>();
    public HashMap<String, String> reply = new HashMap<String, String>();

    @Override
    public void onEnable() {
        plugin = this;

        checkIfReload();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("procandr").setExecutor(new ProCandrCommand());
        getCommand("procandr").setTabCompleter(new ProCandrCommand());
        getCommand("join").setExecutor(new JoinCommand());
        getCommand("leave").setExecutor(new LeaveCommand());
        getCommand("cops").setExecutor(new CopsCommand());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("top").setExecutor(new TopCommand());
        getCommand("open").setExecutor(new OpenCellCommand());
        getCommand("mod").setExecutor(new ModCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("reply").setExecutor(new ReplyCommand());
        getCommand("message").setExecutor(new MessageCommand());
        getCommand("vote").setExecutor(new VoteCommand());

        getServer().getPluginManager().registerEvents(new ToolInteraction(), this);
        getServer().getPluginManager().registerEvents(new JoinAndLeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new EngineInteraction(), this);
        getServer().getPluginManager().registerEvents(new PlayerCameraCaptureEvent(), this);
        getServer().getPluginManager().registerEvents(new RightClickJoinSignEvent(), this);
        getServer().getPluginManager().registerEvents(new StaffAccessEvent(), this);
        getServer().getPluginManager().registerEvents(new OnItemDropEvent(), this);
        getServer().getPluginManager().registerEvents(new JobEventHandler(), this);
        getServer().getPluginManager().registerEvents(new OnRespawnAndDeath(), this);
        getServer().getPluginManager().registerEvents(new OnUseJobDoor(), this);
        getServer().getPluginManager().registerEvents(new MainShopMenu(), this);
        getServer().getPluginManager().registerEvents(new ArmoryShopMenu(), this);
        getServer().getPluginManager().registerEvents(new WeaponryShopMenu(), this);
        getServer().getPluginManager().registerEvents(new PotionryShopMenu(), this);
        getServer().getPluginManager().registerEvents(new GUIOpenerHandler(), this);
        getServer().getPluginManager().registerEvents(new OnFallIntoVoid(), this);
        getServer().getPluginManager().registerEvents(new OnPotionUsed(), this);
        getServer().getPluginManager().registerEvents(new OnChangeWorldsEvent(), this);
        getServer().getPluginManager().registerEvents(new OnPortal(), this);

        doConfig();
        reloadMaps();
        new ItemsConfig().reloadConfig();

        new BukkitRunnable() {
            public void run() {
                for (String map : getConfig().getStringList("maps"))
                    new Game(map, true).updateJoinSign();
            }
        }.runTaskLater(this, 60L);

        setupEconomy();

        getSkull("garbagehead" , "http://textures.minecraft.net/texture/bfdddb18aa521dbf313dad2a32273383c992ca2ca36d12c2f7417d1e5d5bc865");
        getSkull("dirthead", "http://textures.minecraft.net/texture/1ab43b8c3d34f125e5a3f8b92cd43dfd14c62402c33298461d4d4d7ce2d3aea");


        if (Bukkit.getOnlinePlayers().size() == 0)
            return;
        new StatsTable().onEnable();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.ADVENTURE);
            ProCandrV4.plugin.clearInventory(p);
            new MainShopMenu().giveItem(p);
            p.setLevel(0);
            p.setFoodLevel(20);

            new ItemsTable().onJoin(p, p.getUniqueId().toString());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String joinTime = dtf.format(now);
            ActiveConfig ac = new ActiveConfig();
            ac.getConfig().set(p.getUniqueId().toString() + ".joinTime", joinTime.toString());
            ac.srConfig();
        }
    }

    @Override
    public void onDisable() {
        if (Bukkit.getOnlinePlayers().size() != 0)
            new StatsTable().onDisable();
    }

    public void reloadMaps() {
        for (String map : getConfig().getStringList("maps"))
            new MapsConfig(map).reloadConfig();
    }

    public void clearInventory(Player p) {
        p.getInventory().addItem(p.getInventory().getItemInOffHand());
        p.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
        p.getInventory().remove(Material.COOKED_BEEF);
        p.getInventory().remove(Material.STICK);
        p.getInventory().remove(Material.DIAMOND_BOOTS);
        p.getInventory().remove(Material.DIAMOND_HELMET);
        p.getInventory().remove(Material.DIAMOND_CHESTPLATE);
        p.getInventory().remove(Material.DIAMOND_LEGGINGS);
        p.getInventory().remove(Material.DIAMOND_SWORD);
        p.getInventory().remove(Material.BOW);
        p.getInventory().remove(Material.ARROW);
        p.getInventory().remove(Material.EMERALD);
        p.getInventory().remove(Material.SHEARS);
        p.getInventory().remove(Material.PHANTOM_MEMBRANE);
        p.getInventory().remove(Material.WRITTEN_BOOK);
        p.getInventory().remove(Material.WOODEN_HOE);
        p.getInventory().remove(Material.RED_CONCRETE_POWDER);
        p.getInventory().remove(Material.RED_CONCRETE);
        p.getInventory().remove(Material.CHICKEN);
        p.getInventory().remove(Material.COOKED_CHICKEN);
        p.getInventory().remove(Material.TRIPWIRE_HOOK);
        p.getInventory().remove(Material.GLASS_BOTTLE);

        if (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET)
            p.getInventory().setHelmet(null);
        if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE)
            p.getInventory().setChestplate(null);
        if (p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS)
            p.getInventory().setLeggings(null);
        if (p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS)
            p.getInventory().setBoots(null);

        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() != null) {
                if (!item.getType().toString().contains("leather") && item.getType() != Material.POTION && item.getType() != Material.SPLASH_POTION && !item.getType().toString().contains("shovel")
                        && !item.getType().toString().contains("pickaxe") && !item.getType().toString().contains("chainmail") && !item.getType().toString().contains("turtle"))
                    p.getInventory().remove(item);
            }
        }
    }

    public void getSkull(String name, String url) {
        heads.put(name, SkullCreator.itemFromUrl(url));
    }

    public List<String> getList(String path, FileConfiguration MC) {
        if (!MC.contains(path))
            return new ArrayList<String>();
        return MC.getStringList(path);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }

    public void checkIfReload() {
        if (Bukkit.getOnlinePlayers().size() == 0)
            return;
        new StatsTable().onEnable();
        for (Player p : Bukkit.getOnlinePlayers()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String joinTime = dtf.format(now);
            ActiveConfig ac = new ActiveConfig();
            ac.getConfig().set(p.getUniqueId().toString() + ".joinTime", joinTime.toString());
            ac.srConfig();
        }
    }

    private void doConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void updateConfig() {
        getConfig().options().copyDefaults(true);
        boolean change = false;
        Configuration defaults = getConfig().getDefaults();
        for (String defaultKey : defaults.getKeys(true)) {
            if (!getConfig().contains(defaultKey)) {
                getConfig().set(defaultKey, defaults.get(defaultKey));
                change = true;
            }
        }
        if (change) srConfig();
    }


    public void srConfig() {
        saveConfig();
        reloadConfig();
    }

    public String getStringFLocation(Location loc, Boolean yawPitch) { //world;x;y;z;yaw;pitch
        if (yawPitch)
            return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() +";" +loc.getPitch();
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ();
    }

    public Location getLocationFString(String s, Boolean yawPitch) {
        String[] part = s.split(";");
        if (yawPitch)
            return new Location(Bukkit.getServer().getWorld(part[0]), intS(part[1]), intS(part[2]), intS(part[3]),floatS(part[4]),floatS(part[5]));
        return new Location(Bukkit.getServer().getWorld(part[0]), intS(part[1]), intS(part[2]), intS(part[3]));
    }

    private Double intS(String s) {
        return Double.parseDouble(s);
    }

    private Float floatS(String s) {
        return Float.parseFloat(s);
    }

    public void checkHours(Double hours, String uuid, Player p) {
        String star = "hours";
        String prevStar = "hours";
        if (hours >= 100) {
            star = 100 + star;
            prevStar = 50 + prevStar;
        } else if (hours >= 50) {
            star = 50 + star;
            prevStar = 10 + prevStar;
        } else if (hours >= 10) {
            star = 10 + star;
        }

        if (star.equalsIgnoreCase("hours"))
            return;
        if (p.hasPermission("ProEverything.suffixs." + star))
            return;
        final String starr = star;
        final String prevStarr = prevStar;
        ProCandrV4.plugin.getServer().getScheduler().scheduleSyncDelayedTask(ProCandrV4.plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission set ProEverything.suffixs." + starr + " true");
                if (p.hasPermission("ProEverything.suffixs." + prevStarr))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission unset ProEverything.suffixs." + prevStarr);
            }
        });
    }

    public Location getSpawn() {
        return getLocationFString(getConfig().getString("spawn"), true);
    }
}
