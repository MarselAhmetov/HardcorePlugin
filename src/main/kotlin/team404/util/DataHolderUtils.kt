package team404.util

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

fun <R> PersistentDataHolder.getData(namespacedKey: NamespacedKey, dataType: PersistentDataType<*, R>): R? =
    this.persistentDataContainer.get(namespacedKey, dataType)

fun <R : Any> PersistentDataHolder.setData(namespacedKey: NamespacedKey, dataType: PersistentDataType<*, R>, value: R) {
    this.persistentDataContainer.set(namespacedKey, dataType, value)
}

fun PersistentDataHolder.removeData(namespacedKey: NamespacedKey) {
    this.persistentDataContainer.remove(namespacedKey)
}