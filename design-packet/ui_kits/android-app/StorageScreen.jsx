function StorageScreen() {
  const { SegmentedTabs, TextField, FilterChip, ItemSlot, Panel, Slot } = window.PssstDesignSystem_7dc1e9;
  const D = window.PSSST_DATA;
  const ic = D.icons || {};
  const [tab, setTab] = React.useState('bank'); const [q, setQ] = React.useState(''); const [sort, setSort] = React.useState('Slot Order');
  let items = D.bank.filter(([n]) => n.toLowerCase().includes(q.toLowerCase()));
  if (sort === 'Quantity') items = [...items].sort((a, b) => b[1] - a[1]);
  const inv = D.inventory;
  return React.createElement('div', { style: { flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column' } },
    React.createElement('div', { style: { padding: '8px 16px 0' } },
      React.createElement(SegmentedTabs, { value: tab, onChange: setTab, tabs: [{ id: 'bank', label: 'Bank' }, { id: 'inv', label: 'Inventory' }, { id: 'pot', label: 'Potion Storage' }] })),
    tab === 'bank' && React.createElement('div', { style: { flex: 1, minHeight: 0, padding: 16, display: 'flex', flexDirection: 'column' } },
      React.createElement(TextField, { label: 'Search Bank by Name or ID', placeholder: 'e.g. Abyssal whip or 4151', value: q, onChange: (e) => setQ(e.target.value) }),
      React.createElement('div', { style: { display: 'flex', gap: 8, marginTop: 8 } },
        ['Slot Order', 'Value (High)', 'Quantity'].map(m => React.createElement(FilterChip, { key: m, shape: 'square', selected: sort === m, onClick: () => setSort(m) }, m))),
      React.createElement('div', { style: { ...rowBetween, margin: '8px 0' } },
        React.createElement('span', { style: PT.bodyMd }, 'Items: ', D.bank.length, q ? ' (' + items.length + ')' : ''),
        React.createElement('span', { style: { font: '700 13px/18px var(--font-sans)', color: 'var(--accent)' } }, 'Value: ', D.wealth.bank)),
      items.length === 0 ? React.createElement(Empty, null, "No items match '", q, "'") :
      React.createElement('div', { style: { flex: 1, minHeight: 0, overflowY: 'auto', display: 'grid', gridTemplateColumns: 'repeat(auto-fill,minmax(72px,1fr))', gap: 6, alignContent: 'start', paddingBottom: 16 } },
        items.map(([n, qn], i) => React.createElement(ItemSlot, { key: n + i, name: n, quantity: qn, iconSrc: ic[n] })))),
    tab === 'inv' && React.createElement('div', { style: { padding: 16 } },
      React.createElement(Panel, { padding: 12, rivets: true, style: rowBetween },
        React.createElement('span', { style: PT.titleMd }, 'Inventory (4 \u00D7 7)'),
        React.createElement('span', { style: PT.bodyMd }, inv.filter(Boolean).length, ' / 28 Slots')),
      React.createElement('div', { style: { display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 8, padding: 4, marginTop: 12 } },
        inv.map((it, i) => it ? React.createElement(ItemSlot, { key: i, name: it[0], quantity: it[1], iconSrc: ic[it[0]] }) : React.createElement(ItemSlot, { key: i, empty: true, slotNumber: i + 1 })))),
    tab === 'pot' && React.createElement(Scroll, { gap: 8 },
      React.createElement(Panel, { padding: 12, rivets: true, style: rowBetween },
        React.createElement('span', { style: PT.bodyMd }, D.potions.length, ' Types \u2022 ', D.potions.reduce((a, p) => a + p[1], 0), ' Total Doses'),
        React.createElement('span', { style: { font: '700 13px/18px var(--font-sans)', color: 'var(--accent)' } }, 'Value: ', D.wealth.potions)),
      D.potions.map(([n, d, p]) => React.createElement(Panel, { key: n, padding: 10, style: { display: 'flex', alignItems: 'center', gap: 12 } },
        React.createElement(Slot, { style: { width: 44, height: 44, display: 'grid', placeItems: 'center', flex: 'none' } },
          ic[n] ? React.createElement('img', { src: ic[n], alt: n, style: { width: 32, height: 32, imageRendering: 'pixelated', objectFit: 'contain' } }) :
          React.createElement('span', { style: { font: '500 9px/11px var(--font-sans)', color: 'var(--text-body)', textAlign: 'center' } }, n.split(' ')[0])),
        React.createElement('div', { style: { flex: 1, minWidth: 0 } },
          React.createElement('div', { style: { ...PT.bodyLg, fontSize: 14 } }, n),
          React.createElement('div', { style: PT.bodySm }, d, ' doses (', Math.floor(d / 4), ' \u00D7 (4)', d % 4 ? ' + ' + (d % 4) + 'd' : '', ')')),
        React.createElement('span', { style: { font: '600 12px/16px var(--font-sans)', color: 'var(--osrs-stack-yellow)' } }, p, ' / (4)')))));
}
window.StorageScreen = StorageScreen;
