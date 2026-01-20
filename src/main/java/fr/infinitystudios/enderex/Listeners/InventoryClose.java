package fr.infinitystudios.enderex.Listeners;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.*;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent e) {
        new InvUtils().HandleClosingChest((Player) e.getPlayer(), e.getInventory());
    }

}
