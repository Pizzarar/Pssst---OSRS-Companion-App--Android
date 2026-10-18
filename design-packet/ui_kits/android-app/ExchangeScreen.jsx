function ExchangeScreen() {
  const { Panel, Slot, StatusChip, ProgressBar } = window.PssstDesignSystem_7dc1e9;
  const D = window.PSSST_DATA;
  const ic = D.icons || {};
  const active = D.ge.filter(g => !g.empty).length, done = D.ge.filter(g => g.state === 'SOLD' || g.state === 'BOUGHT').length;
  const tone = { BOUGHT: 'ready', SOLD: 'ready', BUYING: 'gold', SELLING: 'active', CANCELLED: 'danger' };
  return React.createElement(Scroll, { gap: 8 },
    React.createElement(Panel, { padding: 14, rivets: true, style: { ...rowBetween, marginBottom: 4 } },
      React.createElement('div', null,
        React.createElement('div', { style: PT.titleMd }, 'Grand Exchange Offers'),
        React.createElement('div', { style: PT.bodySm }, active, ' / 8 Slots Active \u2022 ', done, ' Complete')),
      React.createElement('span', { style: PT.yellowVal }, D.wealth.ge)),
    D.ge.map(g => g.empty ? React.createElement(Panel, { key: g.slot, padding: 12, style: rowBetween },
      React.createElement('span', { style: { ...PT.bodyMd, opacity: .6 } }, 'Slot ', g.slot, ' \u2014 Empty'),
      React.createElement('span', { style: { ...PT.labelSm, opacity: .4 } }, 'Empty')) :
      React.createElement(Panel, { key: g.slot, padding: 12 },
        React.createElement('div', { style: { display: 'flex', alignItems: 'center', gap: 12 } },
          React.createElement(Slot, { padding: 4, style: { width: 44, height: 44, display: 'grid', placeItems: 'center', flex: 'none' } },
            ic[g.name] ? React.createElement('img', { src: ic[g.name], alt: g.name, style: { width: 32, height: 32, imageRendering: 'pixelated', objectFit: 'contain' } }) :
            React.createElement('span', { style: { font: '500 9px/11px var(--font-sans)', color: 'var(--text-body)', textAlign: 'center', textShadow: 'var(--text-shadow-hard)' } }, g.name.split(' ')[0])),
          React.createElement('div', { style: { flex: 1, minWidth: 0 } },
            React.createElement('div', { style: { ...PT.bodyLg, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' } }, g.name),
            React.createElement('div', { style: PT.bodySm }, 'Slot ', g.slot, ' \u2022 ', g.sold.toLocaleString(), ' / ', g.total.toLocaleString(), ' @ ', g.price)),
          React.createElement(StatusChip, { tone: tone[g.state] }, g.state)),
        React.createElement(ProgressBar, { value: g.progress, tone: g.progress >= 1 ? 'success' : 'accent', style: { marginTop: 8 } }),
        React.createElement('div', { style: { ...rowBetween, marginTop: 4 } },
          React.createElement('span', { style: PT.bodySm }, 'Spent/Received: ', g.spent),
          React.createElement('span', { style: { font: '600 12px/16px var(--font-sans)', color: 'var(--osrs-stack-yellow)' } }, 'Total: ', g.value)))));
}
window.ExchangeScreen = ExchangeScreen;
