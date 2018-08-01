package me.realized.de.certificates.util;

import java.util.function.Consumer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtil {

    public static void editMeta(final ItemStack item, final Consumer<ItemMeta> consumer) {
        final ItemMeta meta = item.getItemMeta();
        consumer.accept(meta);
        item.setItemMeta(meta);
    }

    private ItemUtil() {}
}
