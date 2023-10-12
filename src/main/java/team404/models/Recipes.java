package team404.models;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import team404.constants.RecipeConstants;

import java.util.List;

public class Recipes {

    public static Recipe getReviveStuffRecipe(Plugin plugin) {
        ItemStack reviveStuff = new ItemStack(Material.STICK, 1);
        ItemMeta reviveStuffMeta = reviveStuff.getItemMeta();
        reviveStuffMeta.displayName(Component.text(RecipeConstants.REVIVE_STICK_NAME));
        reviveStuffMeta.lore(List.of(Component.text(RecipeConstants.REVIVE_STICK_LORE)));
        reviveStuff.setItemMeta(reviveStuffMeta);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "stick"), reviveStuff);
        recipe.shape(RecipeConstants.REVIVE_STICK_SHAPE);
        recipe.setIngredient('A', Material.DIAMOND);
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('X', Material.IRON_INGOT);
        return recipe;
    }
}
