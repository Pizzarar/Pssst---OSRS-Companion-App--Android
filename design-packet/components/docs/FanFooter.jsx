import React from 'react';
export const FAN_NOTICE = 'Pssst is a free fan project and is not affiliated with or endorsed by Jagex. Old School RuneScape is a trademark of Jagex Ltd.';
/** Required footer on every page. */
export function FanFooter({ style }) {
  return React.createElement('footer', { style: { borderTop: '1px solid var(--border-frame)', padding: '16px 0', font: '400 12px/16px var(--font-sans)',
    color: 'var(--text-muted)', textWrap: 'pretty', ...style } }, FAN_NOTICE);
}
