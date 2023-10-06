package team404;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class StickListener implements Listener {

    private final static String INVENTORY_NAME = "Players reincarnation";
    private final static Material MATERIAL_TO_CLICK = Material.STICK;

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

    private Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, INVENTORY_NAME);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(onlinePlayer);

            List<String> lore = new ArrayList<>();
            lore.add(TextUtils.greenText("Required Materials:"));

            for (Pair<Integer, Material> pair : loadRequiredMaterials()) {
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

    public List<Pair<Integer, Material>> loadRequiredMaterials() {
        // make random select of materials
        return List.of(
                Pair.of(1, Material.DIAMOND),
                Pair.of(2, Material.GOLD_INGOT),
                Pair.of(3, Material.IRON_INGOT)
        );
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
            Player playerToSpawn = ((SkullMeta) clickedItem.getItemMeta()).getOwningPlayer().getPlayer();
            spawnPlayer(playerToSpawn.getPlayer(), player);
        }

        event.setCancelled(true); // Prevents taking items from the inventory
    }

    private void spawnPlayer(Player playerToSpawn, Player player) {
        // spawn player instead of villager
        Location location = player.getLocation().add(1, 0, 0); // Adjust the spawn location as needed
        player.getWorld().spawnEntity(location, EntityType.VILLAGER);
    }

    private boolean hasAtLeastTenWoodenPlanks(Player player) {
        int count = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isWoodenPlank(item.getType())) {
                count += item.getAmount();
                if (count >= 10) {
                    removePlanks(player, 10);
                    return true;
                }
            }
        }

        return false;
    }

    private void removePlanks(Player player, int amount) {
        int remaining = amount;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isWoodenPlank(item.getType())) {
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

    private boolean isWoodenPlank(Material material) {
        switch (material) {
            case MANGROVE_PLANKS:
            case BAMBOO_PLANKS:
            case CHERRY_PLANKS:
            case OAK_PLANKS:
            case SPRUCE_PLANKS:
            case BIRCH_PLANKS:
            case JUNGLE_PLANKS:
            case ACACIA_PLANKS:
            case DARK_OAK_PLANKS:
            case CRIMSON_PLANKS:
            case WARPED_PLANKS:
                return true;
            default:
                return false;
        }
    }
}

