package team404;

import org.bukkit.ChatColor;

public class TextUtils {

    public static String greenText(String text) {
        return ChatColor.GREEN + text;
    }

    public static String appendCheckMark(String text) {
        return ChatColor.GREEN + "✔ " + text;
    }

    public static String appendCross(String text) {
        return ChatColor.RED + "✖ " + text;
    }
}
