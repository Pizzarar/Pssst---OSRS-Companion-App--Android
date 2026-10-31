"""16x16 ArtIcons + ornament sprites, authored as fill-only role grids.

`outlined()` adds the 1px 'o' outline around every fill pixel (4-neighbour), so the
SpriteGridTest enclosure rule holds by construction. Internal 'o' seams may be written
explicitly. `python tools/art/sprites.py` rewrites the row strings of each named Sprite in
Icons.kt / Ornaments.kt in place (KDoc and naturalTones lines are left alone).
"""
import math
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
ART = os.path.join(ROOT, "app", "src", "main", "java", "com", "pssst", "theme", "art")


def outlined(rows):
    h, w = len(rows), len(rows[0])
    g = [list(r) for r in rows]
    for y in range(h):
        for x in range(w):
            if rows[y][x] != ".":
                continue
            for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h and rows[ny][nx] not in ".o":
                    g[y][x] = "o"
                    break
    return ["".join(r) for r in g]


def from_fn(fn, size=16):
    return ["".join(fn(x, y) for x in range(size)) for y in range(size)]


# ------------------------------------------------------------------ procedural

def star_fn(x, y):
    dx, dy = x + 0.5 - 8, y + 0.5 - 8
    ax, ay = abs(dx), abs(dy)
    if not (1 <= x <= 14 and 1 <= y <= 14):
        return "."
    vertical = ay > ax and ax <= 2.6 - ay * 0.3
    horizontal = ax >= ay and ay <= 2.6 - ax * 0.3
    diagonal = abs(ax - ay) <= 0.6 and ax <= 4.6
    if ax + ay <= 2.1:
        if (x, y) == (7, 7):
            return "g"
        return "a" if dx + dy < 0 else "d"
    if vertical:
        return ("h" if dy < 0 else "b") if dx < 0 else ("b" if dy < 0 else "s")
    if horizontal:
        return ("h" if dx < 0 else "b") if dy < 0 else ("b" if dx < 0 else "s")
    if diagonal:
        return "h" if dx + dy < 0 else ("b" if dx * dy < 0 else "s")
    return "."


def gear_fn(x, y):
    dx, dy = x + 0.5 - 8, y + 0.5 - 8
    r = math.hypot(dx, dy)
    ang = (math.degrees(math.atan2(dy, dx)) + 360) % 45
    tooth = (ang <= 11 or ang >= 34) and r <= 7.1
    if not (1 <= x <= 14 and 1 <= y <= 14):
        return "."
    if r <= 1.6:
        return "."
    if r <= 2.7:
        return "a" if dx + dy < 0 else "d"
    if r <= 5.2 or tooth:
        lit = -(dx + dy) / max(r, 0.1)
        if r > 4.3 and not tooth or (tooth and r > 5.2):
            return "h" if lit > 0.35 else ("s" if lit < -0.35 else "b")
        return "b"
    return "."


