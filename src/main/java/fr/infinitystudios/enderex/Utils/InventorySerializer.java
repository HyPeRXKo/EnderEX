package fr.infinitystudios.enderex.Utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;

public class InventorySerializer {

    public static String inventoryToNBTBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // Taille de l'inventory
            dos.writeInt(inventory.getSize());

            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);

                if (item == null) {
                    // On écrit une longueur 0 → pas d’item
                    dos.writeInt(0);
                } else {
                    byte[] nbt = item.serializeAsBytes();
                    dos.writeInt(nbt.length);
                    dos.write(nbt);
                }
            }

            dos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error in NBT Serialisation", e);
        }
    }

    public static Inventory nbtBase64ToInventory(String base64) {
        try {
            byte[] data = Base64.getDecoder().decode(base64);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);

            int size = dis.readInt();
            Inventory inv = Bukkit.createInventory(null, size);

            for (int i = 0; i < size; i++) {
                int length = dis.readInt();

                if (length > 0) {
                    byte[] nbt = new byte[length];
                    dis.readFully(nbt);

                    ItemStack item = ItemStack.deserializeBytes(nbt);
                    inv.setItem(i, item);
                }
            }

            return inv;

        } catch (Exception e) {
            throw new RuntimeException("Error in NBT Serialisation", e);
        }
    }

    public static Inventory OLDfromBase64(String data) throws IOException, ClassNotFoundException {
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