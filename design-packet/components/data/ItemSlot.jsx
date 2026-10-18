import React from 'react';
import { Slot } from '../surfaces/Slot.jsx';
export function formatQuantity(q) {
  if (q >= 10000000) return Math.floor(q / 1000000) + 'M';
  if (q >= 100000) return Math.floor(q / 1000) + 'K';
  return String(q);
}
function qtyColor(q) { return q >= 10000000 ? 'var(--osrs-stack-green)' : q >= 100000 ? 'var(--osrs-stack-white)' : 'var(--osrs-stack-yellow)'; }
/** Bank / inventory slot: stack count top-left in game stack colours, icon centred, name at the bottom. */
export function ItemSlot({ name, quantity, iconSrc, empty = false, size, slotNumber, style }) {
  const showQty = !empty && quantity != null && quantity > 1;
  const sh = 'var(--text-shadow-hard)';
  return React.createElement(Slot, { hasItem: !empty, style: { aspectRatio: '1', width: size, overflow: 'hidden', ...style } },
    !empty && iconSrc && React.createElement('img', { src: iconSrc, alt: name, style: { position: 'absolute', left: '50%', top: '50%', width: 36, height: 36,
      transform: 'translate(-50%,-50%)', imageRendering: 'pixelated', objectFit: 'contain' } }),
    !empty && !iconSrc && React.createElement('div', { style: { position: 'absolute', inset: 3, display: 'grid', placeItems: 'center', textAlign: 'center',
      font: '600 11px/13px var(--font-sans)', color: 'var(--text-body)', textShadow: sh, overflow: 'hidden' } }, name),
    showQty && React.createElement('span', { style: { position: 'absolute', top: 2, left: 4, font: '700 11px/14px var(--font-mono)', color: qtyColor(quantity), textShadow: sh } }, formatQuantity(quantity)),
    !empty && iconSrc && name && React.createElement('span', { style: { position: 'absolute', left: 2, right: 2, bottom: 1, textAlign: 'center', whiteSpace: 'nowrap',
      overflow: 'hidden', textOverflow: 'ellipsis', font: '500 9px/12px var(--font-sans)', color: 'var(--text-muted)', textShadow: sh } }, name),
    empty && slotNumber != null && React.createElement('span', { style: { position: 'absolute', inset: 0, display: 'grid', placeItems: 'center',
      font: '400 11px/14px var(--font-sans)', color: 'var(--text-muted)', opacity: .4 } }, slotNumber));
}
