import React from 'react';
const T = {
  ready:   { bg: 'linear-gradient(180deg,#5CD660,#388E3C)', fg: '#000', border: 'var(--outer-frame)', shadow: 'inset 1px 1px 0 #85E688, inset -1px -1px 0 #1B5E20', w: 800, sh: 'none' },
  done:    { bg: 'var(--surface-empty)', fg: 'var(--text-muted)', border: 'var(--outer-frame)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  neutral: { bg: 'var(--surface-empty)', fg: 'var(--text-body)', border: 'var(--border-frame)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  gold:    { bg: 'var(--surface-empty)', fg: 'var(--accent)', border: 'var(--accent)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  active:  { bg: 'var(--surface-empty)', fg: 'var(--osrs-stack-yellow)', border: 'var(--border-frame)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  danger:  { bg: 'var(--surface-empty)', fg: 'var(--status-danger)', border: 'var(--status-danger)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
};
/** Chiseled 3px state badge: READY / GROWN / DONE / ACTIVE / BUYING / CANCELLED, and summary counts. Mirrors OsrsStatusBadge(). */
export function StatusChip({ tone = 'done', onClick, children, style }) {
  const t = T[tone] || T.done;
  return React.createElement(onClick ? 'button' : 'span', { onClick, style: {
    display: 'inline-flex', alignItems: 'center', height: 26, padding: '0 10px', boxSizing: 'border-box', borderRadius: 3,
    background: t.bg, color: t.fg, border: '1px solid ' + t.border, boxShadow: t.shadow, textShadow: t.sh,
    font: t.w + ' 11px/15px var(--font-sans)', whiteSpace: 'nowrap', cursor: onClick ? 'pointer' : 'default', flex: 'none', ...style } }, children);
}
