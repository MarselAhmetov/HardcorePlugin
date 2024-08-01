package team404.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.KeyedBossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.slf4j.LoggerFactory
import team404.service.NamespaceKeyManager
import team404.service.PlayerRevivalService
import team404.constant.*
import team404.model.Recipes
import team404.model.request.PlayerRevivedRequest
import team404.util.HttpClient.sendPostRequest
import team404.util.TextUtils
import team404.util.TextUtils.formatTime
import team404.util.getData
import team404.util.removeData
import team404.util.setData
import java.util.concurrent.atomic.AtomicInteger


class InventoryListener(private val plugin: Plugin) : Listener {
    companion object {
        private const val WORLD_NAME = "world"
        private const val SECONDS_BEFORE_RESPAWN = 5
    }
    private val logger = LoggerFactory.getLogger(InventoryListener::class.java)

    private val playerRevivalService: PlayerRevivalService = PlayerRevivalService.getInstance(plugin)
    private val buybackTime: Long = plugin.config.getLong("buyback-time")

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        logger.info(NamespaceKeyManager.getKey(plugin, "hfsbjgh").namespace)
        event.player
            .takeIf { playerRevivalService.playersToSpawn.contains(it.name) }
            ?.also {
                val buybackCount = it.getData(NamespaceKeyManager.getKey(plugin, BUYBACK_COUNT_KEY), PersistentDataType.INTEGER) ?: 0
                if (buybackCount > 0) {
                    buybackPlayer(it)
                } else {
                    respawnPlayer(it)
                }
            }
    }

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
            headOwner.player?.getData(
                NamespaceKeyManager.getKey(plugin, BUYBACK_TIME_LEFT_KEY),
                PersistentDataType.LONG
            )?.let {
                lore.add(Component.text("Выкупается").color(TextColor.fromHexString(GOLDEN_HEX)))
                lore.add(Component.text("Осталось времени: ${formatTime(it)}").color(TextColor.fromHexString(GOLDEN_HEX)))
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
        val player = event.whoClicked as? Player ?: return
        when (PlainTextComponentSerializer.plainText().serialize(event.view.title())) {
            REVIVE_INVENTORY_NAME -> {
                val clickedItem = event.currentItem ?: return
                event.isCancelled = true // Prevents taking items from the inventory
                if (clickedItem.type != Material.PLAYER_HEAD) {
                    return
                }
                val offlinePlayerToSpawn = (clickedItem.itemMeta as SkullMeta).owningPlayer ?: return

                val isPlayerBoughtback = offlinePlayerToSpawn.player?.getData(
                    NamespaceKeyManager.getKey(plugin, BUYBACK_TIME_LEFT_KEY), PersistentDataType.LONG
                ) != null

                if (player.uniqueId != offlinePlayerToSpawn.uniqueId && isPlayerBoughtback) {
                    player.sendMessage(PLAYER_BOUGHT_BACK)
                    return
                }

                val playerToSpawnName = offlinePlayerToSpawn.name ?: return
                if (!playerRevivalService.respawnablePlayers.containsKey(playerToSpawnName)) {
                    player.sendMessage(PLAYER_ALREADY_REVIVED)
                    return
                }

                val materials = playerRevivalService.respawnablePlayers[playerToSpawnName] ?: return
                if (!checkMaterialInInventory(player, materials)) {
                    player.sendMessage(Component.text(NOT_ENOUGH_RESOURCES).color(TextColor.fromHexString(RED_HEX)))
                    return
                }

                playerRevivalService.removeRespawnablePlayer(playerToSpawnName)

                player.closeInventory()
                removeItems(player, materials)

                player.removeData(NamespaceKeyManager.getKey(plugin, BUYBACK_COUNT_KEY))
                player.removeData(NamespaceKeyManager.getKey(plugin, BUYBACK_TIME_LEFT_KEY))

                if (isPlayerBoughtback && player.uniqueId == offlinePlayerToSpawn.uniqueId) {
                    Bukkit.getBossBar(NamespaceKeyManager.getKey(plugin, "${player.name}$BUYBACK_TIMER_KEY"))
                        ?.apply {
                            this.removeAll()
                            Bukkit.removeBossBar(this.key)
                        }
                    player.sendMessage(Component.text(YOU_ARE_FREE).color(TextColor.fromHexString(GOLDEN_HEX)))
                } else {
                    player.sendMessage(
                        Component.text(PLAYER_REVIVED.format(playerToSpawnName))
                            .color(TextColor.fromHexString(GREEN_HEX))
                    )
                    playerRevivalService.addPlayerToRespawn(playerToSpawnName)
                    respawnPlayer(offlinePlayerToSpawn)
                }
            }

            BUYBACK_INVENTORY_NAME -> {
                val clickedItem = event.currentItem ?: return
                event.isCancelled = true
                if (clickedItem.type != Material.PLAYER_HEAD) {
                    return
                }

                val buybackCount = player.getData(
                    NamespaceKeyManager.getKey(plugin, BUYBACK_COUNT_KEY),
                    PersistentDataType.INTEGER
                )
                if (buybackCount != null && buybackCount >= MAX_BUYBACK_COUNT) {
                    player.sendMessage(CANT_BUYBACK)
                    return
                }

                val playerToSpawnName = player.name
                if (!playerRevivalService.respawnablePlayers.containsKey(playerToSpawnName)) {
                    player.sendMessage(PLAYER_ALREADY_REVIVED)
                    return
                }

                playerRevivalService.addPlayerToRespawn(playerToSpawnName)
                player.sendMessage(
                    Component.text(PLAYER_REVIVED.format(playerToSpawnName))
                        .color(TextColor.fromHexString(GREEN_HEX))
                )
                player.closeInventory()
                player.setData(
                    NamespaceKeyManager.getKey(plugin, BUYBACK_TIME_LEFT_KEY),
                    PersistentDataType.LONG,
                    buybackTime
                )
                buybackPlayer(player)
            }
        }
    }

    fun startBuybackTimer(player: Player) {
        val bar = Bukkit.getBossBar(NamespaceKeyManager.getKey(plugin, "${player.name}$BUYBACK_TIMER_KEY")) ?: createBossBar(player)
        bar.addPlayer(player)
    }

    fun createBossBar(player: Player): KeyedBossBar {
        return Bukkit.createBossBar(
            NamespaceKeyManager.getKey(plugin, "${player.name}$BUYBACK_TIMER_KEY"),
            "Таймер",
            BarColor.WHITE,
            BarStyle.SOLID
        )
    }

    private fun buybackPlayer(player: Player) {
        if (!player.isOnline) {
            return
        }
        val buybackCount = player.getData(
            NamespaceKeyManager.getKey(plugin, BUYBACK_COUNT_KEY),
            PersistentDataType.INTEGER
        ) ?: 0
        player.setData(
            NamespaceKeyManager.getKey(plugin, BUYBACK_COUNT_KEY),
            PersistentDataType.INTEGER,
            buybackCount + 1
        )
        val counter = AtomicInteger(SECONDS_BEFORE_RESPAWN)
        object : BukkitRunnable() {
            override fun run() {
                val currentCounter = counter.getAndDecrement()
                player.clearTitle()
                if (currentCounter > 0) {
                    player.showTitle(
                        Title.title(
                            Component.text(YOU_WILL_BE_REVIVED_IN.format(currentCounter)),
                            Component.empty()
                        )
                    )
                } else {
                    if (player.isOnline) {
                        val location = player.respawnLocation ?: Bukkit.getWorld(WORLD_NAME)?.spawnLocation ?: return
                        player.teleport(location)
                        player.gameMode = GameMode.SURVIVAL
                        player.spawnParticle(Particle.TOTEM_OF_UNDYING, location, 100)
                        player.playEffect(EntityEffect.TOTEM_RESURRECT)
                        player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                        player.clearActivePotionEffects()
                        playerRevivalService.removePlayerToRespawn(player.name)
                        startBuybackTimer(player)
                        sendPostRequest(
                            "${plugin.config.getString("bot-address")}$PLAYER_REVIVED_PATH",
                            PlayerRevivedRequest(player.name)
                        )
                    }
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }

    private fun respawnPlayer(offlinePlayer: OfflinePlayer) {
        val player = offlinePlayer.player ?: return
        val counter = AtomicInteger(SECONDS_BEFORE_RESPAWN)
        object : BukkitRunnable() {
            override fun run() {
                val currentCounter = counter.getAndDecrement()
                player.clearTitle()
                if (currentCounter > 0) {
                    player.showTitle(
                        Title.title(
                            Component.text(YOU_WILL_BE_REVIVED_IN.format(currentCounter)),
                            Component.empty()
                        )
                    )
                } else {
                    if (offlinePlayer.isOnline) {
                        val location = player.respawnLocation ?: Bukkit.getWorld(WORLD_NAME)?.spawnLocation ?: return
                        player.apply {
                            this.teleport(location)
                            this.gameMode = GameMode.SURVIVAL
                            this.spawnParticle(Particle.TOTEM_OF_UNDYING, location, 100)
                            this.playEffect(EntityEffect.TOTEM_RESURRECT)
                            this.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                            this.clearActivePotionEffects()
                            playerRevivalService.removePlayerToRespawn(this.name)
                        }

                        sendPostRequest(
                            "${plugin.config.getString("bot-address")}$PLAYER_REVIVED_PATH",
                            PlayerRevivedRequest(player.name)
                        )
                    }
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }

    private fun removeItems(player: Player, materialMap: Map<Material, Int>) {
        materialMap.forEach { (material, amount) -> removeItems(player, material, amount) }
    }

    private fun removeItems(player: Player, material: Material, amount: Int) {
        var remaining = amount
        player.inventory.contents
            .filterNotNull()
            .filter { item -> item.type == material }
            .forEach { item ->
                val inStack = item.amount
                if (inStack > remaining) {
                    item.amount = inStack - remaining
                    return
                } else {
                    player.inventory.remove(item)
                    remaining -= inStack
                }
                if (remaining <= 0) {
                    return
                }
            }
    }
}