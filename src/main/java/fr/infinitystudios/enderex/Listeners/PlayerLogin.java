package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.FileUtils;
import fr.infinitystudios.enderex.Utils.InvUtils;
import fr.infinitystudios.enderex.Utils.UsermapCache;
import org.bukkit.entity.Player;
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
        if(invfromfile != null){
            EnderCache.set(e.getPlayer().getUniqueId(), invfromfile);
        }

        if(plugin.getConfig().getBoolean("transferenderchesttoenderexonlogin")) {
            Player p = e.getPlayer();
            if(p.hasPermission("enderex.chest.3") || p.hasPermission("enderex.chest.4") || p.hasPermission("enderex.chest.5") || p.hasPermission("enderex.chest.6")) {
                new InvUtils().TransferVanillaChestToEnderExOnLogin(e.getPlayer());
            }
        }
        //plugin.getLogger().info("still loaded for " + e.getPlayer().getName());
    }


    @EventHandler
    private void onPlayerLogout(PlayerQuitEvent e) {
        if(!EnderCache.contains(e.getPlayer().getUniqueId())){return;}
        EnderCache.save(e.getPlayer().getUniqueId());
        EnderCache.remove(e.getPlayer().getUniqueId());
    }
}
