"""Previews and lints the 16x16 sprites in Icons.kt / Ornaments.kt (authoring aid, not shipped).

Mirrors SpriteGridTest: valid roles, 1px enclosure, no fill on the canvas edge,
>= 20 transparent and >= 12 outline pixels per 16x16 sprite.
Renders natural / GOLD / STONE rows to build/art-preview/sprites.png.
"""
import os
import re
import sys

from PIL import Image, ImageDraw

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
sys.path.insert(0, HERE)
from palette import PALETTE  # noqa: E402

ART = os.path.join(ROOT, "app", "src", "main", "java", "com", "pssst", "theme", "art")


def theme_colours():
    src = open(os.path.join(ROOT, "app", "src", "main", "java", "com", "pssst", "theme", "Color.kt"),
               encoding="utf-8").read()
    return {m.group(1): int(m.group(2), 16) & 0xFFFFFF
            for m in re.finditer(r"val (Osrs\w+) = Color\(0x([0-9A-Fa-f]{8})\)", src)}


T = theme_colours()
P = PALETTE


def tones(o, h, b, s, a, d, l=None, m=None, k=None, g=None):
    return {"o": o, "h": h, "b": b, "s": s, "a": a, "d": d,
            "l": l if l is not None else h, "m": m if m is not None else b,
            "k": k if k is not None else s, "g": g if g is not None else h}


O = T["OsrsOuterFrame"]
TONES = {
    "GOLD": tones(O, T["OsrsGoldHighlight"], T["OsrsGold"], T["OsrsGoldDark"], T["OsrsIronEdgeLight"], T["OsrsIronBody"]),
    "STONE": tones(O, T["OsrsRivetLight"], T["OsrsBorderLight"], T["OsrsBorderOutline"], T["OsrsIronEdgeLight"], T["OsrsIronBody"]),
    "HERB": tones(O, T["OsrsHerbLight"], T["OsrsHerbGreen"], T["OsrsHerbDark"], T["OsrsWoodLight"], T["OsrsWoodBase"],
                  g=T["OsrsGoldHighlight"]),
    "WOOD": tones(O, T["OsrsWoodLight"], T["OsrsWoodBase"], T["OsrsWoodDark"], T["OsrsIronEdgeLight"], T["OsrsIronBody"],
                  g=T["OsrsGoldHighlight"]),
    "PARCHMENT": tones(O, T["OsrsTextPrimary"], T["OsrsTextMuted"], T["OsrsBorderLight"], T["OsrsGold"], T["OsrsGoldDark"]),
    "WARNING": tones(O, T["OsrsRed"], T["OsrsRed"], T["OsrsRedDark"], T["OsrsTextPrimary"], T["OsrsGold"]),
    "LEATHER": tones(O, T["OsrsLeatherHighlight"], T["OsrsLeatherBase"], T["OsrsLeatherCrease"], T["OsrsGold"], T["OsrsGoldDark"]),
    "QUEST_BLUE": tones(O, P["QuestBlueLight"], P["QuestBlueMid"], P["QuestBlueDark"], T["OsrsGoldHighlight"], T["OsrsGold"],
                        g=T["OsrsTextPrimary"]),
    "TERRACOTTA_SPROUT": tones(O, T["OsrsHerbLight"], T["OsrsHerbGreen"], T["OsrsHerbDark"], P["CyanMoteLight"],
                               P["CyanMoteDark"], T["OsrsGoldHighlight"], T["OsrsGold"], T["OsrsBrassMid"],
                               P["CyanMoteMid"]),
    "BLIGHT_WARNING": tones(O, P["BlightLight"], P["BlightMid"], P["BlightDark"], T["OsrsTextPrimary"], T["OsrsRed"],
                            T["OsrsRed"], T["OsrsRedDark"], P["Outline"]),
    "WATERMARK": tones(T["OsrsBorderDark"], T["OsrsBorderLight"], T["OsrsBorderOutline"], T["OsrsSlotRecessed"],
                       T["OsrsBorderLight"], T["OsrsBorderDark"]),
}

SPRITE_RE = re.compile(r"val (\w+) = Sprite\(\s*((?:\"[^\"]*\",?\s*)+)(?:naturalTones = SpriteTones\.(\w+))?", re.S)


def parse(path):
    out = []
    for m in SPRITE_RE.finditer(open(path, encoding="utf-8").read()):
        rows = re.findall(r"\"([^\"]*)\"", m.group(2))
        out.append((m.group(1), rows, m.group(3)))
    return out


def lint(name, rows, square16=True):
    errs = []
    h = len(rows)
    w = len(rows[0])
    if any(len(r) != w for r in rows):
        errs.append("ragged rows")
        return errs
    for y in range(h):
        for x in range(w):
            ch = rows[y][x]
            if ch not in ".ohbsadlmkg":
                errs.append(f"bad role {ch!r} at {x},{y}")
            if ch in ".o":
                continue
            for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
                nx, ny = x + dx, y + dy
                if not (0 <= nx < w and 0 <= ny < h):
                    errs.append(f"fill {ch} on edge at {x},{y}")
                elif rows[ny][nx] == ".":
                    errs.append(f"fill {ch} at {x},{y} touches '.' at {nx},{ny}")
    if square16:
        flat = "".join(rows)
        if (w, h) != (16, 16):
            errs.append(f"size {w}x{h}")
        if flat.count(".") < 20:
            errs.append(f"only {flat.count('.')} transparent")
        if flat.count("o") < 12:
            errs.append(f"only {flat.count('o')} outline")
    return errs


def render(rows, tone, scale):
    w, h = len(rows[0]), len(rows)
    img = Image.new("RGB", (w * scale, h * scale), (0x22, 0x1D, 0x16))
    d = ImageDraw.Draw(img)
    for y, r in enumerate(rows):
        for x, ch in enumerate(r):
            if ch == ".":
                continue
            v = tone[ch]
            d.rectangle([x * scale, y * scale, x * scale + scale - 1, y * scale + scale - 1],
                        fill=((v >> 16) & 255, (v >> 8) & 255, v & 255))
    return img


def main():
    icons = parse(os.path.join(ART, "Icons.kt"))
    orn = parse(os.path.join(ART, "Ornaments.kt"))
    bad = 0
    for name, rows, nat in icons:
        for e in lint(name, rows):
            print(f"{name}: {e}")
            bad += 1
    for name, rows, nat in orn:
        for e in lint(name, rows, square16=(name == "WatermarkScaleSprite")):
            print(f"{name}: {e}")
            bad += 1
    scale, pad = 6, 6
    cols = len(icons) + 1
    sheet = Image.new("RGB", (cols * (16 * scale + pad) + pad, 3 * (16 * scale + pad) + pad), (12, 9, 6))
    for row, mode in enumerate(("natural", "GOLD", "STONE")):
        for i, (name, rows, nat) in enumerate(icons):
            tone = TONES[(nat or "GOLD") if mode == "natural" else mode]
            sheet.paste(render(rows, tone, scale), (pad + i * (16 * scale + pad), pad + row * (16 * scale + pad)))
    wm = [r for n, r, _ in orn if n == "WatermarkScaleSprite"][0]
    sheet.paste(render(wm, TONES["WATERMARK"], scale), (pad + len(icons) * (16 * scale + pad), pad))
    os.makedirs(os.path.join(ROOT, "build", "art-preview"), exist_ok=True)
    sheet.save(os.path.join(ROOT, "build", "art-preview", "sprites.png"))
    print(f"{len(icons)} icons, {bad} lint problems")
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main())