SPRITES = {
    "OverviewShield": [
        "................",
        ".hhhhhhobbbbbbs.",
        ".hbbbbbobsssssg.",
        ".hbbbbbobssssss.",
        ".hbbbbbobssssss.",
        ".hbbbbbobssssss.",
        ".hbbbbohobsssss.",
        ".ooooohgsoooooo.",
        "..bbbbosohhhhs..",
        "..bsssohbbbbs...",
        "...bssohbbbs....",
        "....bsohbbs.....",
        ".....bohbs......",
        "......ohs.......",
        "................",
        "................",
    ],
    "StorageChest": [
        "................",
        "................",
        "...hhhhhhhhhhb..",
        "..hhbabbbbbabbs.",
        ".hbbbabbbbbabbs.",
        ".hbbbabbbbbabbs.",
        ".oooooogggooooo.",
        ".hbbbabgggbabbs.",
        ".hbbbaboogbabbs.",
        ".hbbbabggggabbs.",
        ".hbbbabbbbbabbs.",
        ".hbbbabbbbbabbs.",
        ".sssssasssssass.",
        "................",
        "................",
        "................",
    ],
    "ExchangeCoins": [
        "................",
        "................",
        "..hhhhhhb...g...",
        "..sssssss.......",
        "..hhhhhhb.......",
        "..sssssss.......",
        "..hhhhhhb.hhhb..",
        "..sssssssossss..",
        "..hhhhhhbohhhb..",
        "..sssssssossss..",
        "..hhhhhhbohhhb..",
        "..sssssssossss..",
        "..hhhhhhbohhhb..",
        "..sssssssossss..",
        "................",
        "................",
    ],
    "ProgressStar": from_fn(star_fn),
    "TimersHourglass": [
        "................",
        ".aaaaaaaaaaaaaa.",
        ".dddddddddddddd.",
        "..a.bsssssss.a..",
        "..a..bhhhhs..a..",
        "..a..bshhss..a..",
        "..a...bhs....a..",
        "..a....bs....a..",
        "..a....hs....a..",
        "..a...bhss...a..",
        "..a..bsshss..a..",
        "..a..bhhhhs..a..",
        "..a.bhhhhhhs.a..",
        ".aaaaaaaaaaaaaa.",
        ".dddddddddddddd.",
        "................",
    ],
    "DrawerLedgerMenu": [
        "................",
        "..addddddddddd..",
        "..addddddddddd..",
        ".ahhhhhhhhhhhha.",
        ".abbbbbbbbbbbba.",
        "..oooooooooooo..",
        "..addddddddddd..",
        ".ahhhhhhhhhhhha.",
        ".abbbbbbbbbbbba.",
        "..oooooooooooo..",
        "..addddddddddd..",
        ".ahhhhhhhhhhhha.",
        ".abbbbbbbbbbbba.",
        "..oooooooooooo..",
        "..addddddddddd..",
        "................",
    ],
    "EditQuill": [
        "................",
        "...........hhb..",
        ".........hhhbbs.",
        "........hhhbbs..",
        ".......hhhbbs...",
        "......hhbbbs....",
        ".....hhbbss.....",
        ".....hbbss......",
        "....hbbs........",
        "....hbs.........",
        "...hbs..........",
        "...as...........",
        "..ad............",
        ".ad.............",
        ".d..............",
        "................",
    ],
    "Gear": from_fn(gear_fn),
    "Check": [
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
    ],
    "Birdhouse": [
        "................",
        ".......hb.......",
        "......hhbs......",
        ".....hhhbbs.....",
        "....hhhhbbss....",
        "..hhhhhhbbssss..",
        "...oooooooooo...",
        "....hbbbbbbs....",
        "....hbboobbs....",
        "....hbboobbs....",
        "....hbbaabbs....",
        "....ssssssss....",
        ".......hs.......",
        ".......hs.......",
        "......hbbs......",
        "................",
    ],
    "HerbSprig": [
        "................",
        "......hb........",
        ".....hhbs.......",
        ".....hbbs.......",
        ".hb...hbs...hs..",
        ".hhb..hbs..hbs..",
        "..hbb.hbs.hbs...",
        "...hbbabsbbs....",
        "....bsabss......",
        "......ad........",
        "......ad........",
        ".....aad........",
        ".....add........",
        "......d.........",
        "................",
        "................",
    ],
    "TreeCrown": [
        "................",
        "....hhhhhhb.....",
        "..hhhhhhhbbbs...",
        ".hhhhbbbbbbbbs..",
        ".hhbbbbhbbbbbs..",
        ".hbbbbhhhbbbbss.",
        ".hbbbbbbbbbbbss.",
        "..sbbbbbbbbbss..",
        "...ssbbbbbsss...",
        ".....ssaass.....",
        "......aadd......",
        "......aadd......",
        "......aadd......",
        ".....aaaddd.....",
        "....aaddddd.....",
        "................",
    ],
    "BadgeReady": [
        "................",
        "............g...",
        ".......hb.......",
        "......hhbs......",
        ".hb...hbbs...bs.",
        ".hhb..hbbs..bbs.",
        "..hbb.hbbs.bbs..",
        "...hbbhbbsbbs...",
        "....hbbbbbbs....",
        ".....hbbbbs.....",
        "......hbbs......",
        ".......ad.......",
        ".......ad.......",
        ".....aaaddd.....",
        "................",
        "................",
    ],
    "BadgeGrowing": [
        # can + pot use the second-material ramp (l/m/k) so GOLD/STONE retints keep them bright;
        # water drops use the accents (a/g)
        "................",
        "..llll..........",
        ".lmmmml.........",
        ".lmmmmml........",
        ".lmmmmmlll......",
        ".lmmmmmm.lll....",
        "..kkkkkk...lk...",
        "............a...",
        "..........g.....",
        "............a...",
        ".........hb.hb..",
        "..........hbb...",
        "...........b....",
        ".........lllll..",
        "..........kkk...",
        "................",
    ],
    "BadgeEmpty": [
        "................",
        "................",
        "................",
        ".....hhhhhh.....",
        "...hhbbbbbbss...",
        "..hbbddddddbbs..",
        ".hbddddddddddbs.",
        ".hbddaddddaddbs.",
        ".hbddddddddddbs.",
        "..hbbddddddbbs..",
        "...sbbbbbbbbs...",
        ".....ssssss.....",
        "................",
        "................",
        "................",
        "................",
    ],
    "BadgeWarning": [
        "................",
        ".lm.........a...",
        ".lm.............",
        ".lm.......hb....",
        ".lm......hbbs...",
        ".lm.....hbbbbs..",
        "........hbbsbs..",
        ".lm....hbbs.bs..",
        ".lm....hbs..bs..",
        ".......hbs...s..",
        "..a....bs.......",
        ".......bs.......",
        "......bs........",
        ".....bs.........",
        "....ss..........",
        "................",
    ],
    "WatermarkScaleSprite": [
        "................",
        ".......hb.......",
        "..hhhhhhbbbbbs..",
        "..h....hb....s..",
        "..h....hb....s..",
        "..h....hb....s..",
        ".hbs...hb...hbs.",
        ".sss...hb...sss.",
        ".......hb.......",
        ".......hb.......",
        ".......hb.......",
        ".......hb.......",
        ".....hhbbss.....",
        "....hbbbbbbs....",
        "................",
        "................",
    ],
    "BulletDiamondSprite": [
        "..o..",
        ".oho.",
        "ohbso",
        ".oso.",
        "..o..",
    ],
}


def final():
    out = {}
    for name, rows in SPRITES.items():
        if name == "BulletDiamondSprite":
            out[name] = rows
            continue
        assert len(rows) == 16 and all(len(r) == 16 for r in rows), name
        out[name] = outlined(rows)
    return out


def write_back(grids):
    for fname in ("Icons.kt", "Ornaments.kt"):
        path = os.path.join(ART, fname)
        src = open(path, encoding="utf-8").read()
        for name, rows in grids.items():
            m = re.search(rf"(val {name} = Sprite\(\n)((?:\s*\"[^\"]*\",?\n)+)", src)
            if not m:
                continue
            indent = re.match(r"\s*", m.group(2)).group(0)
            body = "".join(f'{indent}"{r}",\n' for r in rows)
            if not re.search(r"naturalTones", src[m.end():m.end() + 80]):
                body = body[:-2] + "\n"  # last row without trailing comma
            src = src[:m.start(2)] + body + src[m.end(2):]
        open(path, "w", encoding="utf-8", newline="\n").write(src)


if __name__ == "__main__":
    write_back(final())
    print("sprites written")
