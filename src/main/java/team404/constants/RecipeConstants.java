package team404.constants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

public class RecipeConstants {
    public static final String REVIVE_STICK_NAME = "Посох возрождения";
    public static final String REVIVE_STICK_LORE = "Посох для возрождения";
    public static final String[] REVIVE_STICK_SHAPE = {"AX ", "XS ", "  S"};
    public static final Map<Character, Material> REVIVE_STICK_RECIPE_MATERIALS = Map.of(
            'A', Material.DIAMOND,
            'S', Material.STICK,
            'X', Material.IRON_INGOT);
    public static final Enchantment REVIVE_STICK_ENCHANTMENT = Enchantment.DURABILITY;
    public static final int REVIVE_STICK_ENCHANTMENT_LEVEL = 100;
    public static final Material REVIVE_STICK_MATERIAL = Material.STICK;
}
