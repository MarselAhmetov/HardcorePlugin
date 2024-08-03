package team404.util

import org.bukkit.Material
import org.bukkit.entity.Player

fun removeItems(player: Player, materialMap: Map<Material, Int>) {
    materialMap.forEach { (material, amount) -> removeItems(player, material, amount) }
}

fun removeItems(player: Player, material: Material, amount: Int) {
    var remaining = amount
    player.inventory.contents
        .filterNotNull()
        .filter { item -> item.type == material }
        .forEach { item ->
            val inStack = item.amount
            if (inStack > remaining) {
                item.amount = inStack - remaining
                return
            } else {
                player.inventory.remove(item)
                remaining -= inStack
            }
            if (remaining <= 0) {
                return
            }
        }
}