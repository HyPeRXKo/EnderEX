package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.FileUtils;
import fr.infinitystudios.enderex.Utils.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.Collection;

public class InventoryClose implements Listener {

    private static final EnderEX plugin = EnderEX.getPlugin();

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent e){
        FileUtils fu = new FileUtils();
        String temp = plugin.getConfig().getString("title");
        temp = temp.replace("%level%", "");
        temp = ChatColor.translateAlternateColorCodes('&', temp);
        if(e.getView().getTitle().contains(temp)){
            fu.SaveChestConfig((Player) e.getPlayer(), e.getInventory());
            int openedec = 0;
            Player p = (Player) e.getPlayer();
            EnderChest ogec = InvUtils.ecstorage.get(p);
            Collection<EnderChest> values = InvUtils.ecstorage.values();
            ArrayList<EnderChest> eclist = new ArrayList<>(values);
            for(int i = 0; i < eclist.size(); i++){
                if(eclist.get(i).equals(ogec)){
                    openedec++;
                }
            }
            if(openedec == 0){plugin.getLogger().severe("ERROR IN THE EC STORAGE PLUGIN, CONTACT HYPER IMMEDIATLY");}
            if(openedec == 1){ogec.close(); InvUtils.ecstorage.remove(p);}
            if(openedec >= 2){InvUtils.ecstorage.remove(p);}
        }
    }
}
