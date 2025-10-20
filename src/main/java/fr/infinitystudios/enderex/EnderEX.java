package fr.infinitystudios.enderex;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.Commands.EnderEXCommand;
import fr.infinitystudios.enderex.Commands.EnderEXSeeCommand;
import fr.infinitystudios.enderex.Listeners.InventoryClose;
import fr.infinitystudios.enderex.Listeners.InventoryOpen;
import fr.infinitystudios.enderex.Listeners.PlayerLogin;
import fr.infinitystudios.enderex.Utils.ConfigManager;
import fr.infinitystudios.enderex.Utils.FileUtils;
import fr.infinitystudios.enderex.Utils.UsermapCache;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.UUID;


public final class EnderEX extends JavaPlugin {

    private static EnderEX plugin;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.getLogger().info("Starting");
        configManager = new ConfigManager(this);
        configManager.loadAndUpdateConfig();
        getCommand("EnderEX").setExecutor(new EnderEXCommand());
        getCommand("EnderEXsee").setExecutor(new EnderEXSeeCommand());
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);
        getServer().getPluginManager().registerEvents(new InventoryOpen(), this);
        getServer().getPluginManager().registerEvents(new PlayerLogin(), this);
        checkfolder();
        UsermapCache.load();
        periodicsave();
    }

    @Override
    public void onDisable() {
        EnderCache.saveAll();
        UsermapCache.save();
        plugin.getLogger().info("Quitting");
    }

    public static EnderEX getPlugin(){
        return plugin;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static void checkfolder(){
        File subfolder = new File(plugin.getDataFolder() + "/data/");
        if( !subfolder.exists() ) subfolder.mkdir();
    }

    public void periodicsave() {
        long delay = 20L * 2 * 60;
        long period = 20L * 5 * 60;
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            try {
                UsermapCache.save();
                if(plugin.getConfig().getBoolean("console_save_messages")) {
                    plugin.getLogger().info("Usermap saved");
                }
                EnderCache.saveAll();
            } catch (Exception e) {
                plugin.getLogger().severe(e.getMessage());
            }
        },delay, period);
    }

}
