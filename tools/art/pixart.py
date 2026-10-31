"""Tiny deterministic pixel-art authoring kit for Pssst scenes.

Every canvas cell holds a palette *name* (see palette.py) or None (transparent).
Objects are built as pixel masks, shaded with a top-left light ramp, and stamped
with an optional 1px outline that overwrites whatever sits underneath, so
overlapping objects always stay separated by a clean dark seam.
"""
from __future__ import annotations

from typing import Callable, Iterable

Mask = set[tuple[int, int]]

N4 = ((-1, 0), (1, 0), (0, -1), (0, 1))
N8 = N4 + ((-1, -1), (1, -1), (-1, 1), (1, 1))


class Canvas:
    def __init__(self, w: int, h: int):
        self.w = w
        self.h = h
        self.px: list[list[str | None]] = [[None] * w for _ in range(h)]

    def inside(self, x: int, y: int) -> bool:
        return 0 <= x < self.w and 0 <= y < self.h

    def set(self, x: int, y: int, name: str | None) -> None:
        if self.inside(x, y):
            self.px[y][x] = name

    def get(self, x: int, y: int) -> str | None:
        return self.px[y][x] if self.inside(x, y) else None

    def fill(self, mask: Iterable[tuple[int, int]], name: str) -> None:
        for x, y in mask:
            self.set(x, y, name)

    def paint(self, mask: Mask, fn: Callable[[int, int], str | None]) -> None:
        for x, y in mask:
            n = fn(x, y)
            if n is not None:
                self.set(x, y, n)

    def outline(self, mask: Mask, name: str = "Outline", diag: bool = False) -> None:
        for x, y in ring(mask, diag):
            self.set(x, y, name)

    def stamp(self, art: list[str], ox: int, oy: int, legend: dict[str, str],
              outline: bool = True, diag: bool = False, flip: bool = False) -> Mask:
        """Stamps ASCII art (chars mapped through legend, '.' transparent)."""
        mask: Mask = set()
        cells = []
        for y, row in enumerate(art):
            if flip:
                row = row[::-1]
            for x, ch in enumerate(row):
                if ch in ". ":
                    continue
                if ch not in legend:
                    raise KeyError(f"char {ch!r} missing from legend")
                mask.add((ox + x, oy + y))
                cells.append((ox + x, oy + y, legend[ch]))
        if outline:
            self.outline(mask, diag=diag)
        for x, y, n in cells:
            self.set(x, y, n)
        return mask

    def blit(self, other: "Canvas", ox: int, oy: int) -> None:
        for y in range(other.h):
            for x in range(other.w):
                n = other.px[y][x]
                if n is not None:
                    self.set(ox + x, oy + y, n)

    def rows(self, legend_for: dict[str, str]) -> list[str]:
        return ["".join("." if n is None else legend_for[n] for n in row) for row in self.px]

    def names(self) -> list[str]:
        seen: dict[str, int] = {}
        for row in self.px:
            for n in row:
                if n is not None:
                    seen[n] = seen.get(n, 0) + 1
        return sorted(seen, key=lambda k: -seen[k])

    def counts(self) -> dict[str, int]:
        seen: dict[str, int] = {}
        for row in self.px:
            for n in row:
                if n is not None:
                    seen[n] = seen.get(n, 0) + 1
        return seen


# ---------------------------------------------------------------- masks

def ring(mask: Mask, diag: bool = False) -> Mask:
    out: Mask = set()
    for x, y in mask:
        for dx, dy in (N8 if diag else N4):
            p = (x + dx, y + dy)
            if p not in mask:
                out.add(p)
    return out


def rect(x: int, y: int, w: int, h: int) -> Mask:
    return {(x + i, y + j) for i in range(w) for j in range(h)}


def ellipse(cx: float, cy: float, rx: float, ry: float) -> Mask:
    out: Mask = set()
    for y in range(int(cy - ry) - 1, int(cy + ry) + 2):
        for x in range(int(cx - rx) - 1, int(cx + rx) + 2):
            dx = (x + 0.5 - cx) / rx
            dy = (y + 0.5 - cy) / ry
            if dx * dx + dy * dy <= 1.0:
                out.add((x, y))
    return out


def polygon(pts: list[tuple[float, float]]) -> Mask:
    xs = [p[0] for p in pts]
    ys = [p[1] for p in pts]
    out: Mask = set()
    for y in range(int(min(ys)) - 1, int(max(ys)) + 2):
        for x in range(int(min(xs)) - 1, int(max(xs)) + 2):
            if _point_in_poly(x + 0.5, y + 0.5, pts):
                out.add((x, y))
    return out


