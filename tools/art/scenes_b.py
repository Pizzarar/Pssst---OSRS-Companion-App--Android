"""Empty-state scenes 2-7 (48x36)."""
from pixart import Canvas, rect, ellipse, polygon, thick_line, line, ring, text_mask
from scenes import W, H, OAK, IRON, STONE, GOLD, REDWOOD, PARCH, VELVET, shaded, glyph, bank_empty

# --------------------------------------------------------------- 2. Farming empty

LEPRECHAUN = [
    # 01234567890123
    "....dddddd....",  # 0 top hat crown
    "....dhhhHd....",  # 1
    "....dhHHHd....",  # 2
    "....dhHHHd....",  # 3
    "....kkYykk....",  # 4 band + gold buckle
    "..dhhHHHHHHd..",  # 5 brim
    "....ookkoo....",  # 6 scowling brow
    "....SoSSoS....",  # 7 eyes
    "....sSNNSs....",  # 8 red nose
    "...lRRRRRRr...",  # 9 ginger beard
    "...lRRRRRRr...",  # 10
    "..CClRRRRrCC..",  # 11 coat shoulders
    ".ChCCrRRrCCCd.",  # 12
    ".ChKKKKKKKKCd.",  # 13 arms crossed
    ".ChSKKKKKKSCd.",  # 14 fists
    "..CCCCCCCCCd..",  # 15
    "..dd......dd..",  # 16 legs
    "..dd......dd..",  # 17
    ".PPP.....PPP..",  # 18 buckled shoes
]
LEPRECHAUN_LEGEND = {
    "d": "HatGreenDark", "h": "HatGreenLight", "H": "HatGreenMid", "k": "Outline",
    "Y": "GoldBright", "y": "GoldMid", "o": "Outline", "S": "SkinLight", "s": "SkinMid",
    "N": "NoseRed", "R": "BeardGinger", "r": "BeardGingerDark", "l": "BeardGingerLight",
    "C": "HatGreenMid", "K": "HatGreenDark", "P": "OakDeep",
}

CROW = [
    # 0123456789
    "...nnn....",
    "..nNNNn...",
    "BBNeNNNn..",
    "..nNNNNNn.",
    "...nNNNNNn",
    "....nNNNNN",
    ".....mmmm.",
    "......ff..",
]
CROW_LEGEND = {"n": "CrowMid", "N": "CrowLight", "e": "GoldBright", "B": "GoldMid",
               "m": "CrowDark", "f": "GoldDeep"}


def farming_empty() -> Canvas:
    c = Canvas(W, H)
    # Dry, furrowed fallow soil
    soil = polygon([(17, 36), (19, 28), (48, 27), (48, 36)])
    shaded(c, soil, ("SoilLight", "SoilMid", "SoilDark"))
    for y in (30, 33):
        for x in range(19, 47):
            if (x + y) % 6 != 0 and (x, y) in soil:
                c.set(x, y, "SoilDark")
    for y in (29, 32):
        for x in range(20, 46, 3):
            if (x, y) in soil:
                c.set(x, y, "SoilLight")
    # Weeds sprouting where crops should be
    for wx in (22, 27):
        weed = {(wx, 26), (wx + 1, 25), (wx - 1, 25), (wx, 27), (wx + 2, 24), (wx - 2, 24)}
        c.fill(ring(weed) - soil, "Outline")
        c.fill(weed, "WeedMid")
        c.set(wx + 2, 24, "WeedLight")
        c.set(wx - 2, 24, "WeedLight")
    # Rake stuck tines-first in the furrows
    handle = thick_line(33, 25, 43, 7, 2)
    shaded(c, handle, OAK)
    head = rect(29, 25, 11, 2)
    shaded(c, head, IRON)
    for tx in (30, 32, 34, 36, 38):
        tine = rect(tx, 27, 1, 2)
        c.fill(ring(tine) - head, "Outline")
        c.fill(tine, "IronMid")
    # Crow perched on the rake handle, eyeing the weeds
    c.stamp(CROW, 37, 0, CROW_LEGEND)
    # Upright iron compost bucket
    bucket = polygon([(2, 24), (16, 24), (15, 36), (3, 36)])
    shaded(c, bucket, IRON)
    for y in (27, 32):
        for x in range(3, 15):
            if (x, y) in bucket and c.get(x, y) != "Outline":
                c.set(x, y, "IronDark")
    c.fill(rect(3, 24, 13, 1), "CompostDark")
    # Grumpy tool leprechaun perched on the bucket, arms crossed
    c.stamp(LEPRECHAUN, 2, 5, LEPRECHAUN_LEGEND)
    return c


