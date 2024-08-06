package team404.client

import org.bukkit.Material
import team404.botAddress
import team404.client.HttpClient.sendPostRequest
import team404.constant.PLAYER_DEAD_PATH
import team404.constant.PLAYER_REVIVED_PATH
import team404.model.request.PlayerDeadRequest
import team404.model.request.PlayerRevivedRequest

object TelegramDemoBotClient {
    fun sendPlayerRevivedRequest(name: String) {
        botAddress?.let {
            sendPostRequest(
                "${it}$PLAYER_REVIVED_PATH",
                PlayerRevivedRequest(name)
            )
        }
    }

    fun sendPlayerDeadRequest(name: String, materials: Map<Material, Int>) {
        botAddress?.let {
            sendPostRequest("${it}$PLAYER_DEAD_PATH",
                PlayerDeadRequest(name, materials.mapKeys { it.key.name })
            )
        }
    }
}