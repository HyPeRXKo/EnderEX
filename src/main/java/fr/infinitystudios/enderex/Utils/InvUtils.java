package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.Chests.EnderCache;
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
import java.util.UUID;

public class InvUtils {

    private static final EnderEX plugin = EnderEX.getPlugin();

    //public static EnderChest getEchest() {
        //return Echest;
    //}

    //public static void setEchest(EnderChest echest) {
        //Echest = echest;
    //}

    public static Map<Player, EnderChest> ecstorage = new HashMap<>();
    public static Map<Inventory, UUID> adminstorage = new HashMap<>();

    public static boolean regressionbool(){
       return plugin.getConfig().getBoolean("loseitemsonregression");
    }


    //private static EnderChest Echest;

    /*
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

     */


    public Inventory CloneInventoryFromCache(Player p){
        Inventory inv;
        FileUtils fu = new FileUtils();
        String temp;
        int level = fu.getLevel(p);
        temp = plugin.getConfig().getString("title");
        temp = temp.replace("%level%", plugin.getConfig().getString("level" + level));
        temp = ChatColor.translateAlternateColorCodes('&', temp);
        if(EnderCache.contains(p.getUniqueId())) {
            inv = EnderCache.get(p.getUniqueId());
        }
        else{
            return Bukkit.createInventory(null, 9*level, temp);
        }
        Inventory clone = Bukkit.createInventory(null, 9*level, temp);
        if(inv.getSize() > clone.getSize()){
            if(!regressionbool()){
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou cannot open your enderchest because you lost your previous level."));
                return null;
            }
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou lost some items due to your level regression."));
            for(int i = 0; i < 9*level; i++){
                clone.setItem(i, inv.getItem(i));
            }
            //plugin.getLogger().info(fu.getLevel(p) + " " + clone.getSize());
            return clone;
        }
        clone.setContents(inv.getContents());
        //plugin.getLogger().info(fu.getLevel(p) + " " + clone.getSize());
        return clone;
    }
    public Inventory CloneInventoryFromFileAdmin(UUID uuid){
        FileUtils fu = new FileUtils();
        Inventory inv;
        String temp;
        inv = fu.loadPlayerChest(uuid);
        if(inv == null){
            return null;
        }

        temp = plugin.getConfig().getString("titleadmin");
        temp = temp.replace("%player%", UsermapCache.getname(uuid));
        temp = ChatColor.translateAlternateColorCodes('&', temp);

        Inventory clone = Bukkit.createInventory(null, inv.getSize(), temp);
        clone.setContents(inv.getContents());

        return clone;
    }

    public Inventory LevelzeroInventory(){
        return Bukkit.createInventory(null, 9, plugin.getConfig().getString("title"));
    }

    public Inventory GetChestInventoryAdmin(UUID uuid){
        //FileUtils fu = new FileUtils();
        Inventory inv;
        if(EnderCache.contains(uuid)) {
            inv = EnderCache.get(uuid);
        }
        else {
            return CloneInventoryFromFileAdmin(uuid);
        }

        if(inv == null){
            return null;
        }

        String temp;
        temp = plugin.getConfig().getString("titleadmin");
        temp = temp.replace("%player%", UsermapCache.getname(uuid));
        temp = ChatColor.translateAlternateColorCodes('&', temp);

        Inventory clone = Bukkit.createInventory(null, inv.getSize(), temp);
        clone.setContents(inv.getContents());

        return clone;
    }

    public void TransferVanillaChestToEnderEx(Player commandSender, UUID uuidtarget, Boolean force) {
        Player target = plugin.getServer().getPlayer(uuidtarget);
        ItemStack[] vanillaContent = target.getEnderChest().getContents();
        if (target.getEnderChest().isEmpty()) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cThis player has no items in their enderchest."));
            return;
        }

        Inventory inv = CloneInventoryFromCache(target);
        if (inv.isEmpty()) {
            inv.setContents(vanillaContent);
            EnderCache.set(uuidtarget, inv);
        }
        else {
            if (force) {
                inv.setContents(vanillaContent);
                EnderCache.set(uuidtarget, inv);
            }
            else {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cThis player already has items in their EnderEX chest."));
                return;
            }
        }

        if (plugin.getConfig().getBoolean("erasevanillaenderchestontransfer")) {
            target.getEnderChest().clear();
        }

        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &aYou have transferred the items from " + target.getName() + "'s enderchest to your his EnderEX chest."));
        return;
    }

    public void TransferVanillaChestToEnderExOnLogin(Player target) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            UUID uuidtarget = target.getUniqueId();
            ItemStack[] vanillaContent = target.getEnderChest().getContents();
            if (target.getEnderChest().isEmpty()) {
                return;
            }

            Inventory inv = CloneInventoryFromCache(target);
            if (inv.isEmpty()) {
                inv.setContents(vanillaContent);
                EnderCache.set(uuidtarget, inv);
            }

            if (plugin.getConfig().getBoolean("erasevanillaenderchestontransfer")) {
                target.getEnderChest().clear();
            }
        }, 20L);
    }

    /*
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

     */

    public static boolean permcheck(Player p){
        return p.hasPermission("enderex.chest.1") ||
                p.hasPermission("enderex.chest.2") ||
                p.hasPermission("enderex.chest.3") ||
                p.hasPermission("enderex.chest.4") ||
                p.hasPermission("enderex.chest.5") ||
                p.hasPermission("enderex.chest.6");
    }
}
