package team404.untitled;

import org.bukkit.plugin.java.JavaPlugin;

public final class HardcorePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new StickListener(), this);
    }
}