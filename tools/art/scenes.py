"""48x36 empty-state scenes. Light comes from the top-left; every object carries
its own 1px Outline so overlapping props stay separated on dark stone."""
from pixart import (Canvas, rect, ellipse, polygon, thick_line, line, solid, edge_shade,
                    text_mask, ring)

W, H = 48, 36

OAK = ("OakLight", "OakMid", "OakDark")
IRON = ("IronLight", "IronMid", "IronDark")
STONE = ("StoneLight", "StoneMid", "StoneDark")
GOLD = ("GoldBright", "GoldMid", "GoldDeep")
REDWOOD = ("RedwoodLight", "RedwoodMid", "RedwoodDark")
PARCH = ("ParchLight", "ParchMid", "ParchDark")
VELVET = ("VelvetLight", "VelvetMid", "VelvetDark")


def shaded(c: Canvas, mask, ramp, outline=True, diag=False):
    return solid(mask, c, edge_shade(mask, *ramp), outline=outline, diag=diag)


def glyph(c: Canvas, s: str, x: int, y: int, fill: str = "TextPrimary", gap: int = 1):
    m = text_mask(s, x, y, gap)
    c.fill(ring(m, diag=True), "Outline")
    c.fill(m, fill)
    return m


def grain(c: Canvas, mask, name: str, every: int = 3, vertical: bool = True, seed: int = 0):
    """Sparse plank seams/wood grain inside a mask."""
    for x, y in sorted(mask):
        k = x if vertical else y
        if (k + seed) % every == 0 and ((x * 7 + y * 3 + seed) % 5) != 0:
            if c.get(x, y) not in ("Outline", None):
                c.set(x, y, name)


# --------------------------------------------------------------- 1. Bank empty

CLERK_HEAD = [
    "...gggg...",
    ".gGGssSSg.",
    "gGGsSSSSSs",
    "gGsSSSSSSs",
    "gGsSoooSSk",
    "gGgSSSSSHk",
    ".gGsSSHHHk",
    "..gkkkkkk.",
]
CLERK_HEAD_LEGEND = {
    "g": "HairGreyDark", "G": "HairGreyMid", "S": "SkinLight", "s": "SkinMid",
    "k": "SkinDark", "o": "Outline", "H": "HairGreyLight",
}

ARMS = [
    "VVVVVVVVVVVVVbbSS",
    "VvvvvvvvvvvvvbbSS",
    "vdddddddddddddbss",
]
ARMS_LEGEND = {"V": "VestLight", "v": "VestMid", "d": "VestDark", "b": "Bone", "S": "SkinLight",
               "s": "SkinDark"}


def bank_empty() -> Canvas:
    c = Canvas(W, H)
    # Teller grate behind the clerk
    shaded(c, rect(1, 5, 25, 2), IRON)
    for bx in (3, 9, 21):
        shaded(c, rect(bx, 7, 1, 15), ("IronMid", "IronMid", "IronDark"))
    # Clerk's slumped back (vest)
    shaded(c, ellipse(10.5, 21.5, 8.5, 6), ("VestLight", "VestMid", "VestDark"))
    for x, y in ((15, 16), (16, 16), (17, 17)):
        c.set(x, y, "Bone")
    # Head resting on crossed arms, eyes shut
    c.stamp(CLERK_HEAD, 12, 13, CLERK_HEAD_LEGEND)
    c.stamp(ARMS, 4, 20, ARMS_LEGEND)
    # Oak teller counter with brass name plate
    shaded(c, rect(0, 23, 26, 2), OAK)
    front = rect(1, 25, 24, 10)
    shaded(c, front, ("OakMid", "OakDark", "OakDeep"))
    for px in (7, 18):
        for y in range(26, 34):
            c.set(px, y, "OakDeep")
    plate = rect(10, 28, 6, 3)
    shaded(c, plate, GOLD)
    # Snoring Zzz drifting up-left
    glyph(c, "Z", 6, 1)
    zz = {(1, 10), (2, 10), (3, 10), (3, 11), (2, 12), (1, 13), (2, 13), (3, 13)}
    c.fill(ring(zz, diag=True), "Outline")
    c.fill(zz, "ParchMid")
    # --- Open, empty bank chest: lid flung back on its hinge, dark empty cavity in front
    lid = rect(29, 4, 16, 9)
    shaded(c, lid, ("OakMid", "OakDark", "OakDeep"))
    for x in (33, 37, 41):
        for y in range(5, 12):
            c.set(x, y, "OakDeep")
    for y in (6, 10):
        for x in range(30, 44):
            c.set(x, y, "IronDark")
    for x in (31, 42):  # hinges
        c.set(x, 13, "IronMid")
    body = rect(28, 21, 18, 13)
    shaded(c, body, OAK)
    for y in (25, 29):
        for x in range(29, 45):
            c.set(x, y, "OakDark")
    # Five-row cavity: warm rim on top and sides, hollow dark inside
    cavity = rect(28, 14, 18, 7)
    c.outline(cavity)
    c.fill(cavity, "RecessRim")
    c.fill(rect(29, 15, 16, 5), "HollowDark")
    c.fill(rect(29, 15, 16, 1), "RecessRim")
    for sx in (31, 42):
        strap = rect(sx, 21, 2, 13)
        c.paint(strap, edge_shade(strap, *IRON))
    hasp = rect(36, 21, 3, 4)
    shaded(c, hasp, GOLD)
    c.set(37, 23, "Outline")
    # Cobweb draped across the open corner, lid to rim
    for x, y in line(29, 11, 33, 17) + line(29, 15, 34, 13) + line(29, 18, 32, 15) + line(31, 12, 30, 19):
        c.set(x, y, "Cobweb")
    # Moth fluttering up out of the chest: two cream wings around a dark body
    moth = {(35, 0), (36, 1), (37, 1), (38, 1), (39, 0), (35, 1), (39, 1), (36, 2), (38, 2), (37, 2)}
    c.fill(ring(moth) - {(37, -1)}, "Outline")
    c.fill(moth, "MothLight")
    c.set(37, 1, "Outline")
    c.set(37, 2, "OakDeep")
    for x, y in ((38, 5), (37, 8), (38, 11)):
        c.set(x, y, "TextPrimary")
    return c