# --------------------------------------------------------------- 3. Birdhouses empty

CLOCKBIRD = [
    # perched on the right eave, leaning forward and down toward the entrance hole
    "......K.K.",  # brass wind-up key bow
    ".......K..",  # key shaft into its back
    "....bbbbb.",
    "..bBBBBBbt",
    ".bBBBBBbtt",
    "bwebbbbb..",  # bone eye ring + pupil
    "Bbb.......",
    "gg........",  # beak pointing down at the hole
    ".f..f.....",  # feet gripping the shingles
]
CLOCKBIRD_LEGEND = {"K": "GoldBright", "b": "SteelBlueMid", "B": "SteelBlueLight", "t": "SteelBlueDark",
                    "e": "Outline", "w": "Bone", "g": "GoldMid", "f": "GoldDeep"}

QMARK = [
    ".####.",
    "##..##",
    "....##",
    "...##.",
    "..##..",
    "..##..",
    "......",
    "..##..",
]


def birdhouse_empty() -> Canvas:
    c = Canvas(W, H)
    post = rect(20, 28, 5, 8)
    shaded(c, post, ("OakMid", "OakDark", "OakDeep"))
    box = rect(13, 15, 19, 14)
    shaded(c, box, REDWOOD)
    for x in (18, 27):
        for y in range(16, 28):
            c.set(x, y, "RedwoodDark")
    # Recessed entrance hole with a warm inner bevel so it reads on dark stone
    hole = {(x, y) for x in range(18, 28) for y in range(16, 26)
            if (x + 0.5 - 23) ** 2 + (y + 0.5 - 21) ** 2 <= 12.5}
    c.fill(ring(hole), "Outline")
    c.fill(hole, "RogueDark")
    c.fill({(x, y) for x, y in hole if (x + 0.5 - 23) ** 2 + (y + 0.5 - 21) ** 2 <= 5}, "HollowDark")
    peg = rect(21, 26, 4, 1)
    shaded(c, peg, OAK)
    roof = polygon([(9, 16.5), (22.5, 5), (36, 16.5)])
    shaded(c, roof, ("RedwoodLight", "RedwoodMid", "RedwoodDeep"))
    for y in (9, 12, 15):
        for x in range(9, 37):
            if (x, y) in roof and c.get(x, y) == "RedwoodMid" and (x + y) % 4 != 0:
                c.set(x, y, "RedwoodDark")
    for x, y in ((16, 12), (22, 8), (29, 12)):
        c.set(x, y, "GoldBright")
    c.stamp(CLOCKBIRD, 28, 5, CLOCKBIRD_LEGEND)
    q = {(x + 41, y + 4) for y, row in enumerate(QMARK) for x, ch in enumerate(row) if ch == "#"}
    c.fill(ring(q, diag=True), "Outline")
    c.fill(q, "TextPrimary")
    return c


# --------------------------------------------------------------- 4. Potions empty

