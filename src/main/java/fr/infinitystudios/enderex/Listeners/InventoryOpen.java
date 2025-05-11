package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class InventoryOpen implements Listener {

    private static final EnderEX plugin = EnderEX.getPlugin();


    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            if (block.getType() == Material.ENDER_CHEST && Action.RIGHT_CLICK_BLOCK == e.getAction() && !e.isCancelled()) {
                e.setCancelled(true);
                if (InvUtils.permcheck(e.getPlayer())) {
                    Player p = e.getPlayer();
                    InvUtils iu = new InvUtils();
                    Inventory chest = iu.CloneInventoryFromCache(p);
                    if (chest != null){
                        p.openInventory(chest);
                        ((EnderChest) block.getState()).open();
                        InvUtils.ecstorage.put(e.getPlayer(), (EnderChest) block.getState());
                    }
                }
                else{e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&dEnderEX&7] &cYou don't have an enderchest yet."));}
            }
        }
    }
}
