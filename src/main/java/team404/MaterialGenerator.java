package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import team404.models.RequiredResource;
import team404.models.MaterialTier;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static team404.models.MaterialTier.END;
import static team404.models.MaterialTier.NETHER;
import static team404.models.MaterialTier.OVER_WORLD;

public class MaterialGenerator {
    private static final List<RequiredResource> valuableMaterialList = List.of(
            new RequiredResource(Material.COPPER_INGOT, 1, 10, OVER_WORLD),
            new RequiredResource(Material.COPPER_INGOT, 10, 20, NETHER),
            new RequiredResource(Material.COPPER_INGOT, 20, 32, END),
            new RequiredResource(Material.COAL, 1, 10, OVER_WORLD),
            new RequiredResource(Material.COAL, 10, 20, NETHER),
            new RequiredResource(Material.COAL, 20, 32, END),
            new RequiredResource(Material.IRON_INGOT, 1, 10, OVER_WORLD),
            new RequiredResource(Material.IRON_INGOT, 10, 20, NETHER),
            new RequiredResource(Material.IRON_INGOT, 20, 32, END),
            new RequiredResource(Material.GOLD_INGOT, 1, 5, OVER_WORLD),
            new RequiredResource(Material.GOLD_INGOT, 5, 10, NETHER),
            new RequiredResource(Material.GOLD_INGOT, 10, 15, END),
            new RequiredResource(Material.DIAMOND, 1, 3, NETHER),
            new RequiredResource(Material.DIAMOND, 3, 5, END),
            new RequiredResource(Material.EMERALD, 1, 3, NETHER),
            new RequiredResource(Material.EMERALD, 3, 5, END),
            new RequiredResource(Material.NETHERITE_INGOT, 1, 2, END)
    );

    private static final List<RequiredResource> foodMaterialList = List.of(
            new RequiredResource(Material.CARROT, 10, 15, OVER_WORLD),
            new RequiredResource(Material.APPLE, 10, 15, OVER_WORLD),
            new RequiredResource(Material.SWEET_BERRIES, 10, 15, OVER_WORLD),
            new RequiredResource(Material.BAKED_POTATO, 10, 15, OVER_WORLD),
            new RequiredResource(Material.BEETROOT, 10, 15, OVER_WORLD),
            new RequiredResource(Material.BREAD, 10, 15, OVER_WORLD),
            new RequiredResource(Material.MELON, 1, 5, OVER_WORLD),
            new RequiredResource(Material.PUMPKIN, 1, 5, OVER_WORLD),
            new RequiredResource(Material.COOKED_PORKCHOP, 5, 10, NETHER),
            new RequiredResource(Material.COOKED_BEEF, 5, 10, NETHER),
            new RequiredResource(Material.COOKED_CHICKEN, 5, 10, NETHER),
            new RequiredResource(Material.COOKED_RABBIT, 1, 3, NETHER),
            new RequiredResource(Material.GLOW_BERRIES, 5, 10, NETHER),
            new RequiredResource(Material.COOKED_MUTTON, 5, 10, NETHER),
            new RequiredResource(Material.COOKED_COD, 5, 10, NETHER),
            new RequiredResource(Material.COOKED_SALMON, 5, 10, NETHER),
            new RequiredResource(Material.GOLDEN_APPLE, 1, 3, END),
            new RequiredResource(Material.BEETROOT_SOUP, 1, 1, END),
            new RequiredResource(Material.RABBIT_STEW, 1, 1, END),
            new RequiredResource(Material.MUSHROOM_STEW, 1, 1, END),
            new RequiredResource(Material.GOLDEN_CARROT, 1, 3, END),
            new RequiredResource(Material.PUMPKIN_PIE, 1, 3, END)

    );

