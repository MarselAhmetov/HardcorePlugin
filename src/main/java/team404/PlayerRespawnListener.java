package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import team404.models.MaterialTier;

import java.util.List;

import static team404.PlayerToReviveStore.respawnablePlayers;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        respawnablePlayers.put(player.getName(), loadRequiredMaterials(getMaterialTier(player)));
    }

    private MaterialTier getMaterialTier(Player player) {
        Advancement netherAdvancement = Bukkit.getAdvancement(NamespacedKey.fromString("story/enter_the_nether"));
        Advancement endAdvancement = Bukkit.getAdvancement(NamespacedKey.fromString("story/enter_the_end"));

        if (player.getAdvancementProgress(netherAdvancement).isDone()) {
            return MaterialTier.NETHER;
        }
        if (player.getAdvancementProgress(endAdvancement).isDone()) {
            return MaterialTier.END;
        }
        return MaterialTier.OVER_WORLD;
    }

    public List<Pair<Integer, Material>> loadRequiredMaterials(MaterialTier tier) {
        MaterialGenerator generator = new MaterialGenerator();
        return generator.getMaterialsList(tier);
    }
}

