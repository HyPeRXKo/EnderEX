package fr.infinitystudios.enderex.Commands;

import fr.infinitystudios.enderex.Utils.InvUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class EnderEXSeeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String temp;
        InvUtils iu = new InvUtils();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("enderex.chest.admin")) {
                temp = "&7[&dEnderEX&7] &cYou do not have the permission to use this command.";
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', temp));
            }
            if (p.hasPermission("enderex.chest.admin") && args.length != 1) {
                temp = "&7[&dEnderEX&7] &cYou need to specify a player.";
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', temp));
            }
            if (args.length == 1) {
                Player arg = Bukkit.getPlayerExact(args[0]);
                if (arg != null) {
                    Inventory chest = iu.GetChestInventoryAdmin(arg);
                    if (chest != null) p.openInventory(chest);
                    else{temp = "&7[&dEnderEX&7] &cThe player doesn't have an enderchest";p.sendMessage(ChatColor.translateAlternateColorCodes('&', temp));}
                }
                else{temp = "&7[&dEnderEX&7] &cThe player you specified is incorrect";p.sendMessage(ChatColor.translateAlternateColorCodes('&', temp));}
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
