package me.realized.de.certificates.util;

import java.util.List;
import org.bukkit.ChatColor;

public final class StringUtil {

    public static String color(final String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> color(final List<String> list) {
        list.replaceAll(s -> s = color(s));
        return list;
    }

    private StringUtil() {}
}
