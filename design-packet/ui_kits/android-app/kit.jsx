const PT = {
  headline: { font: '700 24px/30px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)', margin: 0 },
  titleMd: { font: '700 17px/22px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)' },
  titleSm: { font: '700 14px/18px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)' },
  bodyLg: { font: '600 15px/22px var(--font-sans)', color: 'var(--text-body)' },
  bodyMd: { font: '400 13px/18px var(--font-sans)', color: 'var(--text-muted)' },
  bodySm: { font: '400 12px/16px var(--font-sans)', color: 'var(--text-muted)' },
  labelSm: { font: '500 11px/16px var(--font-sans)', color: 'var(--text-muted)' },
  yellowVal: { font: '800 17px/22px var(--font-serif)', color: 'var(--osrs-stack-yellow)', textShadow: 'var(--text-shadow-hard)' },
};
const rowBetween = { display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 8 };
function PhoneFrame({ children }) {
  return <div style={{ width: 412, height: 892, borderRadius: 36, padding: 10, background: '#0b0907', boxShadow: '0 0 0 1px #3a3128', flex: 'none' }}>
    <div style={{ width: '100%', height: '100%', borderRadius: 28, overflow: 'hidden', background: 'var(--surface-page)', display: 'flex', flexDirection: 'column', position: 'relative' }}>
      <div style={{ height: 28, flex: 'none', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 20px', font: '500 12px/16px var(--font-sans)', color: 'var(--text-muted)', background: 'var(--surface-panel-top)' }}><span>9:41</span><span>●●● 87%</span></div>
      <div style={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column', position: 'relative' }}>{children}</div>
    </div></div>;
}
function Scroll({ children, pad = 16, gap = 12 }) {
  return <div style={{ flex: 1, minHeight: 0, overflowY: 'auto', padding: pad, display: 'flex', flexDirection: 'column', gap }}>{children}</div>;
}
function Empty({ children }) { return <div style={{ flex: 1, display: 'grid', placeItems: 'center', padding: 24, textAlign: 'center', ...PT.bodyMd, whiteSpace: 'pre-line' }}>{children}</div>; }
Object.assign(window, { PT, rowBetween, PhoneFrame, Scroll, Empty });
