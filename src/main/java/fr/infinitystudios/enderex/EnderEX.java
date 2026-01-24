package fr.infinitystudios.enderex;

import fr.infinitystudios.enderex.Chests.EnderCache;
import fr.infinitystudios.enderex.Commands.EnderExPaperCommand;
import fr.infinitystudios.enderex.Listeners.InventoryClick;
import fr.infinitystudios.enderex.Listeners.InventoryClose;
import fr.infinitystudios.enderex.Listeners.InventoryOpen;
import fr.infinitystudios.enderex.Listeners.PlayerLogin;
import fr.infinitystudios.enderex.Utils.*;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public final class EnderEX extends JavaPlugin {

    private static EnderEX plugin;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private FloodgateApi floodgateApi;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.getLogger().info("Starting");

        configManager = new ConfigManager(this);
        configManager.loadAndUpdateConfig();

        try {
            BackupUtil.createBackup(this);
        } catch (IOException e) {
            getLogger().severe("Failed to create the backup!");
            e.printStackTrace();
        }

        try {
            databaseManager = new DatabaseManager(this);
            databaseManager.connect();
            databaseManager.initDatabase();
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to the database!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        FileUtils fu = new FileUtils();
        fu.convertUserMap();

        if(getServer().getPluginManager().getPlugin("Floodgate") != null){
            floodgateApi = FloodgateApi.getInstance();
            plugin.getLogger().info("Floodgate detected");
        }



        getServer().getPluginManager().registerEvents(new PlayerLogin(), this);
        getServer().getPluginManager().registerEvents(new InventoryOpen(), this);
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(EnderExPaperCommand.createCommand(), "Main Command", List.of("ex","ec","enderchest"));
        });

        checkfolder();

        periodicsave();
        plugin.getLogger().info("Finished loading");
    }

    @Override
    public void onDisable() {
        EnderCache.saveAll();

        try {
            if (databaseManager != null) {
                databaseManager.close();
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to close the database!");
            e.printStackTrace();
        }

        InvUtils.ecstorage.clear();
        InvUtils.openedChests.clear();



        //UsermapCache.save();
        plugin.getLogger().info("Quitting");
    }

    public static EnderEX getPlugin(){
        return plugin;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public FloodgateApi getFloodgateApi() {
        return floodgateApi;
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
                //UsermapCache.save();
                if(plugin.getConfig().getBoolean("console_save_messages")) {
                    plugin.getLogger().info("Usermap saved");
                }
                EnderCache.saveAll();
            } catch (Exception e) {
                plugin.getLogger().severe(e.getMessage());
            }
        },delay, period);
    }

    public Platform playerPlatformResolver(UUID uuid){
        if (floodgateApi == null) {
            return Platform.JAVA;
        } else if (!floodgateApi.isFloodgatePlayer(uuid)) {
            return Platform.JAVA;
        } else if (floodgateApi.getPlayer(uuid).isLinked()) {
            return Platform.JAVA;
        } else {
            return Platform.BEDROCK;
        }
    }

}
