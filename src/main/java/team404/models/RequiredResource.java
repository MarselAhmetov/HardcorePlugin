package team404.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

@Data
@NoArgsConstructor
public class RequiredResource {
    private Material material;
    private int lowerThreshold;
    private int upperThreshold;
    private MaterialTier tier;
}
