package team404.service

import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

object NamespaceKeyManager {
    private val keys: MutableMap<String, NamespacedKey> = HashMap()

    fun getKey(plugin: Plugin, key: String) =
        keys.computeIfAbsent(key) { NamespacedKey(plugin, key) }

    fun getKey(namespace: String, key: String) =
        keys.computeIfAbsent(key) { NamespacedKey(namespace, key) }
}