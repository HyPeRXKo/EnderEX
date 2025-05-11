package fr.infinitystudios.enderex.Utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class InventorySerializer {

    /** Convertit un Inventory en String Base64 */
    public static String toBase64(Inventory inventory) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream out = new BukkitObjectOutputStream(byteStream);
        // Taille + items
        out.writeInt(inventory.getSize());
        for (ItemStack item : inventory.getContents()) {
            out.writeObject(item);
        }
        out.close();
        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }

    /** Reconstruit un Inventory à partir d’une String Base64 */
    public static Inventory fromBase64(String data) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        BukkitObjectInputStream in = new BukkitObjectInputStream(byteStream);

        int size = in.readInt();
        Inventory inv = Bukkit.createInventory(null, size, "Restored Inventory");
        // Récupération des ItemStack
        for (int i = 0; i < size; i++) {
            ItemStack item = (ItemStack) in.readObject();
            inv.setItem(i, item);
        }
        in.close();
        return inv;
    }
}