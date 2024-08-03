package team404.service

import org.apache.commons.lang3.tuple.Pair
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import team404.util.FileUtils.appendToFile
import team404.util.FileUtils.createFile
import team404.util.FileUtils.readAllLines
import team404.util.FileUtils.writeToFile
import java.io.File

class PlayerRevivalFileManager private constructor(plugin: Plugin) {

    private val playersToReviveFile: File = createFile(plugin.dataFolder, PLAYERS_TO_REVIVE_FILE_NAME)
    private val respawnablePlayersFile: File = createFile(plugin.dataFolder, RESPAWNABLE_PLAYERS_FILE_NAME)

    fun readRespawnablePlayers(): Map<String, Map<Material, Int>> {
        return readAllLines(respawnablePlayersFile)
            .filter { it.isNotEmpty() }
            .map { processRespawnablePlayer(it) }
            .associate { it.left to it.right }
    }

    fun readPlayersToSpawn(): List<String> {
        return readAllLines(playersToReviveFile)
    }

    private fun processRespawnablePlayer(line: String): Pair<String, Map<Material, Int>> {
        val keys = line.split(",")
        val playerName = keys[0]
        val materialMap = keys.drop(1)
            .map { it.split(":") }
            .associate { (material, count) -> Material.getMaterial(material) to count.toInt() }
            .filterKeys { it != null }
            .mapKeys { it.key!! }
            .toMutableMap()
        return Pair.of(playerName, materialMap)
    }

    private fun respawnablePlayersToContent(map: Map<String, Map<Material, Int>>): Collection<String> {
        return map.entries.map { respawnablePlayersToContent(it.key, it.value) }
    }

    private fun respawnablePlayersToContent(playerName: String, materials: Map<Material, Int>): String {
        return "$playerName," + materials.entries.joinToString(",") { "${it.key}:${it.value}" } + "\n"
    }

    fun syncRespawnablePlayers(map: Map<String, Map<Material, Int>>) {
        writeToFile(respawnablePlayersFile, respawnablePlayersToContent(map))
    }

    fun syncPlayersToRespawn(playersToRevive: Collection<String>) {
        writeToFile(playersToReviveFile, playersToRevive)
    }

    fun addRespawnablePlayer(playerName: String, materials: Map<Material, Int>) {
        appendToFile(respawnablePlayersFile, respawnablePlayersToContent(playerName, materials))
    }

    fun addPlayerToRespawn(playerName: String) {
        appendToFile(playersToReviveFile, "$playerName\n")
    }

    companion object {
        private const val PLAYERS_TO_REVIVE_FILE_NAME = "players_to_revive.txt"
        private const val RESPAWNABLE_PLAYERS_FILE_NAME = "respawnable_players.txt"

        @Volatile
        private var instance: PlayerRevivalFileManager? = null

        fun getInstance(plugin: Plugin): PlayerRevivalFileManager =
            instance ?: synchronized(this) {
                instance ?: PlayerRevivalFileManager(plugin).also { instance = it }
            }
    }
}
