package team404.handlers;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import team404.MaterialGenerator;
import team404.PlayerRevivalService;
import team404.models.MaterialTier;

import java.util.List;
import java.util.Optional;

public class PlayerRespawnListener implements Listener {

    private final PlayerRevivalService playerRevivalService;

    public PlayerRespawnListener(Plugin plugin) {
        this.playerRevivalService = PlayerRevivalService.getInstance(plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        var materials = loadRequiredMaterials(getMaterialTier(player));
        playerRevivalService.addRespawnablePlayer(player.getName(), materials);
    }

    private MaterialTier getMaterialTier(Player player) {
        if (isAdvancementDone(player, "story/enter_the_end")) {
            return MaterialTier.END;
        }
        if (isAdvancementDone(player, "story/enter_the_nether")) {
            return MaterialTier.NETHER;
        }
        return MaterialTier.OVER_WORLD;
    }

    private boolean isAdvancementDone(Player player, String key) {
        return Optional.ofNullable(NamespacedKey.fromString(key))
                .map(Bukkit::getAdvancement)
                .map(it -> player.getAdvancementProgress(it).isDone())
                .orElse(false);
    }

    public List<Pair<Integer, Material>> loadRequiredMaterials(MaterialTier tier) {
        return MaterialGenerator.getMaterialsList(tier);
    }
}

