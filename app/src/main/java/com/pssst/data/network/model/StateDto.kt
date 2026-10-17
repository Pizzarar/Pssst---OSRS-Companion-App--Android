package com.pssst.data.network.model

import com.google.gson.annotations.SerializedName

data class StateResponse(
    @SerializedName("data") val data: StateDataDto? = null,
    @SerializedName("updated_at") val updatedAt: Long = 0L
)

data class StateDataDto(
    @SerializedName("inventory") val inventory: List<ItemEntryDto> = emptyList(),
    @SerializedName("bank") val bank: List<ItemEntryDto> = emptyList(),
    @SerializedName("varbits") val varbits: Map<String, Int> = emptyMap(),
    @SerializedName("slayer") val slayer: Map<String, Any?> = emptyMap(),
    @SerializedName("ge_offers", alternate = ["geOffers"]) val geOffers: List<GeOfferDto> = emptyList(),
    @SerializedName("skills") val skills: List<SkillEntryDto> = emptyList(),
    @SerializedName("potion_storage", alternate = ["potionStorage"]) val potionStorage: List<PotionEntryDto> = emptyList(),
    @SerializedName("quests") val quests: Map<String, String> = emptyMap(),
    @SerializedName("farming") val farming: List<FarmingEntryDto> = emptyList()
)

/**
 * One farming patch as last observed by the plugin. regionId + varbitId identify the patch: the varbit is a
 * per-region slot, not a fixed patch. observedAt is epoch ms and only moves when value changes. The crop fields
 * are absent when the plugin could not decode the value. tickMinutes is 0 when nothing is advancing.
 */
data class FarmingEntryDto(
    @SerializedName("regionId") val regionId: Int = 0,
    @SerializedName("regionName") val regionName: String = "",
    @SerializedName("patchName") val patchName: String = "",
    @SerializedName("patchType") val patchType: String = "",
    @SerializedName("varbitId") val varbitId: Int = 0,
    @SerializedName("value") val value: Int = 0,
    @SerializedName("observedAt") val observedAt: Long = 0L,
    @SerializedName("cropName") val cropName: String? = null,
    @SerializedName("itemId") val itemId: Int = 0,
    @SerializedName("cropState") val cropState: String? = null,
    @SerializedName("stage") val stage: Int = 0,
    @SerializedName("stages") val stages: Int = 0,
    @SerializedName("tickMinutes") val tickMinutes: Int = 0
) {
    val patchKey: String get() = "$regionId.$varbitId"
}

data class ItemEntryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("quantity") val quantity: Int
)

data class GeOfferDto(
    @SerializedName("slot") val slot: Int = 0,
    @SerializedName("itemId", alternate = ["item_id", "id"]) val itemId: Int = 0,
    @SerializedName("quantitySold", alternate = ["quantity_sold", "transferred"]) val quantitySold: Int = 0,
    @SerializedName("totalQuantity", alternate = ["total_quantity", "quantity"]) val totalQuantity: Int = 0,
    @SerializedName("price") val price: Int = 0,
    @SerializedName("spent") val spent: Long = 0L,
    @SerializedName("state") val state: String = "EMPTY"
)

data class SkillEntryDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("level") val level: Int = 1,
    @SerializedName("boostedLevel", alternate = ["boosted_level"]) val boostedLevel: Int = 1,
    @SerializedName("xp") val xp: Long = 0L
)

data class PotionEntryDto(
    @SerializedName("itemId", alternate = ["item_id", "id"]) val itemId: Int = 0,
    @SerializedName("doses", alternate = ["quantity"]) val doses: Int = 0
)
