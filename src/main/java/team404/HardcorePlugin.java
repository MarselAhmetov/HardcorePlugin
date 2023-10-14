package team404;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team404.models.Recipes;

import static team404.DevCommandExecutor.GET_RESOURCES_COMMAND;
import static team404.DevCommandExecutor.GET_STICK_COMMAND;

public final class HardcorePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        registerEvents();
        registerRecipes();
        registerCommands();
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);}
    public void registerRecipes() {
        Bukkit.addRecipe(Recipes.getReviveStuffRecipe(this));
    }
    public void registerCommands() {
        var commandExecutor = new DevCommandExecutor();
        getCommand(GET_RESOURCES_COMMAND).setExecutor(commandExecutor);
        getCommand(GET_STICK_COMMAND).setExecutor(commandExecutor);
    }
}
