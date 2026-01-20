package fr.infinitystudios.enderex.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupUtil {

    public static void createBackup(JavaPlugin plugin) throws IOException {
        File dataFolder = new File(plugin.getDataFolder(), "data");

        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            plugin.getLogger().warning("Data folder does not exist, backup aborted.");
            return;
        }

        // backups/
        File backupsFolder = new File(plugin.getDataFolder(), "backups");
        if (!backupsFolder.exists() && backupsFolder.mkdirs()) {
            plugin.getLogger().info("Backups folder created.");
        }

        // Timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File zipFile = new File(backupsFolder, timestamp + ".zip");

        // Zip creation
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Path basePath = dataFolder.toPath();

            Files.walk(basePath).forEach(path -> {
                try {
                    if (Files.isDirectory(path)) return;

                    Path relativePath = basePath.relativize(path);
                    ZipEntry entry = new ZipEntry("data/" + relativePath.toString().replace("\\", "/"));

                    zos.putNextEntry(entry);
                    Files.copy(path, zos);
                    zos.closeEntry();

                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        plugin.getLogger().info("Backup created: " + zipFile.getName());

        // Enforce max backups
        enforceMaxBackups(plugin, backupsFolder);
    }

    private static void enforceMaxBackups(JavaPlugin plugin, File backupsFolder) {
        int maxBackups = plugin.getConfig().getInt("max_backups", -1);

        if (maxBackups <= 0) return;

        File[] backups = backupsFolder.listFiles((dir, name) -> name.endsWith(".zip"));
        if (backups == null || backups.length <= maxBackups) return;

        // Sort by last modified (oldest first)
        Arrays.sort(backups, Comparator.comparingLong(File::lastModified));

        int toDelete = backups.length - maxBackups;
        for (int i = 0; i < toDelete; i++) {
            if (backups[i].delete()) {
                plugin.getLogger().info("Old backup deleted: " + backups[i].getName());
            }
        }
    }
}