package com.pssst.theme.art

/**
 * 16x16 low-resolution pixel-art sprites for navigation, status chips, headers, and table badges.
 *
 * Every sprite is drawn on a 16x16 art-pixel grid with a 1-art-px dark outline (`'o'`),
 * top-left highlight (`'h'`), flat base fill (`'b'`), bottom-right shadow (`'s'`),
 * secondary hardware/material accents (`'a'`, `'d'`), and optional second-material / glint
 * roles (`'l'`, `'m'`, `'k'`, `'g'`, see [SpriteTones]).
 *
 * Nav, top-bar and chip sprites are retinted [SpriteTones.GOLD] / [SpriteTones.STONE] at their
 * call sites, so their materials separate by value and `'o'` seams rather than hue. The four
 * status badges render in their natural tones.
 *
 * Authored in `tools/art/sprites.py` (fill-only grids, outline added automatically) and written
 * back here by `python tools/art/sprites.py`.
 */
object ArtIcons {

    /**
     * Overview nav icon: quartered heraldic kite shield with a raised central boss.
     */
    val OverviewShield = Sprite(
        ".oooooo.ooooooo.",
        "ohhhhhhobbbbbbso",
        "ohbbbbbobsssssgo",
        "ohbbbbbobsssssso",
        "ohbbbbbobsssssso",
        "ohbbbbbobsssssso",
        "ohbbbbohobssssso",
        ".ooooohgsoooooo.",
        ".obbbbosohhhhso.",
        ".obsssohbbbbso..",
        "..obssohbbbso...",
        "...obsohbbso....",
        "....obohbso.....",
        ".....oohso......",
        ".......oo.......",
        "................",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Storage nav icon: domed oak bank chest with iron straps and a padlock hasp.
     */
    val StorageChest = Sprite(
        "................",
        "...ooooooooooo..",
        "..ohhhhhhhhhhbo.",
        ".ohhbabbbbbabbso",
        "ohbbbabbbbbabbso",
        "ohbbbabbbbbabbso",
        ".oooooogggooooo.",
        "ohbbbabgggbabbso",
        "ohbbbaboogbabbso",
        "ohbbbabggggabbso",
        "ohbbbabbbbbabbso",
        "ohbbbabbbbbabbso",
        "osssssasssssasso",
        ".oooooooooooooo.",
        "................",
        "................",
        naturalTones = SpriteTones.WOOD
    )

    /**
     * Exchange nav icon: a tall and a short stack of GP coins with a glint.
     */
    val ExchangeCoins = Sprite(
        "................",
        "..ooooooo...o...",
        ".ohhhhhhbo.ogo..",
        ".ossssssso..o...",
        ".ohhhhhhbo......",
        ".osssssssooooo..",
        ".ohhhhhhbohhhbo.",
        ".osssssssosssso.",
        ".ohhhhhhbohhhbo.",
        ".osssssssosssso.",
        ".ohhhhhhbohhhbo.",
        ".osssssssosssso.",
        ".ohhhhhhbohhhbo.",
        ".osssssssosssso.",
        "..ooooooo.oooo..",
        "................",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Progression nav icon: eight-point quest star with gold centre facets.
     */
    val ProgressStar = Sprite(
        ".......oo.......",
        "......ohbo......",
        "...o..ohbo..o...",
        "..oho.ohbo.obo..",
        "...ohohhbbobo...",
        "....ohhhbbbo....",
        ".ooohhhaabbbooo.",
        "ohhhhhagddbbbbbo",
        "obbbbbadddssssso",
        ".ooobbbddsssooo.",
        "....obbbssso....",
        "...obobbssoso...",
        "..obo.obso.oso..",
        "...o..obso..o...",
        "......obso......",
        ".......oo.......",
        naturalTones = SpriteTones.QUEST_BLUE
    )

    /**
     * Timers nav icon: framed hourglass with sand trickling through the neck.
     */
    val TimersHourglass = Sprite(
        ".oooooooooooooo.",
        "oaaaaaaaaaaaaaao",
        "oddddddddddddddo",
        ".oaobsssssssoao.",
        ".oaoobhhhhsooao.",
        ".oaoobshhssooao.",
        ".oao.obhsoo.oao.",
        ".oao..obso..oao.",
        ".oao..ohso..oao.",
        ".oao.obhsso.oao.",
        ".oaoobsshssooao.",
        ".oaoobhhhhsooao.",
        ".oaobhhhhhhsoao.",
        "oaaaaaaaaaaaaaao",
        "oddddddddddddddo",
        ".oooooooooooooo.",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Top-bar drawer button: three brass clasp bars across a dark ledger (reads as a menu).
     */
    val DrawerLedgerMenu = Sprite(
        "..oooooooooooo..",
        ".oadddddddddddo.",
        ".oadddddddddddo.",
        "oahhhhhhhhhhhhao",
        "oabbbbbbbbbbbbao",
        ".oooooooooooooo.",
        ".oadddddddddddo.",
        "oahhhhhhhhhhhhao",
        "oabbbbbbbbbbbbao",
        ".oooooooooooooo.",
        ".oadddddddddddo.",
        "oahhhhhhhhhhhhao",
        "oabbbbbbbbbbbbao",
        ".oooooooooooooo.",
        ".oadddddddddddo.",
        "..oooooooooooo..",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Top-bar edit icon: sweeping feather quill with a metal nib.
     */
    val EditQuill = Sprite(
        "...........ooo..",
        ".........oohhbo.",
        "........ohhhbbso",
        ".......ohhhbbso.",
        "......ohhhbbso..",
        ".....ohhbbbso...",
        "....ohhbbsso....",
        "....ohbbsso.....",
        "...ohbbsoo......",
        "...ohbso........",
        "..ohbso.........",
        "..oaso..........",
        ".oado...........",
        "oado............",
        "odo.............",
        ".o..............",
        naturalTones = SpriteTones.GOLD
    )

    /**
     * Goal card settings icon: eight-tooth cogwheel with an iron hub and open axle.
     */
    val Gear = Sprite(
        ".......oo.......",
        "......ohho......",
        "...oooohhoooo...",
        "..ohhhhbbhhbbo..",
        "..ohbbbbbbbbbo..",
        "..ohbbbaabbbso..",
        ".oohbbaoodbbsoo.",
        "ohhbbao..odbbsso",
        "ohhbbao..odbbsso",
        ".oohbbdoodbbsoo.",
        "..ohbbbddbbbso..",
        "..obbbbbbbbbso..",
        "..obbssbbsssso..",
        "...oooossoooo...",
        "......osso......",
        ".......oo.......",
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
     * Birdhouse chip and timer icon: pitched-roof birdhouse with entrance hole and perch on a post.
     */
    val Birdhouse = Sprite(
        ".......oo.......",
        "......ohbo......",
        ".....ohhbso.....",
        "....ohhhbbso....",
        "..oohhhhbbssoo..",
        ".ohhhhhhbbsssso.",
        "..oooooooooooo..",
        "...ohbbbbbbso...",
        "...ohbboobbso...",
        "...ohbboobbso...",
        "...ohbbaabbso...",
        "...osssssssso...",
        "....ooohsooo....",
        "......ohso......",
        ".....ohbbso.....",
        "......oooo......",
        naturalTones = SpriteTones.WOOD
    )

    /**
     * Herb patch chip and timer icon: three-leaf herb sprig on a wooden stem.
     */
    val HerbSprig = Sprite(
        "......oo........",
        ".....ohbo.......",
        "....ohhbso......",
        ".oo.ohbbso..oo..",
        "ohbo.ohbso.ohso.",
        "ohhboohbsoohbso.",
        ".ohbbohbsohbso..",
        "..ohbbabsbbso...",
        "...obsabssoo....",
        "....ooadoo......",
        ".....oado.......",
        "....oaado.......",
        "....oaddo.......",
        ".....odo........",
        "......o.........",
        "................",
        naturalTones = SpriteTones.HERB
    )

    /**
     * Tree patch icon: leafy oak canopy over a flared trunk.
     */
    val TreeCrown = Sprite(
        "....ooooooo.....",
        "..oohhhhhhboo...",
        ".ohhhhhhhbbbso..",
        "ohhhhbbbbbbbbso.",
        "ohhbbbbhbbbbbso.",
        "ohbbbbhhhbbbbsso",
        "ohbbbbbbbbbbbsso",
        ".osbbbbbbbbbsso.",
        "..ossbbbbbssso..",
        "...oossaassoo...",
        ".....oaaddo.....",
        ".....oaaddo.....",
        ".....oaaddo.....",
        "....oaaadddo....",
        "...oaadddddo....",
        "....ooooooo.....",
        naturalTones = SpriteTones.HERB
    )

    /**
     * READY status badge: upright three-lobed herb fan with a gold harvest glint.
     */
    val BadgeReady = Sprite(
        "............o...",
        ".......oo..ogo..",
        "......ohbo..o...",
        ".oo..ohhbso..oo.",
        "ohbo.ohbbso.obso",
        "ohhboohbbsoobbso",
        ".ohbbohbbsobbso.",
        "..ohbbhbbsbbso..",
        "...ohbbbbbbso...",
        "....ohbbbbso....",
        ".....ohbbso.....",
        "......oado......",
        ".....ooadoo.....",
        "....oaaadddo....",
        ".....oooooo.....",
        "................",
        naturalTones = SpriteTones.HERB
    )

    /**
     * GROWING status badge: brass watering can pouring water onto a potted sprout.
     */
    val BadgeGrowing = Sprite(
        "..oooo..........",
        ".ollllo.........",
        "olmmmmlo........",
        "olmmmmmloo......",
        "olmmmmmllloo....",
        "olmmmmmmolllo...",
        ".okkkkkkooolko..",
        "..oooooo..ooao..",
        ".........ogoo...",
        ".........oooao..",
        "........ohbohbo.",
        ".........ohbbo..",
        ".........ooboo..",
        "........olllllo.",
        ".........okkko..",
        "..........ooo...",
        naturalTones = SpriteTones.TERRACOTTA_SPROUT
    )

    /**
     * EMPTY status badge: low stone ring around bare, dry soil.
     */
    val BadgeEmpty = Sprite(
        "................",
        "................",
        ".....oooooo.....",
        "...oohhhhhhoo...",
        "..ohhbbbbbbsso..",
        ".ohbbddddddbbso.",
        "ohbddddddddddbso",
        "ohbddaddddaddbso",
        "ohbddddddddddbso",
        ".ohbbddddddbbso.",
        "..osbbbbbbbbso..",
        "...oossssssoo...",
        ".....oooooo.....",
        "................",
        "................",
        "................",
        naturalTones = SpriteTones.STONE
    )

    /**
     * WARNING status badge: bold red "!" beside a curled, blighted leaf and a fly.
     */
    val BadgeWarning = Sprite(
        ".oo.........o...",
        "olmo.......oao..",
        "olmo......ooo...",
        "olmo.....ohbo...",
        "olmo....ohbbso..",
        "olmo...ohbbbbso.",
        ".oo....ohbbsbso.",
        "olmo..ohbbsobso.",
        "olmo..ohbsoobso.",
        ".oo...ohbso.oso.",
        ".oao..obso...o..",
        "..o...obso......",
        ".....obso.......",
        "....obso........",
        "...osso.........",
        "....oo..........",
        naturalTones = SpriteTones.BLIGHT_WARNING
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
