package fr.infinitystudios.enderex.Utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private YamlConfiguration loadDefaultConfig() {
        try (InputStream is = plugin.getResource("config.yml")) {
            if (is == null) throw new IllegalStateException("config.yml missing in jar");
            return YamlConfiguration.loadConfiguration(
                    new InputStreamReader(is, StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //NEW

    private static final Map<String, String> RENAME_V1_TO_V2 = new LinkedHashMap<>() {{
        put("loseitemsonregression", "lose_items_on_regressions");
        put("consolesavemessages", "console_save_messages");
        put("transferenderchesttoenderexonlogin", "transfer_vanilla_enderchest_to_enderex_on_login");
        put("disablephysicalenderchestswitchtoenderex", "disable_physical_enderchest_switch_to_enderex");
        put("erasevanillaenderchestontransfer", "erase_vanilla_enderchest_on_transfer");
    }};

    public void loadAndUpdateConfig() {
        File file = new File(plugin.getDataFolder(), "config.yml");

        // Première installation
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
            plugin.reloadConfig();
            return;
        }

        // Ancienne config (valeurs utilisateur)
        YamlConfiguration oldCfg = YamlConfiguration.loadConfiguration(file);

        // Nouvelle config (structure + commentaires)
        YamlConfiguration newCfg = loadDefaultConfig();

        int oldVersion = oldCfg.getInt("configversion", 1);

        if (oldVersion == 1) {
            migrateByRenameMap(oldCfg, newCfg, RENAME_V1_TO_V2);
            copyUnchangedKeysV1(oldCfg, newCfg);
            newCfg.set("plugin_mode", "advanced");
            newCfg.set("configversion", 2);

            try {
                newCfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        plugin.reloadConfig();
    }

    private void migrateByRenameMap(YamlConfiguration oldCfg, YamlConfiguration newCfg, Map<String, String> renameMap) {
        for (Map.Entry<String, String> entry : renameMap.entrySet()) {
            String oldKey = entry.getKey();
            String newKey = entry.getValue();

            if (oldCfg.contains(oldKey)) {
                newCfg.set(newKey, oldCfg.get(oldKey));
            }
        }
    }

    private void copyUnchangedKeysV1(YamlConfiguration oldCfg, YamlConfiguration newCfg) {
        for (String key : oldCfg.getKeys(false)) {

            // On ignore la version
            if (key.equals("configversion")) continue;

            // Si la clé a été renommée → déjà gérée
            if (RENAME_V1_TO_V2.containsKey(key)) continue;

            // Si la clé existe dans la nouvelle config → on écrase avec la valeur utilisateur
            if (newCfg.contains(key)) {
                newCfg.set(key, oldCfg.get(key));
            }
        }
    }


}
