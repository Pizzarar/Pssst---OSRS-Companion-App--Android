import React from 'react';
/** Stone-tile sub-tab bar: raised stone tile when idle, sunken Slot well + gold border when active. Mirrors OsrsSegmentedTabs(). */
export function SegmentedTabs({ tabs = [], value, onChange, style }) {
  return React.createElement('div', { style: {
    display: 'flex', gap: 4, padding: 4, borderRadius: 4,
    background: 'var(--osrs-dark-stone)', border: '1px solid var(--outer-frame)',
    boxShadow: 'inset 1px 1px 0 #0E0B08', ...style } },
    tabs.map((t) => {
      const sel = t.id === value;
      const label = t.count > 0 ? t.label + ' (' + t.count + ')' : t.label;
      return React.createElement('button', { key: t.id, onClick: () => onChange && onChange(t.id), style: {
        flex: 1, padding: '7px 4px', borderRadius: 3, cursor: 'pointer',
        border: '1px solid ' + (sel ? 'var(--accent)' : 'var(--outer-frame)'),
        background: sel ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: sel ? 'var(--shadow-slot)' : 'var(--shadow-panel)',
        color: sel ? 'var(--accent)' : t.count > 0 ? 'var(--status-success)' : 'var(--text-muted)',
        textShadow: 'var(--text-shadow-hard)',
        font: (sel ? '700' : '600') + ' 11px/15px var(--font-sans)' } }, label);
    }));
}