def _point_in_poly(px: float, py: float, pts: list[tuple[float, float]]) -> bool:
    inside = False
    n = len(pts)
    j = n - 1
    for i in range(n):
        xi, yi = pts[i]
        xj, yj = pts[j]
        if (yi > py) != (yj > py):
            xcross = (xj - xi) * (py - yi) / (yj - yi) + xi
            if px < xcross:
                inside = not inside
        j = i
    return inside


def line(x0: int, y0: int, x1: int, y1: int) -> list[tuple[int, int]]:
    pts = []
    dx = abs(x1 - x0)
    dy = -abs(y1 - y0)
    sx = 1 if x0 < x1 else -1
    sy = 1 if y0 < y1 else -1
    err = dx + dy
    while True:
        pts.append((x0, y0))
        if x0 == x1 and y0 == y1:
            break
        e2 = 2 * err
        if e2 >= dy:
            err += dy
            x0 += sx
        if e2 <= dx:
            err += dx
            y0 += sy
    return pts


def thick_line(x0: int, y0: int, x1: int, y1: int, width: int = 2) -> Mask:
    out: Mask = set()
    for x, y in line(x0, y0, x1, y1):
        for i in range(width):
            for j in range(width):
                out.add((x + i, y + j))
    return out


def shift(mask: Mask, dx: int, dy: int) -> Mask:
    return {(x + dx, y + dy) for x, y in mask}


def mirror_x(mask: Mask, axis2: int) -> Mask:
    """Mirror around x = axis2/2 (axis2 = left+right sum)."""
    return {(axis2 - x, y) for x, y in mask}


# ---------------------------------------------------------------- shading

def edge_shade(mask: Mask, light: str, mid: str, dark: str,
               light_dirs=((-1, 0), (0, -1)), dark_dirs=((1, 0), (0, 1)),
               depth: int = 1) -> Callable[[int, int], str]:
    """Top-left lit bevel: border pixels facing light get `light`, facing away get `dark`."""

    def tone(x: int, y: int) -> str:
        for d in range(1, depth + 1):
            for dx, dy in light_dirs:
                if (x + dx * d, y + dy * d) not in mask:
                    return light
        for d in range(1, depth + 1):
            for dx, dy in dark_dirs:
                if (x + dx * d, y + dy * d) not in mask:
                    return dark
        return mid

    return tone


def ramp_shade(mask: Mask, tones: list[str], cx: float, cy: float,
               lx: float = -0.7, ly: float = -0.7) -> Callable[[int, int], str]:
    """Spherical-ish ramp: tone picked by distance along the light vector from centre."""
    xs = [p[0] for p in mask]
    ys = [p[1] for p in mask]
    span = max(max(xs) - min(xs), max(ys) - min(ys), 1) / 2

    def tone(x: int, y: int) -> str:
        t = ((x + 0.5 - cx) * lx + (y + 0.5 - cy) * ly) / span  # +1 = toward light
        t = max(-1.0, min(1.0, t))
        idx = int(round((1 - t) / 2 * (len(tones) - 1)))
        return tones[max(0, min(len(tones) - 1, idx))]

    return tone


def solid(mask: Mask, canvas: Canvas, fn, outline: bool = True, outline_name: str = "Outline",
          diag: bool = False) -> Mask:
    if outline:
        canvas.outline(mask, outline_name, diag)
    canvas.paint(mask, fn)
    return mask


# ---------------------------------------------------------------- 3x5 font

FONT_3X5 = {
    "A": ["###", "#.#", "###", "#.#", "#.#"],
    "B": ["##.", "#.#", "##.", "#.#", "##."],
    "C": ["###", "#..", "#..", "#..", "###"],
    "E": ["###", "#..", "##.", "#..", "###"],
    "G": ["###", "#..", "#.#", "#.#", "###"],
    "K": ["#.#", "#.#", "##.", "#.#", "#.#"],
    "P": ["###", "#.#", "###", "#..", "#.."],
    "R": ["##.", "#.#", "##.", "#.#", "#.#"],
    "S": ["###", "#..", "###", "..#", "###"],
    "T": ["###", ".#.", ".#.", ".#.", ".#."],
    "Z": ["###", "..#", ".#.", "#..", "###"],
    "?": ["###", "..#", ".##", "...", ".#."],
    "0": ["###", "#.#", "#.#", "#.#", "###"],
    ":": [".", "#", ".", "#", "."],
}


def text_mask(s: str, x: int, y: int, gap: int = 1) -> Mask:
    out: Mask = set()
    cx = x
    for ch in s:
        g = FONT_3X5[ch]
        for j, row in enumerate(g):
            for i, c in enumerate(row):
                if c == "#":
                    out.add((cx + i, y + j))
        cx += len(g[0]) + gap
    return out
