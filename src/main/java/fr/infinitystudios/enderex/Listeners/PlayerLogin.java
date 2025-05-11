package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.UsermapCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLogin implements Listener {

    private EnderEX plugin = EnderEX.getPlugin();

    @EventHandler
    private void onPlayerLogin(PlayerLoginEvent e) {
        if(UsermapCache.contains(e.getPlayer().getName())) return;
        UsermapCache.put(e.getPlayer().getName(), e.getPlayer().getUniqueId());
    }
}
