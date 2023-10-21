package team404.handlers;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import team404.PlayerRevivalService;

import java.util.List;

import static team404.models.Recipes.getReviveStuffItemStack;

public class DevCommandExecutor implements CommandExecutor {

    public static final String GET_RESOURCES_COMMAND = "dev";
    public static final String GET_STICK_COMMAND = "stick";

    private final PlayerRevivalService playerRevivalService;

    public DevCommandExecutor(Plugin plugin) {
        playerRevivalService = PlayerRevivalService.getInstance(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }

        if (!player.isOp()) {
            return true;
        }

        if (command.getName().equals(GET_RESOURCES_COMMAND)) {
            for (List<Pair<Integer, Material>> value : playerRevivalService.getRespawnablePlayers().values()) {
                for (Pair<Integer, Material> pair : value) {
                    player.getInventory().addItem(new ItemStack(pair.getRight(), pair.getLeft()));
                }
            }
        }
        if (command.getName().equals(GET_STICK_COMMAND)) {
            player.getInventory().addItem(getReviveStuffItemStack());
        }
        return true;
    }
}

