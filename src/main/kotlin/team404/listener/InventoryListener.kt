package team404.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import org.slf4j.LoggerFactory
import team404.constant.BUYBACK_INVENTORY_NAME
import team404.constant.CANT_BUYBACK
import team404.constant.GOLDEN_HEX
import team404.constant.GREEN_HEX
import team404.constant.INVENTORY_ROW_SIZE
import team404.constant.MAX_BUYBACK_COUNT
import team404.constant.NOT_ENOUGH_RESOURCES
import team404.constant.PLAYER_ALREADY_REVIVED
import team404.constant.PLAYER_BOUGHT_BACK
import team404.constant.PLAYER_REVIVED
import team404.constant.PLUGIN_NAMESPACE
import team404.constant.RED_HEX
import team404.constant.REVIVE_INVENTORY_NAME
import team404.constant.YOU_ARE_FREE
import team404.model.Recipes
import team404.service.PlayerRevivalService
import team404.util.TextUtils
import team404.util.TextUtils.formatTime
import team404.util.getBuybackCount
import team404.util.getBuybackTimeLeft
import team404.util.getBuybackTimerBar
import team404.util.isInBuyback
import team404.util.removeBuybackCount
import team404.util.removeBuybackTimeLeft
import team404.util.removeItems
import team404.util.setIsInBuyback


class InventoryListener(plugin: Plugin) : Listener {
    private val logger = LoggerFactory.getLogger(PLUGIN_NAMESPACE)

    private val playerRevivalService: PlayerRevivalService = PlayerRevivalService.getInstance(plugin)

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        val item = event.item
        if (!Recipes.isReviveStuff(item)) {
            return
        }
        event.player.also { player ->
            val inventory = getReviveInventory(player)
            player.openInventory(inventory)
        }
    }

    private fun getReviveInventory(player: Player): Inventory {
        val inv = Bukkit.createInventory(null, getReviveInventorySize(), Component.text(REVIVE_INVENTORY_NAME))
        playerRevivalService.respawnablePlayers.forEach { (name, materials) ->
            val headOwner = Bukkit.getOfflinePlayer(name)
            val playerHead = ItemStack(Material.PLAYER_HEAD)
            val skullMeta = playerHead.itemMeta as SkullMeta
            skullMeta.owningPlayer = headOwner
            val lore = materials.map { (material, amount) ->
                val item = ItemStack(material)
                val text = if (checkMaterialInInventory(player, material, amount)) {
                    TextUtils.appendCheckMark(item)
                } else {
                    TextUtils.appendCross(item)
                }
                text.appendSpace().append(Component.text(amount))
            }.toMutableList()
            headOwner.player?.getBuybackTimeLeft()
                ?.let {
                    lore.add(Component.text("Выкупается").color(TextColor.fromHexString(GOLDEN_HEX)))
                    lore.add(
                        Component.text("Осталось времени: ${formatTime(it)}").color(TextColor.fromHexString(GOLDEN_HEX))
                    )
                }
            skullMeta.lore(lore)
            playerHead.itemMeta = skullMeta
            inv.addItem(playerHead)
        }
        return inv
    }

    private fun getReviveInventorySize(): Int {
        val players = playerRevivalService.respawnablePlayers
        if (players.isEmpty()) {
            return INVENTORY_ROW_SIZE
        }
        val rowsCount = players.size / INVENTORY_ROW_SIZE + if (players.size % INVENTORY_ROW_SIZE != 0) 1 else 0
        return rowsCount * INVENTORY_ROW_SIZE
    }

    private fun checkMaterialInInventory(player: Player, requiredMaterial: Material, requiredAmount: Int): Boolean {
        var count = 0
        for (item in player.inventory.contents) {
            if (item != null && item.type == requiredMaterial) {
                count += item.amount
            }
        }
        return count >= requiredAmount
    }

    private fun checkMaterialInInventory(player: Player, materials: Map<Material, Int>?): Boolean {
        materials ?: return false
        return materials.all { (material, amount) -> checkMaterialInInventory(player, material, amount) }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val reviver = event.whoClicked as? Player ?: return
        when (PlainTextComponentSerializer.plainText().serialize(event.view.title())) {
            REVIVE_INVENTORY_NAME -> {
                val clickedItem = event.currentItem ?: return
                event.isCancelled = true // Prevents taking items from the inventory
                if (clickedItem.type != Material.PLAYER_HEAD) {
                    return
                }
                val offlinePlayerToSpawn = (clickedItem.itemMeta as SkullMeta).owningPlayer ?: return

                val isPlayerBoughtback = offlinePlayerToSpawn.isInBuyback()
                if (reviver.uniqueId != offlinePlayerToSpawn.uniqueId && isPlayerBoughtback) {
                    reviver.sendMessage(PLAYER_BOUGHT_BACK)
                    return
                }

                val playerToSpawnName = offlinePlayerToSpawn.name ?: return
                if (!playerRevivalService.respawnablePlayers.containsKey(playerToSpawnName) || playerRevivalService.playersToSpawn.contains(playerToSpawnName)) {
                    reviver.sendMessage(PLAYER_ALREADY_REVIVED)
                    return
                }

                val materials = playerRevivalService.respawnablePlayers[playerToSpawnName] ?: return
                if (!checkMaterialInInventory(reviver, materials)) {
                    reviver.sendMessage(Component.text(NOT_ENOUGH_RESOURCES).color(TextColor.fromHexString(RED_HEX)))
                    return
                }

                playerRevivalService.removeRespawnablePlayer(playerToSpawnName)

                reviver.closeInventory()
                removeItems(reviver, materials)

                if (isPlayerBoughtback && reviver.uniqueId == offlinePlayerToSpawn.uniqueId) {
                    reviver.setIsInBuyback(false)
                    reviver.removeBuybackCount()
                    reviver.removeBuybackTimeLeft()
                    reviver.getBuybackTimerBar()?.apply {
                            this.removeAll()
                            Bukkit.removeBossBar(this.key)
                        }
                    reviver.sendMessage(Component.text(YOU_ARE_FREE).color(TextColor.fromHexString(GOLDEN_HEX)))
                } else {
                    reviver.removeBuybackCount()
                    reviver.removeBuybackTimeLeft()
                    reviver.sendMessage(
                        Component.text(PLAYER_REVIVED.format(playerToSpawnName))
                            .color(TextColor.fromHexString(GREEN_HEX))
                    )
                    playerRevivalService.addPlayerToRespawn(playerToSpawnName)
                    playerRevivalService.respawnPlayer(offlinePlayerToSpawn)
                }
            }

            BUYBACK_INVENTORY_NAME -> {
                val clickedItem = event.currentItem ?: return
                event.isCancelled = true
                if (clickedItem.type != Material.PLAYER_HEAD) {
                    return
                }

                val buybackCount = reviver.getBuybackCount()
                if (buybackCount != null && buybackCount >= MAX_BUYBACK_COUNT) {
                    reviver.sendMessage(CANT_BUYBACK)
                    return
                }

                val playerToSpawnName = reviver.name
                if (!playerRevivalService.respawnablePlayers.containsKey(playerToSpawnName)) {
                    reviver.sendMessage(PLAYER_ALREADY_REVIVED)
                    return
                }

                playerRevivalService.addPlayerToRespawn(playerToSpawnName)
                reviver.sendMessage(
                    Component.text(PLAYER_REVIVED.format(playerToSpawnName))
                        .color(TextColor.fromHexString(GREEN_HEX))
                )
                reviver.closeInventory()
                playerRevivalService.buybackPlayer(reviver)
            }
        }
    }
}