import React from 'react';
import { AppIcon } from '../icons/AppIcon.jsx';
/** App top bar: Chiseled stone panel header, hamburger, "Pssst • Tab" serif gold title, relative sync time, chiseled Sync plaque button. */
export function TopBar({ title = 'Overview', syncedAgo, onMenu, onSync, actionLabel = 'Sync', style }) {
  return React.createElement('div', { style: {
    backgroundImage: 'var(--stone-grain), linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
    borderBottom: '2px solid var(--outer-frame)',
    boxShadow: 'var(--shadow-panel)', ...style } },
    React.createElement('div', { style: { height: 58, display: 'flex', alignItems: 'center', padding: '0 8px', gap: 6 } },
      React.createElement('button', { onClick: onMenu, 'aria-label': 'Menu', style: {
        width: 38, height: 38, borderRadius: 3, border: '1px solid var(--outer-frame)',
        background: 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: 'var(--shadow-panel)', color: 'var(--accent)', display: 'grid', placeItems: 'center', cursor: 'pointer' } },
        React.createElement(AppIcon, { name: 'menu' })),
      React.createElement('div', { style: { flex: 1, minWidth: 0, display: 'flex', alignItems: 'center', gap: 8, paddingLeft: 4 } },
        React.createElement('span', { style: { font: '700 17px/22px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)', whiteSpace: 'nowrap' } }, 'Pssst \u2022 ' + title),
        syncedAgo && React.createElement('span', { style: { font: '500 11px/16px var(--font-sans)', color: 'var(--text-muted)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' } }, '(' + syncedAgo + ')')),
      React.createElement('button', { onClick: onSync, style: {
        height: 34, padding: '0 12px', borderRadius: 3,
        border: '1px solid var(--outer-frame)',
        background: 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: 'var(--shadow-panel)', cursor: 'pointer',
        font: '700 13px/16px var(--font-serif)', color: 'var(--osrs-stack-yellow)', textShadow: 'var(--text-shadow-hard)' } }, actionLabel)));
}
