package team404;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team404.handlers.DevCommandExecutor;
import team404.handlers.InventoryListener;
import team404.handlers.PlayerRespawnListener;
import team404.models.Recipes;

import java.util.Optional;

import static team404.handlers.DevCommandExecutor.GET_RESOURCES_COMMAND;
import static team404.handlers.DevCommandExecutor.GET_STICK_COMMAND;

public final class HardcorePlugin extends JavaPlugin {
    private final PlayerRevivalService playerRevivalService = PlayerRevivalService.getInstance(this);

    @Override
    public void onEnable() {
        registerEvents();
        registerRecipes();
        registerCommands();
        createConfigFiles();
        playerRevivalService.loadData();
        MaterialLoader.loadMaterials(this.getDataFolder());
        getLogger().info("Hardcore plugin enabled");
    }

    private void createConfigFiles() {
        saveDefaultConfig();
        saveResource("materials/valuable_material_list.json", false);
        saveResource("materials/food_material_list.json", false);
        saveResource("materials/nature_material_list.json", false);
        saveResource("materials/ingredients_material_list.json", false);
    }
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);}
    private void registerRecipes() {
        Bukkit.addRecipe(Recipes.getReviveStuffRecipe(this));
    }
    private void registerCommands() {
        var commandExecutor = new DevCommandExecutor(this);
        Optional.ofNullable(getCommand(GET_RESOURCES_COMMAND))
                .ifPresent(it -> it.setExecutor(commandExecutor));
        Optional.ofNullable(getCommand(GET_STICK_COMMAND))
                .ifPresent(it -> it.setExecutor(commandExecutor));
    }
}
