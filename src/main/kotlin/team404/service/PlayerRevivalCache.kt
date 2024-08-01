package team404.service

import org.bukkit.Material

class PlayerRevivalCache private constructor() {
    val respawnablePlayers: MutableMap<String, Map<Material, Int>> = mutableMapOf()
    val playersToSpawn: MutableSet<String> = mutableSetOf()

    fun addAllRespawnablePlayers(players: Map<String, Map<Material, Int>>) {
        players.forEach { (key, value) ->
            respawnablePlayers[key] = value.toMutableMap()
        }
    }

    fun addAllPlayersToSpawn(players: Collection<String>) {
        playersToSpawn.addAll(players)
    }

    companion object {
        @Volatile
        private var instance: PlayerRevivalCache? = null

        fun getInstance(): PlayerRevivalCache =
            instance ?: synchronized(this) {
                instance ?: PlayerRevivalCache().also { instance = it }
            }
    }
}
