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
    @SerializedName("quests") val quests: Map<String, String> = emptyMap()
)

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
