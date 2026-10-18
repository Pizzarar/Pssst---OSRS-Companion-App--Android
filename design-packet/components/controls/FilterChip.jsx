import React from 'react';
/** Chiseled 3px filter chip: raised stone plaque when idle, recessed Slot well with 1px gold border when selected. Mirrors OsrsFilterChip(). */
export function FilterChip({ selected = false, shape = 'chip', onClick, children, style }) {
  const sq = shape === 'square';
  return React.createElement('button', { onClick, 'aria-pressed': selected, style: {
    height: sq ? 26 : 28, padding: sq ? '0 10px' : '0 12px', borderRadius: 3, cursor: 'pointer',
    border: '1px solid ' + (selected ? 'var(--accent)' : 'var(--outer-frame)'),
    background: selected ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
    boxShadow: selected ? 'var(--shadow-slot)' : 'var(--shadow-panel)',
    color: selected ? 'var(--accent)' : 'var(--text-muted)',
    textShadow: 'var(--text-shadow-hard)',
    font: (selected ? '700' : '600') + ' 11px/15px var(--font-sans)', ...style } }, children);
}
