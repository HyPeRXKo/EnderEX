package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class FileUtils {
    private static final EnderEX plugin = EnderEX.getPlugin();

    private static final FileConfiguration userMapConfig = new YamlConfiguration();
    private static final File userMapFile = new File(plugin.getDataFolder(), "usermap.yml");

    private TextComponent cc(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

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

            // Deserialisation de l'inventaire (Conversion si besoin)
            if(!cfg.contains("dataversion")){
                cfg.set("dataversion", 2);
                Inventory oldinv = InventorySerializer.OLDfromBase64(base64);
                base64 = InventorySerializer.inventoryToNBTBase64(oldinv);
                cfg.set("chest", base64);
            }

            Inventory loaded = InventorySerializer.nbtBase64ToInventory(base64);

            int level = getLevel(loaded.getSize());

            // Crée un nouvel Inventory aux bonnes dimensions/titre et copie le contenu
            Inventory inv = plugin.getServer().createInventory(null, 9 * level, buildTitle(level));
            inv.setContents(loaded.getContents());
            return inv;

        } catch (IOException | ClassNotFoundException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Error load chest for " + uuid.toString() + ": " + e.getMessage());
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
            String base64 = InventorySerializer.inventoryToNBTBase64(inventory);

            // Stocke dans le YML
            FileConfiguration cfg = new YamlConfiguration();
            if(!cfg.contains("dataversion")) {
                cfg.set("dataversion", 2);
            }
            cfg.set("chest", base64);
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error save chest for " + uuid.toString() + ": " + e.getMessage());
        }
    }

    public void convertUserMap() {
        if(!userMapFile.exists()) return;
        try {
            userMapConfig.load(userMapFile);
            if (userMapConfig.contains("usermap")) {
                Map<String, Object> raw = userMapConfig.getConfigurationSection("usermap").getValues(false);
                for (Map.Entry<String, Object> entry : raw.entrySet()) {
                    String name = entry.getKey();
                    String uuidStr = entry.getValue().toString();
                    UUID uuid = UUID.fromString(uuidStr);

                    Platform platform = uuidStr.startsWith("00000000-0000-0000") ? Platform.BEDROCK : Platform.JAVA;

                    UserEntry userEntry = null;


                    try {
                       userEntry = plugin.getDatabaseManager().getUserByUUID(uuid);
                    } catch (SQLException e) {
                        plugin.getLogger().warning("Error accessing the database.");
                    }
                    
                    if(userEntry != null){continue;}
                    
                    try {
                        plugin.getDatabaseManager().insertUser(name, uuid, platform);
                    } catch (IllegalArgumentException | SQLException ex) {
                        plugin.getLogger().warning("Error in the conversion for user " + name + ": " + uuidStr);
                    }
                }
            }
            plugin.getLogger().info("Old UserMap converted to the new database system.");
            boolean trydeleting = userMapFile.delete();
            if(!trydeleting) {
                plugin.getLogger().warning("Failed to delete old usermap.yml");
            }
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to convert usermap.yml: " + e.getMessage());
        }
    }

    /** Construit le titre en remplaçant %level% */
    private TextComponent buildTitle(int level) {
        String template = plugin.getConfig().getString("title", "EnderEx - Level %level%");
        template = template.replace("%level%", plugin.getConfig().getString("level" + level, String.valueOf(level)));
        return cc(template);
    }

    public static Integer getLevel(Player p) {
        if(getPluginMode() == PluginMode.SIMPLE){
            return plugin.getConfig().getInt("simple_rows", 3);
        }

        if (p.hasPermission("enderex.chest.6")) return 6;
        else if (p.hasPermission("enderex.chest.5")) return 5;
        else if (p.hasPermission("enderex.chest.4")) return 4;
        else if (p.hasPermission("enderex.chest.3")) return 3;
        else if (p.hasPermission("enderex.chest.2")) return 2;
        else if (p.hasPermission("enderex.chest.1")) return 1;
        return 0;
    }
    public Integer getLevel(Integer rows) {
        return switch (rows) {
            case 9 -> 1;
            case 18 -> 2;
            case 27 -> 3;
            case 36 -> 4;
            case 45 -> 5;
            case 54 -> 6;
            default -> 0;
        };
    }

    public static PluginMode getPluginMode() {
        String configMode = plugin.getConfig().getString("plugin_mode", "SIMPLE");
        try {
            return PluginMode.valueOf(configMode.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid plugin_mode in config.yml. Defaulting to SIMPLE.");
            return PluginMode.SIMPLE;
        }
    }
}
