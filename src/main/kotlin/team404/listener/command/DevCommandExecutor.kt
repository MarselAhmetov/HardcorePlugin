package team404.listener.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import team404.service.PlayerRevivalService

class DevCommandExecutor(plugin: Plugin) : CommandExecutor {

    companion object {
        const val GET_RESOURCES_COMMAND = "dev"
    }

    private val playerRevivalService: PlayerRevivalService = PlayerRevivalService.getInstance(plugin)

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player || !commandSender.isOp) {
            return true
        }

        if (command.name == GET_RESOURCES_COMMAND) {
            playerRevivalService.respawnablePlayers.values.forEach { value ->
                value.forEach { (material, amount) ->
                    commandSender.inventory.addItem(ItemStack(material, amount))
                }
            }
        }
        return true
    }
}