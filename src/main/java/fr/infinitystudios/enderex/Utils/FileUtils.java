package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class FileUtils {
    private static final EnderEX plugin = EnderEX.getPlugin();

    private static final FileConfiguration userMapConfig = new YamlConfiguration();
    private static final File userMapFile = new File(plugin.getDataFolder(), "usermap.yml");

    /*
    public FileConfiguration GetChestConfig(Player p) {
        FileConfiguration invdata;
        File invdatafile = new File(plugin.getDataFolder() + "/data/", p.getUniqueId() + ".yml");
        if (!invdatafile.exists()) {
            return null;
        }
        invdata = new YamlConfiguration();
        try {
            invdata.load(invdatafile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return invdata;
    }

     */

    /*
    public void SaveChestConfig(Player p, Inventory chest) {
        FileConfiguration invdata;
        File invdatafile = new File(plugin.getDataFolder() + "/data/", p.getUniqueId() + ".yml");
        if(!invdatafile.exists()) invdatafile.getParentFile().mkdirs();
        invdata = new YamlConfiguration();
        invdata.set("player", p.getName());
        invdata.set("chest", chest.getContents());
        try {
            invdata.save(invdatafile);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
     */

    public Inventory loadPlayerChest(UUID uuid) {
        try {
            File file = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
            if (!file.exists()) {
                return null;
            }

            FileConfiguration cfg = new YamlConfiguration();
            cfg.load(file);

            String base64 = cfg.getString("chest", "");
            if (base64.isEmpty()) {
                return null;
            }

            // Désérialise et récupère un Inventory
            Inventory loaded = InventorySerializer.fromBase64(base64);

            int level = getLevel(loaded.getSize());

            // Crée un nouvel Inventory aux bonnes dimensions/titre et copie le contenu
            Inventory inv = Bukkit.createInventory(null, 9 * level, buildTitle(level));
            inv.setContents(loaded.getContents());
            return inv;

        } catch (IOException | ClassNotFoundException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Erreur load chest pour " + UsermapCache.getname(uuid) + ": " + e.getMessage());
            // Fallback : inventaire vide
            return null;
        }
    }


    /** Sauvegarde l'inventaire passé en Base64 dans data/<UUID>.yml, clé "chest" */
    public void savePlayerChest(UUID uuid, Inventory inventory) {
        try {
            // Prépare le fichier data/<UUID>.yml
            File dir = new File(plugin.getDataFolder(), "data");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, uuid + ".yml");

            // Sérialise l'inventaire
            String base64 = InventorySerializer.toBase64(inventory);

            // Stocke dans le YML
            FileConfiguration cfg = new YamlConfiguration();
            cfg.set("chest", base64);
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("error save chest for " + UsermapCache.getname(uuid) + ": " + e.getMessage());
        }
    }

    public void saveUserMap(Map<String, UUID> USER_MAP) {
        if(!userMapFile.exists()) userMapFile.getParentFile().mkdirs();
        try {
            userMapConfig.set("usermap", null);
            for (Map.Entry<String, UUID> entry : USER_MAP.entrySet()) {
                userMapConfig.set("usermap." + entry.getKey(), entry.getValue().toString());
            }
            userMapConfig.save(userMapFile);
            plugin.getLogger().info("UserMap saved, entries: " + USER_MAP.size());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save usermap.yml: " + e.getMessage());
        }
    }

    public void loadUserMap() {
        if(!userMapFile.exists()) return;
        try {
            userMapConfig.load(userMapFile);
            if (userMapConfig.contains("usermap")) {
                Map<String, Object> raw = userMapConfig.getConfigurationSection("usermap").getValues(false);
                for (Map.Entry<String, Object> entry : raw.entrySet()) {
                    String name = entry.getKey();
                    String uuidStr = entry.getValue().toString();
                    try {
                        UsermapCache.putname(name, UUID.fromString(uuidStr));
                    } catch (IllegalArgumentException ex) {
                        plugin.getLogger().warning("Invalid UUID for user " + name + ": " + uuidStr);
                    }
                }
            }
            plugin.getLogger().info("UserMap loaded, entries: " + UsermapCache.usermapCACHE.size());
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to load usermap.yml: " + e.getMessage());
        }
    }

    /** Construit le titre en remplaçant %level% */
    private String buildTitle(int level) {
        String template = plugin.getConfig().getString("title", "EnderEx - Level %level%");
        template = template.replace("%level%", plugin.getConfig().getString("level" + level, String.valueOf(level)));
        return ChatColor.translateAlternateColorCodes('&', template);
    }

    public Integer getLevel(Player p) {
        if (p.hasPermission("enderex.level.1")) return 1;
        if (p.hasPermission("enderex.level.2")) return 2;
        if (p.hasPermission("enderex.level.3")) return 3;
        if (p.hasPermission("enderex.level.4")) return 4;
        if (p.hasPermission("enderex.level.5")) return 5;
        if (p.hasPermission("enderex.level.6")) return 6;
        return 0;
    }
    public Integer getLevel(Integer rows) {
        if (rows == 9) return 1;
        if (rows == 18) return 2;
        if (rows == 27) return 3;
        if (rows == 36) return 4;
        if (rows == 45) return 5;
        if (rows == 54) return 6;
        return 0;
    }
}
