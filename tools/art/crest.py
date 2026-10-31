"""36x36 Pssst crest: a gold-trimmed heraldic shield, crimson and royal blue, carrying a bold
chiselled gold "P". The whole emblem sits inside the adaptive-icon safe circle (centre 18,18,
radius 11 logical px), so every launcher mask shows all of it.
"""
from pixart import Canvas, ring

# Shield silhouette in 36x36 coordinates: straight sides, then a curved point.
SHIELD_ROWS = {
    9: (11, 24), 10: (11, 24), 11: (11, 24), 12: (11, 24), 13: (11, 24), 14: (11, 24),
    15: (11, 24), 16: (11, 24), 17: (11, 24), 18: (11, 24), 19: (11, 24),
    20: (12, 23), 21: (12, 23), 22: (13, 22), 23: (14, 21), 24: (15, 20), 25: (16, 19),
    26: (17, 18),
}

P_GLYPH = [
    "#####.",
    "##..##",
    "##..##",
    "#####.",
    "##....",
    "##....",
    "##....",
]


def build() -> Canvas:
    c = Canvas(36, 36)
    # Shield: outline, gold trim ring, split crimson / royal-blue field
    shield = {(x, y) for y, (x0, x1) in SHIELD_ROWS.items() for x in range(x0, x1 + 1)}
    c.outline(shield, diag=True)
    trim = {p for p in shield if any(q not in shield for q in ((p[0] - 1, p[1]), (p[0] + 1, p[1]),
                                                              (p[0], p[1] - 1), (p[0], p[1] + 1)))}
    field = shield - trim
    for x, y in trim:
        lit = (x <= 17 and y <= 20) or y == 9
        c.set(x, y, "GoldBright" if lit else "GoldDeep")
    for x, y in field:
        edge = (x - 1, y) in trim or (x, y - 1) in trim
        if x <= 17:
            c.set(x, y, "VelvetLight" if edge else "VelvetMid")
        else:
            c.set(x, y, "QuestBlueLight" if edge else "QuestBlueMid")
    # Inner shadow along the lower edge for depth
    for x, y in field:
        if (x, y + 1) in trim:
            c.set(x, y, "VelvetDark" if x <= 17 else "QuestBlueDark")
    # Bone rivets in the upper corners of the trim
    c.set(12, 10, "Bone")
    c.set(23, 10, "Bone")

    # Chiselled gold "P"
    p = {(x + 15, y + 11) for y, row in enumerate(P_GLYPH) for x, ch in enumerate(row) if ch == "#"}
    c.fill(ring(p, diag=True) & shield, "Outline")
    for x, y in p:
        lit = (x - 1, y) not in p or (x, y - 1) not in p
        shade = (x + 1, y) not in p or (x, y + 1) not in p
        c.set(x, y, "GoldBright" if lit else ("GoldDeep" if shade else "GoldMid"))
    return c
