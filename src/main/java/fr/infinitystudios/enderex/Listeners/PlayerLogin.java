package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.FileUtils;
import fr.infinitystudios.enderex.Utils.UsermapCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class PlayerLogin implements Listener {

    private EnderEX plugin = EnderEX.getPlugin();

    @EventHandler
    private void onPlayerLogin(PlayerLoginEvent e) {
        if(!UsermapCache.containsname(e.getPlayer().getName())){
            UsermapCache.putname(e.getPlayer().getName(), e.getPlayer().getUniqueId());
            UsermapCache.putuuid(e.getPlayer().getUniqueId(), e.getPlayer().getName());
        }

        FileUtils fu = new FileUtils();
        int level = fu.getLevel(e.getPlayer());
        if(level == 0){
            return;
        }
        Inventory invfromfile = new FileUtils().loadPlayerChest(e.getPlayer().getUniqueId());
        if(invfromfile == null){
            return;
        }
        EnderCache.set(e.getPlayer().getUniqueId(), invfromfile);
        //plugin.getLogger().info("still loaded for " + e.getPlayer().getName());
    }


    @EventHandler
    private void onPlayerLogout(PlayerQuitEvent e) {
        if(!EnderCache.contains(e.getPlayer().getUniqueId())){return;}
        EnderCache.save(e.getPlayer().getUniqueId());
        EnderCache.remove(e.getPlayer().getUniqueId());
    }
}
