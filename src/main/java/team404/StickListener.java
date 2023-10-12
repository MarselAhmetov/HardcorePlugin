package team404;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import team404.constants.ColorHexConstants;
import team404.models.MaterialTier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static team404.constants.InventoryConstants.INVENTORY_NAME;
import static team404.constants.InventoryConstants.INVENTORY_ROW_SIZE;
import static team404.constants.MessagesConstants.*;

public class StickListener implements Listener {

    private final static Material MATERIAL_TO_CLICK = Material.STICK;
    private final static String WORLD_NAME = "world";

    private final Map<String, List<Pair<Integer, Material>>> map = new HashMap<>();

    private final HardcorePlugin plugin;

    public StickListener(HardcorePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.getType() != MATERIAL_TO_CLICK) {
            return;
        }
        Player player = event.getPlayer();
        Inventory inventory = getInventory(player);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        map.put(player.getName(), loadRequiredMaterials(getMaterialTier(player)));
    }

    private Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, getInventorySize(), Component.text(INVENTORY_NAME));
        for (Map.Entry<String, List<Pair<Integer, Material>>> entry : map.entrySet()) {
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
        if (map.isEmpty()) {
            return INVENTORY_ROW_SIZE;
        }
        int rowsCount = map.size() / INVENTORY_ROW_SIZE;
        if (map.size() % INVENTORY_ROW_SIZE != 0) {
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
        for (Pair<Integer, Material> material : materials) {
            if (!checkMaterialInInventory(player, material)) {
                return false;
            }
        }
        return true;
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) ||
                !event.getView().title().contains(Component.text(INVENTORY_NAME))) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            OfflinePlayer playerToSpawn = ((SkullMeta) clickedItem.getItemMeta()).getOwningPlayer();
            List<Pair<Integer, Material>> materials = map.get(playerToSpawn.getName());
            if (checkMaterialInInventory(player, materials)) {
                final AtomicInteger counter = new AtomicInteger(SECONDS_BEFORE_RESPAWN);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int currentCounter = counter.getAndDecrement();
                        playerToSpawn.getPlayer().clearTitle();
                        if (currentCounter > 0) {
                            playerToSpawn.getPlayer().showTitle(
                                    Title.title(
                                            Component.text(YOU_WILL_BE_REVIVED_IN.formatted(currentCounter)),
                                            Component.empty()
                                    )
                            );
                        } else {
                            spawnPlayer(playerToSpawn, player);
                            removeItems(player, materials);
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
            } else {
                player.sendMessage(Component.text(NOT_ENOUGH_RESOURCES)
                        .color(TextColor.fromHexString(ColorHexConstants.RED_HEX)));
            }
        }
        event.setCancelled(true); // Prevents taking items from the inventory
    }

    private void spawnPlayer(OfflinePlayer playerToSpawn, Player player) {
        if (playerToSpawn.isOnline()) {
            if (map.containsKey(playerToSpawn.getName())) {
                map.remove(playerToSpawn.getName());
                Location location = playerToSpawn.getBedSpawnLocation();
                playerToSpawn.getPlayer().teleport(location != null ? location : Bukkit.getWorld(WORLD_NAME).getSpawnLocation());
                playerToSpawn.getPlayer().setGameMode(GameMode.SURVIVAL);
            } else {
                player.sendMessage(PLAYER_ALREADY_REVIVED);
            }
        } else {
            player.sendMessage(PLAYER_NOT_ON_SERVER);
        }
        player.closeInventory();
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

