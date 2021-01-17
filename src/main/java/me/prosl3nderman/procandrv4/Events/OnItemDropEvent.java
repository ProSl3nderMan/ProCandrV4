package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class OnItemDropEvent implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName())) {
            if (!p.hasPermission("ProCandrV4.dropItemsOutsideGames")) {
                e.setCancelled(true);
                return;
            }
            return;
        }
        Material dropped = e.getItemDrop().getItemStack().getType();
        if (dropped != Material.COOKED_BEEF && (dropped != Material.STICK && dropped != Material.DIAMOND_BOOTS && dropped != Material.DIAMOND_HELMET && dropped != Material.BOW && dropped != Material.ARROW
                && p.hasPermission(ProCandrV4.plugin.getConfig().getString("dropCopItemsPermission"))))
            e.setCancelled(true);
    }
}
