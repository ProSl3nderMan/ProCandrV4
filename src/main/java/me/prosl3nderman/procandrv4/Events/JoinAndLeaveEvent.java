package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.ActiveConfig;
import me.prosl3nderman.procandrv4.Database.ItemsTable;
import me.prosl3nderman.procandrv4.Database.StatsTable;
import me.prosl3nderman.procandrv4.ItemsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import me.prosl3nderman.procandrv4.shop.MainShopMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JoinAndLeaveEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setGameMode(GameMode.ADVENTURE);
        p.setFlying(false);
        p.setAllowFlight(false);

        ProCandrV4.plugin.getServer().getScheduler().scheduleSyncDelayedTask(ProCandrV4.plugin, new Runnable() {
            @Override
            public void run() {
                p.setFlying(false);
            }
        }, 20L);
        ProCandrV4.plugin.clearInventory(p);
        new MainShopMenu().giveItem(p);
        p.setLevel(0);
        p.setFoodLevel(20);
        p.teleport(ProCandrV4.plugin.getLocationFString(ProCandrV4.plugin.getConfig().getString("spawn"), true));

        StatsTable CD = new StatsTable();
        if (!p.hasPlayedBefore()) {
            CD.newPlayer(p.getUniqueId().toString(), p.getName());

            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Welcome " + ChatColor.DARK_PURPLE + p.getName() + ChatColor.LIGHT_PURPLE + " to DragonsDoom!");
            p.sendMessage(ChatColor.GOLD + "Here's a link to our discord: " + ChatColor.WHITE + "https://discordapp.com/invite/zqG53PS");
        }
        CD.onJoin(p, p.getUniqueId().toString());
        new ItemsTable().onJoin(p, p.getUniqueId().toString());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String joinTime = dtf.format(now);
        ActiveConfig ac = new ActiveConfig();
        ac.getConfig().set(p.getUniqueId().toString() + ".joinTime", joinTime);
        ac.srConfig();
        new BukkitRunnable() {
            public void run() {
                if (!p.getWorld().getName().contains("lobby")) {
                    p.teleport(ProCandrV4.plugin.getLocationFString(ProCandrV4.plugin.getConfig().getString("spawn"), true));
                }
            }
        }.runTaskLater(ProCandrV4.plugin, 50L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (ProCandrV4.plugin.game.containsKey(p.getName()))
            ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName())).removePlayer(p);

        new StatsTable().onLeave(p, p.getUniqueId().toString());
        ItemsConfig IC = new ItemsConfig();
        IC.getConfig().set(p.getUniqueId().toString(), null);
        IC.srConfig();
    }
}
