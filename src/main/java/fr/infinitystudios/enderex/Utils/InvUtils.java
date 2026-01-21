package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.EnderEX;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.EnderChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvUtils {

    private static final EnderEX plugin = EnderEX.getPlugin();
    private TextComponent cc(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
    public boolean regressionbool(){
        return plugin.getConfig().getBoolean("lose_items_on_regressions");
    }

    public static Map<Player, EnderChest> ecstorage = new HashMap<>();

    //FIRST = CHEST OWNER (content), SECOND = WHO OPENED IT (own player/admin)
    public static Map<UUID, UUID> openedChests = new HashMap<>();

    public Inventory CloneInventoryFromCache(Player p){
        Inventory inv;
        String temp;
        int level = FileUtils.getLevel(p);

        temp = plugin.getConfig().getString("title");
        temp = temp.replace("%level%", plugin.getConfig().getString("level" + level));
        TextComponent title = cc(temp);


        if(EnderCache.contains(p.getUniqueId())) {
            inv = EnderCache.get(p.getUniqueId());
        }
        else{
            return plugin.getServer().createInventory(new EnderExChestHolder(), 9*level, title);
        }
        Inventory clone = plugin.getServer().createInventory(new EnderExChestHolder(), 9*level, title);

        RegressionState regressionState = regressionCheck(p, inv);

        if(regressionState == RegressionState.PROTECTED){
            return null;
        }

        if(regressionState == RegressionState.LOST || regressionState == RegressionState.DOWNSIZED){
            for(int i = 0; i < 9*level; i++){
                clone.setItem(i, inv.getItem(i));
            }
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
        String playername = null;

        try {
            playername = plugin.getDatabaseManager().getUserByUUID(uuid).name();
        } catch (SQLException ex){
            plugin.getLogger().warning("Database error!");
            ex.printStackTrace();
        }

        if(playername == null) playername = "error";

        temp = temp.replace("%player%", playername);

        TextComponent title = cc(temp);

        Inventory clone = plugin.getServer().createInventory(new AdminEnderExChestHolder(uuid), inv.getSize(), title);
        clone.setContents(inv.getContents());

        return clone;
    }

    public Inventory GetChestInventoryAdmin(UUID uuid){
        //FileUtils fu = new FileUtils();
        Inventory inv;
        String temp;
        if(EnderCache.contains(uuid)) {
            inv = EnderCache.get(uuid);
        }

        else {
            return CloneInventoryFromFileAdmin(uuid);
        }

        if(inv == null){
            return null;
        }

        temp = plugin.getConfig().getString("titleadmin");
        String playername = null;

        try {
            playername = plugin.getDatabaseManager().getUserByUUID(uuid).name();
        } catch (SQLException ex){
            plugin.getLogger().warning("Database error!");
            ex.printStackTrace();
        }

        if(playername == null) playername = "error";

        temp = temp.replace("%player%", playername);

        TextComponent title = cc(temp);

        Inventory clone = Bukkit.createInventory(new AdminEnderExChestHolder(uuid), inv.getSize(), title);
        clone.setContents(inv.getContents());

        return clone;
    }

    public void TransferVanillaChestToEnderEx(CommandSender commandSender, UUID uuidtarget, Boolean force) {
        Player target = plugin.getServer().getPlayer(uuidtarget);
        ItemStack[] vanillaContent = target.getEnderChest().getContents();
        if (target.getEnderChest().isEmpty()) {
            new MessagesUtils().TransferNoEnderchest(commandSender);
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
                new MessagesUtils().TransferEnderchestNotEmpty(commandSender);
                return;
            }
        }

        if (plugin.getConfig().getBoolean("erase_vanilla_enderchest_on_transfer")) {
            target.getEnderChest().clear();
        }

        new MessagesUtils().TransferSuccesful(commandSender, target.getName());
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

            if (plugin.getConfig().getBoolean("erase_vanilla_enderchest_on_transfer")) {
                target.getEnderChest().clear();
            }
        }, 20L);
    }


    public void HandleClosingChest(Player p, Inventory inv){
        if(inv.getHolder(false) instanceof AdminEnderExChestHolder adminInv){
            UUID chestOwner = adminInv.getTarget();
            if(EnderCache.contains(chestOwner)){
                EnderCache.set(chestOwner, inv);
            }
            else{
                new FileUtils().savePlayerChest(chestOwner, inv);
            }

            new MessagesUtils().EnderchestSaved(p);
            openedChests.remove(chestOwner);
            return;
        }
        if(inv.getHolder(false) instanceof EnderExChestHolder){
            EnderCache.set(p.getUniqueId(), inv);
            openedChests.remove(p.getUniqueId());

            EnderChest enderChest = InvUtils.ecstorage.get(p);
            if(enderChest == null){return;}

            enderChest.close();
            ecstorage.remove(p);

            return;
        }
    }

    public boolean permCheck(Player p){
        if(getPluginMode() == PluginMode.SIMPLE){
            return true;
        }

        return p.hasPermission("enderex.chest.1") ||
                p.hasPermission("enderex.chest.2") ||
                p.hasPermission("enderex.chest.3") ||
                p.hasPermission("enderex.chest.4") ||
                p.hasPermission("enderex.chest.5") ||
                p.hasPermission("enderex.chest.6");
    }

    public RegressionState regressionCheck(Player p){
        if(EnderCache.contains(p.getUniqueId())) {
            Inventory inv = EnderCache.get(p.getUniqueId());
            RegressionState regressionState = regressionCheck(p, inv);
            if(regressionState == RegressionState.PROTECTED){
                new MessagesUtils().RegressionProtected(p);
            } else if(regressionState == RegressionState.LOST){
                new MessagesUtils().RegressionLost(p);
            }
            return regressionState;
        }
        return RegressionState.FALSE;
    }

    public RegressionState regressionCheck(Player p, Inventory inv) {
        int maxSlots = 9 * FileUtils.getLevel(p);

        if (inv.getSize() <= maxSlots) {
            return RegressionState.FALSE;
        }

        for (int i = maxSlots; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);

            if (item != null && !item.getType().isAir()) {
                return regressionbool()
                        ? RegressionState.LOST
                        : RegressionState.PROTECTED;
            }
        }

        return RegressionState.DOWNSIZED;
    }

    private PluginMode getPluginMode() {
        String configMode = plugin.getConfig().getString("plugin_mode", "SIMPLE");
        try {
            return PluginMode.valueOf(configMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid plugin_mode in config.yml. Defaulting to SIMPLE.");
            return PluginMode.SIMPLE;
        }
    }
}
