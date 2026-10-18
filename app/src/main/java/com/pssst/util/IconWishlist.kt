package com.pssst.util

/** Item icons the app itself uses (top bar, easter egg), asked for like any other so they arrive even if you don't own them. */
object IconWishlist {
    const val RANARR_WEED = 257
    const val BIRD_HOUSE = 21512
    const val COINS = 995
    const val SLAYER_HELMET = 11864

    /** Funny ones for the skills page: rubber chicken, banana, red partyhat, gnomeball, santa hat, bucket, cabbage, pet rock, mime mask, frog token. */
    val EASTER_EGG = listOf(4566, 1963, 1038, 751, 1050, 1925, 1965, 3695, 3057, 6183)

    val IDS: List<Int> = listOf(RANARR_WEED, BIRD_HOUSE, COINS, SLAYER_HELMET) + EASTER_EGG
}
