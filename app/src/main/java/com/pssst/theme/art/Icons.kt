package com.pssst.theme.art

/**
 * 16x16 low-resolution pixel-art sprites for navigation, status chips, headers, and table badges.
 *
 * Every sprite is drawn on a 16x16 art-pixel grid with a 1-art-px dark outline (`'o'`),
 * top-left highlight (`'h'`), flat base fill (`'b'`), bottom-right shadow (`'s'`), and
 * secondary hardware/material accents (`'a'`, `'d'`).
 */
object ArtIcons {

    /**
     * Overview nav icon: Heraldic kite shield with iron boss and chevron.
     */
    val OverviewShield = Sprite(
        ".oooooooooooooo.",
        "ohhhhhhaabbbbbso",
        "ohbbbbaaaabbbbso",
        "ohbbbaadddabbbso",
        "ohbbaadddddabbso",
        "ohbaaddhhhddabso",
        "ohbaddhhhhhddaso",
        ".obbbhhhhhhbbso.",
        ".ohbbbbbbbbbbso.",
        "..ohbbbbbbbbso..",
        "..ohbbbbbbbbso..",
        "...ohbbbbbbso...",
        "....ohbbbbso....",
        ".....ohbbso.....",
        "......ohso......",
        ".......oo.......",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Storage nav icon: Iron-bound oak coffer chest with bevelled lid and lock plate.
     */
    val StorageChest = Sprite(
        "................",
        "..oooooooooooo..",
        ".ohhhhaaaahhhbo.",
        "ohbbbbaaaabbbbso",
        "ohbbbbaaaabbbbso",
        "oooooooooooooooo",
        "ohbaabooooabbaao",
        "ohbaabohhbabbaao",
        "ohbaabohsbabbaao",
        "ohbaabooooabbaao",
        "ohbaabbbbbabbaao",
        "ohbaabbbbbabbaao",
        "osbaabssssabbaao",
        "osbaabssssabbaao",
        ".oooooooooooooo.",
        "................",
        naturalTones = SpriteTones.WOOD
    )

    /**
     * Exchange nav icon: Twin stacked GP coin columns with ridged rims.
     */
    val ExchangeCoins = Sprite(
        ".......oooooooo.",
        "......ohhhhhhbso",
        "......osssssssso",
        "......ohhhhhhbso",
        ".ooooooooossssso",
        "ohhhhhhbsohhhhso",
        "ossssssssossssso",
        "ohhhhhhbsohhhhso",
        "ossssssssossssso",
        "ohhhhhhbsohhhhso",
        "ossssssssossssso",
        "ohhhhhhbsooooooo",
        "osssssssso......",
        "ohhhhhhbso......",
        "osssssssso......",
        ".oooooooo.......",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Progression nav icon: 8-point faceted compass star crest.
     */
    val ProgressStar = Sprite(
        ".......oo.......",
        "......ohbo......",
        ".....ohhhbo.....",
        "..oo.ohbbbo.oo..",
        ".ohboohbbboobso.",
        ".ohhhhhhhhhbbso.",
        ".oohhhhaabbbsoo.",
        "ohhhhhhaabbbbbso",
        "obbbbbbaaddsssso",
        ".oossbbaaddssso.",
        ".ohbbbbbbbbbbso.",
        ".obsoosbbboobso.",
        "..oo.osbbbo.oo..",
        ".....osssso.....",
        "......osso......",
        ".......oo.......",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Timers nav icon: Wooden-framed sandglass with upper and lower bulbs.
     */
    val TimersHourglass = Sprite(
        ".oooooooooooooo.",
        ".oaaaaaaaaaaaao.",
        ".oooooooooooooo.",
        "..ohbbbbbbbbso..",
        "...ohbaaaabso...",
        "...ohbaddabso...",
        "....ohaddaso....",
        ".....oohsoo.....",
        ".....oohsoo.....",
        "....ohbaabso....",
        "...ohbbaabbso...",
        "...ohbaaaabso...",
        "..ohbaaaaaabso..",
        ".oooooooooooooo.",
        ".oaaaaaaaaaaaao.",
        ".oooooooooooooo.",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Top-bar drawer button icon: Three riveted ledger clasp bars.
     */
    val DrawerLedgerMenu = Sprite(
        "................",
        ".oooooooooooooo.",
        "ohahhhhhhhhhhaso",
        "odssssssssssssdo",
        ".oooooooooooooo.",
        "................",
        ".oooooooooooooo.",
        "ohahhhhhhhhhhaso",
        "odssssssssssssdo",
        ".oooooooooooooo.",
        "................",
        ".oooooooooooooo.",
        "ohahhhhhhhhhhaso",
        "odssssssssssssdo",
        ".oooooooooooooo.",
        "................",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Top-bar edit icon: Scribe's feather quill and brass nib.
     */
    val EditQuill = Sprite(
        "..........ooooo.",
        ".........ohhhbbo",
        "........ohhhbbbo",
        ".......ohhhbbbso",
        "......ohhhbbbso.",
        ".....ohhhbbbso..",
        "....ohhhbbbso...",
        "....ohhbbbso....",
        "...ohbbbbso.....",
        "...ohbbboo......",
        "..ooaaoo........",
        "..oaddo.........",
        ".ooado..........",
        ".oaoo...........",
        "ooo.............",
        "................",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Goal card settings icon: 8-tooth forged iron cogwheel.
     */
    val Gear = Sprite(
        "......oooo......",
        ".....ohhhbo.....",
        "..oo.ohbbbo.oo..",
        ".ohboohbbboobso.",
        ".ohhhhhhhhhbbso.",
        "..ohbbaaaabbso..",
        "ooohbaooooabsooo",
        "ohbbbaooooabbbso",
        "ohbbbaooooabbbso",
        "ooosbaooooabssoo",
        "..osbbadddbbso..",
        ".osbbbbbbbbbsso.",
        ".obsoosbbboobso.",
        "..oo.osbbbo.oo..",
        ".....osssso.....",
        "......oooo......",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Chiseled pixel check mark for done-editing and checkboxes.
     */
    val Check = Sprite(
        "................",
        "............ooo.",
        "...........ohbbo",
        "..........ohbbso",
        ".........ohbbso.",
        "..ooo...ohbbso..",
        ".ohbbo.ohbbso...",
        "ohbbbbohbbsso...",
        "ohbbbbhbbsso....",
        ".osbbbbbsso.....",
        "..osbbbbsso.....",
        "...osbbbsso.....",
        "....osbsso......",
        ".....osso.......",
        "......oo........",
        "................",
        naturalTones = SpriteTones.HERB
    )

    /**
     * Birdhouse chip & timer icon: Pitched-roof wooden birdhouse on a post.
     */
    val Birdhouse = Sprite(
        ".......oo.......",
        "......ohbo......",
        "....oohhhboo....",
        "...ohhhhhhbso...",
        "..ohhhhhhhhbso..",
        ".oooooooooooooo.",
        "..ohbbbbbbbbso..",
        "..ohbbaaaabbso..",
        "..ohbaooooabso..",
        "..ohbaooooabso..",
        "..ohbbadddbbso..",
        "..osbbbbbbbbso..",
        ".oooooooooooooo.",
        "......ohso......",
        "......ohso......",
        "......oooo......",
        naturalTones = SpriteTones.WOOD
    )

    /**
     * Herb patch chip & timer icon: Three-leaf herb sprig with stem.
     */
    val HerbSprig = Sprite(
        ".......ooo......",
        "......ohbbo.....",
        ".....ohhhbso....",
        ".....ohbbbso....",
        "..ooo.ohbso.ooo.",
        ".ohbboohbsoohbbo",
        "ohhhbbohbsohhbso",
        "ohbbbbshbsobbbso",
        ".osbbbshbsobbso.",
        "..ooooohbsoooo..",
        "......ohbso.....",
        "......oaado.....",
        ".....oaaadoo....",
        ".....oaddo......",
        ".....oooo.......",
        "................",
        naturalTones = SpriteTones.HERB
    )

    /**
     * Tree patch icon: Leafy tree crown over a wooden trunk.
     */
    val TreeCrown = Sprite(
        "....oooooooo....",
        "..oohhhhhhhboo..",
        ".ohhhhhhhhhbbso.",
        ".ohhhbbbbbbbbso.",
        "ohhhbbbbbbbbbbso",
        "ohhhbbbbbbbbbbso",
        "ohbbbbbbbbbbbbso",
        "osbbbbbbbbbbbbso",
        ".osbbbbbbbbbbso.",
        "..oossbbbbssoo..",
        "....ooaaddoo....",
        ".....oaaddo.....",
        ".....oaaddo.....",
        "....ooaaddoo....",
        "...oaaadddddo...",
        "...oooooooooo...",
        naturalTones = SpriteTones.HERB
    )

    /**
     * Timers READY status badge: Stepped stone seal with grown-green pixel check.
     */
    val BadgeReady = Sprite(
        "..oooooooooooo..",
        ".ohhhhhhhhhhhbo.",
        "ohbbbbbbbbbbbbso",
        "ohbbbbbbbbbbhhoo",
        "ohbbbbbbbbbhhooo",
        "ohbbbbbbbbhhooso",
        "ohbhhbbbbhhoosso",
        "ohboohbbhhoossbo",
        "ohbboohbhoossbso",
        "ohbbboohoossbbso",
        "ohbbbboooosbbbso",
        "ohbbbbboosbbbbso",
        "ohbbbbbbbbbbbbso",
        "osssssssssssssso",
        ".osssssssssssso.",
        "..oooooooooooo..",
        naturalTones = SpriteTones.HERB
    )

    /**
     * Timers GROWING status badge: Compact pixel sandglass.
     */
    val BadgeGrowing = Sprite(
        "..oooooooooooo..",
        ".oaaaaaaaaaaaao.",
        "..ohbbbbbbbbso..",
        "..ohbaaaaaabso..",
        "...ohbaddabso...",
        "....ohaddaso....",
        ".....oohsoo.....",
        ".....oohsoo.....",
        "....ohbaabso....",
        "...ohbbaabbso...",
        "..ohbaaaaaabso..",
        "..ohbaaaaaabso..",
        "..osssssssssso..",
        ".oaaaaaaaaaaaao.",
        "..oooooooooooo..",
        "................",
        naturalTones = SpriteTones.STONE
    )

    /**
     * Timers EMPTY status badge: Recessed stone ring with horizontal bar.
     */
    val BadgeEmpty = Sprite(
        "....oooooooo....",
        "..oohhhhhhhboo..",
        ".ohhhbbbbbbhhso.",
        ".ohbbboooobbbso.",
        "ohbboo....oobbso",
        "ohbo........obso",
        "ohbo.oooooo.obso",
        "ohbo.ohhhbo.obso",
        "ohbo.osssso.obso",
        "ohbo.oooooo.obso",
        "ohbo........obso",
        "ohbboo....oobbso",
        ".osbbboooobbsso.",
        ".osssbbbbbbssso.",
        "..oossssssssoo..",
        "....oooooooo....",
        naturalTones = SpriteTones.STONE
    )

    /**
     * Timers WARNING status badge: Chiseled warning triangle with exclamation mark.
     */
    val BadgeWarning = Sprite(
        ".......oo.......",
        "......ohbo......",
        ".....ohhhbo.....",
        ".....ohbbbo.....",
        "....ohboobso....",
        "....ohboobso....",
        "...ohbboobbso...",
        "...ohbboobbso...",
        "..ohbbboobbbso..",
        "..ohbbbbbbbbso..",
        ".ohbbbboobbbbso.",
        ".ohbbbboobbbbso.",
        "ohbbbbbbbbbbbbso",
        "osssssssssssssso",
        ".oooooooooooooo.",
        "................",
        naturalTones = SpriteTones.WARNING
    )

    val ALL_SPRITES: List<Pair<String, Sprite>> = listOf(
        "OverviewShield" to OverviewShield,
        "StorageChest" to StorageChest,
        "ExchangeCoins" to ExchangeCoins,
        "ProgressStar" to ProgressStar,
        "TimersHourglass" to TimersHourglass,
        "DrawerLedgerMenu" to DrawerLedgerMenu,
        "EditQuill" to EditQuill,
        "Gear" to Gear,
        "Check" to Check,
        "Birdhouse" to Birdhouse,
        "HerbSprig" to HerbSprig,
        "TreeCrown" to TreeCrown,
        "BadgeReady" to BadgeReady,
        "BadgeGrowing" to BadgeGrowing,
        "BadgeEmpty" to BadgeEmpty,
        "BadgeWarning" to BadgeWarning
    )
}
