"""32x24 Overview vignettes. Each state pair shares one composition so the
state change reads at a glance."""
from pixart import Canvas, rect, polygon, thick_line, line, ring
from scenes import IRON, OAK, GOLD, shaded

VW, VH = 32, 24

GHRIM = [
    # 0123456789012
    "....SSSSS....",  # 0 bald crown
    "...SSSSSSs...",  # 1
    "..gSSSSSSsg..",  # 2 grey fringe
    "..gEESSEEsg..",  # 3 eyes
    "..gSSSkSSsg..",  # 4 nose
    "..gSSSSSSsg..",  # 5
    "...HHHHHHH...",  # 6 grey beard
    "...HhHHHHHg..",  # 7
    "....HHHHHg...",  # 8
    ".VVVGHHHGVVV.",  # 9 royal shoulders, gold trim
    "VVVVGVHVGVVVv",  # 10
    "VVVVGVVVGVVvv",  # 11
    "VVVVGVVVGVVvv",  # 12
]
GHRIM_LEGEND = {
    "S": "SkinLight", "s": "SkinMid", "k": "SkinDark", "g": "HairGreyDark", "H": "HairGreyLight",
    "h": "Bone", "V": "CrownVelvet", "v": "VelvetDark", "G": "GoldMid", "E": "Outline",
}

CROWN = [
    "B...B...B",
    "GV.VGV.VG",
    "GVVVGVVVG",
    "GGGGGGGGG",
    "GeGGsGGeG",
    "ggggggggg",
]
CROWN_LEGEND = {"B": "GoldBright", "G": "GoldMid", "g": "GoldDeep", "V": "CrownVelvet",
                "e": "GemEmerald", "s": "GemSapphire"}
CROWN_TARNISHED = {"B": "GoldMid", "G": "GoldDeep", "g": "GoldShadow", "V": "VelvetDark",
                   "e": "GoldShadow", "s": "GoldShadow"}


