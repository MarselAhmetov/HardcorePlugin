package team404.constant

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
// Common
const val PLUGIN_NAMESPACE: String = "hardcore-plugin"
const val WORLD_NAME = "world"
const val SECONDS_BEFORE_RESPAWN = 5

// TelegramBotConstants
const val PLAYER_DEAD_PATH: String = "/api/player/dead"
const val PLAYER_REVIVED_PATH: String = "/api/player/revived"

// RecipeConstants
const val REVIVE_STICK_NAME: String = "Посох возрождения"
const val REVIVE_STICK_LORE: String = "Посох для возрождения"
val REVIVE_STICK_SHAPE: Array<String> = arrayOf("S")
val REVIVE_STICK_RECIPE_MATERIALS: Map<Char, Material> = mapOf('S' to Material.STICK)
val REVIVE_STICK_ENCHANTMENT: Enchantment = Enchantment.UNBREAKING
const val REVIVE_STICK_ENCHANTMENT_LEVEL: Int = 100
val REVIVE_STICK_MATERIAL: Material = Material.STICK

// MessagesConstants
const val NOT_ENOUGH_RESOURCES: String = "У вас не хватает ресурсов"
const val PLAYER_ALREADY_REVIVED: String = "Игрок уже возрожден"
const val PLAYER_BOUGHT_BACK: String = "Игрок выкупился"
const val YOU_WILL_BE_REVIVED_IN: String = "Вы будете воскрешены через %s сек..."
const val PLAYER_REVIVED: String = "Игрок %s воскрешается"
const val CANT_BUYBACK: String = "Вы не можете большое выкупаться"
const val YOU_ARE_FREE: String = "Вы свободны"

// InventoryConstants
const val REVIVE_INVENTORY_NAME: String = "Возрождение игроков"
const val BUYBACK_INVENTORY_NAME: String = "Выкуп"
const val INVENTORY_ROW_SIZE: Int = 9
const val MAX_BUYBACK_COUNT: Int = 1

// Namespace keys Must be [a-z0-9/._-]
const val BUYBACK_TIME_LEFT_KEY: String = "buyback_time_left"
const val BUYBACK_COUNT_KEY: String = "buyback_count"
const val BUYBACK_TIMER_KEY: String = "buyback_timer"
const val RESPAWN_MATERIALS_MULTIPLIER_KEY: String = "respawn_materials_multiplier"
const val REVIVE_STICK_RECIPE_KEY: String = "revive_stick"

// Color Constants
const val GREEN_HEX: String = "#00FF00"
const val RED_HEX: String = "#FF0000"
const val GOLDEN_HEX: String = "#FFD700"
