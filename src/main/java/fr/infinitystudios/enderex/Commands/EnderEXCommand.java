package fr.infinitystudios.enderex.Commands;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.InvUtils;
import fr.infinitystudios.enderex.Utils.UsermapCache;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

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
                    if (chest != null) {
                        p.openInventory(chest);
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou don't have an enderchest yet."));
                }
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("transfer")) {
                if (!p.hasPermission("enderex.chest.admin") || !p.isOp()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou don't have the permission to use this command."));
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou need to specify a player."));
                }
            }
            if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("transfer")) {
                if (!p.hasPermission("enderex.chest.admin") || !p.isOp()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou don't have the permission to use this command."));
                } else {
                    UUID targetplayer = UsermapCache.getuuid(args[1]);
                    if (targetplayer != null) {
                        Player target = p.getServer().getPlayer(targetplayer);
                        if (target == null) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cThe player you specified is incorrect."));
                            return true;
                        } else {
                            if (!target.hasPermission("enderex.chest.3") || !target.hasPermission("enderex.chest.4") || !target.hasPermission("enderex.chest.5") || !target.hasPermission("enderex.chest.6")) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cThe player don't have a big enough permission."));
                                return true;
                            } else {
                                if (args.length == 3 && args[2].equalsIgnoreCase("force")) {
                                    iu.TransferVanillaChestToEnderEx(p, targetplayer, true);
                                }
                                else {
                                    iu.TransferVanillaChestToEnderEx(p, targetplayer, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
        //else if (sender instanceof ConsoleCommandSender) {}
    }

    // args.length = +1 to index
    //[enderex] args[0/1]    args[1/2]   args[2/3]
    //enderex   transfer    (player)  (force)          (player)



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 0) {
            return List.of("transfer");
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("transfer")) {
            return plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("transfer")) {
            return List.of("force");
        }
        return null;
    }
}
