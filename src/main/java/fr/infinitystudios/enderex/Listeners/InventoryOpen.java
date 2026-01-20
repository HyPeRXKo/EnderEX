package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.InvUtils;
import fr.infinitystudios.enderex.Utils.MessagesUtils;
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


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(plugin.getConfig().getBoolean("disable_physical_enderchest_switch_to_enderex")){return;}
        if(plugin.getConfig().getBoolean("need_permission_for_physical_opening") && !p.hasPermission("enderex.physicalopen")){
            new MessagesUtils().NoPermissionPhysical(p);
            return;
        }

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        if (block.getType() != Material.ENDER_CHEST) return;

        if(((EnderChest) block.getState()).isBlocked()) {return;}

        e.setCancelled(true);
        InvUtils iu = new  InvUtils();

        if(InvUtils.openedChests.containsKey(p.getUniqueId())){
            new MessagesUtils().ChestAlreadyOpened(p);
            return;
        }

        if(!iu.permCheck(p)){
            new MessagesUtils().NoEnderchest(p);
            return;
        }


        Inventory chest = iu.CloneInventoryFromCache(p);
        if(chest == null){return;}

        p.openInventory(chest);
        ((EnderChest) block.getState()).open();
        InvUtils.ecstorage.put(e.getPlayer(), (EnderChest) block.getState());
        InvUtils.openedChests.put(p.getUniqueId(), p.getUniqueId());
    }
}