def potions_empty() -> Canvas:
    c = Canvas(W, H)
    for bx in (8, 37):
        br = rect(bx, 25, 2, 6) | rect(bx - 2, 25, 4, 2)
        shaded(c, br, IRON)
    shelf = rect(2, 22, 44, 3)
    shaded(c, shelf, OAK)
    # Stone mortar with a wooden pestle
    pestle = thick_line(14, 15, 20, 7, 2)
    shaded(c, pestle, ("CorkLight", "OakLight", "OakDark"))
    bowl = polygon([(5, 15), (20, 15), (17, 22), (8, 22)])
    shaded(c, bowl, STONE)
    c.fill(rect(7, 15, 11, 1), "StoneDeep")
    ash = {(20, 21), (21, 21), (22, 21), (21, 20)}
    c.fill(ring(ash) - shelf, "Outline")
    c.fill(ash, "AshGrey")
    # Tipped-over glass vial lying on its side
    vial = ellipse(30, 18, 5.5, 3.6)
    shaded(c, vial, ("GlassLight", "GlassMid", "GlassDark"))
    neck = rect(35, 17, 3, 3)
    shaded(c, neck, ("GlassLight", "GlassMid", "GlassDark"))
    c.fill(rect(27, 16, 3, 1), "GlassLight")
    c.fill({(x, y) for x, y in vial if y >= 20 and c.get(x, y) != "Outline"}, "HerbloreDark")
    cork = rect(41, 19, 3, 3)
    shaded(c, cork, ("CorkLight", "CorkLight", "CorkDark"))
    # Puddle and drip running off the shelf edge
    pud = rect(36, 21, 6, 1)
    c.fill(pud, "HerbloreMid")
    c.set(37, 21, "HerbloreLight")
    drip = rect(40, 25, 2, 2) | rect(40, 27, 1, 2)
    c.fill(ring(drip) - shelf, "Outline")
    c.fill(drip, "HerbloreMid")
    c.fill(rect(40, 22, 2, 3), "HerbloreMid")
    c.fill(rect(40, 22, 1, 5), "HerbloreLight")
    drop = {(40, 30), (39, 31), (40, 31), (41, 31), (39, 32), (40, 32), (41, 32)}
    c.fill(ring(drop), "Outline")
    c.fill(drop, "HerbloreMid")
    c.set(39, 31, "HerbloreLight")
    return c


# --------------------------------------------------------------- 5. Quests empty

def quests_empty() -> Canvas:
    c = Canvas(W, H)
    post = polygon([(19, 0), (29, 0), (30, 36), (18, 36)])
    shaded(c, post, ("OakMid", "OakDark", "OakDeep"))
    for x, y in line(22, 1, 23, 34) + line(26, 3, 27, 33):
        c.set(x, y, "OakDeep")
    note = rect(10, 6, 28, 24) - {(37, 14), (36, 15), (37, 15), (37, 16)}
    shaded(c, note, PARCH)
    for x, y in ((10, 6), (37, 6), (10, 29), (37, 29)):
        c.set(x, y, "ParchDeep")
    for y, x0, x1 in ((10, 13, 30), (13, 13, 33), (16, 13, 31), (19, 13, 26)):
        for x in range(x0, x1 + 1):
            if (x * 5 + y) % 7 != 0:
                c.set(x, y, "InkBrown")
    # Blue quest-star wax seal with ribbon tails
    tails = thick_line(21, 27, 19, 33, 2) | thick_line(26, 27, 28, 33, 2)
    shaded(c, tails, ("QuestBlueLight", "QuestBlueMid", "QuestBlueDark"))
    seal = ellipse(24, 25, 4.6, 4.6)
    shaded(c, seal, ("QuestBlueLight", "QuestBlueMid", "QuestBlueDark"))
    star = {(24, 22), (24, 23), (22, 24), (23, 24), (24, 24), (25, 24), (26, 24), (24, 25),
            (23, 26), (25, 26)}
    c.fill(star, "GoldBright")
    c.set(24, 24, "GoldMid")
    # Rune dagger driven through the top corner of the notice
    blade = thick_line(36, 4, 29, 11, 2)
    shaded(c, blade, ("RuneLight", "RuneMid", "RuneDark"))
    guard = thick_line(35, 1, 40, 6, 2)
    shaded(c, guard, GOLD)
    grip = thick_line(40, 0, 43, -3, 2) | rect(39, 0, 4, 2)
    shaded(c, grip, ("HiltLeather", "HiltLeather", "HiltLeatherDark"))
    return c


# --------------------------------------------------------------- 6. Dailies done

