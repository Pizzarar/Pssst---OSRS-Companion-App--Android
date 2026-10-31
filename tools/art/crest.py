"""36x36 Informant Crest: a hooded informant peeking around a stone pillar,
hand cupped to whisper "Pssst..." toward a glowing gold sync scroll.

Key silhouettes (eyes, face, glove, scroll) sit inside the adaptive-icon safe
circle (centre 18,18, radius 11 logical px). The pillar shaft and the cloak hem
are framing only, so round launcher masks may trim them without losing the read.
"""
from pixart import Canvas, rect, solid, edge_shade

HOOD = [
    # 0123456789012345
    "...LLLL.........",  # 0  cowl crown
    "..LMMMMLR.......",  # 1
    ".LMMMMMMMR......",  # 2
    "LMMMMMMMMMR.....",  # 3
    "LMMMMMDDDDDR....",  # 4  hood lip
    "LMMMMDEEEEEER...",  # 5  shadowed brow
    "LMMMMDEEYYEER...",  # 6  glinting eyes, the only bright thing in the cowl
    ".LMMMDEEEEESk...",  # 7  scroll-lit nose tip
    "..LMMDEEEEEk....",  # 8  face stays in hood shadow
    "...LMMDEEEk.....",  # 9  neck curves in behind the pillar
    "....LMMDDk......",  # 10
    "...LMMMMMDR.....",  # 11
    "..LMMMMMMMDRR...",  # 12 shoulders swell back out
    ".LMMDMMMMMMMDR..",  # 13 cloak folds
    "LMMDMMMMMMGMMDR.",  # 14 brass cloak pin
    "LMMDDMMMMMDMMMDR",  # 15
    "LMMMDMMMMMDDMMMD",  # 16
    "LMMMDMMMMMMDMMMD",  # 17
    "LMMMDDMMMMMDMMMD",  # 18
    "LMMMMDMMMMMDMMMD",  # 19
]
HOOD_LEGEND = {
    "L": "RogueLight", "M": "RogueMid", "D": "RogueDark", "E": "HollowDark",
    "R": "RogueRim", "Y": "GoldBright", "G": "GoldMid",
    "S": "SkinLight", "s": "SkinMid", "k": "SkinDark",
}

GLOVE = [
    "S.S.",
    "SSSS",
    "SSSs",
    "SSsk",
    ".sk.",
]
GLOVE_LEGEND = {"S": "SkinLight", "s": "SkinMid", "k": "SkinDark"}

SCROLL = [
    "BGGGB",
    "gGGGg",
    ".WWw.",
    ".Wvw.",
    ".WWw.",
    ".Wvw.",
    "BGGGB",
    "gGGGg",
]
SCROLL_LEGEND = {
    "B": "GoldBright", "G": "GoldMid", "g": "GoldDeep",
    "W": "ParchLight", "w": "ParchMid", "v": "ParchDeep",
}


def build() -> Canvas:
    c = Canvas(36, 36)
    # Informant bust
    c.stamp(HOOD, 8, 8, HOOD_LEGEND)
    # Bare hand cupped at the mouth; its own outline keeps it apart from the shadowed face
    c.stamp(GLOVE, 19, 15, GLOVE_LEGEND)
    # Stone pillar in front of the cowl's back edge (framing; may be masked)
    shaft = rect(6, 10, 3, 19)
    solid(shaft, c, edge_shade(shaft, "StoneLight", "StoneMid", "StoneDark"))
    for y in (14, 20, 26):  # cut-stone courses: dark mortar seam, lit top edge below it
        for x in range(6, 9):
            c.set(x, y, "Outline")
            c.set(x, y + 1, "StoneLight")
    cap = rect(5, 8, 5, 2)
    solid(cap, c, edge_shade(cap, "StoneLight", "StoneMid", "StoneDark"))
    # Glowing sync scroll the secret is whispered toward
    c.stamp(SCROLL, 22, 10, SCROLL_LEGEND)
    # Whisper motes drifting from the glove to the scroll, plus scroll glints
    for (x, y, n) in ((22, 18, "CyanMoteLight"), (23, 20, "CyanMoteMid"),
                      (25, 20, "CyanMoteLight"), (27, 12, "GoldBright"), (20, 9, "GoldBright")):
        c.set(x, y, n)
    return c
