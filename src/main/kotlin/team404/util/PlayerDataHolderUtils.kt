package team404.util

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.KeyedBossBar
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import team404.constant.BUYBACK_COUNT_KEY
import team404.constant.BUYBACK_TIMER_KEY
import team404.constant.BUYBACK_TIME_LEFT_KEY
import team404.constant.PLUGIN_NAMESPACE
import team404.constant.RESPAWN_MATERIALS_MULTIPLIER_KEY
import team404.service.NamespaceKeyManager

fun Player.setBuybackTimeLeft(buybackTimeLeft: Long) {
    this.setData(
        NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, BUYBACK_TIME_LEFT_KEY),
        PersistentDataType.LONG,
        buybackTimeLeft
    )
}

fun Player.setBuybackCount(buybackCount: Int) {
    this.setData(
        NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, BUYBACK_COUNT_KEY),
        PersistentDataType.INTEGER,
        buybackCount
    )
}

fun Player.setRespawnMaterialsMultiplier(respawnMaterialsMultiplier: Int) {
    this.setData(
        NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, RESPAWN_MATERIALS_MULTIPLIER_KEY),
        PersistentDataType.INTEGER,
        respawnMaterialsMultiplier
    )
}

fun Player.getBuybackTimeLeft() =
    this.getData(NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, BUYBACK_TIME_LEFT_KEY), PersistentDataType.LONG)

fun Player.getBuybackCount() =
    this.getData(NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, BUYBACK_COUNT_KEY), PersistentDataType.INTEGER)

fun Player.getRespawnMaterialsMultiplier() =
    this.getData(
        NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, RESPAWN_MATERIALS_MULTIPLIER_KEY),
        PersistentDataType.INTEGER
    )

fun Player.removeBuybackTimeLeft() =
    this.removeData(NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, BUYBACK_TIME_LEFT_KEY))

fun Player.removeBuybackCount() =
    this.removeData(NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, BUYBACK_COUNT_KEY))

fun Player.removeRespawnMaterialsMultiplier() =
    this.removeData(NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, RESPAWN_MATERIALS_MULTIPLIER_KEY))

fun Player.getBuybackTimerBar() =
    Bukkit.getBossBar(
        NamespaceKeyManager.getKey(
            PLUGIN_NAMESPACE,
            "${this.name.lowercase()}$BUYBACK_TIMER_KEY"
        )
    )

fun Player.removeBuybackTimerBar() =
    Bukkit.removeBossBar(
        NamespaceKeyManager.getKey(
            PLUGIN_NAMESPACE,
            "${this.name.lowercase()}$BUYBACK_TIMER_KEY"
        )
    )

fun Player.getOrCreateBuybackTimerBar(): KeyedBossBar =
    Bukkit.getBossBar(NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, "${this.name.lowercase()}$BUYBACK_TIMER_KEY")) ?: createBossBar(this)

private fun createBossBar(player: Player): KeyedBossBar {
    return Bukkit.createBossBar(
        NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, "${player.name.lowercase()}$BUYBACK_TIMER_KEY"),
        "Таймер",
        BarColor.WHITE,
        BarStyle.SOLID
    )
}
