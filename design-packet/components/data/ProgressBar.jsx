import React from 'react';
const C = { success: 'var(--status-success)', accent: 'var(--accent)', danger: 'var(--status-danger)' };
/** Framed 3-band progress bar: recessed dark-stone trough + 1px top specular highlight, midtone fill, 1px bottom shadow. Mirrors OsrsProgressBar(). */
export function ProgressBar({ value = 0, tone = 'success', thin = false, style }) {
  const p = Math.max(0, Math.min(1, value));
  return React.createElement('div', { role: 'progressbar', 'aria-valuenow': Math.round(p * 100), style: {
    height: thin ? 6 : 8, background: 'var(--surface-empty)', borderRadius: 2,
    border: '1px solid var(--outer-frame)', boxShadow: 'inset 1px 1px 0 #0E0B08',
    overflow: 'hidden', boxSizing: 'border-box', ...style } },
    p > 0 && React.createElement('div', { style: {
      width: p * 100 + '%', height: '100%', background: C[tone] || C.success,
      boxShadow: 'inset 0 1px 0 rgba(255,255,255,.38), inset 0 -1px 0 rgba(0,0,0,.45)',
      transition: 'width var(--motion-base) var(--ease-standard)' } }));
}
