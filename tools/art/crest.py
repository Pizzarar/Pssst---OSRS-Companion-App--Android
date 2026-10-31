"""36x36 Pssst crest: a heraldic shield quartered crimson and royal blue, in a 2px chiselled gold
border with four rivets, carrying a bold chiselled gold "P".

The 16x21 heater shield (x 10..25, y 8..28) is as large as a flat-topped shield can be while
every pixel stays inside the adaptive-icon safe circle (centre 18,18, radius 11 logical px),
so every launcher mask shows all of it; build() asserts this.
"""
from pixart import Canvas, ring

CX, CY, R = 18.0, 18.0, 11.0

P_GLYPH = [
    "#####.",
    "######",
    "##..##",
    "##..##",
    "######",
    "#####.",
    "##....",
    "##....",
    "##....",
]


# Heater shield rows (y: x0..x1): chamfered top, straight 16px sides, curved taper to a point.
SHIELD_ROWS = {
    8: (12, 23), 9: (11, 24),
    10: (10, 25), 11: (10, 25), 12: (10, 25), 13: (10, 25), 14: (10, 25), 15: (10, 25),
    16: (10, 25), 17: (10, 25), 18: (10, 25),
    19: (11, 24), 20: (11, 24), 21: (12, 23), 22: (12, 23), 23: (13, 22), 24: (14, 21),
    25: (15, 20), 26: (16, 19), 27: (17, 18), 28: (17, 18),
}


def build() -> Canvas:
    c = Canvas(36, 36)
    shield = {(x, y) for y, (x0, x1) in SHIELD_ROWS.items() for x in range(x0, x1 + 1)}
    assert all((x + 0.5 - CX) ** 2 + (y + 0.5 - CY) ** 2 <= R * R for x, y in shield), "shield leaves safe circle"
    c.outline(shield, diag=True)

    def edge(p, s):
        x, y = p
        return any(q not in s for q in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)))

    outer = {p for p in shield if edge(p, shield)}
    inner_area = shield - outer
    inner = {p for p in inner_area if edge(p, inner_area)}
    field = inner_area - inner
    lit = lambda x, y: x + y <= 34  # top-left half catches the light
    for x, y in outer:
        c.set(x, y, "GoldBright" if lit(x, y) else "GoldDeep")
    for x, y in inner:
        c.set(x, y, "GoldMid" if lit(x, y) else "GoldShadow")
    # Quartered field: crimson top-left / bottom-right, royal blue top-right / bottom-left
    for x, y in field:
        crimson = (x <= 17) == (y <= 17)
        top_edge = (x, y - 1) in inner or (x - 1, y) in inner
        low_edge = (x, y + 1) in inner or (x + 1, y) in inner
        if crimson:
            c.set(x, y, "VelvetLight" if top_edge else ("VelvetDark" if low_edge else "VelvetMid"))
        else:
            c.set(x, y, "QuestBlueLight" if top_edge else ("QuestBlueDark" if low_edge else "QuestBlueMid"))
    # Four rivets seated in the border
    for x, y in ((12, 10), (23, 10), (12, 20), (23, 20)):
        if (x, y) in inner:
            c.set(x, y, "Bone")

    # Bold chiselled gold "P"
    p = {(x + 15, y + 11) for y, row in enumerate(P_GLYPH) for x, ch in enumerate(row) if ch == "#"}
    c.fill(ring(p, diag=True) & shield, "Outline")
    for x, y in p:
        is_lit = (x - 1, y) not in p or (x, y - 1) not in p
        shade = (x + 1, y) not in p or (x, y + 1) not in p
        c.set(x, y, "GoldBright" if is_lit else ("GoldDeep" if shade else "GoldMid"))
    return c
