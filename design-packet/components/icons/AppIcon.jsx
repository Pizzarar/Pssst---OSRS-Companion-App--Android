import React from 'react';
const P = {
  menu: [['path', { d: 'M3.6 6H20.4M3.6 12H20.4M3.6 18H20.4', strokeWidth: 2.88 }]],
  shield: [['path', { d: 'M12 3.6L20.4 6L19.2 15.6L12 21.12L4.8 15.6L3.6 6Z' }]],
  chest: [['rect', { x: 4.32, y: 8.4, width: 15.36, height: 12 }], ['path', { d: 'M4.32 12.48H19.68' }], ['rect', { x: 10.56, y: 11.52, width: 2.88, height: 2.88, fill: 'currentColor', stroke: 'none' }]],
  coins: [['circle', { cx: 10.8, cy: 13.2, r: 6.72 }], ['circle', { cx: 13.92, cy: 9.6, r: 6.72 }]],
  star: [['path', { d: 'M12 2.88L14.88 9.12H21.6L16.08 13.2L18.24 19.68L12 15.84L5.76 19.68L7.92 13.2L2.4 9.12H9.12Z' }]],
  hourglass: [['rect', { x: 3.6, y: 2.88, width: 16.8, height: 1.92, fill: 'currentColor', stroke: 'none' }], ['rect', { x: 3.6, y: 19.2, width: 16.8, height: 1.92, fill: 'currentColor', stroke: 'none' }],
    ['path', { d: 'M5.28 4.8H18.72L12 12L18.72 19.2H5.28L12 12Z' }], ['path', { d: 'M7.2 18.72H16.8L12 14.88Z', fill: 'currentColor', stroke: 'none' }]],
};
/** The app's own line glyphs (transcribed from DashboardScreen.kt Canvas icons). Colour via currentColor. */
export function AppIcon({ name = 'shield', size = 24, color, style }) {
  const parts = P[name] || P.shield;
  return React.createElement('svg', { width: size, height: size, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.92,
    style: { color, display: 'block', flex: 'none', ...style }, 'aria-hidden': true },
    parts.map(([t, a], i) => React.createElement(t, { key: i, ...a })));
}
