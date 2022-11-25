package fr.infinitystudios.enderex.Utils;

import fr.infinitystudios.enderex.EnderEX;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    private static final EnderEX plugin = EnderEX.getPlugin();

    public FileConfiguration GetChestConfig(Player p) {
        FileConfiguration invdata;
        File invdatafile = new File(plugin.getDataFolder() + "/data/", p.getUniqueId() + ".yml");
        if (!invdatafile.exists()) {return null;}
        invdata = new YamlConfiguration();
        try {
            invdata.load(invdatafile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return invdata;
    }

    public void SaveChestConfig(Player p,Inventory chest){
        FileConfiguration invdata;
        File invdatafile = new File(plugin.getDataFolder() + "/data/", p.getUniqueId() + ".yml");
        invdata = new YamlConfiguration();
        invdata.set("player", p.getName());
        invdata.set("chest", chest.getContents());
        try {
            invdata.save(invdatafile);}
        catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();}
        }


}
