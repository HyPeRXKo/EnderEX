package fr.infinitystudios.enderex.Utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EnderExChestHolder implements InventoryHolder {

    @Override
    public Inventory getInventory() {
        return null; // OBLIGATOIRE mais jamais utilisé
    }

}
