package team404;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class PlayerRevivalCache {
    private final Map<String, List<Pair<Integer, Material>>> respawnablePlayers = new HashMap<>();
    private final Set<String> playersToSpawn = new HashSet<>();

    private static PlayerRevivalCache instance;

    private PlayerRevivalCache() {}

    public static PlayerRevivalCache getInstance() {
        if (instance == null) {
            instance = new PlayerRevivalCache();
        }
        return instance;
    }

    public void addAllRespawnablePlayers(Map<String, List<Pair<Integer, Material>>> players) {
        respawnablePlayers.putAll(players);
    }

    public void addAllPlayersToSpawn(Collection<String> players) {
        playersToSpawn.addAll(players);
    }
}
