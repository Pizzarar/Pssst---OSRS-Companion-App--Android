import React from 'react';
/** "Jump to Section" row in the side drawer. Selected = recessed Slot well + 1px gold border. */
export function DrawerItem({ selected = false, onClick, children, style }) {
  return React.createElement('button', { onClick, style: { display: 'block', width: '100%', textAlign: 'left', cursor: 'pointer',
    borderRadius: 3, padding: '9px 12px', marginBottom: 4,
    border: '1px solid ' + (selected ? 'var(--accent)' : 'transparent'),
    background: selected ? 'var(--surface-empty)' : 'transparent',
    boxShadow: selected ? 'var(--shadow-slot)' : 'none',
    color: selected ? 'var(--accent)' : 'var(--text-body)',
    textShadow: 'var(--text-shadow-hard)',
    font: (selected ? '700' : '500') + ' 13px/18px var(--font-sans)', ...style } }, children);
}
