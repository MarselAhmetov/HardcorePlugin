package team404;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.Translatable;

public class TextUtils {
    private static final String GREEN_HEX = "#00FF00";
    private static final String RED_HEX = "#FF0000";

    public static Component appendCheckMark(Translatable text) {
        return Component.text("✔ ")
                .append(Component.translatable(text))
                .color(TextColor.fromHexString(GREEN_HEX));
    }

    public static Component appendCross(Translatable text) {
        return Component.text("✖ ")
                .append(Component.translatable(text))
                .color(TextColor.fromHexString(RED_HEX));
    }
}
