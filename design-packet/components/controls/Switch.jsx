import React from 'react';
/** Chiseled stone toggle switch: recessed rectangular stone track with beveled rectangular slider thumb. Mirrors OsrsSwitch(). */
export function Switch({ checked, defaultChecked = false, onChange, style, ...rest }) {
  const [inner, setInner] = React.useState(defaultChecked);
  const on = checked !== undefined ? checked : inner;
  const toggle = () => { if (checked === undefined) setInner(!on); onChange && onChange(!on); };
  return React.createElement('button', { ...rest, role: 'switch', 'aria-checked': on, onClick: toggle,
    style: { width: 48, height: 26, borderRadius: 3, padding: 0, position: 'relative', cursor: 'pointer', boxSizing: 'border-box',
      background: 'var(--surface-empty)',
      border: '1px solid ' + (on ? 'var(--status-success)' : 'var(--outer-frame)'),
      boxShadow: 'var(--shadow-slot)',
      flex: 'none', ...style } },
    React.createElement('span', { style: { position: 'absolute', top: 3, left: on ? 23 : 3, width: 20, height: 18,
      borderRadius: 2,
      background: on ? 'linear-gradient(180deg,#5CD660,#2E7D32)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
      border: '1px solid var(--outer-frame)',
      boxShadow: on ? 'inset 1px 1px 0 #85E688, inset -1px -1px 0 #1B5E20' : 'var(--shadow-panel)',
      transition: 'left var(--motion-base) var(--ease-standard)' } }));
}
