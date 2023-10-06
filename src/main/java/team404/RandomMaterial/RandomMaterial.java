package team404.RandomMaterial;

import org.bukkit.*;

public class RandomMaterial {
    public Material material;
    public Integer lowerThreshold;
    public Integer upperThreshold;
    public RandomMaterialTier tier;

    public RandomMaterial(Material material,  Integer lowerThreshold, Integer upperThreshold, RandomMaterialTier tier) {
        this.material = material;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.tier = tier;
    }
}
