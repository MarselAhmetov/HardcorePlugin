package team404.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import team404.PlayerRevivalService;
import team404.models.requests.PlayerRevivedRequest;
import team404.utils.TextUtils;
import team404.constants.ColorHexConstants;
import team404.models.Recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static team404.constants.InventoryConstants.INVENTORY_NAME;
import static team404.constants.MessagesConstants.NOT_ENOUGH_RESOURCES;
import static team404.constants.MessagesConstants.PLAYER_ALREADY_REVIVED;
import static team404.constants.MessagesConstants.PLAYER_REVIVED;
import static team404.constants.MessagesConstants.YOU_WILL_BE_REVIVED_IN;
import static team404.constants.TelegramBotConstants.PLAYER_REVIVED_PATH;
import static team404.utils.HttpClient.sendPostRequest;

public class InventoryListener implements Listener {
    private static final String WORLD_NAME = "world";
    private static final int SECONDS_BEFORE_RESPAWN = 5;
    private static final int INVENTORY_ROW_SIZE = 9;
    private final Plugin plugin;
    private final PlayerRevivalService playerRevivalService;

    public InventoryListener(Plugin plugin) {
        this.plugin = plugin;
        this.playerRevivalService = PlayerRevivalService.getInstance(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OfflinePlayer player = event.getPlayer();
        if (playerRevivalService.getPlayersToSpawn().contains(player.getName())) {
            respawnPlayer(player);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (!Recipes.isReviveStuff(item)) {
            return;
        }
        Player player = event.getPlayer();
        Inventory inventory = getInventory(player);
        player.openInventory(inventory);
    }

    private Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, getInventorySize(), Component.text(INVENTORY_NAME));
        for (Map.Entry<String, List<Pair<Integer, Material>>> entry : playerRevivalService.getRespawnablePlayers().entrySet()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
            List<Component> lore = new ArrayList<>();
            for (Pair<Integer, Material> pair : entry.getValue()) {
                ItemStack name = new ItemStack(pair.getRight());
                if (checkMaterialInInventory(player, pair)) {
                    lore.add(TextUtils.appendCheckMark(name)
                            .appendSpace()
                            .append(Component.text(pair.getLeft())));
                } else {
                    lore.add(TextUtils.appendCross(name)
                            .appendSpace()
                            .append(Component.text(pair.getLeft())));
                }
            }
            skullMeta.lore(lore);
            playerHead.setItemMeta(skullMeta);
            inv.addItem(playerHead);
        }
        return inv;
    }

    private int getInventorySize() {
        var players = playerRevivalService.getRespawnablePlayers();
        if (players.isEmpty()) {
            return INVENTORY_ROW_SIZE;
        }
        int rowsCount = players.size() / INVENTORY_ROW_SIZE;
        if (players.size() % INVENTORY_ROW_SIZE != 0) {
            rowsCount++;
        }
        return rowsCount * INVENTORY_ROW_SIZE;
    }

    private boolean checkMaterialInInventory(Player player, Pair<Integer, Material> material) {
        int requiredAmount = material.getLeft();
        Material requiredMaterial = material.getRight();

        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == requiredMaterial) {
                count += item.getAmount();
            }
        }

        return count >= requiredAmount;
    }

    public boolean checkMaterialInInventory(Player player, List<Pair<Integer, Material>> materials) {
        if (materials == null) {
            return false;
        }
        for (Pair<Integer, Material> material : materials) {
            if (!checkMaterialInInventory(player, material)) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) ||
                !PlainTextComponentSerializer.plainText().serialize(event.getView().title()).equals(INVENTORY_NAME)) {
            return;
        }
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) {
            return;
        }
        event.setCancelled(true); // Prevents taking items from the inventory
        // check that head clicked
        if (clickedItem.getType() != Material.PLAYER_HEAD) {
            return;
        }

        OfflinePlayer offlinePlayerToSpawn = ((SkullMeta) clickedItem.getItemMeta()).getOwningPlayer();
        // check that player not revived yet
        var playerToSpawnName = offlinePlayerToSpawn.getName();
        if (!playerRevivalService.getRespawnablePlayers().containsKey(playerToSpawnName)) {
            player.sendMessage(PLAYER_ALREADY_REVIVED);
            return;
        }
        // check that player who clicked has enough materials
        List<Pair<Integer, Material>> materials = playerRevivalService.getRespawnablePlayers().get(playerToSpawnName);
        if (!checkMaterialInInventory(player, materials)) {
            player.sendMessage(Component.text(NOT_ENOUGH_RESOURCES)
                    .color(TextColor.fromHexString(ColorHexConstants.RED_HEX)));
            return;
        }
        // add player to spawn map
        playerRevivalService.addPlayerToRespawn(playerToSpawnName);
        playerRevivalService.removeRespawnablePlayer(playerToSpawnName);

        player.sendMessage(
                Component.text(PLAYER_REVIVED.formatted(playerToSpawnName))
                        .color(TextColor.fromHexString(ColorHexConstants.GREEN_HEX))
        );
        player.closeInventory();
        removeItems(player, materials);
        // try respawn
        respawnPlayer(offlinePlayerToSpawn);
    }

    private void respawnPlayer(OfflinePlayer offlinePlayer) {
        final AtomicInteger counter = new AtomicInteger(SECONDS_BEFORE_RESPAWN);
        if (!offlinePlayer.isOnline()) {
            return;
        }
        Player player = offlinePlayer.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                int currentCounter = counter.getAndDecrement();
                player.clearTitle();
                if (currentCounter > 0) {
                    player.showTitle(
                            Title.title(
                                    Component.text(YOU_WILL_BE_REVIVED_IN.formatted(currentCounter)),
                                    Component.empty()
                            )
                    );
                } else {
                    // check that player is online
                    if (offlinePlayer.isOnline()) {
                        // if online teleport and set gamemode and remove from to spawn list
                        Location location = offlinePlayer.getRespawnLocation();
                        location = location != null ? location : Bukkit.getWorld(WORLD_NAME).getSpawnLocation();
                        player.teleport(location);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.spawnParticle(Particle.TOTEM_OF_UNDYING, location, 100);
                        player.playEffect(EntityEffect.TOTEM_RESURRECT);
                        player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
                        player.clearActivePotionEffects();
                        playerRevivalService.removePlayerToRespawn(player.getName());
                        sendPostRequest(
                                plugin.getConfig().getString("bot-address") + PLAYER_REVIVED_PATH,
                                new PlayerRevivedRequest(player.getName())
                        );
                    }
                    // if not do nothing
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void removeItems(Player player, List<Pair<Integer, Material>> itemsList) {
        for (Pair<Integer, Material> amountMaterialPair : itemsList) {
            removeItems(player, amountMaterialPair);
        }
    }

    private void removeItems(Player player, Pair<Integer, Material> itemToRemove) {
        int remaining = itemToRemove.getLeft();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType().equals(itemToRemove.getRight())) {
                int inStack = item.getAmount();
                if (inStack > remaining) {
                    item.setAmount(inStack - remaining);
                    return;
                } else {
                    player.getInventory().remove(item);
                    remaining -= inStack;
                }
                if (remaining <= 0) {
                    return;
                }
            }
        }
    }
}

