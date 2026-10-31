"""32x24 Overview vignettes. Each state pair shares one composition so the
state change reads at a glance."""
from pixart import Canvas, rect, thick_line, line
from scenes import GOLD, shaded

VW, VH = 32, 24

def kingdom(warning: bool) -> Canvas:
    """Castle of Miscellania. Both states share one layout so the change reads at a glance:
    healthy flies a gold-and-emerald banner with lit torches and windows; warning has a cracked
    turret, a lost merlon, a tattered crimson banner and an alarm glow in the gatehouse."""
    c = Canvas(VW, VH)
    stone = ("StoneLight", "StoneMid", "StoneDark")

    def courses(mask, rows):
        for x, y in mask:
            if y in rows and c.get(x, y) == "StoneMid":
                c.set(x, y, "StoneDark")

    # Tall keep at the back, banner flying from its roof
    keep = rect(11, 6, 10, 9) | rect(11, 4, 2, 2) | rect(19, 4, 2, 2)
    shaded(c, keep, stone)
    courses(keep, (9, 12))
    window = "HollowDark" if warning else "GoldMid"
    for wx in (14, 17):
        c.set(wx, 7, window)
        c.set(wx, 8, window)
    # Lower front gate wall with battlements
    wall = rect(9, 13, 14, 10)
    for mx in (9, 12, 18, 21):
        wall |= rect(mx, 11, 2, 2)
    shaded(c, wall, stone)
    courses(wall, (16, 20))
    # Flanking turrets
    for tx in (2, 23):
        tower = rect(tx, 9, 7, 14) | rect(tx - 1, 9, 9, 1)
        merlons = [(tx, 2), (tx + 3, 1), (tx + 5, 2)]
        if warning and tx == 23:
            merlons = [(tx, 2), (tx + 5, 2)]  # a merlon has fallen
        for mx, mw in merlons:
            tower |= rect(mx, 7, mw, 2)
        shaded(c, tower, stone)
        courses(tower, (14, 18))
        for y in (11, 12, 13):
            c.set(tx + 3, y, "Outline")
    # Gatehouse arch with an iron portcullis
    arch = rect(13, 16, 6, 7) - {(13, 16), (18, 16)}
    c.outline(arch)
    c.fill(arch, "EyeCrimsonDark" if warning else "HollowDark")
    if warning:
        c.fill({(x, y) for x, y in arch if y >= 20}, "EyeCrimson")
        c.fill({(x, y) for x, y in arch if y == 22}, "FlameOrange")
    for x in (14, 17):
        for y in range(17, 23):
            c.set(x, y, "IronMid")
    for x in range(13, 19):
        if (x, 19) in arch:
            c.set(x, 19, "IronDark")
    # Torches flanking the gate
    flame_top, flame_low = ("FlameOrange", "EyeCrimson") if warning else ("GoldBright", "FlameOrange")
    for fx in (11, 20):
        c.set(fx, 18, "IronDark")
        c.set(fx, 17, flame_low)
        c.set(fx, 16, flame_top)
    if warning:
        # Jagged structural crack down the right turret
        for x, y in ((26, 10), (26, 11), (27, 12), (27, 13), (26, 14), (26, 15), (27, 16), (27, 17), (26, 18)):
            c.set(x, y, "Outline")
        # Bent flagstaff with a tattered, drooping crimson banner
        for x, y in line(16, 4, 17, 0):
            c.set(x, y, "OakDark")
        banner = rect(18, 0, 3, 4) | {(18, 4), (20, 4), (19, 5)}
        c.outline(banner)
        c.fill(banner, "CrownVelvet")
        c.fill(rect(18, 0, 3, 1), "EyeCrimson")
        for x, y in ((23, 1), (24, 3)):
            c.set(x, y, "FlameOrange")
    else:
        # Proud royal banner on the keep
        banner = rect(17, 0, 7, 4) - {(23, 1), (23, 2)}
        c.outline(banner)
        c.fill(banner, "GemEmerald")
        c.fill(rect(17, 0, 6, 1), "GoldBright")
        c.fill(rect(17, 3, 6, 1), "GoldMid")
        c.set(19, 1, "GoldBright")
        c.set(19, 2, "GoldMid")
        for y in range(0, 6):
            c.set(16, y, "OakDark")
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
