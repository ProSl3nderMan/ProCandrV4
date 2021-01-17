package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OnUseJobDoor implements Listener {

    @EventHandler
    public void onRightClickJobDoorWithKey(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        if (p.getInventory().getItemInMainHand().getType() != Material.TRIPWIRE_HOOK)
            return;
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        if (e.getClickedBlock().getType() != Material.IRON_DOOR) {
            p.sendMessage(ChatColor.RED + "This is not a job door! Read the description on the key to find the location of your job door.");
            return;
        }
        if (game.doorIsAJobDoor(e.getClickedBlock().getLocation()) == false) {
            p.sendMessage(ChatColor.RED + "This is not a job door! Read the description on the key to find the location of your job door.");
            return;
        }
        if (!game.keyIsTheRightKey(e.getClickedBlock().getLocation(), p.getInventory().getItemInMainHand().getItemMeta().getDisplayName())) {
            p.sendMessage(ChatColor.RED + "This is not the right color key for this color door! Read the description on the key to find the location of your job door.");
            return;
        }
        game.teleportThroughDoor(e.getClickedBlock().getLocation(), p);
    }
}
