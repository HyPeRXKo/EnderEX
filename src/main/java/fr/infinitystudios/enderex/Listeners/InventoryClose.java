package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.FileUtils;
import fr.infinitystudios.enderex.Utils.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class InventoryClose implements Listener {

    private static final EnderEX plugin = EnderEX.getPlugin();

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent e){
        if(InvUtils.adminstorage.containsKey(e.getInventory())){
            UUID playeruuid = InvUtils.adminstorage.get(e.getInventory());
            if(EnderCache.contains(playeruuid)){
                EnderCache.set(playeruuid, e.getInventory());
                //plugin.getLogger().info("Adminchest saved.");
            }
            else{
                new FileUtils().savePlayerChest(playeruuid, e.getInventory());
                //plugin.getLogger().info("Adminchest FILE saved.");
            }

            //EnderCache.debug();
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &aEnderchest saved."));
            return;
        }

        String temp = plugin.getConfig().getString("title");
        temp = temp.replace("%level%", "");
        temp = ChatColor.translateAlternateColorCodes('&', temp);
        if(e.getView().getTitle().contains(temp)){
            Inventory inv = e.getView().getTopInventory();
            EnderCache.set(e.getPlayer().getUniqueId(), inv);
            int openedec = 0;
            Player p = (Player) e.getPlayer();
            EnderChest ogec = InvUtils.ecstorage.get(p);
            Collection<EnderChest> values = InvUtils.ecstorage.values();
            ArrayList<EnderChest> eclist = new ArrayList<>(values);
            for (EnderChest enderChest : eclist) {
                if (enderChest.equals(ogec)) {
                    openedec++;
                }
            }
            //if(openedec == 0){plugin.getLogger().severe("ERROR IN THE EC STORAGE PLUGIN, CONTACT HYPER IMMEDIATELY");}
            if(openedec >= 1){ogec.close(); InvUtils.ecstorage.remove(p);}

            return;
        }

    }
}
