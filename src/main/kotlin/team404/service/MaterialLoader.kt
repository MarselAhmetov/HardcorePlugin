package team404.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import team404.model.RequiredResource
import java.io.File
import java.io.IOException

object MaterialLoader {
    private val objectMapper = ObjectMapper()

    private val logger = LoggerFactory.getLogger(MaterialLoader::class.java)

    fun loadMaterials(dataFolder: File) {
        val materialsFolder = File(dataFolder, "materials")
        if (materialsFolder.mkdirs()) {
            logger.info("Folder ${materialsFolder.name} was created")
        }
        // Список файлов в папке materials
        val materialFiles = materialsFolder.listFiles()

        materialFiles?.forEach { file ->
            if (file.isFile && file.name.endsWith(".json")) {
                val materialListKey = file.name.replace(".json", "")
                // Загружаем список материалов из файла
                val materialList = loadMaterialList(file)
                // Добавляем список в хранилище
                MaterialGenerator.addMaterialList(materialListKey, materialList)
                logger.info("Loaded $materialListKey with ${materialList.size} elements")
            }
        }
        logger.info("Total materials lists loaded - ${MaterialGenerator.materialLists.size}")
    }

    private fun loadMaterialList(file: File): List<RequiredResource> {
        return try {
            if (!file.exists()) {
                emptyList()
            } else {
                objectMapper.readValue(file, object : TypeReference<List<RequiredResource>>() {})
            }
        } catch (e: IOException) {
            logger.info("File ${file.name} was not loaded")
            emptyList()
        }
    }
}
