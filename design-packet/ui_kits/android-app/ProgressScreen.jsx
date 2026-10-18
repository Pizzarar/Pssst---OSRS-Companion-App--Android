function fmtQ(q) { return q >= 10000000 ? Math.floor(q / 1000000) + 'M' : q >= 100000 ? Math.floor(q / 1000) + 'K' : String(q); }
function ProgressScreen() {
  const { SegmentedTabs, Panel, ProgressBar, TextField, FilterChip } = window.PssstDesignSystem_7dc1e9;
  const D = window.PSSST_DATA;
  const [tab, setTab] = React.useState('skills'); const [f, setF] = React.useState('ALL'); const [q, setQ] = React.useState('');
  const total = D.skills.reduce((a, s) => a + s[1], 0), xp = D.skills.reduce((a, s) => a + s[2], 0);
  const qc = D.quests.filter(x => x[1] === 'COMPLETED').length;
  const filters = [['ALL', 'All'], ['COMPLETED', 'Completed'], ['IN_PROGRESS', 'In Progress'], ['NOT_STARTED', 'Not Started']];
  const qs = D.quests.filter(x => (f === 'ALL' || x[1] === f) && x[0].toLowerCase().includes(q.toLowerCase()));
  const qcol = { COMPLETED: ['Completed', 'var(--status-success)'], IN_PROGRESS: ['In Progress', 'var(--osrs-stack-yellow)'], NOT_STARTED: ['Not Started', 'var(--status-danger)'] };
  return React.createElement('div', { style: { flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column' } },
    React.createElement('div', { style: { padding: '8px 16px 0' } },
      React.createElement(SegmentedTabs, { value: tab, onChange: setTab, tabs: [{ id: 'skills', label: 'Skills (Total: ' + total + ')' }, { id: 'quests', label: 'Quests (' + qc + '/' + D.quests.length + ')' }] })),
    tab === 'skills' ? React.createElement(Scroll, { gap: 10 },
      React.createElement(Panel, { padding: 14, rivets: true, style: rowBetween },
        React.createElement('div', null, React.createElement('div', { style: PT.titleMd }, 'Total Level: ', total), React.createElement('div', { style: PT.bodySm }, '23 Skills Tracked')),
        React.createElement('div', { style: { textAlign: 'right' } }, React.createElement('div', { style: PT.bodySm }, 'Total XP'), React.createElement('div', { style: PT.yellowVal }, fmtQ(xp), ' XP'))),
      React.createElement('div', { style: { display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 6 } },
        D.skills.map(([n, l, x]) => React.createElement(Panel, { key: n, padding: 8 },
          React.createElement('div', { style: rowBetween },
            React.createElement('span', { style: { font: '700 11px/16px var(--font-sans)', color: 'var(--text-muted)' } }, n),
            React.createElement('span', { style: { font: '800 13px/18px var(--font-sans)', color: l >= 99 ? 'var(--status-success)' : 'var(--osrs-stack-yellow)' } }, l)),
          React.createElement(ProgressBar, { thin: true, value: l >= 99 ? 1 : ((x * 7) % 100) / 100, tone: l >= 99 ? 'success' : 'accent', style: { margin: '4px 0' } }),
          React.createElement('div', { style: { font: '500 9px/12px var(--font-sans)', color: 'var(--text-body)' } }, fmtQ(x), ' XP'))))) :
    React.createElement(Scroll, { gap: 6 },
      React.createElement(TextField, { label: 'Search Quests', placeholder: 'e.g. Dragon Slayer or Desert Treasure', value: q, onChange: (e) => setQ(e.target.value) }),
      React.createElement('div', { style: { display: 'flex', gap: 6, flexWrap: 'wrap', margin: '2px 0 4px' } },
        filters.map(([id, l]) => React.createElement(FilterChip, { key: id, selected: f === id, onClick: () => setF(id) }, l, ' (', id === 'ALL' ? D.quests.length : D.quests.filter(x => x[1] === id).length, ')'))),
      qs.map(([n, s]) => React.createElement(Panel, { key: n, padding: 10, style: rowBetween },
        React.createElement('span', { style: { font: '600 13px/18px var(--font-sans)', color: qcol[s][1] } }, n),
        React.createElement('span', { style: { font: '500 11px/16px var(--font-sans)', color: qcol[s][1], flex: 'none' } }, qcol[s][0])))));
}
window.ProgressScreen = ProgressScreen;
