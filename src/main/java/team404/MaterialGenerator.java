package team404;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import team404.models.MaterialTier;
import team404.models.RequiredResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MaterialGenerator {

    @Getter
    private static final Map<String, List<RequiredResource>> materialLists = new HashMap<>();

    public static void addMaterialList(String key, List<RequiredResource> materialList) {
        materialLists.put(key, materialList);
    }

    private static Pair<Integer, Material> getRandomPair(List<RequiredResource> list, MaterialTier tier) {
        Random rand = new Random();
        List<RequiredResource> filteredList = list.stream()
                .filter(m -> m.getTier().equals(tier))
                .toList();
        RequiredResource resource = filteredList.get(rand.nextInt(filteredList.size()));
        int lower = resource.getLowerThreshold();
        int higher = resource.getUpperThreshold();
        int count;
        if (higher == lower) {
            count = higher;
        } else {
            count = rand.nextInt(higher - lower) + lower;
        }
        return Pair.of(count, resource.getMaterial());
    }

    public static List<Pair<Integer, Material>> getMaterialsList(MaterialTier tier) {
        return materialLists.values().stream()
                .map(list -> getRandomPair(list, tier))
                .toList();
    }
}
