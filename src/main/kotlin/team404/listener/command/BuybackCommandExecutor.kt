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
import org.bukkit.plugin.Plugin
import org.jetbrains.annotations.NotNull
import team404.constant.BUYBACK_INVENTORY_NAME
import team404.constant.INVENTORY_ROW_SIZE
import team404.service.PlayerRevivalService
import team404.util.TextUtils.formatTime
import team404.util.getBuybackTimeLeft
import team404.util.getBuybackTimerBar
import team404.util.getOrCreateBuybackTimerBar
import team404.util.removeBuybackTimeLeft
import team404.util.removeBuybackTimerBar
import team404.util.setBuybackTimeLeft
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
            checkBuybackTimers()
        }, 0L, 20L)
    }

    private fun checkBuybackTimers() {
        Bukkit.getOnlinePlayers().forEach { player ->
            checkBuybackTimer(player)
        }
    }

    private fun checkBuybackTimer(player: Player) {
        player.getBuybackTimeLeft()
            ?.let { it - 20 }
            ?.also { timeLeftUpdated ->
                if (timeLeftUpdated <= 0) {
                    player.damage(1000.0)
                    if (playerRevivalService.isRespawnablePlayer(player.name)) {
                        player.sendMessage("Ваш таймер истек.")
                        playerRevivalService.removeRespawnablePlayer(player.name)
                    }
                    if (player.health < 0) {
                        player.getBuybackTimerBar()?.also {
                            it.removeAll()
                        }
                        player.removeBuybackTimerBar()
                        player.removeBuybackTimeLeft()
                    }
                } else {
                    player.setBuybackTimeLeft(timeLeftUpdated)
                    player.getOrCreateBuybackTimerBar().apply {
                        addPlayer(player)
                        progress = timeLeftUpdated.toDouble() / buybackTime
                        setTitle("Оставшееся время: ${formatTime(timeLeftUpdated)}")

                    }
                }
            }
    }
}