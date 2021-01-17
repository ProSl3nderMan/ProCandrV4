package me.prosl3nderman.procandrv4.Jobs;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.MapsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Janitor extends Job {

    private Player p;
    private String map;
    private List<String> dirtAndGarbageCleaned;

    public Janitor(Player p) {
        this.p = p;
        map = ProCandrV4.plugin.game.get(p.getName());

        dirtAndGarbageCleaned = new ArrayList<>();
    }

    @Override
    public void setupJob() {
        ProCandrV4.plugin.games.get(map).setJob(p, this);

        ItemStack broom = new ItemStack(Material.WOODEN_HOE, 1);
        ItemMeta meta = broom.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Broom");
        meta.setLore(Arrays.asList("Right click grass and dead bushes around the prison to clean.", "Need to clean 10 to complete the task."));
        broom.setItemMeta(meta);

        p.getInventory().addItem(broom);

        FileConfiguration cfg = ProCandrV4.plugin.getConfig();
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.janitorRobbers.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.janitorRobbers.secondLine")));
        p.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.janitorRobbers.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.janitorRobbers.secondLine")), 0, 100, 40);
        /*
        p.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "You have been given the job of the Janitor! Make sure to fill your exp bar by cleaning up grass and dead bushes inside the prison to get some " +
                "extra money!");
        p.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "To clean grass and dead bushes up, just right click the grass and dead bushes with your broom (wooden hoe).");
        */
    }

    @Override
    public void resetAndRemoveJob() { //resets the player's job and removes them
        ProCandrV4.plugin.games.get(map).removeJob(p);
        despawnDirtAndGarbage(p);
    }

    @Override
    public void handleEvent(PlayerInteractEvent e) { //OnCleanGarbageNDirt
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Location blockAbove = e.getClickedBlock().getLocation().clone().add(0,1,0);
        Boolean cleanedContainsBlockAbove = dirtAndGarbageCleaned.contains(ProCandrV4.plugin.getStringFLocation(blockAbove, false));
        if (dirtAndGarbageCleaned.contains(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false)) || cleanedContainsBlockAbove) {
            new BukkitRunnable() {
                public void run() {
                    if (cleanedContainsBlockAbove)
                        p.sendBlockChange(blockAbove, Material.MOVING_PISTON.createBlockData());
                    else
                        p.sendBlockChange(e.getClickedBlock().getLocation(), Material.MOVING_PISTON.createBlockData());
                }
            }.runTaskLater(ProCandrV4.plugin, 3L);
            return;
        }
        boolean usingBroom = p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE;
        boolean sweepingGrassAndBushes = e.getClickedBlock().getType() == Material.GRASS || e.getClickedBlock().getType() == Material.DEAD_BUSH;
        if (usingBroom == false && sweepingGrassAndBushes == false)
            return;
        if (usingBroom == false && sweepingGrassAndBushes) {
            p.sendMessage(ChatColor.RED + "Must have the broom out to sweep this up!");
            e.setCancelled(true);
            return;
        }
        if (!sweepingGrassAndBushes) {
            p.sendMessage(ChatColor.RED + "You can't sweep this! You can only sweep grass and dead bushes!");
            return;
        }

        dirtAndGarbageCleaned.add(ProCandrV4.plugin.getStringFLocation(e.getClickedBlock().getLocation(), false));

        new BukkitRunnable() {
            public void run() {
                p.sendBlockChange(e.getClickedBlock().getLocation(), Material.MOVING_PISTON.createBlockData());

                Game game = ProCandrV4.plugin.games.get(map);
                if (game.getCops().size() != 0) {
                    for (String cop : game.getCops())
                        Bukkit.getPlayer(cop).sendBlockChange(e.getClickedBlock().getLocation(), Material.MOVING_PISTON.createBlockData());
                }
            }
        }.runTaskLater(ProCandrV4.plugin, 3L);

        p.setLevel(p.getLevel() + 1);
        if (p.getLevel() == 10) {
            p.sendMessage(ChatColor.GOLD + "You have reached your cleaning goal of 10!");
            p.setLevel(0);
            p.getInventory().remove(Material.WOODEN_HOE);

            ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK, 1);
            ItemMeta meta = key.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + "Yellow Key");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Use this key at the yellow door somewhere in the prison!"));
            key.setItemMeta(meta);

            p.getInventory().addItem(key);
            p.sendMessage(ChatColor.GOLD + "You have been given the yellow key to the yellow door somewhere in the prison!");
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        removeCleanDirtAndGarbage(p);
    }

    private void removeCleanDirtAndGarbage(Player p) {
        if (p == null || !p.isOnline())
            return;
        for (String locString : dirtAndGarbageCleaned) {
            Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
            p.sendBlockChange(loc, Material.AIR.createBlockData());
        }
    }

    //Garbage: /give @p minecraft:player_head{display:{Name:"{\"text\":\"Garbage\"}"},SkullOwner:{Id:"6cf37d20-e232-47da-9eb1-d980b14eaec4",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmZkZGRiMThhYTUyMWRiZjMxM2RhZDJhMzIyNzMzODNjOTkyY2EyY2EzNmQxMmMyZjc0MTdkMWU1ZDViYzg2NSJ9fX0="}]}}} 1
    //Dirt: /give @p minecraft:player_head{display:{Name:"{\"text\":\"Dirt\"}"},SkullOwner:{Id:"ca021f3f-5002-46b2-bf34-9857790901cf",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFiNDNiOGMzZDM0ZjEyNWU1YTNmOGI5MmNkNDNkZmQxNGM2MjQwMmMzMzI5ODQ2MWQ0ZDRkN2NlMmQzYWVhIn19fQ=="}]}}} 1

    private List<String> getDirtAndGarbageSpot() {
        if (new MapsConfig(map).getConfig().contains("dirtAndGarbageSpots"))
            return new MapsConfig(map).getConfig().getStringList("dirtAndGarbageSpots");
        return new ArrayList<>();
    }

    /*private void spawnDirtAndGarbage(Player p) {
        if (p == null || !p.isOnline())
            return;
        for (String locString : dirtAndGarbage) {
            Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
            loc.getBlock().setType(Material.MOVING_PISTON);
        }
        new BukkitRunnable() {
            public void run() {
                for (String locString : dirtAndGarbage) {
                    Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
                    if (new Random().nextInt(10) > 5)
                        p.sendBlockChange(loc, Material.GRASS.createBlockData());
                    else
                        p.sendBlockChange(loc, Material.DEAD_BUSH.createBlockData());
                }
            }
        }.runTaskLater(ProCandrV4.plugin, 10L);
    }*/

    private void despawnDirtAndGarbage(Player p) {
        if (p == null || !p.isOnline())
            return;
        for (String locString : dirtAndGarbageCleaned) {
            Location loc = ProCandrV4.plugin.getLocationFString(locString, false);
            if (p != null && p.isOnline())
                p.sendBlockChange(loc, loc.getBlock().getBlockData());
        }
    }
}
