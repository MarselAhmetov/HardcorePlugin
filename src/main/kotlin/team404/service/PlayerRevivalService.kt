package team404.service

import org.bukkit.Material
import org.bukkit.plugin.Plugin

class PlayerRevivalService private constructor(plugin: Plugin) {

    private val cache = PlayerRevivalCache.getInstance()
    private val fileRepository = PlayerRevivalFileManager.getInstance(plugin)

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

    val respawnablePlayers: Map<String, Map<Material, Int>>
        get() = cache.respawnablePlayers

    fun isRespawnablePlayer(playerName: String): Boolean {
        return cache.respawnablePlayers.containsKey(playerName)
    }

    val playersToSpawn: Set<String>
        get() = cache.playersToSpawn

    companion object {
        @Volatile
        private var instance: PlayerRevivalService? = null

        fun getInstance(plugin: Plugin): PlayerRevivalService =
            instance ?: synchronized(this) {
                instance ?: PlayerRevivalService(plugin).also { instance = it }
            }
    }
}
