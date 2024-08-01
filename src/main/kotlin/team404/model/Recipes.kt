package team404.model

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.Plugin
import team404.service.NamespaceKeyManager
import team404.constant.*

object Recipes {

    fun getReviveStuffItemStack(): ItemStack =
        ItemStack(REVIVE_STICK_MATERIAL, 1).apply {
            this.addUnsafeEnchantment(REVIVE_STICK_ENCHANTMENT, REVIVE_STICK_ENCHANTMENT_LEVEL)
            this.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            this.lore(listOf(Component.text(REVIVE_STICK_LORE)))
            this.itemMeta.displayName(Component.text(REVIVE_STICK_NAME))
        }

    fun getReviveStuffRecipe(plugin: Plugin): Recipe {
        val recipe = ShapedRecipe(NamespaceKeyManager.getKey(plugin, REVIVE_STICK_RECIPE_KEY), getReviveStuffItemStack())
        recipe.shape(*REVIVE_STICK_SHAPE)
        for ((key, value) in REVIVE_STICK_RECIPE_MATERIALS) {
            recipe.setIngredient(key, value)
        }
        return recipe
    }

    fun isReviveStuff(item: ItemStack?): Boolean {
        return item != null &&
                item.type == REVIVE_STICK_MATERIAL &&
                item.containsEnchantment(REVIVE_STICK_ENCHANTMENT) &&
                item.getEnchantmentLevel(REVIVE_STICK_ENCHANTMENT) == REVIVE_STICK_ENCHANTMENT_LEVEL
    }
}