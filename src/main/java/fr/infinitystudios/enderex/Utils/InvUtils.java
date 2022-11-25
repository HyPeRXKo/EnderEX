package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.EnderChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvUtils {

    private static final EnderEX plugin = EnderEX.getPlugin();

    //public static EnderChest getEchest() {
        //return Echest;
    //}

    //public static void setEchest(EnderChest echest) {
        //Echest = echest;
    //}

    public static Map<Player, EnderChest> ecstorage = new HashMap<>();


    //private static EnderChest Echest;

    @SuppressWarnings("unchecked")
    public Inventory GetChestInventory(Player p,int Level){
        FileUtils fu = new FileUtils();
        FileConfiguration config = fu.GetChestConfig(p);
        if(config != null){
            ItemStack[] chestitems = ((List<ItemStack>) config.get("chest")).toArray(new ItemStack[0]);
            String temp;
            temp = plugin.getConfig().getString("title");
            temp = temp.replace("%level%", plugin.getConfig().getString("level" + Level));
            temp = ChatColor.translateAlternateColorCodes('&', temp);
            Inventory chest = Bukkit.createInventory(p, 9*Level, temp);
            chest.setContents(chestitems);
            return chest;
        }
        else{
            if(permcheck(p)){
                String temp;
                temp = plugin.getConfig().getString("title");
                temp = temp.replace("%level%", plugin.getConfig().getString("level" + Level));
                temp = ChatColor.translateAlternateColorCodes('&', temp);
                return Bukkit.createInventory(p, 9*Level, temp);
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public Inventory GetChestInventoryAdmin(Player p) {
        FileUtils fu = new FileUtils();
        FileConfiguration config = fu.GetChestConfig(p);
        if (config != null) {
            ItemStack[] chestitems = ((List<ItemStack>) config.get("chest")).toArray(new ItemStack[0]);
            String temp;
            Inventory chest;
            temp = plugin.getConfig().getString("titleadmin");
            temp = temp.replace("%player%", p.getName());
            temp = ChatColor.translateAlternateColorCodes('&', temp);
            if (p.hasPermission("enderex.chest.1")) {
                 chest = Bukkit.createInventory(p, 9, temp);
            } else if (p.hasPermission("enderex.chest.2")) {
                 chest = Bukkit.createInventory(p, 9 * 2, temp);
            } else if (p.hasPermission("enderex.chest.3")) {
                 chest = Bukkit.createInventory(p, 9 * 3, temp);
            } else if (p.hasPermission("enderex.chest.4")) {
                 chest = Bukkit.createInventory(p, 9 * 4, temp);
            } else if (p.hasPermission("enderex.chest.5")) {
                 chest = Bukkit.createInventory(p, 9 * 5, temp);
            } else if (p.hasPermission("enderex.chest.6")) {
                 chest = Bukkit.createInventory(p, 9 * 6, temp);
            }
            else return null;
            chest.setContents(chestitems);
            return chest;
        }
        return null;
    }

    public static boolean permcheck(Player p){
        return p.hasPermission("enderex.chest.1") ||
                p.hasPermission("enderex.chest.2") ||
                p.hasPermission("enderex.chest.3") ||
                p.hasPermission("enderex.chest.4") ||
                p.hasPermission("enderex.chest.5") ||
                p.hasPermission("enderex.chest.6");

    }
}
