package team404;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.Translatable;

public class TextUtils {
    public static Component appendCheckMark(Translatable text) {
        return Component.text("✔ ")
                .append(Component.translatable(text))
                .color(TextColor.fromHexString(ColorHexConstants.GREEN_HEX));
    }

    public static Component appendCross(Translatable text) {
        return Component.text("✖ ")
                .append(Component.translatable(text))
                .color(TextColor.fromHexString(ColorHexConstants.RED_HEX));
    }
}
