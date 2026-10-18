function Drawer({ open, tab, onTab, onClose, onDisconnect, serverUrl }) {
  const { Panel, Switch, DrawerItem, Button } = window.PssstDesignSystem_7dc1e9;
  const D = window.PSSST_DATA;
  const [notif, setNotif] = React.useState(true);
  const tabs = [['overview', 'Overview'], ['storage', 'Storage'], ['exchange', 'Exchange'], ['progress', 'Progress'], ['timers', 'Timers']];
  return React.createElement('div', { style: { position: 'absolute', inset: 0, zIndex: 5, pointerEvents: open ? 'auto' : 'none' } },
    React.createElement('div', { onClick: onClose, style: { position: 'absolute', inset: 0, background: 'rgba(0,0,0,.45)', opacity: open ? 1 : 0, transition: 'opacity 200ms' } }),
    React.createElement('div', { style: { position: 'absolute', top: 0, bottom: 0, left: 0, width: 310, backgroundImage: 'var(--stone-grain), linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))', borderRight: '2px solid var(--outer-frame)', boxShadow: 'var(--shadow-panel)', transform: open ? 'none' : 'translateX(-100%)', transition: 'transform 250ms cubic-bezier(.2,0,0,1)', padding: 16, boxSizing: 'border-box', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' } },
      React.createElement('div', null,
        React.createElement('div', { style: { font: '700 20px/26px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)' } }, 'Pssst Companion'),
        React.createElement('div', { style: PT.bodySm }, 'OSRS Offline-First Tracker'),
        React.createElement('div', { style: { height: 1, background: 'var(--outer-frame)', boxShadow: '0 1px 0 var(--bevel-hi)', margin: '14px 0' } }),
        React.createElement(Panel, { padding: 12, rivets: true, style: { display: 'flex', flexDirection: 'column', gap: 6 } },
          React.createElement('span', { style: { font: '700 11px/16px var(--font-sans)', color: 'var(--accent)' } }, 'Connection Status'),
          React.createElement('span', { style: { ...PT.bodySm, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' } }, 'Server: ', serverUrl),
          React.createElement('span', { style: PT.bodySm }, 'Sync Token: ', D.token.slice(0, 6), '...', D.token.slice(-6)),
          React.createElement('span', { style: { font: '600 11px/16px var(--font-sans)', color: 'var(--status-success)' } }, 'OK \u2022 synced 2 min. ago')),
        React.createElement(Panel, { padding: '10px 12px', style: { marginTop: 10 } },
          React.createElement('div', { style: { font: '600 13px/18px var(--font-sans)', color: 'var(--text-body)' } }, 'Item prices'),
          React.createElement('div', { style: PT.bodySm }, 'As of 17 Oct 2026 \u2022 refreshed from your RuneLite client on each sync')),
        React.createElement(Panel, { padding: '8px 12px', style: { ...rowBetween, marginTop: 10 } },
          React.createElement('div', null,
            React.createElement('div', { style: { font: '600 13px/18px var(--font-sans)', color: 'var(--text-body)' } }, 'Notifications'),
            React.createElement('div', { style: PT.bodySm }, notif ? 'Active alerts' : 'Disabled')),
          React.createElement(Switch, { checked: notif, onChange: setNotif })),
        React.createElement('div', { style: { font: '700 11px/16px var(--font-sans)', color: 'var(--accent)', margin: '14px 0 6px' } }, 'Jump to Section'),
        tabs.map(([id, l]) => React.createElement(DrawerItem, { key: id, selected: tab === id, onClick: () => { onTab(id); onClose(); } }, l))),
      React.createElement('div', { style: { display: 'flex', flexDirection: 'column', gap: 8 } },
        React.createElement(Button, { variant: 'text', tone: 'sync', fullWidth: true, onClick: onClose }, 'Sync Now'),
        React.createElement(Button, { variant: 'text', tone: 'danger', fullWidth: true, onClick: onDisconnect }, 'Disconnect / Change Token'))));
}
function Dashboard({ serverUrl, onDisconnect, initialTab = 'overview' }) {
  const { TopBar, BottomNav } = window.PssstDesignSystem_7dc1e9;
  const [tab, setTab] = React.useState(initialTab); const [drawer, setDrawer] = React.useState(false);
  const titles = { overview: 'Overview', storage: 'Storage', exchange: 'Exchange', progress: 'Progress', timers: 'Timers' };
  const S = { overview: React.createElement(OverviewScreen, { onNavigate: setTab }), storage: React.createElement(StorageScreen), exchange: React.createElement(ExchangeScreen), progress: React.createElement(ProgressScreen), timers: React.createElement(TimersScreen) }[tab];
  return React.createElement(React.Fragment, null,
    React.createElement(TopBar, { title: titles[tab], syncedAgo: '2 min. ago', onMenu: () => setDrawer(true) }),
    React.createElement('div', { style: { flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column', background: 'var(--surface-page)' } }, S),
    React.createElement(BottomNav, { value: tab, onChange: setTab }),
    React.createElement(Drawer, { open: drawer, tab, onTab: setTab, onClose: () => setDrawer(false), onDisconnect: () => { setDrawer(false); onDisconnect(); }, serverUrl }));
}
window.Dashboard = Dashboard;
