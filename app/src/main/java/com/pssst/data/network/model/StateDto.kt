package com.pssst.data.network.model

import com.google.gson.annotations.SerializedName

data class StateResponse(
    @SerializedName("data") val data: StateDataDto? = null,
    @SerializedName("updated_at") val updatedAt: Long = 0L
)

data class IconDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("png") val png: String? = null
)

data class StateDataDto(
    /**
     * Epoch ms of the last moment the game itself fed this snapshot (not when the PC served it, and not when this
     * phone fetched it). Absent (0) from plugins that predate it; then [StateResponse.updatedAt] is the best guess.
     */
    @SerializedName("observedAt") val observedAt: Long = 0L,
    /** When the bank and potion storage were last read from the open bank (they are carried over between sessions). */
    @SerializedName("bankObservedAt") val bankObservedAt: Long = 0L,
    /** Item icons the app asked for in its last reply (item id to name and PNG), drawn by the player's RuneLite. */
    @SerializedName("icons") val icons: Map<String, IconDto>? = null,
    @SerializedName("inventory") val inventory: List<ItemEntryDto> = emptyList(),
    /** Null when the player turned off Include Bank in RuneLite (the plugin then leaves the bank out). */
    @SerializedName("bank") val bank: List<ItemEntryDto>? = null,
    @SerializedName("varbits") val varbits: Map<String, Int> = emptyMap(),
    @SerializedName("varps") val varps: Map<String, Int> = emptyMap(),
    /** GE price (gp) of each held or offered item, by the id that has the price; from the plugin's ItemManager. */
    @SerializedName("prices") val prices: Map<String, Long> = emptyMap(),
    /** High Alchemy price (gp) of each held item; from the plugin's ItemManager. */
    @SerializedName("haPrices", alternate = ["ha_prices"]) val haPrices: Map<String, Long> = emptyMap(),
    @SerializedName("slayer") val slayer: SlayerEntryDto? = null,
    @SerializedName("ge_offers", alternate = ["geOffers"]) val geOffers: List<GeOfferDto> = emptyList(),
    @SerializedName("skills") val skills: List<SkillEntryDto> = emptyList(),
    @SerializedName("potion_storage", alternate = ["potionStorage"]) val potionStorage: List<PotionEntryDto>? = null,
    @SerializedName("quests") val quests: Map<String, String> = emptyMap(),
    @SerializedName("farming") val farming: List<FarmingEntryDto> = emptyList(),
    @SerializedName("birdhouses") val birdhouses: List<BirdhouseEntryDto> = emptyList()
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
    @SerializedName("quantity") val quantity: Int,
    /** For a noted item: the unnoted item id, which is the one that has a name and a price. Absent otherwise. */
    @SerializedName("baseId") val baseId: Int? = null,
    /** Bank tab (1..9, 0 = Main tab). Absent for non-bank items. */
    @SerializedName("tab") val tab: Int? = null
) {
    /** The id to look up in the item manifest. */
    val manifestId: Int get() = baseId ?: id
}

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

/**
 * One Fossil Island birdhouse space as last observed by the plugin. state is SEEDED, BUILT, EMPTY or UNKNOWN.
 * houseType/houseItemId are absent for empty or unknown spaces. readyAt (epoch ms) is 50 minutes after the seeded
 * value was first observed, and 0 when the space is not seeded.
 */
data class BirdhouseEntryDto(
    @SerializedName("space") val space: String = "",
    @SerializedName("spaceName") val spaceName: String = "",
    @SerializedName("varpId") val varpId: Int = 0,
    @SerializedName("value") val value: Int = 0,
    @SerializedName("observedAt") val observedAt: Long = 0L,
    @SerializedName("state") val state: String = "UNKNOWN",
    @SerializedName("houseType") val houseType: String? = null,
    @SerializedName("houseItemId") val houseItemId: Int = 0,
    @SerializedName("readyAt") val readyAt: Long = 0L
)

/**
 * The current Slayer assignment. The plugin resolves the names from the game's own tables, so there is no task table
 * on this side. amount is the kills remaining: 0 with no names means no assignment.
 */
data class SlayerEntryDto(
    @SerializedName("taskName") val taskName: String? = null,
    @SerializedName("taskLocation") val taskLocation: String? = null,
    @SerializedName("amount") val amount: Int = 0,
    @SerializedName("initialAmount") val initialAmount: Int = 0,
    @SerializedName("points") val points: Int = 0,
    @SerializedName("streak") val streak: Int = 0
)
