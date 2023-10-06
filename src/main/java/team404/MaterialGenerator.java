package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import team404.RandomMaterial.RandomMaterial;
import team404.RandomMaterial.RandomMaterialTier;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MaterialGenerator {
    private static final List<RandomMaterial> valuableMaterialList = Arrays.asList(
            new RandomMaterial(Material.COPPER_INGOT, 1, 10, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.COPPER_INGOT, 10, 20, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COPPER_INGOT, 20, 32, RandomMaterialTier.END),
            new RandomMaterial(Material.COAL, 1, 10, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.COAL, 10, 20, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COAL, 20, 32, RandomMaterialTier.END),
            new RandomMaterial(Material.IRON_INGOT, 1, 10, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.IRON_INGOT, 10, 20, RandomMaterialTier.HELL),
            new RandomMaterial(Material.IRON_INGOT, 20, 32, RandomMaterialTier.END),
            new RandomMaterial(Material.GOLD_INGOT, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.GOLD_INGOT, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.GOLD_INGOT, 10, 15, RandomMaterialTier.END),
            new RandomMaterial(Material.DIAMOND, 1, 3, RandomMaterialTier.HELL),
            new RandomMaterial(Material.DIAMOND, 3, 5, RandomMaterialTier.END),
            new RandomMaterial(Material.EMERALD, 1, 3, RandomMaterialTier.HELL),
            new RandomMaterial(Material.EMERALD, 3, 5, RandomMaterialTier.END),
            new RandomMaterial(Material.NETHERITE_INGOT, 1, 2, RandomMaterialTier.END)
    );

    private static final List<RandomMaterial> foodMaterialList = Arrays.asList(
            new RandomMaterial(Material.CARROT, 10, 15, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.APPLE, 10, 15, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.SWEET_BERRIES, 10, 15, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.BAKED_POTATO, 10, 15, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.BEETROOT, 10, 15, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.BREAD, 10, 15, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.MELON, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.PUMPKIN, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.COOKED_PORKCHOP, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COOKED_BEEF, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COOKED_CHICKEN, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COOKED_RABBIT, 1, 3, RandomMaterialTier.HELL),
            new RandomMaterial(Material.GLOW_BERRIES, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COOKED_MUTTON, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COOKED_COD, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.COOKED_SALMON, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.GOLDEN_APPLE, 1, 3, RandomMaterialTier.END),
            new RandomMaterial(Material.BEETROOT_SOUP, 1, 1, RandomMaterialTier.END),
            new RandomMaterial(Material.RABBIT_STEW, 1, 1, RandomMaterialTier.END),
            new RandomMaterial(Material.MUSHROOM_STEW, 1, 1, RandomMaterialTier.END),
            new RandomMaterial(Material.GOLDEN_CARROT, 1, 3, RandomMaterialTier.END),
            new RandomMaterial(Material.PUMPKIN_PIE, 1, 3, RandomMaterialTier.END)

    );

    private static final List<RandomMaterial> natureMaterialList = Arrays.asList(
            new RandomMaterial(Material.BAMBOO, 10, 20, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.SUGAR_CANE, 10, 20, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.CACTUS, 3, 10, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.ACACIA_LOG, 10, 20, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.BIRCH_LOG, 10, 20, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.OAK_LOG, 10, 20, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.SPRUCE_LOG, 10, 20, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.CRIMSON_STEM, 10, 20, RandomMaterialTier.HELL),
            new RandomMaterial(Material.WARPED_STEM, 10, 20, RandomMaterialTier.HELL),
            new RandomMaterial(Material.DARK_OAK_LOG, 10, 20, RandomMaterialTier.HELL),
            new RandomMaterial(Material.JUNGLE_LOG, 10, 20, RandomMaterialTier.HELL),
            new RandomMaterial(Material.CHERRY_LOG, 10, 20, RandomMaterialTier.END),
            new RandomMaterial(Material.MANGROVE_LOG, 10, 20, RandomMaterialTier.END),
            new RandomMaterial(Material.DANDELION, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.POPPY, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.BLUE_ORCHID, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.ALLIUM, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.AZURE_BLUET, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.RED_TULIP, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.ORANGE_TULIP, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.WHITE_TULIP, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.PINK_TULIP, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.OXEYE_DAISY, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.CORNFLOWER, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.LILY_OF_THE_VALLEY, 1, 5, RandomMaterialTier.HELL),
            new RandomMaterial(Material.NETHER_WART, 5, 10, RandomMaterialTier.HELL),
            new RandomMaterial(Material.PINK_PETALS, 1, 3, RandomMaterialTier.END),
            new RandomMaterial(Material.SUNFLOWER, 1, 3, RandomMaterialTier.END),
            new RandomMaterial(Material.LILAC, 1, 3, RandomMaterialTier.END),
            new RandomMaterial(Material.ROSE_BUSH, 1, 3, RandomMaterialTier.END),
            new RandomMaterial(Material.SEA_PICKLE, 1, 3, RandomMaterialTier.END)
    );

    private static final List<RandomMaterial> ingredientsMaterialList = Arrays.asList(
            new RandomMaterial(Material.BONE, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.STRING, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.ROTTEN_FLESH, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.GUNPOWDER, 1, 5, RandomMaterialTier.OVERWORLD),
            new RandomMaterial(Material.FERMENTED_SPIDER_EYE, 1, 3, RandomMaterialTier.HELL),
            new RandomMaterial(Material.BLAZE_ROD, 1, 3, RandomMaterialTier.HELL),
            new RandomMaterial(Material.GOLD_NUGGET, 5, 15, RandomMaterialTier.HELL),
            new RandomMaterial(Material.GLOWSTONE, 5, 15, RandomMaterialTier.HELL),
            new RandomMaterial(Material.MAGMA_CREAM, 5, 15, RandomMaterialTier.HELL),
            new RandomMaterial(Material.ENDER_PEARL, 1, 3, RandomMaterialTier.END),
            new RandomMaterial(Material.PRISMARINE_SHARD, 1, 5, RandomMaterialTier.END),
            new RandomMaterial(Material.PRISMARINE_CRYSTALS, 1, 5, RandomMaterialTier.END)
    );

    private Pair<Integer, Material> getRandomPair(List<RandomMaterial> list, RandomMaterialTier tier) {
        Random rand = new Random();
        Predicate<RandomMaterial> byTier = material -> material.tier == tier;
        List<RandomMaterial> filteredList = list.stream().filter(byTier).collect(Collectors.toList());
        RandomMaterial randomElement = filteredList.get(rand.nextInt(filteredList.size()));
        Integer lower = randomElement.lowerThreshold;
        Integer higher = randomElement.upperThreshold;
        Integer randomCount;
        if (higher > lower) {
            randomCount = rand.nextInt(higher - lower) + lower;
        } else {
            randomCount = lower;
        }
        return Pair.of(randomCount, randomElement.material);
    }

    public List<Pair<Integer, Material>> getMaterialsList(RandomMaterialTier tier) {
        return Arrays.asList(
                getRandomPair(valuableMaterialList, tier),
                getRandomPair(foodMaterialList, tier),
                getRandomPair(natureMaterialList, tier),
                getRandomPair(ingredientsMaterialList, tier)
        );
    }
}
