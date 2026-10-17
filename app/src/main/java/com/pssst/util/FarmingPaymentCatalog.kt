package com.pssst.util

data class FarmingPaymentDef(
    val cropName: String,
    val patchCategory: String,
    val requiredLevel: Int,
    val paymentItemId: Int,
    val paymentQuantity: Int,
    val fallbackItemName: String
)

/**
 * UNVERIFIED. These crop-to-payment amounts and item ids were written by hand and have not been checked against the
 * game or the Wiki's farmer pages, and nothing in RuneLite carries them. The Payments tab says so. Before removing
 * that notice, check every row against its source and record the source here.
 */
object FarmingPaymentCatalog {
    val PAYMENTS = listOf(
        // Standard Trees
        FarmingPaymentDef("Oak Tree", "Trees", 15, 5968, 1, "Basket of tomatoes"),
        FarmingPaymentDef("Willow Tree", "Trees", 30, 5386, 1, "Basket of apples"),
        FarmingPaymentDef("Maple Tree", "Trees", 45, 5396, 1, "Basket of oranges"),
        FarmingPaymentDef("Yew Tree", "Trees", 60, 6016, 10, "Cactus spines"),
        FarmingPaymentDef("Magic Tree", "Trees", 75, 5974, 25, "Coconuts"),

        // Fruit Trees
        FarmingPaymentDef("Apple Tree", "Fruit Trees", 27, 5986, 9, "Sweetcorn"),
        FarmingPaymentDef("Banana Tree", "Fruit Trees", 33, 5386, 4, "Basket of apples"),
        FarmingPaymentDef("Orange Tree", "Fruit Trees", 39, 5406, 3, "Basket of strawberries"),
        FarmingPaymentDef("Curry Tree", "Fruit Trees", 42, 5416, 5, "Basket of bananas"),
        FarmingPaymentDef("Pineapple Plant", "Fruit Trees", 51, 5982, 10, "Watermelons"),
        FarmingPaymentDef("Papaya Tree", "Fruit Trees", 57, 2114, 10, "Pineapples"),
        FarmingPaymentDef("Palm Tree", "Fruit Trees", 68, 5972, 15, "Papaya fruit"),
        FarmingPaymentDef("Dragonfruit Tree", "Fruit Trees", 81, 5974, 15, "Coconuts"),

        // Special & Hardwood
        FarmingPaymentDef("Teak Tree", "Special", 35, 5464, 15, "Limpwurt root"),
        FarmingPaymentDef("Mahogany Tree", "Special", 55, 5978, 25, "Yanillian hops"),
        FarmingPaymentDef("Calquat Tree", "Special", 72, 5984, 8, "Poison ivy berries"),
        FarmingPaymentDef("Celastrus Tree", "Special", 85, 3138, 8, "Potato cactus"),
        FarmingPaymentDef("Redwood Tree", "Special", 90, 22929, 6, "Dragonfruit"),

        // Allotments
        FarmingPaymentDef("Potato", "Allotments", 1, 5438, 2, "Sack of potatoes"),
        FarmingPaymentDef("Onion", "Allotments", 5, 5458, 1, "Sack of onions"),
        FarmingPaymentDef("Cabbage", "Allotments", 7, 5478, 1, "Sack of cabbages"),
        FarmingPaymentDef("Tomato", "Allotments", 12, 6032, 2, "Sacks of garlic"),
        FarmingPaymentDef("Sweetcorn", "Allotments", 20, 5968, 10, "Basket of tomatoes"),
        FarmingPaymentDef("Strawberry", "Allotments", 31, 5386, 1, "Basket of apples"),
        FarmingPaymentDef("Watermelon", "Allotments", 47, 5970, 10, "Curry leaves"),
        FarmingPaymentDef("Snape Grass", "Allotments", 61, 247, 5, "Jangerberries")
    )

    val ALL_PAYMENT_ITEM_IDS: List<Int> = PAYMENTS.map { it.paymentItemId }.distinct()
}