def kingdom(warning: bool) -> Canvas:
    c = Canvas(VW, VH)
    # Advisor Ghrim
    face = [r for r in GHRIM]
    if warning:
        face[2] = "..gSoSSoSsg.."   # brows raised
        face[3] = "..gThSThsg..."[:13].ljust(13, ".")
        face[3] = "..gThSSThsg.."
        face[5] = "..gSSoooSsg.."  # gaping worried mouth
    c.stamp(face, 1, 9, dict(GHRIM_LEGEND, T="Bone", o="Outline"))
    if warning:
        for (x, y, n) in ((12, 8, "SweatLight"), (13, 9, "CyanMoteMid"), (14, 6, "SweatLight"),
                          (15, 7, "CyanMoteMid"), (1, 9, "SweatLight"), (0, 10, "CyanMoteMid")):
            c.set(x, y, n)
    else:
        # small content crown emblem above his head
        for x, y, n in ((5, 6, "GoldBright"), (7, 5, "GoldBright"), (9, 6, "GoldBright"),
                        (5, 7, "GoldMid"), (6, 7, "GoldMid"), (7, 7, "GoldMid"), (8, 7, "GoldMid"),
                        (9, 7, "GoldMid")):
            c.set(x, y, n)
    # Iron-bound royal coffer
    body = rect(16, 15, 15, 9)
    shaded(c, body, OAK)
    for sx in (18, 27):
        strap = rect(sx, 15, 2, 9)
        c.paint(strap, lambda x, y: "IronLight" if x == sx else "IronDark")
    lock = rect(22, 17, 3, 3)
    shaded(c, lock, GOLD)
    c.set(23, 18, "Outline")
    if warning:
        mouth = rect(16, 12, 15, 3)
        c.outline(mouth)
        c.fill(mouth, "RecessRim")
        c.fill(rect(17, 13, 13, 2), "HollowDark")
        for x, y in line(17, 13, 20, 14) + line(17, 14, 19, 13):
            c.set(x, y, "Cobweb")
        # Tarnished crown toppled onto the lid, cracked down the middle
        # The royal crown, still gold, cleaved by a jagged crack: right half dropped and tilted
        crown = Canvas(9, 6)
        crown.stamp(CROWN, 0, 0, CROWN_LEGEND, outline=False)
        placed = {}
        for y in range(6):
            for x in range(9):
                n = crown.px[y][x]
                if n is None:
                    continue
                if x <= 3:
                    placed[(18 + x, 5 + y)] = n
                elif x >= 5:
                    placed[(19 + x, 6 + y + (x - 5) // 2)] = n
        c.outline(set(placed))
        for (x, y), n in placed.items():
            c.set(x, y, n)
        for x, y in ((22, 5), (22, 6), (23, 6), (23, 7), (22, 8), (22, 9), (23, 9), (23, 10), (22, 11)):
            c.set(x, y, "Outline")
    else:
        # Coffer overflowing with coins, crown proudly on top
        heap = polygon([(15.5, 15), (19, 10), (28, 10), (31.5, 15)])
        shaded(c, heap, GOLD)
        for x, y in ((19, 12), (22, 11), (25, 13), (28, 12), (21, 14), (27, 14)):
            c.set(x, y, "GoldDeep")
        for x, y in ((20, 11), (24, 12), (29, 13)):
            c.set(x, y, "GoldBright")
        c.stamp(CROWN, 19, 3, CROWN_LEGEND)
        for x, y in ((17, 2), (30, 4), (30, 9)):
            c.set(x, y, "GoldBright")
    return c


SKULL = [
    # 01234567890123
    "h............h",  # 0 horn tips
    "hh..........hh",  # 1
    ".hhLLLLLLLLhh.",  # 2
    "..LLLLLLLLLLm.",  # 3
    ".LLLLLLLLLLLLm",  # 4
    ".LLooLLLLooLm.",  # 5
    ".LoEeoLLoEeoLm",  # 6 glowing sockets
    ".LLooLLLLooLm.",  # 7
    ".LLLLLooLLLLm.",  # 8 nasal hollow
    "..mLLLooLLLm..",  # 9
    "...mLLLLLLm...",  # 10
    "...TdTdTdTd...",  # 11 teeth
    "....mmmmmm....",  # 12 jaw
]
SKULL_LEGEND = {"h": "SkullMid", "L": "SkullLight", "m": "SkullDark", "o": "Outline",
                "E": "EyeCrimson", "e": "EyeCrimsonDark", "T": "Bone", "d": "SkullDark"}

GILD = {"SkullLight": "GoldBright", "SkullMid": "GoldMid", "SkullDark": "GoldDeep",
        "Bone": "GoldBright", "BladeLight": "GoldBright", "BladeMid": "GoldMid",
        "BladeDark": "GoldDeep", "IronLight": "GoldBright", "IronMid": "GoldMid",
        "IronDark": "GoldDeep", "EyeCrimson": "TextPrimary", "EyeCrimsonDark": "GoldBright"}


def slayer(gilded: bool) -> Canvas:
    c = Canvas(VW, VH)
    # Broadsword driven through the skull: hilt top-right, blade exits bottom-left
    blade = thick_line(23, 6, 4, 21, 2)
    shaded(c, blade, ("BladeLight", "BladeMid", "BladeDark"))
    c.stamp(SKULL, 9, 5, SKULL_LEGEND)
    # Blade re-emerges over the jaw toward the tip
    tip = thick_line(11, 16, 4, 21, 2)
    shaded(c, tip, ("BladeLight", "BladeMid", "BladeDark"))
    guard = thick_line(21, 3, 27, 9, 2)
    shaded(c, guard, ("GoldBright", "GoldMid", "GoldDeep"))
    grip = thick_line(26, 4, 29, 1, 2)
    shaded(c, grip, ("HiltCrimson", "HiltCrimson", "EyeCrimsonDark"))
    pommel = rect(30, 0, 2, 2)
    shaded(c, pommel, GOLD)
    if gilded:
        for y in range(VH):
            for x in range(VW):
                n = c.px[y][x]
                if n in GILD:
                    c.px[y][x] = GILD[n]
        for x, y in ((3, 3), (2, 4), (4, 4), (3, 5), (28, 16), (6, 11), (29, 20), (16, 1)):
            c.set(x, y, "GoldBright")
        for x, y in ((3, 4), (28, 17)):
            c.set(x, y, "TextPrimary")
    return c


VIGNETTES = {
    "KINGDOM_HEALTHY": lambda: kingdom(False),
    "KINGDOM_WARNING": lambda: kingdom(True),
    "SLAYER_ACTIVE": lambda: slayer(False),
    "SLAYER_COMPLETE": lambda: slayer(True),
}
