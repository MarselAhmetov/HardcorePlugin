package team404.models;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class RequiredResource {
    private final Material material;
    private final int lowerThreshold;
    private final int upperThreshold;
    private final MaterialTier tier;

    public RequiredResource(Material material, int lowerThreshold, int upperThreshold, MaterialTier tier) {
        this.material = material;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.tier = tier;
    }
}
