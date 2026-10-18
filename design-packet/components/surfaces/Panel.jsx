import React from 'react';
/** Carved-stone panel: Panel Light→Panel Dark gradient + stone grain, 1px outer frame + 3D inner bevel, optional corner rivets. Mirrors Modifier.osrsPanel(). */
export function Panel({ padding = 14, radius = 4, rivets = false, as = 'div', style, children, ...rest }) {
  const Tag = as;
  const stud = (pos) => React.createElement('span', { key: pos, style: {
    position: 'absolute', width: 3, height: 3, pointerEvents: 'none',
    top: pos[0] === 't' ? 4 : undefined, bottom: pos[0] === 'b' ? 4 : undefined,
    left: pos[1] === 'l' ? 4 : undefined, right: pos[1] === 'r' ? 4 : undefined,
    background: 'var(--osrs-rivet-hi)', boxShadow: '1px 1px 0 var(--osrs-rivet-lo)'
  } });
  return React.createElement(Tag, { ...rest, style: {
    position: 'relative',
    backgroundImage: 'var(--stone-grain), linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
    border: '1px solid var(--outer-frame)',
    boxShadow: 'var(--shadow-panel)',
    borderRadius: radius,
    padding, boxSizing: 'border-box', color: 'var(--text-body)', ...style } },
    rivets && ['tl', 'tr', 'bl', 'br'].map(stud),
    children);
}
