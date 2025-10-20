package fr.infinitystudios.enderex.Utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class ConfigManager {

    private final JavaPlugin plugin;
    private final File configFile;
    private final Yaml yaml = new Yaml();

    // Renommage automatique des clés (ancienne -> nouvelle)
    private final Map<String, String> renameMap = new LinkedHashMap<>() {{
        put("loseitemsonregression", "lose_items_on_regressions");
        put("consolesavemessages", "console_save_messages");
        put("transferenderchesttoenderexonlogin", "transfer_vanilla_enderchest_to_enderex_on_login");
        put("disablephysicalenderchestswitchtoenderex", "disable_physical_enderchest_switch_to_enderex");
        put("erasevanillaenderchestontransfer", "erase_vanilla_enderchest_on_transfer");
    }};

    // Copie complète des valeurs finales
    private Map<String, Object> currentValues = new LinkedHashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void loadAndUpdateConfig() {
        try {
            if (!configFile.exists()) {
                plugin.saveResource("config.yml", false);
            }

            // Charger le template et la config existante
            Map<String, Object> defaultConfig = loadFromStream(plugin.getResource("config.yml"));
            Map<String, Object> userConfig = loadFromFile(configFile);
            if (userConfig == null) userConfig = new LinkedHashMap<>();

            // Appliquer les renommages
            applyRenames(userConfig);

            // Fusionner les valeurs manquantes
            mergeDefaults(defaultConfig, userConfig);

            currentValues = userConfig;

            userConfig.put("configversion", 2);

            // Réécrire le config en gardant le template et les commentaires
            rewriteConfigFile(userConfig);

            plugin.reloadConfig();

        } catch (Exception e) {
            plugin.getLogger().severe("Erreur lors du chargement de la config : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, Object> loadFromStream(InputStream stream) throws IOException {
        if (stream == null) return new LinkedHashMap<>();
        try (stream) {
            return yaml.load(stream);
        }
    }

    private Map<String, Object> loadFromFile(File file) throws IOException {
        try (InputStream fis = new FileInputStream(file)) {
            return yaml.load(fis);
        }
    }

    private void applyRenames(Map<String, Object> config) {
        for (var entry : renameMap.entrySet()) {
            String oldPath = entry.getKey();
            String newPath = entry.getValue();

            Object oldValue = getValueByPath(config, oldPath);
            if (oldValue != null && getValueByPath(config, newPath) == null) {
                setValueByPath(config, newPath, oldValue);
                removeByPath(config, oldPath);
                plugin.getLogger().info("Clé renommée : " + oldPath + " → " + newPath);
            }
        }
    }

    private void mergeDefaults(Map<String, Object> defaults, Map<String, Object> target) {
        for (var entry : defaults.entrySet()) {
            String key = entry.getKey();
            Object defVal = entry.getValue();

            if (!target.containsKey(key)) {
                target.put(key, defVal);
                plugin.getLogger().info("Ajout clé manquante : " + key);
            } else if (defVal instanceof Map && target.get(key) instanceof Map) {
                mergeDefaults((Map<String, Object>) defVal, (Map<String, Object>) target.get(key));
            }
        }
    }

    private void rewriteConfigFile(Map<String, Object> values) throws IOException {
        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream == null) return;

        String template;
        try (defaultStream) {
            template = new String(defaultStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Appliquer toutes les valeurs sur le template
        template = applyValuesToTemplate(values, "", template);

        // Écrire le fichier final
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
            writer.write(template);
        }
    }

    private String applyValuesToTemplate(Map<String, Object> section, String path, String template) {
        for (Map.Entry<String, Object> entry : section.entrySet()) {
            String key = entry.getKey();
            String fullPath = path.isEmpty() ? key : path + "." + key;
            Object value = entry.getValue();

            if (value instanceof Map) {
                template = applyValuesToTemplate((Map<String, Object>) value, fullPath, template);
            } else {
                template = replaceValueInTemplate(template, fullPath, value);
            }
        }
        return template;
    }

    private String replaceValueInTemplate(String template, String path, Object value) {
        String[] parts = path.split("\\.");
        String lastKey = parts[parts.length - 1];

        // Regex simple pour remplacer la ligne correspondant à la clé
        String regex = "(?m)^\\s*" + Pattern.quote(lastKey) + ":.*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(template);

        String newLine = lastKey + ": " + formatValue(value);

        if (matcher.find()) {
            template = matcher.replaceFirst(Matcher.quoteReplacement(newLine));
        } else {
            // Si la clé n'existe pas, on ne met **pas** de commentaire automatique
            template += "\n" + newLine;
        }

        return template;
    }

    private String formatValue(Object value) {
        if (value instanceof String s) {
            s = s.replace("\"", "\\\"");
            if (s.contains(":") || s.contains("#") || s.contains(" ")) return "\"" + s + "\"";
            return s;
        }
        return String.valueOf(value);
    }

    // ======================== 🔧 UTILITAIRES YAML ========================

    private Object getValueByPath(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length; i++) {
            Object val = current.get(parts[i]);
            if (val == null) return null;
            if (i == parts.length - 1) return val;
            if (!(val instanceof Map)) return null;
            current = (Map<String, Object>) val;
        }
        return null;
    }

    private void setValueByPath(Map<String, Object> map, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new LinkedHashMap<>());
        }
        current.put(parts[parts.length - 1], value);
    }

    private void removeByPath(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) return;
            current = (Map<String, Object>) next;
        }
        current.remove(parts[parts.length - 1]);
    }

    // ======================== 💬 API PUBLIQUE ========================

    public Object get(String path) {
        return getValueByPath(currentValues, path);
    }

    public String getString(String path) {
        Object val = get(path);
        return val != null ? val.toString() : null;
    }

    public int getInt(String path) {
        Object val = get(path);
        return val instanceof Number ? ((Number) val).intValue() : 0;
    }

    public boolean getBoolean(String path) {
        Object val = get(path);
        return val instanceof Boolean ? (Boolean) val : false;
    }

    public void save() throws IOException {
        rewriteConfigFile(currentValues);
    }
}