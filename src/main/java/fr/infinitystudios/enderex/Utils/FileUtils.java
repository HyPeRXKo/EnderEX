package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;
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
                        UsermapCache.put(name, UUID.fromString(uuidStr));
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
}