def dailies_empty() -> Canvas:
    c = Canvas(W, H)
    shaded(c, rect(6, 32, 25, 3), STONE)
    col = rect(13, 17, 10, 15)
    shaded(c, col, STONE)
    for y in range(18, 31, 4):
        for x in range(14, 22):
            c.set(x, y, "StoneDark")
    # Bevelled octagonal dial plate with hour ticks
    dial = rect(6, 11, 24, 5) - {(6, 11), (29, 11), (6, 15), (29, 15)}
    c.outline(dial)
    c.paint(dial, lambda x, y: "StoneLight" if y <= 12 else ("StoneMid" if y <= 14 else "StoneDark"))
    for tx in (8, 11, 24, 27):
        c.set(tx, 12, "StoneDeep")
    # Brass gnomon fin: bright hypotenuse edge, mid body, dark base, cast shadow on the dial
    gnomon = polygon([(12, 11.5), (12, 2), (21, 11.5)])
    c.outline(gnomon)

    def fin(x, y):
        if (x + 1, y) not in gnomon or ((x, y - 1) not in gnomon and x > 12):
            return "GoldBright"  # lit hypotenuse edge
        if x == 12:
            return "GoldMid"     # front edge
        return "GoldDeep" if y >= 10 else "GoldShadow" if (x + y) % 5 == 0 else "GoldDeep"
    c.paint(gnomon, fin)
    for x in range(21, 28):
        c.set(x, 13, "StoneDeep")
    for x, y in line(10, 17, 9, 20) + line(26, 17, 27, 20):
        c.set(x, y, "ParchDeep")
    plank = rect(5, 20, 27, 9)
    shaded(c, plank, ("OakMid", "OakDark", "OakDeep"))
    glyph(c, "RESET", 7, 22)
    # Empty battlestaff barrel with its lid knocked aside
    barrel = rect(34, 20, 12, 15) - {(34, 20), (45, 20), (34, 34), (45, 34)}
    shaded(c, barrel, OAK)
    for y in (24, 30):
        for x in range(35, 45):
            c.set(x, y, "IronMid")
    c.fill(rect(35, 20, 10, 3), "HollowDark")
    c.fill(rect(35, 20, 10, 1), "RecessRim")
    leaf = {(31, 34), (32, 33), (33, 33), (32, 34)}
    c.fill(ring(leaf), "Outline")
    c.fill(leaf, "WeedMid")
    c.set(32, 33, "WeedLight")
    return c


# --------------------------------------------------------------- 7. No GP goal

def goal_empty() -> Canvas:
    c = Canvas(W, H)
    shaded(c, rect(9, 27, 28, 8), STONE)
    shaded(c, rect(7, 25, 32, 3), STONE)
    cush = rect(11, 21, 24, 4)
    shaded(c, cush, VELVET)
    for tx in (10, 35):
        t = rect(tx, 23, 1, 3)
        c.fill(ring(t) - cush, "Outline")
        c.fill(t, "GoldMid")
        c.set(tx, 23, "GoldBright")
    # Chalk ghost of the loot this goal could be: a trophy cup beside a small coin pile.
    trophy = [
        "..#################..",
        "..#...............#..",
        "###...............###",
        "#.#...............#.#",
        "#..#.............#..#",
        ".#.#.............#.#.",
        "..##.............##..",
        "....#...........#....",
        ".....#.........#.....",
        "......##.....##......",
        "........#...#........",
        "........#...#........",
        "......##.....##......",
        ".....#.........#.....",
        ".....###########.....",
    ]
    coin_pile = [
        "..#####..",
        ".#.....#.",
        "..#####..",
        ".#.....#.",
        "#.#####.#",
        ".#.....#.",
        "..#####..",
    ]
    chalk = {(x + 10, y + 3) for y, r in enumerate(trophy) for x, ch in enumerate(r) if ch == "#"}
    chalk |= {(x + 32, y + 12) for y, r in enumerate(coin_pile) for x, ch in enumerate(r) if ch == "#"}
    for x, y in chalk:
        c.set(x, y, "ChalkLight" if (x + y) % 3 else "ChalkDim")
    for x, y in ((20, 0), (19, 1), (21, 1), (20, 2)):
        c.set(x, y, "ChalkLight")
    # Brass price tag with a question mark, tied to the cushion corner
    tag = polygon([(36, 24), (45, 24), (46, 35), (37, 35)])
    shaded(c, tag, GOLD)
    c.fill(text_mask("?", 39, 27), "Outline")
    for x, y in line(34, 22, 37, 25):
        c.set(x, y, "ParchDark")
    return c


SCENES = {
    "CLOSED_CHEST": bank_empty,
    "FALLOW_PATCH": farming_empty,
    "EMPTY_BIRDHOUSE": birdhouse_empty,
    "POTION_RACK": potions_empty,
    "QUEST_SCROLL": quests_empty,
    "SUNDIAL": dailies_empty,
    "LEDGER_BOOKMARK": goal_empty,
}
