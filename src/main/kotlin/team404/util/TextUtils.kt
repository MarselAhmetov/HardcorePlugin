package team404.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.translation.Translatable
import team404.constant.GREEN_HEX
import team404.constant.RED_HEX

object TextUtils {
    fun appendCheckMark(text: Translatable): Component {
        return Component.text("✔ ")
            .append(Component.translatable(text))
            .color(TextColor.fromHexString(GREEN_HEX))
    }

    fun appendCross(text: Translatable): Component {
        return Component.text("✖ ")
            .append(Component.translatable(text))
            .color(TextColor.fromHexString(RED_HEX))
    }

    fun formatTime(ticks: Long): String {
        val seconds = ticks / 20
        val minutes = seconds / 60
        val hours = minutes / 60
        return String.format("%02d:%02d:%02d", hours % 60, minutes % 60, seconds % 60)
    }
}