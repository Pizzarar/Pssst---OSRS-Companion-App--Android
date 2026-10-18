import React from 'react';
import { DocIcon } from './DocIcon.jsx';
const K = { tip: { c: 'var(--accent)', i: 'lightbulb' }, warning: { c: 'var(--status-danger)', i: 'triangle-alert' }, note: { c: 'var(--status-note)', i: 'info' } };
/** Inset callout with a coloured left rule and icon. Tip = gold, Warning = red, Note = muted. */
export function Callout({ kind = 'note', title, children, style }) {
  const k = K[kind] || K.note;
  return React.createElement('div', { role: kind === 'warning' ? 'alert' : 'note', style: { display: 'flex', gap: 10, padding: '10px 12px',
    background: 'var(--surface-inset)', borderRadius: 4, border: '1px solid', borderColor: 'var(--bevel-pressed)', borderLeft: '3px solid ' + k.c, ...style } },
    React.createElement(DocIcon, { name: k.i, size: 18, color: k.c, style: { marginTop: 2 } }),
    React.createElement('div', { style: { flex: 1, minWidth: 0, font: '400 14px/21px var(--font-sans)', color: 'var(--text-body)', textWrap: 'pretty' } },
      title && React.createElement('div', { style: { font: '700 14px/18px var(--font-serif)', color: k.c, marginBottom: 2 } }, title), children));
}
