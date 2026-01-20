package fr.infinitystudios.enderex.Utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class AdminEnderExChestHolder implements InventoryHolder {

    private UUID target;

    public AdminEnderExChestHolder(UUID target) {
        this.target = target;
    }

    public UUID getTarget() {
        return target;
    }

    @Override
    public Inventory getInventory()
    {
        return null;
    }
}
