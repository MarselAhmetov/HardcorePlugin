package team404.service

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import team404.buybackTime
import team404.client.TelegramDemoBotClient.sendPlayerRevivedRequest
import team404.constant.SECONDS_BEFORE_RESPAWN
import team404.constant.WORLD_NAME
import team404.constant.YOU_WILL_BE_REVIVED_IN
import team404.util.getBuybackCount
import team404.util.getOrCreateBuybackTimerBar
import team404.util.setBuybackCount
import team404.util.setBuybackTimeLeft
import java.util.concurrent.atomic.AtomicInteger

class PlayerRevivalService private constructor(private val plugin: Plugin) {

    private val cache = PlayerRevivalCache.getInstance()
    private val fileRepository = PlayerRevivalFileManager.getInstance(plugin)

    val playersToSpawn: Set<String>
        get() = cache.playersToSpawn

    val respawnablePlayers: Map<String, Map<Material, Int>>
        get() = cache.respawnablePlayers


    fun loadData() {
        cache.addAllPlayersToSpawn(fileRepository.readPlayersToSpawn())
        cache.addAllRespawnablePlayers(fileRepository.readRespawnablePlayers())
    }

    fun addRespawnablePlayer(playerName: String, materialMap: Map<Material, Int>) {
        cache.respawnablePlayers[playerName] = materialMap
        fileRepository.addRespawnablePlayer(playerName, materialMap)
    }

    fun addPlayerToRespawn(playerName: String) {
        cache.playersToSpawn.add(playerName)
        fileRepository.addPlayerToRespawn(playerName)
    }

    fun removeRespawnablePlayer(playerName: String) {
        cache.respawnablePlayers.remove(playerName)
        fileRepository.syncRespawnablePlayers(cache.respawnablePlayers)
    }

    fun removePlayerToRespawn(playerName: String) {
        cache.playersToSpawn.remove(playerName)
        fileRepository.syncPlayersToRespawn(cache.playersToSpawn)
    }

    fun isRespawnablePlayer(playerName: String): Boolean {
        return cache.respawnablePlayers.containsKey(playerName)
    }


    fun buybackPlayer(player: Player) {
        val buybackCount = player.getBuybackCount() ?: 0
        player.setBuybackCount(buybackCount + 1)
        if (!player.isOnline) {
            return
        }
        val counter = AtomicInteger(SECONDS_BEFORE_RESPAWN)
        object : BukkitRunnable() {
            override fun run() {
                val currentCounter = counter.getAndDecrement()
                player.clearTitle()
                if (currentCounter > 0) {
                    player.showTitle(
                        Title.title(
                            Component.text(YOU_WILL_BE_REVIVED_IN.format(currentCounter)),
                            Component.empty()
                        )
                    )
                } else {
                    if (player.isOnline) {
                        val location = player.respawnLocation ?: Bukkit.getWorld(WORLD_NAME)?.spawnLocation ?: return
                        player.teleport(location)
                        player.gameMode = GameMode.SURVIVAL
                        player.spawnParticle(Particle.TOTEM_OF_UNDYING, location, 100)
                        player.playEffect(EntityEffect.TOTEM_RESURRECT)
                        player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                        player.clearActivePotionEffects()
                        removePlayerToRespawn(player.name)
                        player.setBuybackTimeLeft(buybackTime)
                        player.getOrCreateBuybackTimerBar().also {
                            it.addPlayer(player)
                        }
                        sendPlayerRevivedRequest(player.name)
                    }
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }

    fun respawnPlayer(offlinePlayer: OfflinePlayer) {
        val player = offlinePlayer.player ?: return
        val counter = AtomicInteger(SECONDS_BEFORE_RESPAWN)
        object : BukkitRunnable() {
            override fun run() {
                val currentCounter = counter.getAndDecrement()
                player.clearTitle()
                if (currentCounter > 0) {
                    player.showTitle(
                        Title.title(
                            Component.text(YOU_WILL_BE_REVIVED_IN.format(currentCounter)),
                            Component.empty()
                        )
                    )
                } else {
                    if (offlinePlayer.isOnline) {
                        val location = player.respawnLocation ?: Bukkit.getWorld(WORLD_NAME)?.spawnLocation ?: return
                        player.apply {
                            this.teleport(location)
                            this.gameMode = GameMode.SURVIVAL
                            this.spawnParticle(Particle.TOTEM_OF_UNDYING, location, 100)
                            this.playEffect(EntityEffect.TOTEM_RESURRECT)
                            this.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                            this.clearActivePotionEffects()
                            removePlayerToRespawn(this.name)
                        }
                        sendPlayerRevivedRequest(player.name)
                    }
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }

    companion object {
        @Volatile
        private var instance: PlayerRevivalService? = null

        fun getInstance(plugin: Plugin): PlayerRevivalService =
            instance ?: synchronized(this) {
                instance ?: PlayerRevivalService(plugin).also { instance = it }
            }
    }
}
