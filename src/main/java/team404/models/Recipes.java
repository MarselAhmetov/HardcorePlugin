package team404.models;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

import static team404.constants.RecipeConstants.*;

public class Recipes {

    public static ItemStack getReviveStuffItemStack() {
        ItemStack reviveStuff = new ItemStack(REVIVE_STICK_MATERIAL, 1);
        reviveStuff.addUnsafeEnchantment(REVIVE_STICK_ENCHANTMENT, REVIVE_STICK_ENCHANTMENT_LEVEL);
        reviveStuff.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ItemMeta reviveStuffMeta = reviveStuff.getItemMeta();
        reviveStuffMeta.displayName(Component.text(REVIVE_STICK_NAME));
        reviveStuffMeta.lore(List.of(Component.text(REVIVE_STICK_LORE)));
        reviveStuff.setItemMeta(reviveStuffMeta);
        return reviveStuff;
    }

    public static Recipe getReviveStuffRecipe(Plugin plugin) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "stick"), getReviveStuffItemStack());
        recipe.shape(REVIVE_STICK_SHAPE);
        for (Map.Entry<Character, Material> entry : REVIVE_STICK_RECIPE_MATERIALS.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }
        return recipe;
    }

    public static boolean isReviveStuff(ItemStack item) {
        return item != null
                && item.getType() == REVIVE_STICK_MATERIAL
                && item.containsEnchantment(REVIVE_STICK_ENCHANTMENT)
                && item.getEnchantmentLevel(REVIVE_STICK_ENCHANTMENT) == REVIVE_STICK_ENCHANTMENT_LEVEL;
    }
}
