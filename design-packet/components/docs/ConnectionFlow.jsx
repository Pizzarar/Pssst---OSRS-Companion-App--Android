import React from 'react';
import { Panel } from '../surfaces/Panel.jsx';
import { DocIcon } from './DocIcon.jsx';
/** Connection-path diagram: stone panels joined by a gold dashed line, a lock glyph on each link. */
export function ConnectionFlow({ nodes = [], style }) {
  const out = [];
  nodes.forEach((n, i) => {
    if (i > 0) out.push(React.createElement('div', { key: 'l' + i, style: { flex: 1, minWidth: 36, position: 'relative', height: 2, alignSelf: 'center',
      backgroundImage: 'linear-gradient(90deg,var(--accent) 60%,transparent 0)', backgroundSize: '8px 2px' } },
      React.createElement('span', { style: { position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%,-50%)', padding: 3, borderRadius: '50%',
        background: 'var(--surface-page)', display: 'grid' } }, React.createElement(DocIcon, { name: 'lock', size: 14, color: 'var(--accent)' }))));
    out.push(React.createElement(Panel, { key: 'n' + i, padding: '10px 12px', style: { display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6, minWidth: 84, flex: 'none' } },
      React.createElement(DocIcon, { name: n.icon || 'monitor', size: 22 }),
      React.createElement('span', { style: { font: '700 13px/16px var(--font-serif)', color: 'var(--text-heading)', textAlign: 'center' } }, n.label),
      n.sub && React.createElement('span', { style: { font: '400 11px/14px var(--font-mono)', color: 'var(--text-muted)', textAlign: 'center' } }, n.sub)));
  });
  return React.createElement('div', { style: { display: 'flex', alignItems: 'stretch', ...style } }, out);
}
