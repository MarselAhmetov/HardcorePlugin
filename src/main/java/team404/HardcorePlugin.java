package team404;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team404.models.Recipes;

public final class HardcorePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.addRecipe(Recipes.getReviveStuffRecipe(this));
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }
}
