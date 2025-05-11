package fr.infinitystudios.enderex.Chests;

import fr.infinitystudios.enderex.EnderEX;
import fr.infinitystudios.enderex.Utils.FileUtils;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;

public class EnderCache {

    private static EnderEX plugin = EnderEX.getPlugin();

    private static final Map<UUID, Inventory> CACHE = new java.util.HashMap<>();

    public static boolean contains(UUID uuid) {
        return CACHE.containsKey(uuid);
    }

    public static Inventory get(UUID uuid) {
        return CACHE.get(uuid);
    }

    public static void set(UUID uuid, Inventory inv) {
        CACHE.put(uuid, inv);
    }

    public static void remove(UUID uuid) {
        CACHE.remove(uuid);
    }

    public static void save(UUID uuid) {
        new FileUtils().savePlayerChest(uuid, CACHE.get(uuid));
    }

    public static void saveAll() {
        for (Map.Entry<UUID, Inventory> entry : CACHE.entrySet()) {
            save(entry.getKey());
        }
    }

    /*
    public static void debug() {
        for (Map.Entry<UUID, Inventory> entry : CACHE.entrySet()) {
            plugin.getLogger().info(entry.getKey().toString() + ": " + entry.getValue().getSize() + " slots");
        }
    }
     */

}
