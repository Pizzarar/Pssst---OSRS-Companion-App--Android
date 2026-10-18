const DS = new Proxy({}, { get: (_, k) => window.PssstDesignSystem_7dc1e9[k] });
const DT = {
  h1: { font: '700 32px/38px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)', margin: 0 },
  h2: { font: '700 24px/30px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)', margin: 0 },
  h3: { font: '700 17px/22px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)', margin: 0 },
  p: { font: '400 15px/22px var(--font-sans)', color: 'var(--text-body)', margin: 0, textWrap: 'pretty' },
  muted: { font: '400 13px/18px var(--font-sans)', color: 'var(--text-muted)', margin: 0 },
};
function Badge({ children, tone = 'accent' }) {
  return <span style={{ font: '400 10px/14px var(--font-pixel)', letterSpacing: '.5px', padding: '3px 7px', borderRadius: 4, background: 'var(--surface-inset)', border: '1px solid', borderColor: 'var(--bevel-pressed)', color: tone === 'danger' ? 'var(--status-danger)' : 'var(--accent)', verticalAlign: 'middle' }}>{children}</span>;
}
function Shot({ n, children }) {
  return <DS.Slot padding={0} style={{ minHeight: 150, display: 'grid', placeItems: 'center', padding: 16, textAlign: 'center' }}>
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}><DS.DocIcon name="image" size={22} /><span style={DT.muted}>Screenshot {n}: {children}</span><span style={{ font: '400 11px/14px var(--font-mono)', color: 'var(--text-muted)' }}>real capture to come</span></div>
  </DS.Slot>;
}
function Section({ id, title, children, badge }) {
  return <section id={id} data-screen-label={title} style={{ display: 'flex', flexDirection: 'column', gap: 16, scrollMarginTop: 80 }}>
    <h2 style={DT.h2}>{title} {badge}</h2>{children}</section>;
}
function Steps({ children }) { return <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>{children}</div>; }
const S = (p) => <DS.SettingChip {...p} />;
const TOC = [['need', 'What you need'], ['install', 'Install Pssst Companion'], ['sync', 'Turn on sync'], ['paths', 'Choose how your phone reaches your PC'], ['app', 'Install the app and pair it'], ['check', 'Check that it works'], ['trouble', 'Troubleshooting'], ['privacy', 'What is sent, and where']];
function DocsShell({ children, active, onNav, theme, onTheme }) {
  return <div data-theme={theme} style={{ minHeight: '100vh', background: 'var(--surface-page)', color: 'var(--text-body)' }}>
    <header style={{ position: 'sticky', top: 0, zIndex: 3, background: 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))', borderBottom: '1px solid var(--border-frame)' }}>
      <div style={{ maxWidth: 1120, margin: '0 auto', height: 60, padding: '0 24px', display: 'flex', alignItems: 'center', gap: 16 }}>
        <span style={{ font: '800 24px/1 var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)' }}>Pssst</span>
        <span style={DT.muted}>Setup guide</span><span style={{ flex: 1 }} />
        <button onClick={onTheme} style={{ border: '1px solid var(--border-frame)', background: 'transparent', borderRadius: 4, padding: '4px 10px', cursor: 'pointer', font: '500 12px/16px var(--font-sans)', color: 'var(--text-muted)' }}>{theme === 'parchment' ? 'Dark' : 'Parchment'}</button>
      </div></header>
    <div style={{ maxWidth: 1120, margin: '0 auto', padding: '32px 24px', display: 'grid', gridTemplateColumns: 'minmax(0,220px) minmax(0,1fr)', gap: 40 }}>
      <nav style={{ position: 'sticky', top: 92, alignSelf: 'start', display: 'flex', flexDirection: 'column', gap: 2 }}>
        {TOC.map(([id, l], i) => <a key={id} href={'#' + id} onClick={() => onNav(id)} style={{ display: 'flex', gap: 10, alignItems: 'center', padding: '7px 10px', borderRadius: 4, textDecoration: 'none', background: active === id ? 'var(--surface-inset)' : 'transparent', color: active === id ? 'var(--accent)' : 'var(--text-body)', font: (active === id ? '700' : '400') + ' 13px/18px var(--font-sans)' }}><span style={{ font: '700 12px/1 var(--font-serif)', color: 'var(--text-muted)', width: 12 }}>{i + 1}</span>{l}</a>)}
      </nav>
      <main style={{ maxWidth: 720, minWidth: 0, display: 'flex', flexDirection: 'column', gap: 48 }}>{children}<DS.FanFooter /></main>
    </div></div>;
}
Object.assign(window, { DS, DT, Badge, Shot, Section, Steps, S, TOC, DocsShell });
