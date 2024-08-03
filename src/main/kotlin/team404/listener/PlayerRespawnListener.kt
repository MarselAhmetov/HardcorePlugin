package team404.listener

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.slf4j.LoggerFactory
import team404.constant.BUYBACK_TIMER_KEY
import team404.constant.PLAYER_DEAD_PATH
import team404.constant.PLUGIN_NAMESPACE
import team404.model.MaterialTier
import team404.model.request.PlayerDeadRequest
import team404.service.MaterialGenerator
import team404.service.NamespaceKeyManager
import team404.service.PlayerRevivalService
import team404.util.HttpClient.sendPostRequest
import team404.util.getBuybackCount
import team404.util.getRespawnMaterialsMultiplier
import team404.util.removeBuybackTimeLeft
import team404.util.removeRespawnMaterialsMultiplier
import team404.util.setRespawnMaterialsMultiplier

class PlayerRespawnListener(private val plugin: Plugin) : Listener {

    private val playerRevivalService: PlayerRevivalService = PlayerRevivalService.getInstance(plugin)

    private val logger = LoggerFactory.getLogger(PlayerRespawnListener::class.java)

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val bossBar = Bukkit.getBossBar(NamespaceKeyManager.getKey(PLUGIN_NAMESPACE, "${player.name.lowercase()}$BUYBACK_TIMER_KEY"))
        bossBar?.let {
            it.removeAll()
            Bukkit.removeBossBar(it.key)
        }
        val buybackCount = player.getBuybackCount() ?: 0
        if (buybackCount > 0) {
            player.setRespawnMaterialsMultiplier(2)
        }
        player.removeBuybackTimeLeft()
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val multiplier = player.getRespawnMaterialsMultiplier() ?: 1
        player.removeRespawnMaterialsMultiplier()
        val materials = getMaterialsToRevive(getMaterialTier(player), multiplier)
        playerRevivalService.run {
            if (respawnablePlayers.containsKey(player.name)) {
                removeRespawnablePlayer(player.name)
            }
            addRespawnablePlayer(player.name, materials)
        }
        sendPostRequest("${plugin.config.getString("bot-address")}$PLAYER_DEAD_PATH",
            PlayerDeadRequest(player.name, materials.mapKeys { it.key.name })
        )
    }

    @EventHandler
    fun onPlayerModeChange(event: PlayerGameModeChangeEvent) {
        val player = event.player
        if (event.newGameMode == GameMode.SPECTATOR && playerRevivalService.respawnablePlayers.containsKey(player.name)) {
            val blindnessEffect = PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0, true, false)
            player.addPotionEffect(blindnessEffect)
        }
    }

    private fun getMaterialTier(player: Player): MaterialTier {
        return when {
            isAdvancementDone(player, "story/enter_the_end") -> MaterialTier.END
            isAdvancementDone(player, "story/enter_the_nether") -> MaterialTier.NETHER
            else -> MaterialTier.OVER_WORLD
        }
    }

    private fun isAdvancementDone(player: Player, key: String): Boolean {
        return NamespacedKey.fromString(key)?.let { Bukkit.getAdvancement(it) }
            ?.let { player.getAdvancementProgress(it).isDone } ?: false
    }

    private fun getMaterialsToRevive(tier: MaterialTier, multiplier: Int): Map<Material, Int> {
        return MaterialGenerator.getMaterialsList(tier, multiplier)
    }
}