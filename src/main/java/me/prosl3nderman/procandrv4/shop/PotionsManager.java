package me.prosl3nderman.procandrv4.shop;

import me.prosl3nderman.procandrv4.ItemsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PotionsManager {

    private HashMap<String, String> potions = new HashMap<>();
    //             potion meta base point data, database name
    private HashMap<String, ItemStack> allPots = new HashMap<>();
    //             potion meta base point data, potion
    // PotionData(PotionType type, boolean extended, boolean upgraded)
    // Potion (PotionType type, int level, boolean splash, boolean extended)

    public PotionsManager() {
        Potion pot;
        PotionMeta pmeta;

        pot = new Potion(PotionType.JUMP, 1);
        potions.put(pot.toItemStack(1).toString(), "jpot1");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.JUMP);
        pot.setHasExtendedDuration(true);
        potions.put(pot.toItemStack(1).toString(), "jpot2");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.JUMP, 1, true);
        potions.put(pot.toItemStack(1).toString(), "jsplash");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.SPEED, 1);
        potions.put(pot.toItemStack(1).toString(), "sppot1");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.SPEED);
        pot.setHasExtendedDuration(true);
        potions.put(pot.toItemStack(1).toString(), "sppot2");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.SPEED, 1, true);
        potions.put(pot.toItemStack(1).toString(), "spsplash");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        // Potion (PotionType type, int level, boolean splash, boolean extended)

        pot = new Potion(PotionType.SLOWNESS, 1, true, false);
        potions.put(pot.toItemStack(1).toString(), "slsplash1");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.SLOWNESS, 1, true, true);
        potions.put(pot.toItemStack(1).toString(), "slsplash2");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.POISON);
        pot.setSplash(true);
        potions.put(pot.toItemStack(1).toString(), "psplash1");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.POISON, 1, true);
        pot.setHasExtendedDuration(true);
        pot.setSplash(true);
        potions.put(pot.toItemStack(1).toString(), "psplash2");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.INSTANT_HEAL, 1, false, false);
        potions.put(pot.toItemStack(1).toString(), "hpot1");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.INSTANT_HEAL, 1, false, true);
        potions.put(pot.toItemStack(1).toString(), "hpot2");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

        pot = new Potion(PotionType.INSTANT_HEAL, 1, true, false);
        potions.put(pot.toItemStack(1).toString(), "hsplash");
        allPots.put(pot.toItemStack(1).toString(), pot.toItemStack(1));

    }

    public String getDBName(ItemStack potion) {
        return potions.get(potion.toString());
    }

    public List<ItemStack> getAllPotions() {
        return new ArrayList<>(allPots.values());
    }

    public Boolean playerOwnsPotion(String uuid, ItemStack potion) {
        ItemsConfig IC = new ItemsConfig();
        if (!IC.getConfig().contains(uuid + ".potions"))
            return false;

        String DBName = potions.get(potion.toString());
        if (IC.getConfig().getStringList(uuid + ".potions").contains(DBName))
            return true;
        return false;
    }

    public String getPrice(ItemStack potion) {
        return ProCandrV4.plugin.getConfig().getString("storePrices.potions." + getDBName(potion));
    }

    public void equipPotion(Player p, ItemStack potion) {
        p.getInventory().remove(Material.POTION);
        p.getInventory().remove(Material.SPLASH_POTION);
        p.getInventory().addItem(potion);
    }

}
