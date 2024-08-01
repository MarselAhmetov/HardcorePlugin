package team404.util

import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

object FileUtils {

    private val logger = LoggerFactory.getLogger(FileUtils::class.java)

    @JvmStatic
    fun readAllLines(file: File): List<String> {
        return try {
            Files.readAllLines(file.toPath())
        } catch (e: IOException) {
            logger.error("Error reading file: ${file.absolutePath}", e)
            Collections.emptyList()
        }
    }

    @JvmStatic
    fun createFile(dataFolder: File, fileName: String): File {
        val folderPath: Path = Paths.get(dataFolder.path)
        return try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath)
                logger.info("Directories created: $folderPath")
            }
            val file = File(dataFolder, fileName)
            if (!file.exists()) {
                Files.createFile(file.toPath())
            }
            file
        } catch (e: IOException) {
            logger.error("Error creating file: ${dataFolder.absolutePath}/$fileName", e)
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun writeToFile(file: File, content: Collection<String>) {
        try {
            Files.write(file.toPath(), content, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
        } catch (e: IOException) {
            logger.error("Error writing to file: ${file.absolutePath}", e)
        }
    }

    @JvmStatic
    fun appendToFile(file: File, text: String) {
        try {
            Files.write(file.toPath(), text.toByteArray(), StandardOpenOption.APPEND)
        } catch (e: IOException) {
            logger.error("Error writing to file: ${file.absolutePath}", e)
        }
    }
}