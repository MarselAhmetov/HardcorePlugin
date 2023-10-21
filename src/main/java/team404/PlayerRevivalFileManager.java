package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static team404.utils.FileUtils.appendToFile;
import static team404.utils.FileUtils.createFile;
import static team404.utils.FileUtils.readAllLines;
import static team404.utils.FileUtils.writeToFile;

public class PlayerRevivalFileManager {

    private static PlayerRevivalFileManager instance;
    private static final String PLAYERS_TO_REVIVE_FILE_NAME = "players_to_revive.txt";
    private static final String RESPAWNABLE_PLAYERS_FILE_NAME = "respawnable_players.txt";

    private final File playersToReviveFile;
    private final File respawnablePlayersFile;

    private PlayerRevivalFileManager(Plugin plugin) {
        playersToReviveFile = createFile(plugin.getDataFolder(), PLAYERS_TO_REVIVE_FILE_NAME);
        respawnablePlayersFile = createFile(plugin.getDataFolder(), RESPAWNABLE_PLAYERS_FILE_NAME);
    }

    public static PlayerRevivalFileManager getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new PlayerRevivalFileManager(plugin);
        }
        return instance;
    }

    public Map<String, List<Pair<Integer, Material>>> readRespawnablePlayers() {
        return readAllLines(respawnablePlayersFile).stream()
                .map(this::processRespawnablePlayer)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public List<String> readPlayersToSpawn() {
        return readAllLines(playersToReviveFile);
    }

    private Pair<String, List<Pair<Integer, Material>>> processRespawnablePlayer(String line) {
        var keys = line.split(",");
        var playerName = keys[0];
        var materialList = Arrays.stream(keys)
                .skip(1)
                .map(a -> {
                    var b = a.split(":");
                    return Pair.of(Integer.valueOf(b[1]), Material.getMaterial(b[0]));
                })
                .collect(Collectors.toList());
        return Pair.of(playerName, materialList);
    }

    public Collection<String> respawnablePlayersToContent(Map<String, List<Pair<Integer, Material>>> map) {
        return map.entrySet().stream()
                .map(entry -> respawnablePlayersToContent(entry.getKey(), entry.getValue()))
                .toList();
    }

    public String respawnablePlayersToContent(String playerName, List<Pair<Integer, Material>> materials) {
        return playerName + "," + materials.stream()
                .map(pair -> pair.getRight() + ":" + pair.getLeft())
                .collect(Collectors.joining(",")) + "\n";
    }

    public void syncRespawnablePlayers(Map<String, List<Pair<Integer, Material>>> map) {
        writeToFile(respawnablePlayersFile, respawnablePlayersToContent(map));
    }

    public void syncPlayersToRespawn(Collection<String> playersToRevive) {
        writeToFile(playersToReviveFile, playersToRevive);
    }

    public void addRespawnablePlayer(String playerName, List<Pair<Integer, Material>> materials) {
        appendToFile(respawnablePlayersFile, respawnablePlayersToContent(playerName, materials));
    }

    public void addPlayerToRespawn(String playerName) {
        appendToFile(playersToReviveFile, playerName + "\n");
    }


}
