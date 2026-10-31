"""Named material colours for every Pssst scene grid (source of ScenePalettes.kt).

Names become Kotlin `val`s in `ScenePalettes`, so they must never contain the
stack-count tokens guarded by ColourDisciplineTest (see that test for the list),
and no value may equal a stack-count colour (pure white, neon yellow, neon green).
Dark subjects always carry a lit rim/mid tone >= 1.8:1 against panel stone #2B251D.
"""

PALETTE: dict[str, int] = {
    # Shared
    "Outline": 0x0C0906,            # == OsrsOuterFrame
    "Bone": 0xF4EAD2,
    "TextPrimary": 0xFFE0A0,        # == OsrsTextPrimary
    "GoldBright": 0xFFD866,
    "GoldMid": 0xE5A93B,
    "GoldDeep": 0xB58024,
    "GoldShadow": 0x7A5418,
    "CyanMoteLight": 0x78D4E4,
    "CyanMoteMid": 0x48A8B8,
    "CyanMoteDark": 0x286470,
    "HollowDark": 0x1A140E,
    "RecessRim": 0x4A3B2C,

    # Stone (pillar, pedestal, sundial, mortar)
    "StoneLight": 0xA89C84,
    "StoneMid": 0x847A66,
    "StoneDark": 0x5E5648,
    "StoneDeep": 0x413B31,

    # Oak / wood
    "OakLight": 0xB88E58,
    "OakMid": 0x9C7848,
    "OakDark": 0x70532E,
    "OakDeep": 0x4E3721,

    # Redwood (birdhouse)
    "RedwoodLight": 0xC8684A,
    "RedwoodMid": 0xA85038,
    "RedwoodDark": 0x7C3624,
    "RedwoodDeep": 0x542216,

    # Iron
    "IronLight": 0xA4A8AE,
    "IronMid": 0x7A7E85,
    "IronDark": 0x53565C,

    # Informant (rogue leather)
    "RogueRim": 0xB08A5C,
    "RogueLight": 0x9E7850,
    "RogueMid": 0x7A5838,
    "RogueDark": 0x5C4028,
    "RogueDeep": 0x3A281A,
    "SkinLight": 0xE8BC8C,
    "SkinMid": 0xD8A878,
    "SkinDark": 0xA87850,

    # Parchment
    "ParchLight": 0xF2DFB6,
    "ParchMid": 0xDCC395,
    "ParchDark": 0xC8A870,
    "ParchDeep": 0x96805C,
    "InkBrown": 0x5A4632,

    # Bank clerk
    "VestLight": 0x7894AC,
    "VestMid": 0x58748C,
    "VestDark": 0x3E5366,
    "HairGreyLight": 0xC8C2B4,
    "HairGreyMid": 0xA49C8C,
    "HairGreyDark": 0x7A7365,
    "Cobweb": 0xC8C0B0,
    "MothLight": 0xF2DFB6,

    # Leprechaun / farming
    "HatGreenLight": 0x6CBF5C,
    "HatGreenMid": 0x3E9444,
    "HatGreenDark": 0x2A6A30,
    "BeardGingerLight": 0xF08A3C,
    "BeardGinger": 0xD86828,
    "BeardGingerDark": 0xA04818,
    "NoseRed": 0xD85848,
    "SoilLight": 0x8E6C48,
    "SoilMid": 0x7A5C3C,
    "SoilDark": 0x523C26,
    "CompostDark": 0x3A2C1E,
    "WeedLight": 0x8CC860,
    "WeedMid": 0x5C9A3C,
    "WeedDark": 0x3C6A28,
    "CrowSheen": 0x9AAAC0,
    "CrowLight": 0x7C8DA3,
    "CrowMid": 0x586578,
    "CrowDark": 0x3E4856,

    # Clockwork bird
    "SteelBlueLight": 0x9CB8D4,
    "SteelBlueMid": 0x7898B8,
    "SteelBlueDark": 0x4E6882,

    # Potion
    "GlassLight": 0xC8E4EA,
    "GlassMid": 0xA8D0D8,
    "GlassDark": 0x68929C,
    "HerbloreLight": 0x8CE87C,
    "HerbloreMid": 0x6CD466,
    "HerbloreDark": 0x38984C,
    "CorkLight": 0xD0A870,
    "CorkDark": 0x9C7444,
    "AshGrey": 0x9A948A,

    # Quest scroll
    "RuneLight": 0x9CE6F2,
    "RuneMid": 0x48A8B8,
    "RuneDark": 0x286470,
    "QuestBlueLight": 0x68A2F2,
    "QuestBlueMid": 0x2E6BC6,
    "QuestBlueDark": 0x1F4A8C,
    "HiltLeather": 0x8C7A5B,
    "HiltLeatherDark": 0x5E5038,

    # Ledger / pedestal
    "VelvetLight": 0xD8504A,
    "VelvetMid": 0xC43B32,
    "VelvetDark": 0x8F241E,
    "ChalkLight": 0xF2DFB6,
    "ChalkDim": 0x9E9580,

    # Kingdom
    "CrownVelvet": 0xB83228,
    "GemEmerald": 0x3CC07A,
    "GemSapphire": 0x5A8EF0,
    "SweatLight": 0xA8E8F4,
    "FlameOrange": 0xF08A30,

    # Blighted crop (BadgeWarning natural tones)
    "BlightLight": 0xD4B04C,
    "BlightMid": 0xB07A34,
    "BlightDark": 0x6E4A22,

    # Slayer
    "SkullLight": 0xE8DEC5,
    "SkullMid": 0xB5A88C,
    "SkullDark": 0x786E58,
    "EyeCrimson": 0xE04030,
    "EyeCrimsonDark": 0x8C1C14,
    "BladeLight": 0xD8E0E8,
    "BladeMid": 0xA0A8B0,
    "BladeDark": 0x646C74,
    "HiltCrimson": 0xA8282A,
}
