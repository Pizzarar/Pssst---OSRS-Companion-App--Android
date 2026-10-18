import React from 'react';
/** Docs line icon (Lucide, via CDN mask). Muted by default. Substitute set — see readme ICONOGRAPHY. */
export function DocIcon({ name = 'monitor', size = 20, color = 'var(--text-muted)', style }) {
  const url = 'url(https://unpkg.com/lucide-static@0.460.0/icons/' + name + '.svg)';
  return React.createElement('span', { 'aria-hidden': true, style: { display: 'inline-block', width: size, height: size, flex: 'none', background: color,
    WebkitMaskImage: url, maskImage: url, WebkitMaskSize: 'contain', maskSize: 'contain', WebkitMaskRepeat: 'no-repeat', maskRepeat: 'no-repeat', ...style } });
}
