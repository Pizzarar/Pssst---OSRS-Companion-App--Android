import React from 'react';
/** Recessed slot: deep 2px top/left inset shadow + bottom/right rim highlight so it reads as carved into stone. Mirrors Modifier.osrsSlot(). */
export function Slot({ hasItem = true, padding = 3, radius = 3, style, children, ...rest }) {
  return React.createElement('div', { ...rest, style: {
    background: hasItem ? 'var(--surface-inset)' : 'var(--surface-empty)',
    border: '1px solid var(--outer-frame)',
    boxShadow: 'var(--shadow-slot)',
    borderRadius: radius,
    padding, boxSizing: 'border-box', position: 'relative', ...style } }, children);
}
