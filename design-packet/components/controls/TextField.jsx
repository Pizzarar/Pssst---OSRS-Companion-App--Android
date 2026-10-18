import React from 'react';
/** Recessed-slot text field: fixed top label + carved dark-stone Slot input box. Mirrors OsrsTextField(). */
export function TextField({ label, value, defaultValue, placeholder, supportingText, error = false, mono = false, onChange, style, inputStyle, ...rest }) {
  const [focus, setFocus] = React.useState(false);
  const [inner, setInner] = React.useState(defaultValue || '');
  const v = value !== undefined ? value : inner;
  const borderC = error ? 'var(--status-danger)' : focus ? 'var(--accent)' : 'var(--outer-frame)';
  return React.createElement('label', { style: { display: 'block', ...style } },
    label && React.createElement('div', { style: {
      marginBottom: 4, font: '700 12px/16px var(--font-serif)',
      color: error ? 'var(--status-danger)' : focus ? 'var(--accent)' : 'var(--text-heading)',
      textShadow: 'var(--text-shadow-hard)' } }, label),
    React.createElement('div', { style: {
      position: 'relative', height: 42, borderRadius: 3,
      background: 'var(--surface-empty)',
      border: '1px solid ' + borderC,
      boxShadow: 'var(--shadow-slot)' } },
      React.createElement('input', { ...rest, value: v, placeholder: placeholder || '',
        onFocus: () => setFocus(true), onBlur: () => setFocus(false),
        onChange: (e) => { if (value === undefined) setInner(e.target.value); onChange && onChange(e); },
        style: { position: 'absolute', inset: 0, width: '100%', boxSizing: 'border-box', padding: '0 12px', border: 0, outline: 0,
          background: 'transparent', color: 'var(--text-body)', caretColor: 'var(--accent)',
          font: '400 14px/20px ' + (mono ? 'var(--font-mono)' : 'var(--font-sans)'), ...inputStyle } })),
    supportingText && React.createElement('div', { style: { padding: '4px 4px 0', font: '500 11px/15px var(--font-sans)',
      color: 'var(--status-danger)' } }, supportingText));
}
