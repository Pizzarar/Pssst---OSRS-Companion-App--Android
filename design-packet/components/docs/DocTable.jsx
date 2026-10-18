import React from 'react';
/** Table with gold header text and alternating Slot Recessed / Panel Dark rows. */
export function DocTable({ columns = [], rows = [], style }) {
  const cell = { padding: '8px 12px', textAlign: 'left', verticalAlign: 'top', font: '400 14px/20px var(--font-sans)', color: 'var(--text-body)' };
  return React.createElement('div', { style: { overflowX: 'auto', borderRadius: 4, border: '1px solid var(--border-frame)', ...style } },
    React.createElement('table', { style: { borderCollapse: 'collapse', width: '100%' } },
      React.createElement('thead', null, React.createElement('tr', { style: { background: 'var(--surface-panel-bottom)', borderBottom: '1px solid var(--border-frame)' } },
        columns.map((c, i) => React.createElement('th', { key: i, style: { ...cell, font: '700 14px/18px var(--font-serif)', color: 'var(--text-heading)' } }, c)))),
      React.createElement('tbody', null, rows.map((r, ri) => React.createElement('tr', { key: ri, style: { background: ri % 2 ? 'var(--surface-panel-bottom)' : 'var(--surface-inset)' } },
        r.map((v, ci) => React.createElement('td', { key: ci, style: cell }, v)))))));
}
