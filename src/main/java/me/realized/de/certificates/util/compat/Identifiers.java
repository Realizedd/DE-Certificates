package me.realized.de.certificates.util.compat;

import me.realized.de.certificates.util.compat.nbt.NBT;
import me.realized.de.certificates.util.reflect.ReflectionUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class Identifiers {

    private static transient final String DUELS_ITEM_IDENTIFIER = "DuelCertificateItem";

    public static ItemStack addIdentifier(final ItemStack item, final Plugin plugin) {
        if (ReflectionUtil.getMajorVersion() < 14) {
            return NBT.setItemString(item, DUELS_ITEM_IDENTIFIER, true);
        }

        final NamespacedKey key = new NamespacedKey(plugin, DUELS_ITEM_IDENTIFIER);
        final ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean hasIdentifier(final ItemStack item, final Plugin plugin) {
        if (ReflectionUtil.getMajorVersion() < 14) {
            return NBT.hasItemKey(item, DUELS_ITEM_IDENTIFIER);
        }

        final NamespacedKey key = new NamespacedKey(plugin, DUELS_ITEM_IDENTIFIER);
        final ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    public static ItemStack removeIdentifier(final ItemStack item, final Plugin plugin) {
        if (ReflectionUtil.getMajorVersion() < 14) {
            return NBT.removeItemTag(item, DUELS_ITEM_IDENTIFIER);
        }

        final NamespacedKey key = new NamespacedKey(plugin, DUELS_ITEM_IDENTIFIER);
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(key);
        item.setItemMeta(meta);
        return item;
    }

    private Identifiers() {}
}
