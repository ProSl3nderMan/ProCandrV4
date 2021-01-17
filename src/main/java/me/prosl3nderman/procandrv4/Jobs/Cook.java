package me.prosl3nderman.procandrv4.Jobs;

import me.prosl3nderman.procandrv4.Furnace;
import me.prosl3nderman.procandrv4.Game;
import me.prosl3nderman.procandrv4.MapsConfig;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Cook extends Job {

    private Player p;
    private String map;
    private HashMap<Location, Furnace> furnaces = new HashMap<>();

    public Cook(Player p) {
        this.p = p;
        map = ProCandrV4.plugin.game.get(p.getName());
    }

    @Override
    public void setupJob() {
        ProCandrV4.plugin.games.get(map).setJob(p, this);

        ItemStack gloves = new ItemStack(Material.PHANTOM_MEMBRANE, 1);
        ItemMeta meta = gloves.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Gloves");
        meta.setLore(Arrays.asList("Step 1) Take chicken out from freezer", "Step 2) Put chicken in the furnace", "Step 3) Take chicken out of the furnace once cooked", "Step 4) Put the chicken on the food trays."));
        gloves.setItemMeta(meta);

        p.getInventory().addItem(gloves);

        FileConfiguration cfg = ProCandrV4.plugin.getConfig();

        p.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.cookRobbers.firstLine")) + " " + ChatColor.translateAlternateColorCodes('&', cfg.getString("messages.cookRobbers.secondLine")));
        p.sendTitle(ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.cookRobbers.firstLine")), ChatColor.translateAlternateColorCodes('&', cfg.getString("titles.cookRobbers.secondLine")), 0, 100, 40);
        /*
        p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "You have been given the job of the Cook! Make sure to fill your exp bar by cooking up some food in the kitchen to get some extra money!");
        p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "To handle food, make sure to have your gloves out (phantom membrane) and take raw food with the freezer, put the raw food in the furnaces, " +
                "then put the cooked food " +
                "on the food trays.");
                */
    }

    @Override
    public void resetAndRemoveJob() { //resets the player's job and removes them
        ProCandrV4.plugin.games.get(map).removeJob(p);
        resetFurnaces();
    }

    @Override
    public void handleEvent(PlayerInteractEvent e) { //OnCookEvent
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.FURNACE) {
            if (p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getType() != Material.PHANTOM_MEMBRANE) {
                Furnace furn = getFurnace(e.getClickedBlock().getLocation());
                if (furnaceIsFurnace(e.getClickedBlock().getLocation()) == false)
                    return;
                e.setCancelled(true);
                if (furn.getSmelting() != null) {
                    p.sendMessage(ChatColor.RED + "Still cooking!");
                    furn.setLit(p, true);
                    return;
                }
                if (furn.getResult() != null) {
                    p.sendMessage(ChatColor.RED + "Must use gloves to grab cooked food!");
                    return;
                }
                p.sendMessage(ChatColor.RED + "Must use gloves to interact with the furnace!");
                return;
            }
        }
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() != Material.PHANTOM_MEMBRANE))
            return;
        if (e.getClickedBlock().getType() == Material.SIGN || e.getClickedBlock().getType() == Material.WALL_SIGN) {
            if (signIsFreezer(e.getClickedBlock().getLocation()) == false) {
                p.sendMessage(ChatColor.RED + "This sign is not attached to a freezer! Freezers have a sign with " + ChatColor.BLACK + "[" + ChatColor.BLUE + "Freezer" + ChatColor.BLACK + "]" + ChatColor.RED
                        + " on it.");
                return;
            }
            e.setCancelled(true);
            onFreezerInteract(p, e);
            return;
        }
        if (e.getClickedBlock().getType() == Material.FURNACE ) {
            if (furnaceIsFurnace(e.getClickedBlock().getLocation()) == false) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "This furnace is not a cook's furnace! Cook's furnaces can be found only in the kitchen.");
                return;
            }
            onFurnaceInteract(p, e);
            return;
        }
        if (e.getClickedBlock().getType() == Material.IRON_TRAPDOOR ) {
            if (trapdoorIsFoodTray(e.getClickedBlock().getLocation()) == false) {
                p.sendMessage(ChatColor.RED + "This iron trapdoor is not a food tray! Cook's food trays can only be found in the kitchen.");
                return;
            }
            e.setCancelled(true);
            onFoodTrayInteract(p);
            return;
        }
    }

    private void onFreezerInteract(Player p, PlayerInteractEvent e) {
        ItemStack newItem = new ItemStack(Material.CHICKEN, 1);

        if (p.getInventory().getItem(7) == null) {
            p.getInventory().setItem(7, newItem);
            return;
        }
        if (p.getInventory().getItem(7) != null && p.getInventory().getItem(7).getType() != Material.CHICKEN) {
            ItemStack oldItem = null;
            oldItem = p.getInventory().getItem(7);
            p.getInventory().setItem(7, newItem);
            p.getInventory().addItem(oldItem);
            return;
        }
        if (p.getInventory().getItem(7).getType() == Material.CHICKEN) {
            if (p.getInventory().getItem(7).getAmount() >= 5) {
                p.sendMessage(ChatColor.RED + "You cannot hold more than 5 uncooked chicken at a time, put your uncooked chicken in the furnaces!");
                return;
            }
            p.getInventory().getItem(7).setAmount(p.getInventory().getItem(7).getAmount() + 1);
            return;
        }
    }

    private void onFurnaceInteract(Player p, PlayerInteractEvent e) {
        e.setCancelled(true);
        Furnace furn = getFurnace(e.getClickedBlock().getLocation());
        if (p.getInventory().getItem(7) != null && p.getInventory().getItem(7).getType() == Material.CHICKEN) {
            if (furn.getSmelting() != null) {
                p.sendMessage(ChatColor.RED + "Something is already cooking!");
                furn.setLit(p, true);
                return;
            }
            if (furn.getResult() != null) {
                newCookedChicken(p, furn);
                return;
            }
            furn.setSmelting(p, new ItemStack(Material.CHICKEN, 1), new ItemStack(Material.COOKED_CHICKEN, 1), 200L);
            p.getInventory().getItem(7).setAmount(p.getInventory().getItem(7).getAmount() - 1);
            return;
        }
        if (furn.getResult() == null) {
            if (furn.getSmelting() != null) {
                p.sendMessage(ChatColor.RED + "Chicken is still cooking!");
                furn.setLit(p, true);
            } else
                p.sendMessage(ChatColor.RED + "Need to get some more raw chicken from the freezer!");
            return;
        }
        newCookedChicken(p, furn);
    }

    private void onFoodTrayInteract(Player p) {
        if (p.getInventory().getItem(8) == null || p.getInventory().getItem(8).getType() != Material.COOKED_CHICKEN) {
            p.sendMessage(ChatColor.RED + "You do not have any cooked chicken to put on the food trays! To cook chicken, grab some from the freezer and put them into the furnaces. Once they are done cooking, " +
                    "take them out of the furnaces and put them on the food trays. Remember to have your gloves out!");
            return;
        }
        p.getInventory().getItem(8).setAmount(p.getInventory().getItem(8).getAmount() - 1);

        p.setLevel(p.getLevel() + 1);
        if (p.getLevel() == 10) {
            p.sendMessage(ChatColor.GOLD + "You have reached your cooking goal of 10!");
            p.setLevel(0);
            p.getInventory().remove(Material.PHANTOM_MEMBRANE);

            ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK, 1);
            ItemMeta meta = key.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Pink Key");
            meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "Use this key at the pink door somewhere in the prison!"));
            key.setItemMeta(meta);

            p.getInventory().addItem(key);
            p.sendMessage(ChatColor.GOLD + "You have been given the pink key to the pink door somewhere in the prison!");
        }
    }

    private void newCookedChicken(Player p, Furnace furn) {
        furn.clearInventory(p);
        removeFurnace(furn.getLocation());
        ItemStack newItem = new ItemStack(Material.COOKED_CHICKEN, 1);
        if (p.getInventory().getItem(8) == null) {
            p.getInventory().setItem(8, newItem);
            return;
        }
        if (p.getInventory().getItem(8) != null && p.getInventory().getItem(8).getType() != Material.COOKED_CHICKEN) {
            ItemStack oldItem = p.getInventory().getItem(8);
            p.getInventory().setItem(8, newItem);
            p.getInventory().addItem(oldItem);
            return;
        }
        p.getInventory().getItem(8).setAmount(p.getInventory().getItem(8).getAmount() + 1);
    }

    private Boolean signIsFreezer(Location loc) {
        if (new MapsConfig(map).getConfig().contains("freezers") && new MapsConfig(map).getConfig().getStringList("freezers").contains(ProCandrV4.plugin.getStringFLocation(loc, false)))
            return true;
        return false;
    }

    private Boolean furnaceIsFurnace(Location loc) {
        if (new MapsConfig(map).getConfig().contains("furnaces") && new MapsConfig(map).getConfig().getStringList("furnaces").contains(ProCandrV4.plugin.getStringFLocation(loc, false)))
            return true;
        return false;
    }

    private Boolean trapdoorIsFoodTray(Location loc) {
        if (new MapsConfig(map).getConfig().contains("foodTrays") && new MapsConfig(map).getConfig().getStringList("foodTrays").contains(ProCandrV4.plugin.getStringFLocation(loc, false)))
            return true;
        return false;
    }

    private void resetFurnaces() {
        MapsConfig MC = new MapsConfig(map);
        for (String furnace : MC.getConfig().getStringList("furnaces")) {
            Location key = ProCandrV4.plugin.getLocationFString(furnace, false);
            if (furnaces.containsKey(key)) {
                if (p != null && p.isOnline())
                    furnaces.get(key).setLit(p, false);
                furnaces.remove(key);
            }
        }
    }

    private Furnace getFurnace(Location loc) {
        if (furnaces.containsKey(loc))
            return furnaces.get(loc);
        furnaces.put(loc, new Furnace(loc));
        return furnaces.get(loc);
    }

    private void removeFurnace(Location loc) {
        if (furnaces.containsKey(loc))
            furnaces.remove(loc);
    }
}
