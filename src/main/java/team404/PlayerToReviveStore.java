package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerToReviveStore {
    public static final Map<String, List<Pair<Integer, Material>>> respawnablePlayers = new HashMap<>();
    public static final Set<String> playersToSpawn = new HashSet<>();
}
