package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UsermapCache {

    private static final EnderEX plugin = EnderEX.getPlugin();

    public static final Map<String, UUID> usermapCACHE = new ConcurrentHashMap<>();
    public static final Map<UUID, String> uuidmapCACHE = new ConcurrentHashMap<>();

    public static boolean containsname(String name) {
        return usermapCACHE.containsKey(name);
    }
    public static boolean containsuuid(UUID uuid) {
        return uuidmapCACHE.containsKey(uuid);
    }

    public static UUID getuuid(String name) {
        return usermapCACHE.get(name);
    }
    public static String getname(UUID uuid) {
        return uuidmapCACHE.get(uuid);
    }

    public static void putname(String name, UUID uuid) {
        usermapCACHE.put(name, uuid);
    }
    public static void putuuid(UUID uuid, String name) {
        uuidmapCACHE.put(uuid, name);
    }

    public static void removename(String name) {
        usermapCACHE.remove(name);
    }
    public static void removeuuid(UUID uuid) {
        uuidmapCACHE.remove(uuid);
    }

    public static void save() {
        new FileUtils().saveUserMap(usermapCACHE);
    }

    public static void load() {
        new FileUtils().loadUserMap();
        for (Map.Entry<String, UUID> entry : usermapCACHE.entrySet()) {
            uuidmapCACHE.put(entry.getValue(), entry.getKey());
        }
    }
}
