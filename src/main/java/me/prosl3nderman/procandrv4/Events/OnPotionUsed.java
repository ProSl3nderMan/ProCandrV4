package me.prosl3nderman.procandrv4.Events;

import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.ProCandrV4;
import me.prosl3nderman.procandrv4.shop.PotionsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.Potion;

public class OnPotionUsed implements Listener {

    @EventHandler
    public void onPlayerConsumePotion(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        if (e.getItem().getType() != Material.POTION)
            return;
        PotionsManager PM = new PotionsManager();
        if (!PM.playerOwnsPotion(p.getUniqueId().toString(), e.getItem()))
            return;
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        game.playerUsedPotion(p, e.getItem());
    }

    @EventHandler
    public void onPlayerThrowPotion(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND)
            return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR)
            return;
        Player p = e.getPlayer();
        if (e.getItem() == null)
            return;
        if (e.getItem().getType() != Material.SPLASH_POTION)
            return;
        if (!ProCandrV4.plugin.game.containsKey(p.getName()))
            return;
        PotionsManager PM = new PotionsManager();
        if (!PM.playerOwnsPotion(p.getUniqueId().toString(), e.getItem()))
            return;
        Game game = ProCandrV4.plugin.games.get(ProCandrV4.plugin.game.get(p.getName()));
        game.playerUsedPotion(p, e.getItem());
    }
}
