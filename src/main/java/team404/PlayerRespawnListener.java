package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import team404.models.MaterialTier;

import java.util.List;
import java.util.Optional;

import static team404.PlayerToReviveStore.respawnablePlayers;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        respawnablePlayers.put(player.getName(), loadRequiredMaterials(getMaterialTier(player)));
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

