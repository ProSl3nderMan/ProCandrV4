package me.prosl3nderman.procandrv4.Commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.prosl3nderman.procandrv4.ProCandrV4;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be done by a player.");
            return true;
        }
        Player p = (Player) commandSender;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("main");
        p.sendMessage(ChatColor.GOLD + "You have been teleported to main server!");
        p.sendPluginMessage(ProCandrV4.plugin, "BungeeCord", out.toByteArray());
        return true;
    }
}
