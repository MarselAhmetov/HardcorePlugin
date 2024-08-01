package team404.service

import org.apache.commons.lang3.tuple.Pair
import org.bukkit.Material
import team404.model.MaterialTier
import team404.model.RequiredResource
import java.util.*

object MaterialGenerator {
    val materialLists: MutableMap<String, List<RequiredResource>> = HashMap()

    fun addMaterialList(key: String, materialList: List<RequiredResource>) {
        materialLists[key] = materialList
    }

    private fun getRandomPair(list: List<RequiredResource>, tier: MaterialTier): Pair<Int, Material> {
        val rand = Random()
        val filteredList = list.filter { it.tier == tier }
        return filteredList[rand.nextInt(filteredList.size)].let { (material, lower, higher, _) ->
            val count = if (higher == lower) {
                higher
            } else {
                rand.nextInt(higher - lower) + lower
            }
            Pair.of(count, material)
        }
    }

    fun getMaterialsList(tier: MaterialTier, multiplier: Int): Map<Material, Int> {
        val materialMap = HashMap<Material, Int>()
        for (i in 0 until multiplier) {
            materialLists.values
                .map { getRandomPair(it, tier) }
                .forEach { (amount, material) ->
                    materialMap[material] = materialMap.getOrDefault(material, 0) + amount
                }
        }
        return materialMap
    }
}
