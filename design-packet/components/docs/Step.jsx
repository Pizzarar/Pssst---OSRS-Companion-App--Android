import React from 'react';
import { StepNumber } from './StepNumber.jsx';
/** One instruction: step number + one action, optional detail below. */
export function Step({ n, children, detail, style }) {
  return React.createElement('div', { style: { display: 'flex', gap: 12, alignItems: 'flex-start', ...style } },
    React.createElement(StepNumber, { n, style: { marginTop: -3 } }),
    React.createElement('div', { style: { flex: 1, minWidth: 0 } },
      React.createElement('div', { style: { font: '400 15px/22px var(--font-sans)', color: 'var(--text-body)', textWrap: 'pretty' } }, children),
      detail && React.createElement('div', { style: { marginTop: 8 } }, detail)));
}
