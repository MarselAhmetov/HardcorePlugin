package team404;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import team404.models.RequiredResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MaterialLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void loadMaterials(File dataFolder) {
        File materialsFolder = new File(dataFolder, "materials");
        if (materialsFolder.mkdirs()) {
            Bukkit.getLogger().info("Folder %s was created".formatted(materialsFolder.getName()));
        }
        // Список файлов в папке materials
        File[] materialFiles = materialsFolder.listFiles();

        if (materialFiles != null) {
            for (File file : materialFiles) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    String materialListKey = file.getName().replace(".json", "");
                    // Загружаем список материалов из файла
                    List<RequiredResource> materialList = loadMaterialList(file);
                    // Добавляем список в хранилище
                    MaterialGenerator.addMaterialList(materialListKey, materialList);
                    Bukkit.getLogger().info("Loaded %s with %s elements"
                            .formatted(materialListKey, materialList.size()));
                }
            }
        }
        Bukkit.getLogger().info("Total materials lists loaded - %s"
                .formatted(MaterialGenerator.getMaterialLists().size()));
    }

    private static List<RequiredResource> loadMaterialList(File file) {
        try {
            if (!file.exists()) {
                return List.of();
            }
            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            Bukkit.getLogger().info("File %s was not loaded".formatted(file.getName()));
            return List.of();
        }
    }
}
