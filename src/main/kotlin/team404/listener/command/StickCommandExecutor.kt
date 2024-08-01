package team404.listener.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import team404.model.Recipes.getReviveStuffItemStack

class StickCommandExecutor() : CommandExecutor {

    companion object {
        const val GET_STICK_COMMAND = "stick"
    }

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player || !commandSender.isOp) {
            return true
        }

        if (command.name == GET_STICK_COMMAND) {
            commandSender.inventory.addItem(getReviveStuffItemStack())
        }
        return true
    }
}