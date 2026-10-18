import React from 'react';
import { AppIcon } from '../icons/AppIcon.jsx';
export const DEFAULT_TABS = [
  { id: 'overview', label: 'Overview', icon: 'shield' }, { id: 'storage', label: 'Storage', icon: 'chest' },
  { id: 'exchange', label: 'Exchange', icon: 'coins' }, { id: 'progress', label: 'Progress', icon: 'star' },
  { id: 'timers', label: 'Timers', icon: 'hourglass' }];
/** Five-tab stone-tile bottom navigation. Idle: raised stone tile; Selected: sunken Slot well + gold border. */
export function BottomNav({ items = DEFAULT_TABS, value, onChange, style }) {
  return React.createElement('nav', { style: {
    background: 'var(--osrs-dark-stone)', borderTop: '2px solid var(--outer-frame)',
    boxShadow: 'inset 0 1px 0 var(--bevel-hi)',
    height: 68, padding: '6px 8px', gap: 6, boxSizing: 'border-box', display: 'flex', ...style } },
    items.map((it) => {
      const sel = it.id === value; const c = sel ? 'var(--accent)' : 'var(--text-muted)';
      return React.createElement('button', { key: it.id, onClick: () => onChange && onChange(it.id), style: {
        flex: 1, borderRadius: 3, cursor: 'pointer',
        border: '1px solid ' + (sel ? 'var(--accent)' : 'var(--outer-frame)'),
        background: sel ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: sel ? 'var(--shadow-slot)' : 'var(--shadow-panel)',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 3, padding: '4px 0' } },
        React.createElement('span', { style: { width: 22, height: 22, display: 'grid', placeItems: 'center', color: c } },
          React.createElement(AppIcon, { name: it.icon, size: 20 })),
        React.createElement('span', { style: { font: (sel ? '700' : '600') + ' 10px/13px var(--font-sans)', color: c, textShadow: 'var(--text-shadow-hard)' } }, it.label));
    }));
}
