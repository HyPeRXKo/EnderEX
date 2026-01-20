package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.Utils.AdminEnderExChestHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {

    @EventHandler
    public  void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().getHolder(false) instanceof AdminEnderExChestHolder adminInv){
            Player p = (Player) e.getWhoClicked();
            if(!p.hasPermission("enderex.edit") || !p.isOp()){
                e.setCancelled(true);
                return;
            }
        }
    }
}
