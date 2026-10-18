function OverviewScreen({ onNavigate }) {
  const { Panel, StatusChip, ProgressBar } = window.PssstDesignSystem_7dc1e9;
  const D = window.PSSST_DATA; const w = D.wealth; const s = D.slayer; const m = D.misc;
  const readyFarm = D.patches.filter(p => p.grown).length, readyBh = D.birdhouses.filter(b => b.chip === 'READY').length, readyDaily = D.dailies.filter(d => d.ready).length;
  const active = D.ge.filter(g => !g.empty).length, done = D.ge.filter(g => g.state === 'SOLD' || g.state === 'BOUGHT').length;
  const stat = (l, v) => React.createElement('div', null, React.createElement('div', { style: PT.bodySm }, l), React.createElement('div', { style: { font: '600 13px/18px var(--font-sans)', color: 'var(--text-body)' } }, v));
  const groove = { height: 1, background: 'var(--outer-frame)', boxShadow: '0 1px 0 var(--bevel-hi)', margin: '8px 0' };
  return React.createElement(Scroll, null,
    React.createElement(Panel, { padding: 14, rivets: true },
      React.createElement('div', { style: rowBetween }, React.createElement('span', { style: PT.titleMd }, 'Total Account Wealth'), React.createElement('span', { style: PT.yellowVal }, w.total)),
      React.createElement('div', { style: groove }),
      React.createElement('div', { style: { ...rowBetween, alignItems: 'flex-start' } }, stat('Bank', w.bank), stat('Inventory', w.inventory), stat('Potions', w.potions), stat('GE Offers', w.ge))),
    React.createElement(Panel, { padding: 12, rivets: true },
      React.createElement('div', { style: PT.titleSm }, 'Activity Status'),
      React.createElement('div', { style: groove }),
      React.createElement('div', { style: { display: 'flex', gap: 8, flexWrap: 'wrap' } },
        React.createElement(StatusChip, { tone: readyFarm ? 'ready' : 'done', onClick: () => onNavigate('timers') }, 'Farming: ', readyFarm),
        React.createElement(StatusChip, { tone: readyBh ? 'ready' : 'done', onClick: () => onNavigate('timers') }, 'Birdhouses: ', readyBh),
        React.createElement(StatusChip, { tone: readyDaily ? 'ready' : 'done', onClick: () => onNavigate('timers') }, 'Dailies: ', readyDaily),
        React.createElement(StatusChip, { tone: done ? 'ready' : 'active', onClick: () => onNavigate('exchange') }, 'GE: ', done, '/', active))),
    React.createElement(Panel, { padding: 14, rivets: true },
      React.createElement('div', { style: rowBetween }, React.createElement('span', { style: PT.titleMd }, 'Active Slayer Task'), React.createElement('span', { style: PT.bodySm }, s.location)),
      React.createElement('div', { style: groove }),
      React.createElement('div', { style: rowBetween }, React.createElement('span', { style: PT.bodyLg }, s.task), React.createElement(StatusChip, { tone: 'active', style: { fontSize: 12 } }, s.amount, ' remaining')),
      React.createElement('div', { style: { ...PT.labelSm, marginTop: 6 } }, 'of ', s.initial, ' \u2022 ', s.points, ' points \u2022 ', s.streak, ' streak')),
    React.createElement(Panel, { padding: 14, rivets: true },
      React.createElement('div', { style: rowBetween }, React.createElement('span', { style: PT.titleMd }, 'Kingdom of Miscellania'), React.createElement('span', { style: { font: '600 14px/20px var(--font-sans)', color: m.approval >= 75 ? 'var(--status-success)' : 'var(--status-danger)' } }, m.approval, '% Approval')),
      React.createElement('div', { style: groove }),
      React.createElement(ProgressBar, { value: m.approval / 100, tone: m.approval >= 75 ? 'success' : 'danger', style: { margin: '8px 0' } }),
      React.createElement('div', { style: rowBetween }, React.createElement('span', { style: PT.bodyMd }, 'Coffer Funds:'), React.createElement('span', { style: { font: '700 14px/20px var(--font-sans)', color: 'var(--osrs-stack-yellow)' } }, m.coffer))));
}
window.OverviewScreen = OverviewScreen;
