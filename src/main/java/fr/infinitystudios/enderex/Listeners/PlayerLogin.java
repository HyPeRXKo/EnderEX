package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.UUID;

import static fr.infinitystudios.enderex.Utils.InvUtils.openedChests;

public class PlayerLogin implements Listener {

    private EnderEX plugin = EnderEX.getPlugin();

    @EventHandler
    private void onPlayerLogin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        UUID uuid = p.getUniqueId();
        String name = p.getName();

        Platform platform = plugin.playerPlatformResolver(uuid);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                DatabaseManager db = plugin.getDatabaseManager();
                UserEntry entry = db.getUserByUUID(uuid);
                if (entry == null) {
                    db.insertUser(name, uuid, platform);
                }
                else {
                    if(entry.uuid().equals(uuid) && entry.platform().equals(platform)) {
                        db.updateName(entry.id(), name);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });


        int level = FileUtils.getLevel(e.getPlayer());
        if(level == 0){
            return;
        }


        Inventory invfromfile = new FileUtils().loadPlayerChest(e.getPlayer().getUniqueId());
        if(invfromfile != null){
            EnderCache.set(e.getPlayer().getUniqueId(), invfromfile);
        }

        if(plugin.getConfig().getBoolean("transfer_vanilla_enderchest_to_enderex_on_login")) {
            if(getPluginMode() == PluginMode.SIMPLE && plugin.getConfig().getInt("simple_rows", 3) >= 3){
                new InvUtils().TransferVanillaChestToEnderExOnLogin(e.getPlayer());
            }

            if(getPluginMode() == PluginMode.ADVANCED){
                if(p.hasPermission("enderex.chest.3") || p.hasPermission("enderex.chest.4") || p.hasPermission("enderex.chest.5") || p.hasPermission("enderex.chest.6")) {
                    new InvUtils().TransferVanillaChestToEnderExOnLogin(e.getPlayer());
                }
            }
        }
        //plugin.getLogger().info("still loaded for " + e.getPlayer().getName());
    }

    @EventHandler
    private void onPlayerLogout(PlayerQuitEvent e) {

        openedChests.values().removeIf(uuid -> uuid.equals(e.getPlayer().getUniqueId()));

        if(!EnderCache.contains(e.getPlayer().getUniqueId())){return;}
        EnderCache.save(e.getPlayer().getUniqueId());
        EnderCache.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerKick(PlayerKickEvent e) {

        openedChests.values().removeIf(uuid -> uuid.equals(e.getPlayer().getUniqueId()));

        if(!EnderCache.contains(e.getPlayer().getUniqueId())){return;}
        EnderCache.save(e.getPlayer().getUniqueId());
        EnderCache.remove(e.getPlayer().getUniqueId());
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
