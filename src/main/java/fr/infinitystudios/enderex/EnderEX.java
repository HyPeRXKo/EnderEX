package fr.infinitystudios.enderex;

import fr.infinitystudios.enderex.Commands.EnderEXCommand;
import fr.infinitystudios.enderex.Commands.EnderEXSeeCommand;
import fr.infinitystudios.enderex.Listeners.InventoryClose;
import fr.infinitystudios.enderex.Listeners.InventoryOpen;
import fr.infinitystudios.enderex.Utils.FileUtils;
import fr.infinitystudios.enderex.Utils.UsermapCache;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class EnderEX extends JavaPlugin {

    private static EnderEX plugin;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.getLogger().info("Starting");
        getConfig().options().configuration();
        saveDefaultConfig();
        getCommand("EnderEX").setExecutor(new EnderEXCommand());
        getCommand("EnderEXsee").setExecutor(new EnderEXSeeCommand());
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);
        getServer().getPluginManager().registerEvents(new InventoryOpen(), this);
        checkfolder();
        UsermapCache.load();
    }

    @Override
    public void onDisable() {
        UsermapCache.save();
        plugin.getLogger().info("Quitting");
    }

    public static EnderEX getPlugin(){
        return plugin;
    }

    public static void checkfolder(){
        File subfolder = new File(plugin.getDataFolder() + "/data/");
        if( !subfolder.exists() ) subfolder.mkdir();
    }

}
