package team404.model

import org.bukkit.Material

data class RequiredResource(
    val material: Material? = null,
    val lowerThreshold: Int = 0,
    val upperThreshold: Int = 0,
    val tier: MaterialTier? = null,
)