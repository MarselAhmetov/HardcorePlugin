package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerRevivalService {

    private static PlayerRevivalService instance;
    private final PlayerRevivalCache cache;
    private final PlayerRevivalFileManager fileRepository;
    private PlayerRevivalService(Plugin plugin) {
        cache = PlayerRevivalCache.getInstance();
        fileRepository = PlayerRevivalFileManager.getInstance(plugin);
    }

    public static PlayerRevivalService getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new PlayerRevivalService(plugin);
        }
        return instance;
    }

    public void loadData() {
        cache.addAllPlayersToSpawn(fileRepository.readPlayersToSpawn());
        cache.addAllRespawnablePlayers(fileRepository.readRespawnablePlayers());
    }

    public void addRespawnablePlayer(String playerName, List<Pair<Integer, Material>> materialList) {
        cache.getRespawnablePlayers().put(playerName, materialList);
        fileRepository.addRespawnablePlayer(playerName, materialList);
    }

    public void addPlayerToRespawn(String playerName) {
        cache.getPlayersToSpawn().add(playerName);
        fileRepository.addPlayerToRespawn(playerName);
    }

    public void removeRespawnablePlayer(String playerName) {
        cache.getRespawnablePlayers().remove(playerName);
        fileRepository.syncRespawnablePlayers(cache.getRespawnablePlayers());
    }

    public void removePlayerToRespawn(String playerName) {
        cache.getPlayersToSpawn().remove(playerName);
        fileRepository.syncPlayersToRespawn(cache.getPlayersToSpawn());
    }

    public Map<String, List<Pair<Integer, Material>>> getRespawnablePlayers() {
        return cache.getRespawnablePlayers();
    }

    public Set<String> getPlayersToSpawn() {
        return cache.getPlayersToSpawn();
    }
}

