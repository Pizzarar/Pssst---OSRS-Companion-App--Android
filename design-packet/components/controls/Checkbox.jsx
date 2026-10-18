import React from 'react';
/** Recessed-slot checkbox: 20x20 carved Slot well with a beveled gold check plaque when checked. Mirrors OsrsCheckbox(). */
export function Checkbox({ checked, defaultChecked = false, onChange, label, style }) {
  const [inner, setInner] = React.useState(defaultChecked);
  const on = checked !== undefined ? checked : inner;
  const toggle = () => { if (checked === undefined) setInner(!on); onChange && onChange(!on); };
  return React.createElement('div', { role: 'checkbox', 'aria-checked': on, tabIndex: 0, onClick: toggle,
    onKeyDown: (e) => { if (e.key === ' ' || e.key === 'Enter') { e.preventDefault(); toggle(); } },
    style: { display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer', minHeight: 36, ...style } },
    React.createElement('span', { style: {
      width: 20, height: 20, boxSizing: 'border-box', borderRadius: 3, flex: 'none',
      border: '1px solid ' + (on ? 'var(--accent)' : 'var(--outer-frame)'),
      background: 'var(--surface-empty)',
      boxShadow: 'var(--shadow-slot)',
      display: 'grid', placeItems: 'center' } },
      on && React.createElement('span', { style: {
        width: 14, height: 14, borderRadius: 2,
        background: 'linear-gradient(180deg,var(--osrs-gold),var(--osrs-gold-dark))',
        boxShadow: 'inset 1px 1px 0 var(--osrs-gold-hi), inset -1px -1px 0 var(--osrs-gold-lo)',
        display: 'grid', placeItems: 'center' } },
        React.createElement('svg', { width: 11, height: 11, viewBox: '0 0 24 24' },
          React.createElement('path', { d: 'M4 12.5l5 5L20 6.5', fill: 'none', stroke: '#000', strokeWidth: 3.5 })))),
    label && React.createElement('span', { style: { font: '500 13px/18px var(--font-sans)', color: on ? 'var(--accent)' : 'var(--text-muted)' } }, label));
}
