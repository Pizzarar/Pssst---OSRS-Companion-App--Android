import React from 'react';
const TONES = { accent: 'var(--accent)', danger: 'var(--status-danger)', sync: 'var(--osrs-stack-yellow)' };
/** Chiseled 3px plaque button: gold (filled) or carved stone (stone/text) with 3D raised/pressed bevels. Mirrors OsrsButton(). */
export function Button({ variant = 'filled', tone = 'accent', fullWidth = false, disabled = false, style, children, ...rest }) {
  const [press, setPress] = React.useState(false);
  const c = TONES[tone] || TONES.accent;
  const filled = variant === 'filled';
  const bg = filled
    ? (tone === 'danger'
        ? (press ? 'linear-gradient(180deg,#B32424,#FF4D4D)' : 'linear-gradient(180deg,#FF4D4D,#B32424)')
        : (press ? 'linear-gradient(180deg,var(--osrs-gold-dark),var(--osrs-gold))' : 'linear-gradient(180deg,var(--osrs-gold),var(--osrs-gold-dark))'))
    : (press ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))');
  const hi = filled ? (tone === 'danger' ? '#FF8A8A' : 'var(--osrs-gold-hi)') : 'var(--bevel-hi)';
  const lo = filled ? (tone === 'danger' ? '#660F0F' : 'var(--osrs-gold-lo)') : 'var(--bevel-lo)';
  const shadow = press
    ? `inset 2px 2px 0 ${lo}, inset -1px -1px 0 ${hi}`
    : `inset 1px 1px 0 ${hi}, inset -1px -1px 0 ${lo}`;
  return React.createElement('button', { ...rest, disabled,
    onPointerDown: () => setPress(true), onPointerUp: () => setPress(false), onPointerLeave: () => setPress(false),
    style: {
      height: 38, padding: filled ? '0 20px' : '0 14px', borderRadius: 3,
      border: '1px solid var(--outer-frame)',
      background: bg,
      boxShadow: shadow,
      color: filled ? (tone === 'danger' ? 'var(--text-body)' : 'var(--text-on-accent)') : c,
      textShadow: filled && tone !== 'danger' ? 'none' : 'var(--text-shadow-hard)',
      font: '700 13px/18px var(--font-serif)', letterSpacing: '.2px', cursor: disabled ? 'default' : 'pointer',
      width: fullWidth ? '100%' : undefined, opacity: disabled ? .42 : 1,
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8, ...style } }, children);
}
