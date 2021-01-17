package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.shop.MainShopMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GUIOpenerHandler implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.EMERALD && (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Shop"))) {
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                new MainShopMenu().openMenu(p);
        }
    }
}
