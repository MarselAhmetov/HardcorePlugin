package team404.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import team404.constant.PLUGIN_NAMESPACE
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets

object HttpClient {
    private val objectMapper = ObjectMapper()
    private val logger = LoggerFactory.getLogger(PLUGIN_NAMESPACE)

    fun sendPostRequest(urlString: String, request: Any) {
        try {
            val url = URI(urlString).toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val requestBody = objectMapper.writeValueAsString(request)
            connection.outputStream.use { os ->
                val input = requestBody.toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                logger.warn("Request was not sent, response code: $responseCode")
            }

            connection.disconnect()
        } catch (e: Exception) {
            logger.warn("Request was not sent", e)
        }
    }
}
