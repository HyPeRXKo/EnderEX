package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.InvUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InventoryOpen implements Listener {

    private static final EnderEX plugin = EnderEX.getPlugin();


    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            if (Material.ENDER_CHEST == block.getType() && Action.RIGHT_CLICK_BLOCK == e.getAction() && !e.isCancelled()) {
                e.setCancelled(true);
                e.getPlayer().performCommand("enderex");
                if (InvUtils.permcheck(e.getPlayer())) {
                    ((EnderChest) block.getState()).open();
                    InvUtils.ecstorage.put(e.getPlayer(), (EnderChest) block.getState());
                }
            }
        }

    }
}
