function TimersScreen() {
  const { SegmentedTabs, Panel, Slot, StatusChip, ProgressBar } = window.PssstDesignSystem_7dc1e9;
  const D = window.PSSST_DATA;
  const ic = D.icons || {};
  const [tab, setTab] = React.useState('dailies');
  const rd = D.dailies.filter(d => d.ready).length, rb = D.birdhouses.filter(b => b.chip === 'READY').length, rf = D.patches.filter(p => p.grown).length;
  const bhTone = { READY: 'ready', ACTIVE: 'active', BUILT: 'neutral', EMPTY: 'done' };
  const iconSlot = (key, label) => React.createElement(Slot, { hasItem: !!ic[key], style: { width: 40, height: 40, display: 'grid', placeItems: 'center', flex: 'none' } },
    ic[key] ? React.createElement('img', { src: ic[key], alt: label, style: { width: 32, height: 32, imageRendering: 'pixelated', objectFit: 'contain' } }) :
    React.createElement('span', { style: { font: '500 9px/11px var(--font-sans)', color: 'var(--text-body)', textAlign: 'center' } }, (label || '').split(' ')[0]));
  return React.createElement('div', { style: { flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column', padding: 16, gap: 12 } },
    React.createElement(SegmentedTabs, { value: tab, onChange: setTab, tabs: [{ id: 'dailies', label: 'Dailies', count: rd }, { id: 'bh', label: 'Birdhouses', count: rb }, { id: 'patches', label: 'Patches', count: rf }, { id: 'pay', label: 'Payments' }] }),
    React.createElement('div', { style: { flex: 1, minHeight: 0, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: 8 } },
      tab === 'dailies' && D.dailies.map(d => React.createElement(Panel, { key: d.title, padding: 12, style: { display: 'flex', alignItems: 'center', gap: 12 } },
        iconSlot(d.icon || d.title, d.title),
        React.createElement('div', { style: { flex: 1, minWidth: 0 } },
          React.createElement('div', { style: { display: 'flex', alignItems: 'center', gap: 6 } },
            React.createElement('span', { style: { ...PT.bodyLg, color: d.ready ? 'var(--accent)' : 'var(--text-body)' } }, d.title),
            d.weekly && React.createElement('span', { style: { font: '700 11px/16px var(--font-sans)', color: 'var(--osrs-stack-yellow)' } }, '[Weekly]')),
          React.createElement('div', { style: PT.bodySm }, d.subtitle),
          React.createElement('div', { style: { ...PT.bodySm, color: d.ready ? 'var(--status-success)' : 'var(--text-muted)' } }, d.status)),
        React.createElement(StatusChip, { tone: d.ready ? 'ready' : 'done' }, d.ready ? 'READY' : 'DONE'))),
      tab === 'bh' && D.birdhouses.map(b => React.createElement(Panel, { key: b.name, padding: 12, style: { display: 'flex', alignItems: 'center', gap: 12 } },
        iconSlot(b.icon || b.type || 'Empty birdhouse', b.type || 'Birdhouse'),
        React.createElement('div', { style: { flex: 1, minWidth: 0 } },
          React.createElement('div', { style: { ...PT.bodyLg, color: b.chip === 'READY' ? 'var(--status-success)' : 'var(--text-body)' } }, b.name),
          React.createElement('div', { style: PT.bodySm }, [b.type, b.status].filter(Boolean).join(' \u2022 '))),
        React.createElement(StatusChip, { tone: bhTone[b.chip] }, b.chip))),
      tab === 'patches' && D.patches.map(p => React.createElement(Panel, { key: p.label, padding: 12 },
        React.createElement('div', { style: { display: 'flex', alignItems: 'center', gap: 12 } },
          iconSlot(p.icon || p.status.split(' \u2022 ')[0], p.status.split(' \u2022 ')[0]),
          React.createElement('div', { style: { flex: 1, minWidth: 0 } },
            React.createElement('div', { style: { ...PT.bodyLg, color: p.grown ? 'var(--status-success)' : 'var(--text-body)' } }, p.label),
            React.createElement('div', { style: PT.bodySm }, p.status)),
          p.grown && React.createElement(StatusChip, { tone: 'ready' }, 'GROWN')),
        p.progress != null && React.createElement(ProgressBar, { value: p.progress, style: { marginTop: 8 } }),
        React.createElement('div', { style: { ...PT.labelSm, marginTop: 6 } }, p.timing || p.seen))),
      tab === 'pay' && React.createElement(React.Fragment, null,
        React.createElement('div', { style: PT.bodySm }, 'Payment amounts are unverified: they are not read from the game.'),
        D.payments.map(p => {
          const payItem = p.icon || p.req.split(' \u00D7 ')[1].split(' \u2022 ')[0];
          return React.createElement(Panel, { key: p.crop, padding: 10, style: { display: 'flex', alignItems: 'center', gap: 12 } },
            iconSlot(payItem, payItem),
            React.createElement('div', { style: { flex: 1, minWidth: 0 } },
              React.createElement('div', { style: { font: '600 13px/18px var(--font-sans)', color: 'var(--text-body)' } }, p.crop, ' (Lvl ', p.lvl, ')'),
              React.createElement('div', { style: PT.bodySm }, p.req)),
            React.createElement(StatusChip, { tone: p.ok ? 'ready' : 'danger' }, p.ok ? 'Owned: ' + p.owned : 'Short (' + p.owned + '/' + p.need + ')'));
        }))));
}
window.TimersScreen = TimersScreen;
