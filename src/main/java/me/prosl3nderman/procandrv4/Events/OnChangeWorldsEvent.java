package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class OnChangeWorldsEvent implements Listener {

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("ProCandrV4.changeWorlds"))
            return;
        if (p.getWorld().getName().equalsIgnoreCase("world") || p.getWorld().getName().equalsIgnoreCase("world_the_end") || p.getWorld().getName().equalsIgnoreCase("world_nether")
                || p.getWorld().getName().equalsIgnoreCase("worlda") || p.getWorld().getName().equalsIgnoreCase("MemberWorld") || p.getWorld().getName().equalsIgnoreCase("hub")
                || p.getWorld().getName().equalsIgnoreCase("DonorWorld") || p.getWorld().getName().equalsIgnoreCase("creative") || p.getWorld().getName().equalsIgnoreCase("BSkyBlock_world")
                || p.getWorld().getName().equalsIgnoreCase("BSkyBlock_world_the_end") || p.getWorld().getName().equalsIgnoreCase("BSkyBlock_world_nether")
                || p.getWorld().getName().equalsIgnoreCase("Aircraft") || p.getWorld().getName().equalsIgnoreCase("powerplant")) {
            if (!ProCandrV4.plugin.game.containsKey(p.getName())) {
                p.teleport(ProCandrV4.plugin.getSpawn());
                return;
            }
            Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
            p.teleport(game.getSpawn(p));
        }
    }
}
