package fr.infinitystudios.enderex.Commands;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class EnderEXCommand implements TabExecutor {

    private static final EnderEX plugin = EnderEX.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        InvUtils iu = new InvUtils();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                if (p.hasPermission("enderex.chest.1")
                        || p.hasPermission("enderex.chest.2")
                        || p.hasPermission("enderex.chest.3")
                        || p.hasPermission("enderex.chest.4")
                        || p.hasPermission("enderex.chest.5")
                        || p.hasPermission("enderex.chest.6")) {
                    Inventory chest = iu.CloneInventoryFromCache(p);
                    if (chest != null) p.openInventory(chest);
                }
                else{p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou don't have an enderchest yet."));}
            }
//        else if (sender instanceof ConsoleCommandSender) {}
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
