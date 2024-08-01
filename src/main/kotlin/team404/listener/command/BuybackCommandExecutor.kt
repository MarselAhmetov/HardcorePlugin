package team404.listener.command

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.jetbrains.annotations.NotNull
import team404.service.NamespaceKeyManager
import team404.service.PlayerRevivalService
import team404.constant.BUYBACK_INVENTORY_NAME
import team404.constant.BUYBACK_TIMER_KEY
import team404.constant.BUYBACK_TIME_LEFT_KEY
import team404.constant.INVENTORY_ROW_SIZE
import team404.util.TextUtils.formatTime
import team404.util.getData
import team404.util.removeData
import team404.util.setData
import java.util.logging.Logger

class BuybackCommandExecutor(private val plugin: Plugin) : CommandExecutor {

    companion object {
        const val BUYBACK_COMMAND = "buyback"
    }

    private val logger: Logger = Logger.getLogger(BuybackCommandExecutor::class.java.name)

    private val playerRevivalService: PlayerRevivalService = PlayerRevivalService.getInstance(plugin)
    private val buybackTime: Long = plugin.config.getLong("buyback-time")

    init {
        startTimerTask()
    }

    override fun onCommand(@NotNull commandSender: CommandSender, @NotNull command: Command, @NotNull s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player || commandSender.gameMode != GameMode.SPECTATOR) {
            return true
        }

        if (command.name == BUYBACK_COMMAND) {
            commandSender.openInventory(getBuybackInventory(commandSender))
        }

        return true
    }

    private fun getBuybackInventory(player: Player): Inventory {
        val inv = Bukkit.createInventory(null, INVENTORY_ROW_SIZE, Component.text(BUYBACK_INVENTORY_NAME))
        val materialMap = playerRevivalService.respawnablePlayers[player.name]

        materialMap?.let {
            val playerHead = ItemStack(Material.PLAYER_HEAD).apply {
                val skullMeta = itemMeta as SkullMeta
                skullMeta.owningPlayer = Bukkit.getOfflinePlayer(player.name)
                it.map { entry ->
                    Component.translatable(ItemStack(entry.key)).appendSpace().append(Component.text(entry.value))
                }.also { skullMeta.lore(it) }
                itemMeta = skullMeta
            }
            inv.setItem(4, playerHead)
        }

        return inv
    }

    private fun startTimerTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            Bukkit.getOnlinePlayers().forEach { player ->
                val timeLeft = player.getData(NamespaceKeyManager.getKey(plugin, BUYBACK_TIME_LEFT_KEY), PersistentDataType.LONG)

                timeLeft?.let {
                    val bossBar = Bukkit.getBossBar(NamespaceKeyManager.getKey(plugin, "${player.name}$BUYBACK_TIMER_KEY"))
                    val timeLeftUpdated = it - 20 // уменьшаем на 1 секунду (20 тиков)

                    if (timeLeftUpdated <= 0) {
                        player.sendMessage("Ваш таймер истек.")
                        bossBar?.removeAll()
                        Bukkit.removeBossBar(NamespaceKeyManager.getKey(plugin, "${player.name}$BUYBACK_TIMER_KEY"))
                        player.removeData(NamespaceKeyManager.getKey(plugin, BUYBACK_TIME_LEFT_KEY))
                        if (playerRevivalService.isRespawnablePlayer(player.name)) {
                            playerRevivalService.removeRespawnablePlayer(player.name)
                            player.damage(100.0)
                        }

                    } else {
                        player.setData(NamespaceKeyManager.getKey(plugin, BUYBACK_TIME_LEFT_KEY), PersistentDataType.LONG, timeLeftUpdated)
                        bossBar?.apply {
                            progress = timeLeftUpdated.toDouble() / buybackTime
                            setTitle("Оставшееся время: ${formatTime(timeLeftUpdated)}")
                        }
                    }
                }

            }
        }, 0L, 20L) // Запуск задачи каждые 20 тиков (1 секунда)
    }
}