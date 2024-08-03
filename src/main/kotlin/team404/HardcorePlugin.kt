package team404

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import team404.listener.InventoryListener
import team404.listener.PlayerRespawnListener
import team404.listener.command.BuybackCommandExecutor
import team404.listener.command.BuybackCommandExecutor.Companion.BUYBACK_COMMAND
import team404.listener.command.DevCommandExecutor
import team404.listener.command.DevCommandExecutor.Companion.GET_RESOURCES_COMMAND
import team404.listener.command.StickCommandExecutor
import team404.listener.command.StickCommandExecutor.Companion.GET_STICK_COMMAND
import team404.model.Recipes
import team404.service.MaterialLoader
import team404.service.PlayerRevivalService
import java.io.File

var botAddress: String? = null
var pluginDataFolder: File? = null
var buybackTime: Long = 72000


class HardcorePlugin : JavaPlugin() {
    private val playerRevivalService = PlayerRevivalService.getInstance(this)

    override fun onEnable() {
        botAddress = config.getString("bot-address")
        pluginDataFolder = this.dataFolder
        buybackTime = config.getLong("buyback-time")
        registerEvents()
        registerRecipes()
        registerCommands()
        createConfigFiles()
        playerRevivalService.loadData()
        MaterialLoader.loadMaterials()
        logger.info("Hardcore plugin enabled")
    }

    private fun createConfigFiles() {
        saveDefaultConfig()
        saveResource("materials/valuable_material_list.json", false)
        saveResource("materials/food_material_list.json", false)
        saveResource("materials/nature_material_list.json", false)
        saveResource("materials/ingredients_material_list.json", false)
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerRespawnListener(this), this)
        server.pluginManager.registerEvents(InventoryListener(this), this)
    }

    private fun registerRecipes() {
        Bukkit.addRecipe(Recipes.getReviveStuffRecipe())
    }

    private fun registerCommands() {
        getCommand(GET_RESOURCES_COMMAND)?.setExecutor(DevCommandExecutor(this))
        getCommand(GET_STICK_COMMAND)?.setExecutor(StickCommandExecutor())
        getCommand(BUYBACK_COMMAND)?.setExecutor(BuybackCommandExecutor(this))
    }
}
