package team404.handlers;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
        if (!playerRevivalService.getRespawnablePlayers().containsKey(player.getName())) {
            var materials = loadRequiredMaterials(getMaterialTier(player));
            playerRevivalService.addRespawnablePlayer(player.getName(), materials);
        }
    }

    @EventHandler
    public void onPlayerModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode().equals(GameMode.SPECTATOR) && playerRevivalService.getRespawnablePlayers().containsKey(player.getName())) {
            PotionEffect blindnessEffect = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false);
            player.addPotionEffect(blindnessEffect);
        }
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

