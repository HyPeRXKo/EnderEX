package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UsermapCache {

    private static final EnderEX plugin = EnderEX.getPlugin();

    public static final Map<String, UUID> usermapCACHE = new ConcurrentHashMap<>();

    public static boolean contains(String name) {
        return usermapCACHE.containsKey(name);
    }

    public static UUID get(String name) {
        return usermapCACHE.get(name);
    }

    public static void put(String name, UUID uuid) {
        usermapCACHE.put(name, uuid);
    }

    public static void remove(String name) {
        usermapCACHE.remove(name);
    }

    public static void save() {
        new FileUtils().saveUserMap(usermapCACHE);
    }
}
