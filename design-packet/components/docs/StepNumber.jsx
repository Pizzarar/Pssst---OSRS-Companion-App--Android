import React from 'react';
/** Gold numeral in a small dark circle with a 1px gold ring. One per step. */
export function StepNumber({ n, size = 28, style }) {
  return React.createElement('span', { style: { width: size, height: size, borderRadius: '50%', flex: 'none', boxSizing: 'border-box',
    display: 'inline-grid', placeItems: 'center', background: 'var(--surface-empty)', border: '1px solid var(--accent)',
    color: 'var(--text-heading)', font: '700 ' + Math.round(size * 0.54) + 'px/1 var(--font-serif)', ...style } }, n);
}
