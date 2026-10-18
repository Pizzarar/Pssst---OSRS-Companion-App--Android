import React from 'react';
/** Exact setting/button name as a mono label in an inset slot. Use every time a setting is named. */
export function SettingChip({ children, style }) {
  return React.createElement('span', { style: { display: 'inline-block', padding: '0 6px', margin: '0 1px', borderRadius: 4, verticalAlign: 'baseline',
    background: 'var(--surface-inset)', border: '1px solid', borderColor: 'var(--bevel-pressed)', color: 'var(--text-body)',
    font: '500 0.87em/1.5 var(--font-mono)', whiteSpace: 'nowrap', ...style } }, children);
}