    private static final List<RequiredResource> natureMaterialList = List.of(
            new RequiredResource(Material.BAMBOO, 10, 20, OVER_WORLD),
            new RequiredResource(Material.SUGAR_CANE, 10, 20, OVER_WORLD),
            new RequiredResource(Material.CACTUS, 3, 10, OVER_WORLD),
            new RequiredResource(Material.ACACIA_LOG, 10, 20, OVER_WORLD),
            new RequiredResource(Material.BIRCH_LOG, 10, 20, OVER_WORLD),
            new RequiredResource(Material.OAK_LOG, 10, 20, OVER_WORLD),
            new RequiredResource(Material.SPRUCE_LOG, 10, 20, OVER_WORLD),
            new RequiredResource(Material.CRIMSON_STEM, 10, 20, NETHER),
            new RequiredResource(Material.WARPED_STEM, 10, 20, NETHER),
            new RequiredResource(Material.DARK_OAK_LOG, 10, 20, NETHER),
            new RequiredResource(Material.JUNGLE_LOG, 10, 20, NETHER),
            new RequiredResource(Material.CHERRY_LOG, 10, 20, END),
            new RequiredResource(Material.MANGROVE_LOG, 10, 20, END),
            new RequiredResource(Material.DANDELION, 1, 5, OVER_WORLD),
            new RequiredResource(Material.POPPY, 1, 5, OVER_WORLD),
            new RequiredResource(Material.BLUE_ORCHID, 1, 5, NETHER),
            new RequiredResource(Material.ALLIUM, 1, 5, NETHER),
            new RequiredResource(Material.AZURE_BLUET, 1, 5, NETHER),
            new RequiredResource(Material.RED_TULIP, 1, 5, NETHER),
            new RequiredResource(Material.ORANGE_TULIP, 1, 5, NETHER),
            new RequiredResource(Material.WHITE_TULIP, 1, 5, NETHER),
            new RequiredResource(Material.PINK_TULIP, 1, 5, NETHER),
            new RequiredResource(Material.OXEYE_DAISY, 1, 5, NETHER),
            new RequiredResource(Material.CORNFLOWER, 1, 5, NETHER),
            new RequiredResource(Material.LILY_OF_THE_VALLEY, 1, 5, NETHER),
            new RequiredResource(Material.NETHER_WART, 5, 10, NETHER),
            new RequiredResource(Material.PINK_PETALS, 1, 3, END),
            new RequiredResource(Material.SUNFLOWER, 1, 3, END),
            new RequiredResource(Material.LILAC, 1, 3, END),
            new RequiredResource(Material.ROSE_BUSH, 1, 3, END),
            new RequiredResource(Material.SEA_PICKLE, 1, 3, END)
    );

    private static final List<RequiredResource> ingredientsMaterialList = List.of(
            new RequiredResource(Material.BONE, 1, 5, OVER_WORLD),
            new RequiredResource(Material.STRING, 1, 5, OVER_WORLD),
            new RequiredResource(Material.ROTTEN_FLESH, 1, 5, OVER_WORLD),
            new RequiredResource(Material.GUNPOWDER, 1, 5, OVER_WORLD),
            new RequiredResource(Material.FERMENTED_SPIDER_EYE, 1, 3, NETHER),
            new RequiredResource(Material.BLAZE_ROD, 1, 3, NETHER),
            new RequiredResource(Material.GOLD_NUGGET, 5, 15, NETHER),
            new RequiredResource(Material.GLOWSTONE, 5, 15, NETHER),
            new RequiredResource(Material.MAGMA_CREAM, 5, 15, NETHER),
            new RequiredResource(Material.ENDER_PEARL, 1, 3, END),
            new RequiredResource(Material.PRISMARINE_SHARD, 1, 5, END),
            new RequiredResource(Material.PRISMARINE_CRYSTALS, 1, 5, END)
    );

    private Pair<Integer, Material> getRandomPair(List<RequiredResource> list, MaterialTier tier) {
        Random rand = new Random();
        List<RequiredResource> filteredList = list.stream()
                .filter(m -> m.getTier().equals(tier))
                .collect(Collectors.toList());
        RequiredResource resource = filteredList.get(rand.nextInt(filteredList.size()));
        int lower = resource.getLowerThreshold();
        int higher = resource.getUpperThreshold();
        Integer count;
        if (higher == lower) {
            count = higher;
        } else {
            count = rand.nextInt(higher - lower) + lower;
        }
        return Pair.of(count, resource.getMaterial());
    }

    public List<Pair<Integer, Material>> getMaterialsList(MaterialTier tier) {
        return Arrays.asList(
                getRandomPair(valuableMaterialList, tier),
                getRandomPair(foodMaterialList, tier),
                getRandomPair(natureMaterialList, tier),
                getRandomPair(ingredientsMaterialList, tier)
        );
    }
}
