import React from 'react';
/** Inset code block for commands, URLs and paths. Optional copy button. */
export function CodeBlock({ children, label, copyable = true, style }) {
  const [ok, setOk] = React.useState(false);
  const text = typeof children === 'string' ? children : '';
  const copy = () => { try { navigator.clipboard.writeText(text); } catch (e) {} setOk(true); setTimeout(() => setOk(false), 1200); };
  return React.createElement('div', { style: { background: 'var(--surface-inset)', border: '1px solid', borderColor: 'var(--bevel-pressed)', borderRadius: 4, ...style } },
    (label || copyable) && React.createElement('div', { style: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 12px 0',
      font: '500 11px/16px var(--font-sans)', color: 'var(--text-muted)' } },
      React.createElement('span', null, label || ''),
      copyable && text && React.createElement('button', { onClick: copy, style: { border: 0, background: 'transparent', cursor: 'pointer', padding: 0,
        font: '700 11px/16px var(--font-sans)', color: ok ? 'var(--status-success)' : 'var(--accent)' } }, ok ? 'Copied' : 'Copy')),
    React.createElement('pre', { style: { margin: 0, padding: '6px 12px 10px', overflowX: 'auto', font: '400 13px/20px var(--font-mono)', color: 'var(--text-body)' } }, children));
}
