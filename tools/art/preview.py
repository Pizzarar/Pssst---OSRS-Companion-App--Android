"""Renders authoring previews (not shipped) into build/art-preview/."""
from __future__ import annotations

import os
import sys

from PIL import Image, ImageDraw

sys.path.insert(0, os.path.dirname(__file__))
from palette import PALETTE  # noqa: E402

PANEL = (0x2B, 0x25, 0x1D)
GROUND = (0x22, 0x1D, 0x16)


def rgb(name: str) -> tuple[int, int, int]:
    v = PALETTE[name]
    return ((v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF)


def render(canvas, scale: int, bg=PANEL) -> Image.Image:
    img = Image.new("RGB", (canvas.w * scale, canvas.h * scale), bg)
    d = ImageDraw.Draw(img)
    for y in range(canvas.h):
        for x in range(canvas.w):
            n = canvas.px[y][x]
            if n is not None:
                d.rectangle([x * scale, y * scale, x * scale + scale - 1, y * scale + scale - 1], fill=rgb(n))
    return img


def sheet(items, scale: int, cols: int, path: str, bg=PANEL, circle: bool = False) -> None:
    pad = 8
    cw = max(c.w for _, c in items) * scale
    ch = max(c.h for _, c in items) * scale
    rows = (len(items) + cols - 1) // cols
    out = Image.new("RGB", (cols * (cw + pad) + pad, rows * (ch + pad) + pad), (12, 9, 6))
    for i, (_, c) in enumerate(items):
        im = render(c, scale, bg)
        if circle:
            dd = ImageDraw.Draw(im)
            r = 11 * scale
            cx = 18 * scale
            dd.ellipse([cx - r, cx - r, cx + r, cx + r], outline=(255, 0, 255))
            v = 6 * scale
            dd.rectangle([v, v, 30 * scale - 1, 30 * scale - 1], outline=(0, 255, 255))
        x = pad + (i % cols) * (cw + pad)
        y = pad + (i // cols) * (ch + pad)
        out.paste(im, (x, y))
    os.makedirs(os.path.dirname(path), exist_ok=True)
    out.save(path)
