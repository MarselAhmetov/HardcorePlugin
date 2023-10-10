package team404;

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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import team404.models.MaterialTier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StickListener implements Listener {

    private final static String INVENTORY_NAME = "Players reincarnation";
    private final static Material MATERIAL_TO_CLICK = Material.STICK;
    private final static String WORLD_NAME = "world";

    private final Map<String, List<Pair<Integer, Material>>> map = new HashMap<>();

    public StickListener() {
        // read file and fill map
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
        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, INVENTORY_NAME);
        for (Map.Entry<String, List<Pair<Integer, Material>>> entry : map.entrySet()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey()));
            List<String> lore = new ArrayList<>();
            lore.add(TextUtils.greenText("Required Materials:"));
            for (Pair<Integer, Material> pair : entry.getValue()) {
                String name = new ItemStack(pair.getRight()).getI18NDisplayName();
                if (checkMaterialInInventory(player, pair)) {
                    lore.add(TextUtils.appendCheckMark(name + " " + pair.getLeft()));
                } else {
                    lore.add(TextUtils.appendCross(name + " " + pair.getLeft()));
                }
            }
            skullMeta.setLore(lore);
            playerHead.setItemMeta(skullMeta);
            inv.addItem(playerHead);
        }
        return inv;
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
        if (!(event.getWhoClicked() instanceof Player) || !event.getView().getTitle().equals(INVENTORY_NAME)) {
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
            spawnPlayer(playerToSpawn, player);
            removeItems(player, materials);
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
                player.sendMessage("Player already revived");
            }
        } else {
            player.sendMessage("Player is not online");
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

