/* @ds-bundle: {"format":4,"namespace":"PssstDesignSystem_7dc1e9","components":[{"name":"Button","sourcePath":"components/controls/Button.jsx"},{"name":"Checkbox","sourcePath":"components/controls/Checkbox.jsx"},{"name":"FilterChip","sourcePath":"components/controls/FilterChip.jsx"},{"name":"SegmentedTabs","sourcePath":"components/controls/SegmentedTabs.jsx"},{"name":"Switch","sourcePath":"components/controls/Switch.jsx"},{"name":"TextField","sourcePath":"components/controls/TextField.jsx"},{"name":"ItemSlot","sourcePath":"components/data/ItemSlot.jsx"},{"name":"ProgressBar","sourcePath":"components/data/ProgressBar.jsx"},{"name":"StatusChip","sourcePath":"components/data/StatusChip.jsx"},{"name":"Callout","sourcePath":"components/docs/Callout.jsx"},{"name":"CodeBlock","sourcePath":"components/docs/CodeBlock.jsx"},{"name":"ConnectionFlow","sourcePath":"components/docs/ConnectionFlow.jsx"},{"name":"DocIcon","sourcePath":"components/docs/DocIcon.jsx"},{"name":"DocTable","sourcePath":"components/docs/DocTable.jsx"},{"name":"FAN_NOTICE","sourcePath":"components/docs/FanFooter.jsx"},{"name":"FanFooter","sourcePath":"components/docs/FanFooter.jsx"},{"name":"SettingChip","sourcePath":"components/docs/SettingChip.jsx"},{"name":"Step","sourcePath":"components/docs/Step.jsx"},{"name":"StepNumber","sourcePath":"components/docs/StepNumber.jsx"},{"name":"AppIcon","sourcePath":"components/icons/AppIcon.jsx"},{"name":"DEFAULT_TABS","sourcePath":"components/navigation/BottomNav.jsx"},{"name":"BottomNav","sourcePath":"components/navigation/BottomNav.jsx"},{"name":"DrawerItem","sourcePath":"components/navigation/DrawerItem.jsx"},{"name":"TopBar","sourcePath":"components/navigation/TopBar.jsx"},{"name":"Panel","sourcePath":"components/surfaces/Panel.jsx"},{"name":"Slot","sourcePath":"components/surfaces/Slot.jsx"}],"sourceHashes":{"components/controls/Button.jsx":"9d7b0226cab9","components/controls/Checkbox.jsx":"c58ed21342d8","components/controls/FilterChip.jsx":"9336712bd601","components/controls/SegmentedTabs.jsx":"cfcbf00223a5","components/controls/Switch.jsx":"ea4d12fd26c6","components/controls/TextField.jsx":"537fc80d7704","components/data/ItemSlot.jsx":"f95c2f8344c4","components/data/ProgressBar.jsx":"e7a52f48b935","components/data/StatusChip.jsx":"86c04e8b68de","components/docs/Callout.jsx":"4143c14df665","components/docs/CodeBlock.jsx":"f32fce6e973d","components/docs/ConnectionFlow.jsx":"13be554a4d63","components/docs/DocIcon.jsx":"b70f640af5b6","components/docs/DocTable.jsx":"e96de8f2a733","components/docs/FanFooter.jsx":"b0e839d7dc97","components/docs/SettingChip.jsx":"3f49ac6fefee","components/docs/Step.jsx":"64fa4774b4a1","components/docs/StepNumber.jsx":"1a5a00918a7b","components/icons/AppIcon.jsx":"682f1e673c6d","components/navigation/BottomNav.jsx":"fe27b2693efa","components/navigation/DrawerItem.jsx":"1355b758bd0a","components/navigation/TopBar.jsx":"464f8e986f6d","components/surfaces/Panel.jsx":"8212674c2e10","components/surfaces/Slot.jsx":"a39639c46ffe","ds-loader.js":"81cb54bd847d","ui_kits/android-app/Dashboard.jsx":"334b0a4b1cc9","ui_kits/android-app/ExchangeScreen.jsx":"d703327f27f3","ui_kits/android-app/OverviewScreen.jsx":"98e45db3e944","ui_kits/android-app/ProgressScreen.jsx":"06bb267575d6","ui_kits/android-app/SetupScreen.jsx":"277cb152eff4","ui_kits/android-app/StorageScreen.jsx":"fef7d003fb63","ui_kits/android-app/TimersScreen.jsx":"54c295bc3059","ui_kits/android-app/data.js":"51165d314976","ui_kits/android-app/kit.jsx":"8f0c0f53f7ae","ui_kits/setup-guide/DocsShell.jsx":"608454e92f76","ui_kits/setup-guide/GuideFinish.jsx":"cf5a9cbf118b","ui_kits/setup-guide/GuideIntro.jsx":"e3d417f065e9","ui_kits/setup-guide/GuidePaths.jsx":"c8bfb36f513c"},"inlinedExternals":[],"unexposedExports":[{"name":"formatQuantity","sourcePath":"components/data/ItemSlot.jsx"}]} */

(() => {

const __ds_ns = (window.PssstDesignSystem_7dc1e9 = window.PssstDesignSystem_7dc1e9 || {});

const __ds_scope = {};

(__ds_ns.__errors = __ds_ns.__errors || []);

// components/controls/Button.jsx
try { (() => {
const TONES = { accent: 'var(--accent)', danger: 'var(--status-danger)', sync: 'var(--osrs-stack-yellow)' };
/** Chiseled 3px plaque button: gold (filled) or carved stone (stone/text) with 3D raised/pressed bevels. Mirrors OsrsButton(). */
function Button({ variant = 'filled', tone = 'accent', fullWidth = false, disabled = false, style, children, ...rest }) {
  const [press, setPress] = React.useState(false);
  const c = TONES[tone] || TONES.accent;
  const filled = variant === 'filled';
  const bg = filled
    ? (tone === 'danger'
        ? (press ? 'linear-gradient(180deg,#B32424,#FF4D4D)' : 'linear-gradient(180deg,#FF4D4D,#B32424)')
        : (press ? 'linear-gradient(180deg,var(--osrs-gold-dark),var(--osrs-gold))' : 'linear-gradient(180deg,var(--osrs-gold),var(--osrs-gold-dark))'))
    : (press ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))');
  const hi = filled ? (tone === 'danger' ? '#FF8A8A' : 'var(--osrs-gold-hi)') : 'var(--bevel-hi)';
  const lo = filled ? (tone === 'danger' ? '#660F0F' : 'var(--osrs-gold-lo)') : 'var(--bevel-lo)';
  const shadow = press
    ? `inset 2px 2px 0 ${lo}, inset -1px -1px 0 ${hi}`
    : `inset 1px 1px 0 ${hi}, inset -1px -1px 0 ${lo}`;
  return React.createElement('button', { ...rest, disabled,
    onPointerDown: () => setPress(true), onPointerUp: () => setPress(false), onPointerLeave: () => setPress(false),
    style: {
      height: 38, padding: filled ? '0 20px' : '0 14px', borderRadius: 3,
      border: '1px solid var(--outer-frame)',
      background: bg,
      boxShadow: shadow,
      color: filled ? (tone === 'danger' ? 'var(--text-body)' : 'var(--text-on-accent)') : c,
      textShadow: filled && tone !== 'danger' ? 'none' : 'var(--text-shadow-hard)',
      font: '700 13px/18px var(--font-serif)', letterSpacing: '.2px', cursor: disabled ? 'default' : 'pointer',
      width: fullWidth ? '100%' : undefined, opacity: disabled ? .42 : 1,
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8, ...style } }, children);
}
Object.assign(__ds_scope, { Button });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/controls/Button.jsx", error: String((e && e.message) || e) }); }

// components/controls/Checkbox.jsx
try { (() => {
/** Recessed-slot checkbox: 20x20 carved Slot well with a beveled gold check plaque when checked. Mirrors OsrsCheckbox(). */
function Checkbox({ checked, defaultChecked = false, onChange, label, style }) {
  const [inner, setInner] = React.useState(defaultChecked);
  const on = checked !== undefined ? checked : inner;
  const toggle = () => { if (checked === undefined) setInner(!on); onChange && onChange(!on); };
  return React.createElement('div', { role: 'checkbox', 'aria-checked': on, tabIndex: 0, onClick: toggle,
    onKeyDown: (e) => { if (e.key === ' ' || e.key === 'Enter') { e.preventDefault(); toggle(); } },
    style: { display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer', minHeight: 36, ...style } },
    React.createElement('span', { style: {
      width: 20, height: 20, boxSizing: 'border-box', borderRadius: 3, flex: 'none',
      border: '1px solid ' + (on ? 'var(--accent)' : 'var(--outer-frame)'),
      background: 'var(--surface-empty)',
      boxShadow: 'var(--shadow-slot)',
      display: 'grid', placeItems: 'center' } },
      on && React.createElement('span', { style: {
        width: 14, height: 14, borderRadius: 2,
        background: 'linear-gradient(180deg,var(--osrs-gold),var(--osrs-gold-dark))',
        boxShadow: 'inset 1px 1px 0 var(--osrs-gold-hi), inset -1px -1px 0 var(--osrs-gold-lo)',
        display: 'grid', placeItems: 'center' } },
        React.createElement('svg', { width: 11, height: 11, viewBox: '0 0 24 24' },
          React.createElement('path', { d: 'M4 12.5l5 5L20 6.5', fill: 'none', stroke: '#000', strokeWidth: 3.5 })))),
    label && React.createElement('span', { style: { font: '500 13px/18px var(--font-sans)', color: on ? 'var(--accent)' : 'var(--text-muted)' } }, label));
}
Object.assign(__ds_scope, { Checkbox });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/controls/Checkbox.jsx", error: String((e && e.message) || e) }); }

// components/controls/FilterChip.jsx
try { (() => {
/** Chiseled 3px filter chip: raised stone plaque when idle, recessed Slot well with 1px gold border when selected. Mirrors OsrsFilterChip(). */
function FilterChip({ selected = false, shape = 'chip', onClick, children, style }) {
  const sq = shape === 'square';
  return React.createElement('button', { onClick, 'aria-pressed': selected, style: {
    height: sq ? 26 : 28, padding: sq ? '0 10px' : '0 12px', borderRadius: 3, cursor: 'pointer',
    border: '1px solid ' + (selected ? 'var(--accent)' : 'var(--outer-frame)'),
    background: selected ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
    boxShadow: selected ? 'var(--shadow-slot)' : 'var(--shadow-panel)',
    color: selected ? 'var(--accent)' : 'var(--text-muted)',
    textShadow: 'var(--text-shadow-hard)',
    font: (selected ? '700' : '600') + ' 11px/15px var(--font-sans)', ...style } }, children);
}
Object.assign(__ds_scope, { FilterChip });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/controls/FilterChip.jsx", error: String((e && e.message) || e) }); }

// components/controls/Switch.jsx
try { (() => {
/** Chiseled stone toggle switch: recessed rectangular stone track with beveled rectangular slider thumb. Mirrors OsrsSwitch(). */
function Switch({ checked, defaultChecked = false, onChange, style, ...rest }) {
  const [inner, setInner] = React.useState(defaultChecked);
  const on = checked !== undefined ? checked : inner;
  const toggle = () => { if (checked === undefined) setInner(!on); onChange && onChange(!on); };
  return React.createElement('button', { ...rest, role: 'switch', 'aria-checked': on, onClick: toggle,
    style: { width: 48, height: 26, borderRadius: 3, padding: 0, position: 'relative', cursor: 'pointer', boxSizing: 'border-box',
      background: 'var(--surface-empty)',
      border: '1px solid ' + (on ? 'var(--status-success)' : 'var(--outer-frame)'),
      boxShadow: 'var(--shadow-slot)',
      flex: 'none', ...style } },
    React.createElement('span', { style: { position: 'absolute', top: 3, left: on ? 23 : 3, width: 20, height: 18,
      borderRadius: 2,
      background: on ? 'linear-gradient(180deg,#5CD660,#2E7D32)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
      border: '1px solid var(--outer-frame)',
      boxShadow: on ? 'inset 1px 1px 0 #85E688, inset -1px -1px 0 #1B5E20' : 'var(--shadow-panel)',
      transition: 'left var(--motion-base) var(--ease-standard)' } }));
}
Object.assign(__ds_scope, { Switch });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/controls/Switch.jsx", error: String((e && e.message) || e) }); }

// components/controls/TextField.jsx
try { (() => {
/** Recessed-slot text field: fixed top label + carved dark-stone Slot input box. Mirrors OsrsTextField(). */
function TextField({ label, value, defaultValue, placeholder, supportingText, error = false, mono = false, onChange, style, inputStyle, ...rest }) {
  const [focus, setFocus] = React.useState(false);
  const [inner, setInner] = React.useState(defaultValue || '');
  const v = value !== undefined ? value : inner;
  const borderC = error ? 'var(--status-danger)' : focus ? 'var(--accent)' : 'var(--outer-frame)';
  return React.createElement('label', { style: { display: 'block', ...style } },
    label && React.createElement('div', { style: {
      marginBottom: 4, font: '700 12px/16px var(--font-serif)',
      color: error ? 'var(--status-danger)' : focus ? 'var(--accent)' : 'var(--text-heading)',
      textShadow: 'var(--text-shadow-hard)' } }, label),
    React.createElement('div', { style: {
      position: 'relative', height: 42, borderRadius: 3,
      background: 'var(--surface-empty)',
      border: '1px solid ' + borderC,
      boxShadow: 'var(--shadow-slot)' } },
      React.createElement('input', { ...rest, value: v, placeholder: placeholder || '',
        onFocus: () => setFocus(true), onBlur: () => setFocus(false),
        onChange: (e) => { if (value === undefined) setInner(e.target.value); onChange && onChange(e); },
        style: { position: 'absolute', inset: 0, width: '100%', boxSizing: 'border-box', padding: '0 12px', border: 0, outline: 0,
          background: 'transparent', color: 'var(--text-body)', caretColor: 'var(--accent)',
          font: '400 14px/20px ' + (mono ? 'var(--font-mono)' : 'var(--font-sans)'), ...inputStyle } })),
    supportingText && React.createElement('div', { style: { padding: '4px 4px 0', font: '500 11px/15px var(--font-sans)',
      color: 'var(--status-danger)' } }, supportingText));
}
Object.assign(__ds_scope, { TextField });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/controls/TextField.jsx", error: String((e && e.message) || e) }); }

// components/data/ProgressBar.jsx
try { (() => {
const C = { success: 'var(--status-success)', accent: 'var(--accent)', danger: 'var(--status-danger)' };
/** Framed 3-band progress bar: recessed dark-stone trough + 1px top specular highlight, midtone fill, 1px bottom shadow. Mirrors OsrsProgressBar(). */
function ProgressBar({ value = 0, tone = 'success', thin = false, style }) {
  const p = Math.max(0, Math.min(1, value));
  return React.createElement('div', { role: 'progressbar', 'aria-valuenow': Math.round(p * 100), style: {
    height: thin ? 6 : 8, background: 'var(--surface-empty)', borderRadius: 2,
    border: '1px solid var(--outer-frame)', boxShadow: 'inset 1px 1px 0 #0E0B08',
    overflow: 'hidden', boxSizing: 'border-box', ...style } },
    p > 0 && React.createElement('div', { style: {
      width: p * 100 + '%', height: '100%', background: C[tone] || C.success,
      boxShadow: 'inset 0 1px 0 rgba(255,255,255,.38), inset 0 -1px 0 rgba(0,0,0,.45)',
      transition: 'width var(--motion-base) var(--ease-standard)' } }));
}
Object.assign(__ds_scope, { ProgressBar });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/data/ProgressBar.jsx", error: String((e && e.message) || e) }); }

// components/data/StatusChip.jsx
try { (() => {
const T = {
  ready:   { bg: 'linear-gradient(180deg,#5CD660,#388E3C)', fg: '#000', border: 'var(--outer-frame)', shadow: 'inset 1px 1px 0 #85E688, inset -1px -1px 0 #1B5E20', w: 800, sh: 'none' },
  done:    { bg: 'var(--surface-empty)', fg: 'var(--text-muted)', border: 'var(--outer-frame)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  neutral: { bg: 'var(--surface-empty)', fg: 'var(--text-body)', border: 'var(--border-frame)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  gold:    { bg: 'var(--surface-empty)', fg: 'var(--accent)', border: 'var(--accent)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  active:  { bg: 'var(--surface-empty)', fg: 'var(--osrs-stack-yellow)', border: 'var(--border-frame)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
  danger:  { bg: 'var(--surface-empty)', fg: 'var(--status-danger)', border: 'var(--status-danger)', shadow: 'var(--shadow-slot)', w: 700, sh: 'var(--text-shadow-hard)' },
};
/** Chiseled 3px state badge: READY / GROWN / DONE / ACTIVE / BUYING / CANCELLED, and summary counts. Mirrors OsrsStatusBadge(). */
function StatusChip({ tone = 'done', onClick, children, style }) {
  const t = T[tone] || T.done;
  return React.createElement(onClick ? 'button' : 'span', { onClick, style: {
    display: 'inline-flex', alignItems: 'center', height: 26, padding: '0 10px', boxSizing: 'border-box', borderRadius: 3,
    background: t.bg, color: t.fg, border: '1px solid ' + t.border, boxShadow: t.shadow, textShadow: t.sh,
    font: t.w + ' 11px/15px var(--font-sans)', whiteSpace: 'nowrap', cursor: onClick ? 'pointer' : 'default', flex: 'none', ...style } }, children);
}
Object.assign(__ds_scope, { StatusChip });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/data/StatusChip.jsx", error: String((e && e.message) || e) }); }

// components/docs/CodeBlock.jsx
try { (() => {
/** Inset code block for commands, URLs and paths. Optional copy button. */
function CodeBlock({
  children,
  label,
  copyable = true,
  style
}) {
  const [ok, setOk] = React.useState(false);
  const text = typeof children === 'string' ? children : '';
  const copy = () => {
    try {
      navigator.clipboard.writeText(text);
    } catch (e) {}
    setOk(true);
    setTimeout(() => setOk(false), 1200);
  };
  return React.createElement('div', {
    style: {
      background: 'var(--surface-inset)',
      border: '1px solid',
      borderColor: 'var(--bevel-pressed)',
      borderRadius: 4,
      ...style
    }
  }, (label || copyable) && React.createElement('div', {
    style: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: '6px 12px 0',
      font: '500 11px/16px var(--font-sans)',
      color: 'var(--text-muted)'
    }
  }, React.createElement('span', null, label || ''), copyable && text && React.createElement('button', {
    onClick: copy,
    style: {
      border: 0,
      background: 'transparent',
      cursor: 'pointer',
      padding: 0,
      font: '700 11px/16px var(--font-sans)',
      color: ok ? 'var(--status-success)' : 'var(--accent)'
    }
  }, ok ? 'Copied' : 'Copy')), React.createElement('pre', {
    style: {
      margin: 0,
      padding: '6px 12px 10px',
      overflowX: 'auto',
      font: '400 13px/20px var(--font-mono)',
      color: 'var(--text-body)'
    }
  }, children));
}
Object.assign(__ds_scope, { CodeBlock });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/CodeBlock.jsx", error: String((e && e.message) || e) }); }

// components/docs/DocIcon.jsx
try { (() => {
/** Docs line icon (Lucide, via CDN mask). Muted by default. Substitute set — see readme ICONOGRAPHY. */
function DocIcon({
  name = 'monitor',
  size = 20,
  color = 'var(--text-muted)',
  style
}) {
  const url = 'url(https://unpkg.com/lucide-static@0.460.0/icons/' + name + '.svg)';
  return React.createElement('span', {
    'aria-hidden': true,
    style: {
      display: 'inline-block',
      width: size,
      height: size,
      flex: 'none',
      background: color,
      WebkitMaskImage: url,
      maskImage: url,
      WebkitMaskSize: 'contain',
      maskSize: 'contain',
      WebkitMaskRepeat: 'no-repeat',
      maskRepeat: 'no-repeat',
      ...style
    }
  });
}
Object.assign(__ds_scope, { DocIcon });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/DocIcon.jsx", error: String((e && e.message) || e) }); }

// components/docs/Callout.jsx
try { (() => {
const K = {
  tip: {
    c: 'var(--accent)',
    i: 'lightbulb'
  },
  warning: {
    c: 'var(--status-danger)',
    i: 'triangle-alert'
  },
  note: {
    c: 'var(--status-note)',
    i: 'info'
  }
};
/** Inset callout with a coloured left rule and icon. Tip = gold, Warning = red, Note = muted. */
function Callout({
  kind = 'note',
  title,
  children,
  style
}) {
  const k = K[kind] || K.note;
  return React.createElement('div', {
    role: kind === 'warning' ? 'alert' : 'note',
    style: {
      display: 'flex',
      gap: 10,
      padding: '10px 12px',
      background: 'var(--surface-inset)',
      borderRadius: 4,
      border: '1px solid',
      borderColor: 'var(--bevel-pressed)',
      borderLeft: '3px solid ' + k.c,
      ...style
    }
  }, React.createElement(__ds_scope.DocIcon, {
    name: k.i,
    size: 18,
    color: k.c,
    style: {
      marginTop: 2
    }
  }), React.createElement('div', {
    style: {
      flex: 1,
      minWidth: 0,
      font: '400 14px/21px var(--font-sans)',
      color: 'var(--text-body)',
      textWrap: 'pretty'
    }
  }, title && React.createElement('div', {
    style: {
      font: '700 14px/18px var(--font-serif)',
      color: k.c,
      marginBottom: 2
    }
  }, title), children));
}
Object.assign(__ds_scope, { Callout });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/Callout.jsx", error: String((e && e.message) || e) }); }

// components/docs/DocTable.jsx
try { (() => {
/** Table with gold header text and alternating Slot Recessed / Panel Dark rows. */
function DocTable({
  columns = [],
  rows = [],
  style
}) {
  const cell = {
    padding: '8px 12px',
    textAlign: 'left',
    verticalAlign: 'top',
    font: '400 14px/20px var(--font-sans)',
    color: 'var(--text-body)'
  };
  return React.createElement('div', {
    style: {
      overflowX: 'auto',
      borderRadius: 4,
      border: '1px solid var(--border-frame)',
      ...style
    }
  }, React.createElement('table', {
    style: {
      borderCollapse: 'collapse',
      width: '100%'
    }
  }, React.createElement('thead', null, React.createElement('tr', {
    style: {
      background: 'var(--surface-panel-bottom)',
      borderBottom: '1px solid var(--border-frame)'
    }
  }, columns.map((c, i) => React.createElement('th', {
    key: i,
    style: {
      ...cell,
      font: '700 14px/18px var(--font-serif)',
      color: 'var(--text-heading)'
    }
  }, c)))), React.createElement('tbody', null, rows.map((r, ri) => React.createElement('tr', {
    key: ri,
    style: {
      background: ri % 2 ? 'var(--surface-panel-bottom)' : 'var(--surface-inset)'
    }
  }, r.map((v, ci) => React.createElement('td', {
    key: ci,
    style: cell
  }, v)))))));
}
Object.assign(__ds_scope, { DocTable });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/DocTable.jsx", error: String((e && e.message) || e) }); }

// components/docs/FanFooter.jsx
try { (() => {
const FAN_NOTICE = 'Pssst is a free fan project and is not affiliated with or endorsed by Jagex. Old School RuneScape is a trademark of Jagex Ltd.';
/** Required footer on every page. */
function FanFooter({
  style
}) {
  return React.createElement('footer', {
    style: {
      borderTop: '1px solid var(--border-frame)',
      padding: '16px 0',
      font: '400 12px/16px var(--font-sans)',
      color: 'var(--text-muted)',
      textWrap: 'pretty',
      ...style
    }
  }, FAN_NOTICE);
}
Object.assign(__ds_scope, { FAN_NOTICE, FanFooter });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/FanFooter.jsx", error: String((e && e.message) || e) }); }

// components/docs/SettingChip.jsx
try { (() => {
/** Exact setting/button name as a mono label in an inset slot. Use every time a setting is named. */
function SettingChip({
  children,
  style
}) {
  return React.createElement('span', {
    style: {
      display: 'inline-block',
      padding: '0 6px',
      margin: '0 1px',
      borderRadius: 4,
      verticalAlign: 'baseline',
      background: 'var(--surface-inset)',
      border: '1px solid',
      borderColor: 'var(--bevel-pressed)',
      color: 'var(--text-body)',
      font: '500 0.87em/1.5 var(--font-mono)',
      whiteSpace: 'nowrap',
      ...style
    }
  }, children);
}
Object.assign(__ds_scope, { SettingChip });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/SettingChip.jsx", error: String((e && e.message) || e) }); }

// components/docs/StepNumber.jsx
try { (() => {
/** Gold numeral in a small dark circle with a 1px gold ring. One per step. */
function StepNumber({
  n,
  size = 28,
  style
}) {
  return React.createElement('span', {
    style: {
      width: size,
      height: size,
      borderRadius: '50%',
      flex: 'none',
      boxSizing: 'border-box',
      display: 'inline-grid',
      placeItems: 'center',
      background: 'var(--surface-empty)',
      border: '1px solid var(--accent)',
      color: 'var(--text-heading)',
      font: '700 ' + Math.round(size * 0.54) + 'px/1 var(--font-serif)',
      ...style
    }
  }, n);
}
Object.assign(__ds_scope, { StepNumber });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/StepNumber.jsx", error: String((e && e.message) || e) }); }

// components/docs/Step.jsx
try { (() => {
/** One instruction: step number + one action, optional detail below. */
function Step({
  n,
  children,
  detail,
  style
}) {
  return React.createElement('div', {
    style: {
      display: 'flex',
      gap: 12,
      alignItems: 'flex-start',
      ...style
    }
  }, React.createElement(__ds_scope.StepNumber, {
    n,
    style: {
      marginTop: -3
    }
  }), React.createElement('div', {
    style: {
      flex: 1,
      minWidth: 0
    }
  }, React.createElement('div', {
    style: {
      font: '400 15px/22px var(--font-sans)',
      color: 'var(--text-body)',
      textWrap: 'pretty'
    }
  }, children), detail && React.createElement('div', {
    style: {
      marginTop: 8
    }
  }, detail)));
}
Object.assign(__ds_scope, { Step });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/Step.jsx", error: String((e && e.message) || e) }); }

// components/icons/AppIcon.jsx
try { (() => {
const P = {
  menu: [['path', {
    d: 'M3.6 6H20.4M3.6 12H20.4M3.6 18H20.4',
    strokeWidth: 2.88
  }]],
  shield: [['path', {
    d: 'M12 3.6L20.4 6L19.2 15.6L12 21.12L4.8 15.6L3.6 6Z'
  }]],
  chest: [['rect', {
    x: 4.32,
    y: 8.4,
    width: 15.36,
    height: 12
  }], ['path', {
    d: 'M4.32 12.48H19.68'
  }], ['rect', {
    x: 10.56,
    y: 11.52,
    width: 2.88,
    height: 2.88,
    fill: 'currentColor',
    stroke: 'none'
  }]],
  coins: [['circle', {
    cx: 10.8,
    cy: 13.2,
    r: 6.72
  }], ['circle', {
    cx: 13.92,
    cy: 9.6,
    r: 6.72
  }]],
  star: [['path', {
    d: 'M12 2.88L14.88 9.12H21.6L16.08 13.2L18.24 19.68L12 15.84L5.76 19.68L7.92 13.2L2.4 9.12H9.12Z'
  }]],
  hourglass: [['rect', {
    x: 3.6,
    y: 2.88,
    width: 16.8,
    height: 1.92,
    fill: 'currentColor',
    stroke: 'none'
  }], ['rect', {
    x: 3.6,
    y: 19.2,
    width: 16.8,
    height: 1.92,
    fill: 'currentColor',
    stroke: 'none'
  }], ['path', {
    d: 'M5.28 4.8H18.72L12 12L18.72 19.2H5.28L12 12Z'
  }], ['path', {
    d: 'M7.2 18.72H16.8L12 14.88Z',
    fill: 'currentColor',
    stroke: 'none'
  }]]
};
/** The app's own line glyphs (transcribed from DashboardScreen.kt Canvas icons). Colour via currentColor. */
function AppIcon({
  name = 'shield',
  size = 24,
  color,
  style
}) {
  const parts = P[name] || P.shield;
  return React.createElement('svg', {
    width: size,
    height: size,
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 1.92,
    style: {
      color,
      display: 'block',
      flex: 'none',
      ...style
    },
    'aria-hidden': true
  }, parts.map(([t, a], i) => React.createElement(t, {
    key: i,
    ...a
  })));
}
Object.assign(__ds_scope, { AppIcon });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/icons/AppIcon.jsx", error: String((e && e.message) || e) }); }

// components/navigation/BottomNav.jsx
try { (() => {
const DEFAULT_TABS = [
  { id: 'overview', label: 'Overview', icon: 'shield' }, { id: 'storage', label: 'Storage', icon: 'chest' },
  { id: 'exchange', label: 'Exchange', icon: 'coins' }, { id: 'progress', label: 'Progress', icon: 'star' },
  { id: 'timers', label: 'Timers', icon: 'hourglass' }];
/** Five-tab stone-tile bottom navigation. Idle: raised stone tile; Selected: sunken Slot well + gold border. */
function BottomNav({ items = DEFAULT_TABS, value, onChange, style }) {
  return React.createElement('nav', { style: {
    background: 'var(--osrs-dark-stone)', borderTop: '2px solid var(--outer-frame)',
    boxShadow: 'inset 0 1px 0 var(--bevel-hi)',
    height: 68, padding: '6px 8px', gap: 6, boxSizing: 'border-box', display: 'flex', ...style } },
    items.map((it) => {
      const sel = it.id === value; const c = sel ? 'var(--accent)' : 'var(--text-muted)';
      return React.createElement('button', { key: it.id, onClick: () => onChange && onChange(it.id), style: {
        flex: 1, borderRadius: 3, cursor: 'pointer',
        border: '1px solid ' + (sel ? 'var(--accent)' : 'var(--outer-frame)'),
        background: sel ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: sel ? 'var(--shadow-slot)' : 'var(--shadow-panel)',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 3, padding: '4px 0' } },
        React.createElement('span', { style: { width: 22, height: 22, display: 'grid', placeItems: 'center', color: c } },
          React.createElement(__ds_scope.AppIcon, { name: it.icon, size: 20 })),
        React.createElement('span', { style: { font: (sel ? '700' : '600') + ' 10px/13px var(--font-sans)', color: c, textShadow: 'var(--text-shadow-hard)' } }, it.label));
    }));
}
Object.assign(__ds_scope, { DEFAULT_TABS, BottomNav });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/BottomNav.jsx", error: String((e && e.message) || e) }); }

// components/navigation/DrawerItem.jsx
try { (() => {
/** "Jump to Section" row in the side drawer. Selected = recessed Slot well + 1px gold border. */
function DrawerItem({ selected = false, onClick, children, style }) {
  return React.createElement('button', { onClick, style: { display: 'block', width: '100%', textAlign: 'left', cursor: 'pointer',
    borderRadius: 3, padding: '9px 12px', marginBottom: 4,
    border: '1px solid ' + (selected ? 'var(--accent)' : 'transparent'),
    background: selected ? 'var(--surface-empty)' : 'transparent',
    boxShadow: selected ? 'var(--shadow-slot)' : 'none',
    color: selected ? 'var(--accent)' : 'var(--text-body)',
    textShadow: 'var(--text-shadow-hard)',
    font: (selected ? '700' : '500') + ' 13px/18px var(--font-sans)', ...style } }, children);
}
Object.assign(__ds_scope, { DrawerItem });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/DrawerItem.jsx", error: String((e && e.message) || e) }); }

// components/navigation/TopBar.jsx
try { (() => {
/** App top bar: Chiseled stone panel header, hamburger, "Pssst • Tab" serif gold title, relative sync time, chiseled Sync plaque button. */
function TopBar({ title = 'Overview', syncedAgo, onMenu, onSync, actionLabel = 'Sync', style }) {
  return React.createElement('div', { style: {
    backgroundImage: 'var(--stone-grain), linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
    borderBottom: '2px solid var(--outer-frame)',
    boxShadow: 'var(--shadow-panel)', ...style } },
    React.createElement('div', { style: { height: 58, display: 'flex', alignItems: 'center', padding: '0 8px', gap: 6 } },
      React.createElement('button', { onClick: onMenu, 'aria-label': 'Menu', style: {
        width: 38, height: 38, borderRadius: 3, border: '1px solid var(--outer-frame)',
        background: 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: 'var(--shadow-panel)', color: 'var(--accent)', display: 'grid', placeItems: 'center', cursor: 'pointer' } },
        React.createElement(__ds_scope.AppIcon, { name: 'menu' })),
      React.createElement('div', { style: { flex: 1, minWidth: 0, display: 'flex', alignItems: 'center', gap: 8, paddingLeft: 4 } },
        React.createElement('span', { style: { font: '700 17px/22px var(--font-serif)', color: 'var(--text-heading)', textShadow: 'var(--text-shadow-hard)', whiteSpace: 'nowrap' } }, 'Pssst \u2022 ' + title),
        syncedAgo && React.createElement('span', { style: { font: '500 11px/16px var(--font-sans)', color: 'var(--text-muted)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' } }, '(' + syncedAgo + ')')),
      React.createElement('button', { onClick: onSync, style: {
        height: 34, padding: '0 12px', borderRadius: 3,
        border: '1px solid var(--outer-frame)',
        background: 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: 'var(--shadow-panel)', cursor: 'pointer',
        font: '700 13px/16px var(--font-serif)', color: 'var(--osrs-stack-yellow)', textShadow: 'var(--text-shadow-hard)' } }, actionLabel)));
}
Object.assign(__ds_scope, { TopBar });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/TopBar.jsx", error: String((e && e.message) || e) }); }

// components/surfaces/Panel.jsx
try { (() => {
/** Carved-stone panel: Panel Light→Panel Dark gradient + stone grain, 1px outer frame + 3D inner bevel, optional corner rivets. Mirrors Modifier.osrsPanel(). */
function Panel({ padding = 14, radius = 4, rivets = false, as = 'div', style, children, ...rest }) {
  const Tag = as;
  const stud = (pos) => React.createElement('span', { key: pos, style: {
    position: 'absolute', width: 3, height: 3, pointerEvents: 'none',
    top: pos[0] === 't' ? 4 : undefined, bottom: pos[0] === 'b' ? 4 : undefined,
    left: pos[1] === 'l' ? 4 : undefined, right: pos[1] === 'r' ? 4 : undefined,
    background: 'var(--osrs-rivet-hi)', boxShadow: '1px 1px 0 var(--osrs-rivet-lo)'
  } });
  return React.createElement(Tag, { ...rest, style: {
    position: 'relative',
    backgroundImage: 'var(--stone-grain), linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
    border: '1px solid var(--outer-frame)',
    boxShadow: 'var(--shadow-panel)',
    borderRadius: radius,
    padding, boxSizing: 'border-box', color: 'var(--text-body)', ...style } },
    rivets && ['tl', 'tr', 'bl', 'br'].map(stud),
    children);
}
Object.assign(__ds_scope, { Panel });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/surfaces/Panel.jsx", error: String((e && e.message) || e) }); }

// components/controls/SegmentedTabs.jsx
try { (() => {
/** Stone-tile sub-tab bar: raised stone tile when idle, sunken Slot well + gold border when active. Mirrors OsrsSegmentedTabs(). */
function SegmentedTabs({ tabs = [], value, onChange, style }) {
  return React.createElement('div', { style: {
    display: 'flex', gap: 4, padding: 4, borderRadius: 4,
    background: 'var(--osrs-dark-stone)', border: '1px solid var(--outer-frame)',
    boxShadow: 'inset 1px 1px 0 #0E0B08', ...style } },
    tabs.map((t) => {
      const sel = t.id === value;
      const label = t.count > 0 ? t.label + ' (' + t.count + ')' : t.label;
      return React.createElement('button', { key: t.id, onClick: () => onChange && onChange(t.id), style: {
        flex: 1, padding: '7px 4px', borderRadius: 3, cursor: 'pointer',
        border: '1px solid ' + (sel ? 'var(--accent)' : 'var(--outer-frame)'),
        background: sel ? 'var(--surface-empty)' : 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
        boxShadow: sel ? 'var(--shadow-slot)' : 'var(--shadow-panel)',
        color: sel ? 'var(--accent)' : t.count > 0 ? 'var(--status-success)' : 'var(--text-muted)',
        textShadow: 'var(--text-shadow-hard)',
        font: (sel ? '700' : '600') + ' 11px/15px var(--font-sans)' } }, label);
    }));
}
Object.assign(__ds_scope, { SegmentedTabs });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/controls/SegmentedTabs.jsx", error: String((e && e.message) || e) }); }

// components/docs/ConnectionFlow.jsx
try { (() => {
/** Connection-path diagram: stone panels joined by a gold dashed line, a lock glyph on each link. */
function ConnectionFlow({
  nodes = [],
  style
}) {
  const out = [];
  nodes.forEach((n, i) => {
    if (i > 0) out.push(React.createElement('div', {
      key: 'l' + i,
      style: {
        flex: 1,
        minWidth: 36,
        position: 'relative',
        height: 2,
        alignSelf: 'center',
        backgroundImage: 'linear-gradient(90deg,var(--accent) 60%,transparent 0)',
        backgroundSize: '8px 2px'
      }
    }, React.createElement('span', {
      style: {
        position: 'absolute',
        left: '50%',
        top: '50%',
        transform: 'translate(-50%,-50%)',
        padding: 3,
        borderRadius: '50%',
        background: 'var(--surface-page)',
        display: 'grid'
      }
    }, React.createElement(__ds_scope.DocIcon, {
      name: 'lock',
      size: 14,
      color: 'var(--accent)'
    }))));
    out.push(React.createElement(__ds_scope.Panel, {
      key: 'n' + i,
      padding: '10px 12px',
      style: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: 6,
        minWidth: 84,
        flex: 'none'
      }
    }, React.createElement(__ds_scope.DocIcon, {
      name: n.icon || 'monitor',
      size: 22
    }), React.createElement('span', {
      style: {
        font: '700 13px/16px var(--font-serif)',
        color: 'var(--text-heading)',
        textAlign: 'center'
      }
    }, n.label), n.sub && React.createElement('span', {
      style: {
        font: '400 11px/14px var(--font-mono)',
        color: 'var(--text-muted)',
        textAlign: 'center'
      }
    }, n.sub)));
  });
  return React.createElement('div', {
    style: {
      display: 'flex',
      alignItems: 'stretch',
      ...style
    }
  }, out);
}
Object.assign(__ds_scope, { ConnectionFlow });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/docs/ConnectionFlow.jsx", error: String((e && e.message) || e) }); }

// components/surfaces/Slot.jsx
try { (() => {
/** Recessed slot: deep 2px top/left inset shadow + bottom/right rim highlight so it reads as carved into stone. Mirrors Modifier.osrsSlot(). */
function Slot({ hasItem = true, padding = 3, radius = 3, style, children, ...rest }) {
  return React.createElement('div', { ...rest, style: {
    background: hasItem ? 'var(--surface-inset)' : 'var(--surface-empty)',
    border: '1px solid var(--outer-frame)',
    boxShadow: 'var(--shadow-slot)',
    borderRadius: radius,
    padding, boxSizing: 'border-box', position: 'relative', ...style } }, children);
}
Object.assign(__ds_scope, { Slot });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/surfaces/Slot.jsx", error: String((e && e.message) || e) }); }

// components/data/ItemSlot.jsx
try { (() => {
function formatQuantity(q) {
  if (q >= 10000000) return Math.floor(q / 1000000) + 'M';
  if (q >= 100000) return Math.floor(q / 1000) + 'K';
  return String(q);
}
function qtyColor(q) {
  return q >= 10000000 ? 'var(--osrs-stack-green)' : q >= 100000 ? 'var(--osrs-stack-white)' : 'var(--osrs-stack-yellow)';
}
/** Bank / inventory slot: stack count top-left in game stack colours, icon centred, name at the bottom. */
function ItemSlot({
  name,
  quantity,
  iconSrc,
  empty = false,
  size,
  slotNumber,
  style
}) {
  const showQty = !empty && quantity != null && quantity > 1;
  const sh = 'var(--text-shadow-hard)';
  return React.createElement(__ds_scope.Slot, {
    hasItem: !empty,
    style: {
      aspectRatio: '1',
      width: size,
      overflow: 'hidden',
      ...style
    }
  }, !empty && iconSrc && React.createElement('img', {
    src: iconSrc,
    alt: name,
    style: {
      position: 'absolute',
      left: '50%',
      top: '50%',
      width: 36,
      height: 36,
      transform: 'translate(-50%,-50%)',
      imageRendering: 'pixelated',
      objectFit: 'contain'
    }
  }), !empty && !iconSrc && React.createElement('div', {
    style: {
      position: 'absolute',
      inset: 3,
      display: 'grid',
      placeItems: 'center',
      textAlign: 'center',
      font: '600 11px/13px var(--font-sans)',
      color: 'var(--text-body)',
      textShadow: sh,
      overflow: 'hidden'
    }
  }, name), showQty && React.createElement('span', {
    style: {
      position: 'absolute',
      top: 2,
      left: 4,
      font: '700 11px/14px var(--font-mono)',
      color: qtyColor(quantity),
      textShadow: sh
    }
  }, formatQuantity(quantity)), !empty && iconSrc && name && React.createElement('span', {
    style: {
      position: 'absolute',
      left: 2,
      right: 2,
      bottom: 1,
      textAlign: 'center',
      whiteSpace: 'nowrap',
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      font: '500 9px/12px var(--font-sans)',
      color: 'var(--text-muted)',
      textShadow: sh
    }
  }, name), empty && slotNumber != null && React.createElement('span', {
    style: {
      position: 'absolute',
      inset: 0,
      display: 'grid',
      placeItems: 'center',
      font: '400 11px/14px var(--font-sans)',
      color: 'var(--text-muted)',
      opacity: .4
    }
  }, slotNumber));
}
Object.assign(__ds_scope, { formatQuantity, ItemSlot });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/data/ItemSlot.jsx", error: String((e && e.message) || e) }); }

// ds-loader.js
try { (() => {
// Resolves window.__dsReady with the design-system namespace.
// Uses the compiled _ds_bundle.js when present; otherwise evaluates the component sources directly (preview fallback).
(function () {
  var NS = 'PssstDesignSystem_7dc1e9';
  var base = document.currentScript.src.replace(/ds-loader\.js.*$/, '');
  var FILES = ['surfaces/Panel', 'surfaces/Slot', 'icons/AppIcon', 'docs/DocIcon', 'docs/StepNumber', 'controls/Button', 'controls/TextField', 'controls/Checkbox', 'controls/Switch', 'controls/SegmentedTabs', 'controls/FilterChip', 'data/StatusChip', 'data/ProgressBar', 'data/ItemSlot', 'navigation/TopBar', 'navigation/BottomNav', 'navigation/DrawerItem', 'docs/Step', 'docs/SettingChip', 'docs/CodeBlock', 'docs/Callout', 'docs/DocTable', 'docs/ConnectionFlow', 'docs/FanFooter'];
  window.__dsReady = async function () {
    if (window[NS]) return window[NS];
    var ns = {};
    for (var i = 0; i < FILES.length; i++) {
      var src = await (await fetch(base + 'components/' + FILES[i] + '.jsx')).text();
      var names = [],
        m,
        re = /export\s+(?:function|const)\s+(\w+)/g;
      while (m = re.exec(src)) names.push(m[1]);
      var code = src.replace(/^import .*$/gm, '').replace(/export\s+(function|const)/g, '$1') + '\nreturn {' + names.join(',') + '};';
      var keys = Object.keys(ns);
      var out = Function.apply(null, ['React'].concat(keys, [code])).apply(null, [window.React].concat(keys.map(function (k) {
        return ns[k];
      })));
      Object.assign(ns, out);
    }
    window[NS] = ns;
    return ns;
  }();
})();
})(); } catch (e) { __ds_ns.__errors.push({ path: "ds-loader.js", error: String((e && e.message) || e) }); }

// ui_kits/android-app/Dashboard.jsx
try { (() => {
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
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/Dashboard.jsx", error: String((e && e.message) || e) }); }

// ui_kits/android-app/ExchangeScreen.jsx
try { (() => {
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
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/ExchangeScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/android-app/OverviewScreen.jsx
try { (() => {
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
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/OverviewScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/android-app/ProgressScreen.jsx
try { (() => {
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
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/ProgressScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/android-app/SetupScreen.jsx
try { (() => {
function SetupScreen({ onConnect }) {
  const { Panel, TextField, Checkbox, Button } = window.PssstDesignSystem_7dc1e9;
  const D = window.PSSST_DATA;
  const [url, setUrl] = React.useState('');
  const [tok, setTok] = React.useState('');
  const [remember, setRemember] = React.useState(true);
  const [err, setErr] = React.useState(null);
  const loop = /^(https?:\/\/)?(127\.0\.0\.1|localhost)/i.test(url.trim());
  const urlWarn = loop ? "127.0.0.1 is this phone, not your PC. Use your PC's address, such as http://192.168.1.20:8788" : err === 'url' ? 'Enter your server address, such as http://192.168.1.20:8788' : null;
  const go = () => { if (!url.trim()) return setErr('url'); if (!tok.trim()) return setErr('tok'); onConnect(url.trim()); };
  return React.createElement('div', { style: { flex: 1, overflowY: 'auto', padding: 24, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' } },
    React.createElement('h1', { style: PT.headline }, 'Welcome to Pssst'),
    React.createElement('p', { style: { ...PT.bodyMd, textAlign: 'center', margin: '8px 0 28px' } }, 'Connect to the Pssst Companion plugin in RuneLite to see your bank, inventory and farming timers on your phone.'),
    React.createElement(Panel, { padding: 16, rivets: true, style: { width: '100%' } },
      React.createElement(TextField, { label: 'Server URL', placeholder: 'http://192.168.1.20:8788', mono: true, value: url, onChange: (e) => { setUrl(e.target.value); setErr(null); }, supportingText: urlWarn, error: !!urlWarn && !loop }),
      React.createElement('div', { style: { height: 14 } }),
      React.createElement(TextField, { label: 'Sync Token', placeholder: 'Paste your Sync Token', mono: true, value: tok, onChange: (e) => { setTok(e.target.value); setErr(null); }, error: err === 'tok', supportingText: err === 'tok' ? 'Enter the Sync Token from Pssst Companion' : null }),
      React.createElement('div', { style: { marginTop: 12 } }, React.createElement(Checkbox, { checked: remember, onChange: setRemember, label: 'Remember this Sync Token on this device' })),
      React.createElement('div', { style: { height: 24 } }),
      React.createElement(Button, { fullWidth: true, onClick: go }, 'Connect & Start Sync'),
      React.createElement('button', { onClick: () => { setUrl(D.serverUrl); setTok(D.token); }, style: { marginTop: 10, width: '100%', border: 0, background: 'transparent', ...PT.labelSm, cursor: 'pointer', textDecoration: 'underline' } }, '(demo) fill example values')),
    React.createElement('p', { style: { ...PT.bodySm, textAlign: 'center', marginTop: 24, whiteSpace: 'pre-line' } }, 'Find your Sync Token in RuneLite \u2192 Configuration \u2192 Pssst Companion \u2192 Master Sync.\nFor the server, use your PC\'s address on the same Wi-Fi, or its Tailscale (or other VPN) address.'));
}
window.SetupScreen = SetupScreen;
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/SetupScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/android-app/StorageScreen.jsx
try { (() => {
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
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/StorageScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/android-app/TimersScreen.jsx
try { (() => {
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
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/TimersScreen.jsx", error: String((e && e.message) || e) }); }

// ui_kits/android-app/data.js
try { (() => {
window.PSSST_DATA = {
  "serverUrl": "http://192.168.1.20:8788",
  "token": "a3f9c1e07b2d4e88b51c9f0d6e2a7c41",
  "wealth": {
    "total": "412.6M GP",
    "bank": "371.2M GP",
    "inventory": "18.4M GP",
    "potions": "9.8M GP",
    "ge": "13.2M GP"
  },
  "slayer": {
    "task": "Abyssal demons",
    "location": "Catacombs of Kourend",
    "amount": 43,
    "initial": 168,
    "points": 1284,
    "streak": 212
  },
  "misc": {
    "approval": 82,
    "coffer": "5.2M"
  },
  "bank": [
    [
      "Coins",
      48213907
    ],
    [
      "Abyssal whip",
      1
    ],
    [
      "Shark",
      2840
    ],
    [
      "Rune arrow",
      152000
    ],
    [
      "Prayer potion(4)",
      64
    ],
    [
      "Saradomin brew(4)",
      42
    ],
    [
      "Super restore(4)",
      58
    ],
    [
      "Dragon bones",
      1260
    ],
    [
      "Ranarr seed",
      87
    ],
    [
      "Snapdragon seed",
      34
    ],
    [
      "Nature rune",
      12450
    ],
    [
      "Law rune",
      8820
    ],
    [
      "Blood rune",
      23100
    ],
    [
      "Death rune",
      4410
    ],
    [
      "Cannonball",
      38200
    ],
    [
      "Zulrah's scales",
      412000
    ],
    [
      "Amulet of glory(6)",
      3
    ],
    [
      "Ring of dueling(8)",
      5
    ],
    [
      "Teak logs",
      1900
    ],
    [
      "Grimy ranarr weed",
      212
    ],
    [
      "Yew logs",
      3400
    ],
    [
      "Magic logs",
      820
    ],
    [
      "Air rune",
      10500000
    ],
    [
      "Feather",
      6200
    ]
  ],
  "inventory": [
    [
      "Abyssal whip",
      1
    ],
    [
      "Dragon defender",
      1
    ],
    [
      "Shark",
      1
    ],
    [
      "Shark",
      1
    ],
    [
      "Shark",
      1
    ],
    [
      "Prayer potion(4)",
      1
    ],
    [
      "Prayer potion(3)",
      1
    ],
    [
      "Super combat potion(4)",
      1
    ],
    [
      "Coins",
      25400
    ],
    [
      "Law rune",
      212
    ],
    [
      "Air rune",
      1840
    ],
    null,
    null,
    null,
    [
      "Teleport to house",
      14
    ],
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null
  ],
  "potions": [
    [
      "Prayer potion",
      248,
      "7.9K GP"
    ],
    [
      "Super restore",
      196,
      "11.2K GP"
    ],
    [
      "Saradomin brew",
      132,
      "8.6K GP"
    ],
    [
      "Stamina potion",
      61,
      "9.1K GP"
    ],
    [
      "Divine super combat",
      28,
      "14.3K GP"
    ]
  ],
  "ge": [
    {
      "slot": 1,
      "name": "Ranarr seed",
      "state": "BUYING",
      "sold": 34,
      "total": 100,
      "price": "48.2K GP",
      "spent": "1.6M GP",
      "value": "4.8M GP",
      "progress": 0.34
    },
    {
      "slot": 2,
      "name": "Zulrah's scales",
      "state": "SOLD",
      "sold": 20000,
      "total": 20000,
      "price": "172 GP",
      "spent": "3.4M GP",
      "value": "3.4M GP",
      "progress": 1
    },
    {
      "slot": 3,
      "name": "Snapdragon seed",
      "state": "SELLING",
      "sold": 6,
      "total": 20,
      "price": "63.5K GP",
      "spent": "381.0K GP",
      "value": "1.3M GP",
      "progress": 0.3
    },
    {
      "slot": 4,
      "name": "Magic logs",
      "state": "CANCELLED",
      "sold": 0,
      "total": 2000,
      "price": "1.1K GP",
      "spent": "0 GP",
      "value": "2.2M GP",
      "progress": 0
    },
    {
      "slot": 5,
      "empty": true
    },
    {
      "slot": 6,
      "empty": true
    },
    {
      "slot": 7,
      "empty": true
    },
    {
      "slot": 8,
      "empty": true
    }
  ],
  "skills": [
    [
      "Attack",
      99,
      13034431
    ],
    [
      "Hitpoints",
      99,
      14211092
    ],
    [
      "Mining",
      85,
      3317845
    ],
    [
      "Strength",
      99,
      13521004
    ],
    [
      "Agility",
      78,
      1704132
    ],
    [
      "Smithing",
      82,
      2463822
    ],
    [
      "Defence",
      94,
      8212341
    ],
    [
      "Herblore",
      90,
      5346332
    ],
    [
      "Fishing",
      86,
      3758492
    ],
    [
      "Ranged",
      99,
      15012993
    ],
    [
      "Thieving",
      80,
      2061444
    ],
    [
      "Cooking",
      95,
      8932881
    ],
    [
      "Prayer",
      77,
      1521003
    ],
    [
      "Crafting",
      84,
      2981004
    ],
    [
      "Firemaking",
      90,
      5432009
    ],
    [
      "Magic",
      96,
      10143997
    ],
    [
      "Fletching",
      91,
      6011420
    ],
    [
      "Woodcutting",
      88,
      4482013
    ],
    [
      "Runecraft",
      77,
      1498221
    ],
    [
      "Slayer",
      93,
      7321998
    ],
    [
      "Farming",
      92,
      6823991
    ],
    [
      "Construction",
      83,
      2701223
    ],
    [
      "Hunter",
      81,
      2273991
    ]
  ],
  "quests": [
    [
      "Dragon Slayer II",
      "COMPLETED"
    ],
    [
      "Desert Treasure I",
      "COMPLETED"
    ],
    [
      "Song of the Elves",
      "IN_PROGRESS"
    ],
    [
      "Monkey Madness II",
      "COMPLETED"
    ],
    [
      "Sins of the Father",
      "IN_PROGRESS"
    ],
    [
      "Desert Treasure II - The Fallen Empire",
      "NOT_STARTED"
    ],
    [
      "A Kingdom Divided",
      "COMPLETED"
    ]
  ],
  "dailies": [
    {
      "title": "Battlestaves (Zaff)",
      "icon": "Battlestaves (Zaff)",
      "subtitle": "Varrock Diary (Easy or better)",
      "status": "Available",
      "ready": true
    },
    {
      "title": "NMZ herb boxes",
      "icon": "NMZ herb boxes",
      "subtitle": "Up to 15 a day",
      "status": "4/15 claimed today • 812K points",
      "ready": true
    },
    {
      "title": "Bonemeal and slime (Morytania)",
      "icon": "Bonemeal and slime (Morytania)",
      "subtitle": "Morytania Diary (Medium or better)",
      "status": "All 26 collected today",
      "ready": false
    },
    {
      "title": "Bert's sand",
      "icon": "Bert's sand",
      "subtitle": "Requires Hand in the Sand",
      "status": "Delivered today",
      "ready": false
    },
    {
      "title": "Tears of Guthix",
      "icon": "Tears of Guthix",
      "subtitle": "Raw values (meaning unverified)",
      "status": "Countdown: 0 • Quest points check: 284",
      "ready": false,
      "weekly": true
    }
  ],
  "birdhouses": [
    {
      "name": "Verdant Valley (north-east)",
      "type": "Magic birdhouse",
      "icon": "Magic birdhouse",
      "status": "Seeded • ready",
      "chip": "READY"
    },
    {
      "name": "Verdant Valley (south-west)",
      "type": "Magic birdhouse",
      "icon": "Magic birdhouse",
      "status": "Seeded • ready",
      "chip": "READY"
    },
    {
      "name": "Mushroom Meadow (north)",
      "type": "Magic birdhouse",
      "icon": "Magic birdhouse",
      "status": "Seeded • ready in ~12m",
      "chip": "ACTIVE"
    },
    {
      "name": "Mushroom Meadow (south)",
      "type": null,
      "icon": "Empty birdhouse",
      "status": "Built, not seeded",
      "chip": "BUILT"
    }
  ],
  "patches": [
    {
      "label": "Farming Guild Herb",
      "icon": "Ranarr weed",
      "status": "Ranarr weed • Fully grown",
      "grown": true,
      "seen": "Seen 1h 22m ago"
    },
    {
      "label": "Ardougne Herb",
      "icon": "Snapdragon",
      "status": "Snapdragon • Fully grown",
      "grown": true,
      "seen": "Seen 1h 24m ago"
    },
    {
      "label": "Farming Guild Tree",
      "icon": "Magic tree",
      "status": "Magic tree • Stage 7/12",
      "progress": 0.58,
      "timing": "Done in ~2h 14m • Seen 38m ago"
    },
    {
      "label": "Catherby Allotment (North)",
      "icon": "Snape grass",
      "status": "Snape grass • Stage 2/3",
      "progress": 0.66,
      "timing": "Done in ~24m • Seen 1h 2m ago"
    },
    {
      "label": "Tree Gnome Stronghold Fruit tree",
      "icon": "Palm tree",
      "status": "Palm tree • Stage 3/6",
      "progress": 0.4,
      "timing": "Done in ~9h 36m • Seen 2h 41m ago"
    }
  ],
  "payments": [
    {
      "crop": "Magic tree",
      "icon": "Coconut",
      "lvl": 75,
      "req": "25 × Coconut • Tree",
      "ok": true,
      "owned": 140
    },
    {
      "crop": "Palm tree",
      "icon": "Papaya fruit",
      "lvl": 68,
      "req": "15 × Papaya fruit • Fruit tree",
      "ok": false,
      "owned": 6,
      "need": 15
    },
    {
      "crop": "Snape grass",
      "icon": "Jangerberries",
      "lvl": 61,
      "req": "5 × Jangerberries • Allotment",
      "ok": true,
      "owned": 32
    }
  ],
  "icons": {
    "Coins": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADfUlEQVR4Xu2XT4hPURTHf285i9/it3j1evXqt5qFxTSb346NhRULC1JKEkkiahI1hZpIUxNpIoRoakQTmsjkTyY0YVBSUkpKUlJSUhbHfN51evfd937v9x5vsnHrlpk5957POed7zn0ajf+r1uVJdv+z5cm1o35md4dywWsNIB8mChqxk8F223GUb18cQKXlyZfZSH4+78jH25E8mQhkaqwY6NPdKAPT3b7y8uTbowWgZwPyYSaUuYuBTB4pcuDJ5/tRfObVVCjTx30ZG2oV2Fdennx9sAA03y/vb4Xy8HwgF0aKHJiMcubFJZPN0d1F9pWWF5cpLtnTtry7Ecq9M4Gc3t/6rYc8sZoMsecnA7k86svhnc36gNAD0f54HMmb66HMnPDl5HCrQKwmCLaWd2R7UzpLagayNeEKNg3lxVpjU96JQ74c2NqUlcv6qgB1mxeJQCmbaoJ/45CMka3lHTv6BIiO1JJtXFUaKDs37GgViEzhgBIo0MsroRzb05Klg/lABMB92Oxa3ywDlIVJQ5mO+T5nALTD+N3baZMBOiitDy/uRuwp8c1xP24CylYKCH0QPZe4JbCB7A7D/vVVA8iMSesjAeK+O6eMjgCvBETEpBgHWgIFsjtsfF8rLiM/00UAbl7t6sOUjSCARnec09ZPM6SW0QgH0QOHqffaFcaBnSEyQvq5mCA4w5xBU0MbmlZWzb2qI2w4d+5gqbKZZ4GZQcRogoM71iVzIw9INaWipYs0CC2bziICJbMlgVimbNRd25pBpg7IoAu0bU1f7AzRohF+RxAD/UmpFYg7VdjDW0oBscwFOFCdaJbIEFm0gSiRChsdET1tbQPx978Csh3Q2jhVIDKI6MmGph57yozuugGphpjudO/eTX8AlOcAKH3pmby0MKXsZq+lrhXIHZTuwHTt84AIgkbhnJa6BJDpNJ2sONBZ48LoHGEuqebU3u2yPKASz4e5XPWAfigJqY2/Ehe+gVQ7pFyB7LZXe3VkAzGruJPOLfmeGX3ou0SHcVBB7Xa3P0MRuT4L2Gc/vkxJbSDO07k9gFj2Z0eyAbI/Q5lNyejvvfU50kaoANRoaKrtlOsbR+Z4HN2yqJ2rMxU+GlMgnhc6k4FaCih/ZaNOQMzfXRBb+GzKOnu2NqBs5rIXefH/RtAKjhmoafBeQS3KKnboBrTIMGbV5fAXtsBoIGhomLwAAAAASUVORK5CYII=",
    "Abyssal whip": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABvUlEQVR4Xu2Xv26DMBDG4QEyZECKIkVCKIoyVR26dOrQpUOHDn3/V3F7nL7689kGAo6z9JNuiH1wP+6PIU3zr2Jq3bRVkQ/Y7feBvfcn93o8upfDwT11XQKwOGQMwfYx9O7rcnaf52GEezuFgNdfn4JQ7RjAQojZLAAMcPJbAOH73PcloBTIQiEAG8MB7Pt6MQ+wSW1QAs6Kh1A/NgtnHyKMcZMUCP3AIBw0XcqmgU9qb4W0JwTIwuRAYHETF4FSIMkORjn1xCnjayy8lN/vLVY7TglfvBRGTJpZrpXM+my1f6DhPRdJs8M3nAPibGDseY9hVgHxzbh3chC22dkPQ8EnufddpBgI69Lk3B8hnPrwOsPEw7FYqUzoOsqBUtps8XUASh0bYbxZxUCcJZzCNnjOBGgDDBRD8d4tQAVgRAqEs2jusLOGEhaCgTQTgApLp/s5A0RBGMg3shx2YZb0fZUz9issD4VpsR4PkJ+uYbczJQr94rW7KT3yDJcegLtqeuRlAFa+0bdoeuTxPV2538Ixt1D86Vutr8IR16CSndzfoSpQLIDh09dmscL5lJICMUClw3JKD8tGXiVgfgACr1tEhnw7UAAAAABJRU5ErkJggg==",
    "Shark": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADvUlEQVR4Xu2X20tUURSH3X9ADz4E0ZNISEiIhCKKojgoo4yi4o284LUhrcxMzbxkpoldsBArlEoqKCiIgogognro31rx7dPaHo/jeMaRfGnBYs7MnL3Ot39r7b3Xycj4b2mZkW0/cjPSFS2Utsp8aSg74wM7MjgjvbVF0lFVIM0VeRIryZVIQY6U558KwP0zSCMDdcU7VIq3VDsfbIo4j5WdTQB4qKBGLjeXSbyhxKk03tuwy8e66xJ6Z6xMKgpz5UTmscOCMjLaWi7DTaWyMtolS5fOye3h9oQ+f6HVOtfcNz3Y5FTLzT5pgfKzstKFMnKtvVLWpvrl4WSf89XxHrk/1m0h+eQ7v3Pf+vSgvQYKlVqrS6Q4L+fwgJ4vDMvm/JBszMXlycx5+0A++c7v/L+1eFFeLl2W18sj9pP/gUStvsZKiZbkHwaQkTcro7tgVAWc/wB4e++qfFidkE9rU/LuwZgF5H/UQ6WmSJEU5GanW0fGzjgIg1NTfgcAqI+PJq1zjXLcS1q76yskUpSXrkoekB9G6yQI5PfP6zesWqouY1iJ9RWF6QAZKz0z9cPgQQB14N+vjlsgUse1Kkw96RZwwLR5QARVGF1JQRBca4kxwHx5PONSp0WOSrpxHkAlY2GQXmGCSzvRStM6+vp01qlE6rTI22tKDwJkXHECpHtOEEih1HVVMgaFVCVUI3VAo5JuAcGnJjEPiFpghsD4XdXyq7Y9a09Zxn7bmHMFrqlDTTbKFFUyNhCyKxC7rt+XRzqcs6x3PsDYdAHkT50W+PX+xlSAjJWaQN83520gVACCc2o23mJdzy1cg+98gHExiKdApJbdm40yZNq2A/14dkuOZ2a6I4CHc2AyQ2rBP9PEszU2hh+IuiPWcFs0rErGwvx8sSC/X92xRUqNEARlANJ240pnbJ+gRu4O1TsgagggJkYfpRtlcFTAvFn92lq0gZgVQJouVccPkwxoOR6TpcFaB8QKJQ5A2nHuPd6aseoARBD2D4L404U6+8NgxsIsDNQ4ICYHEF0nbXAoIGBYIXpAahBNV1gYQG72Ra3P9FQ7IDpPevNo0elwQKSMpUr96Dmk6QoHgxkLgU91VTmnHaY/pzfnZSEUEKuMjVGXKfsM6oSHwYyDmOiIWOc32mH6c3pzfaXaJ97+QMERic1YCNpffHussYcxbzCkLWRM7+hQIL6nqg4QqIHvHOu9xWjaUgbyBwsx8K95qeHBiSdiXNpCAmGpquI3Ly3Jx3uv5ykAZWTsHSyMJYNRS37PH3yk1V4rBuqPAAAAAElFTkSuQmCC",
    "Rune arrow": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABvklEQVR4XuWST0vDQBDFk2MPHgsei4cePJRSWkSxEJpLERUpWAr+QQQRkR7Eq34AP/XKJE539mXSYDKmBx8smckueb952Sj6P4pdvloTGob1dHHrhsm8LajccJSe/xjGQZ3ePLmjs4Xrj46zftjrlYCbKHaz1WNmeHiSZIaypnX99ulOr1bZCoEkuIlyM1r0Ow4G46BmoOW44ybzy2x5oBx8ME2V1GorB7p4ft8kIGuG6e9HGzgtNT6LX68pnwDW3HOdLO83F1sChck1UpgApiH7+cNrcLERlmAaAukAflLf3318FS62PGsAQwon1OC4JyB5seWeAQhJB9DSqTprCrTNEHttzxRGM9xBOrpBVa/tGcCQytPAHgHwrAGQblDVa3sGMKTyNLBHADxrAKQbVPXanjkMplHV454ZEJrJNNbdPRUAz5rBSHM0ZBh60sJ9w3SKk7Mpv0OYSScqwBilUzSTMFy/dD0YwTAEgjdMxyexnvnJEYwB+F5pMPQ0SYcm5+nZmN8hjAbEQxmkw8oN2Eyayj1+j78IgfDrtSQnk0+5ECaEMEvnNwovf3iv/CAtq/iLdgTitcNftF1/AfMNa+S10tBXqAoAAAAASUVORK5CYII=",
    "Prayer potion(4)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC+ElEQVR4Xt2X/UtTURjHPf9CUCAoWkoMzeF7Kr41X5a4zJluyZKGlk6mss2h7KUswyyUBJPKhDQtk2mITgWhICIiIiLm/p9v95zrPd5d90uwnUEPfBncjZ0P3+f7PGdLS/s/iiBWKS2CB94quPpLYbdcgqnpQqqhCA43LDhY72KamajH2NDlVEIRDrO72oEnwTqMDpYxoMLsbNFQcmYUoO0VM6Z8Nax9KcgTgfVmD2y229ydzaU2PBqvxnBfMRoarsJovCYSiqC3r/+ULNZbMJutaDWZUVNrENk6AufQCNOQSl0WG663d8FgMKKkRA63MCCvd4zL4/HC5fLgRmc3mo0mVFRUQ6fLFwlEiyAU+oi9vX2Ew3vY2Q2zdtXWNfBQUxihQA6HEz6fH8FgkIm2q/5KUypgaBG43R74/QGmQCDA8yO4VUoRDES3mEM+vyzassbGFpSXV4kceVoEdZEV9EU3Y5T3Poic2UFkuDtFuiQd9GUO9mgoBqbw8zPkhSaQOz+MzPFupDvaRLlEYIi8ZUBqUciCnSnkvnQj614PMkY6RLhEUPR1Ht3RDfRIUgPR5wX709AtjyNrsleUS3J2KIxWRd+eQ384C92aHzkzDhEuSe58X0DH0Tp3KAZIek//aRZ5G/eRM+eUXfJakX6nNVkuEZRF3jCYeCr+8YIFO3/rIXIXXDg/fReZfluyXJLdaY2swRL9wHQK6OcrHuyLi6Ns/JPoEkHp79ccJp6Kfy2yYOsPnrJgq11Kd7Yn0iV5suio0/yodQroONh0QXKXJuyySwMJ+8FGWDu0MFpRB3mwpQXJXJKWZBJcIqiMLKPt6F1ccaA/SyfB3p5k4692iV4n53pbEueSFkQrOoE82OHHbPwVl+hOOmut5w4lxCWaIarmyCoXnTpF1EUe7OMcKffaGVNlImGUkn8F0oPptlYDUiD6TAk2u/Wl5ajsoCTAyCV/ofKfS75GFCj6SoOtXB1akITDqOvkgBM4rYSAxCv1wVppP/uv9RcGDVValy6FRgAAAABJRU5ErkJggg==",
    "Prayer potion(3)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC/UlEQVR4Xt2X/UtTURzGPf+CPwRCNk2RoSm+TNKYL23qCqc1yy2ZkrhSh5vN7bKXu5VlmIWSYFKZkG8UYonoVBAKIiIiImL/0NM957a7u7tEgrs76AsPgzF2PjzP8z3bzcv7P4YgXTkdgvvcBfiGDRi0n4O1/WyuoQiONu04fNvLNDvZiqDnfC6hiASzt96Dx7EWBNz1DKi6qEhrKLEzSaCdVRumI00svhz0icBxYwBO503JnXfL3XgYMsLrqoXZfAkWS5eWUARDruEM2R39sNkc6LTa0NRs0jI6gjHPOJNHpl67E1eu9sJksqCuTiy3ZkAcF5Tk93Pw+fy4dr0PHRYrGhqM0OsrtASiQ7C1tY39/QPE4/vY3YuzuJpbzFKpKYymQG63B5EIj1gsxkTjar3YngsYOgQTE37wfJQpGo1K/dE4quQQRHg+TTSytrbLMBgatFx5OgQjo2MIBkMIhcIIh8MsOlpqev8Yja1au0Tg9d5BIMBJm+ZyjbDL0trVk4vYCDoS62mq/fYcVYdPUP4mhtIFL3R3B7SKjaD2x9LxQJv3ULrog25yUAuXhAM+zWfASEBHcyjfmkTpiwkUT7tQGOrLtksEhl/LGTAM6PtLVH2YQ8XOFMqWOZTMjqJ45lY2XSLMBSWIHKj641NU7k5DvxJCyfwYdFNDKOQc2XCJsDjqE68zQNKAhDgrD2ag3+BZsYtnbqOQd2bDJdGdzsTGsaJANZ8XUpsm9Khkzs3KfSbWr6ZLhEVB3VFCpAEJm1fz5ZlUbNYjITap3AX5akGd7A6V4ecrEYgW+/0D6NfCbP1pbPROOj3eo1Z05ER3GJCwfTVfF1PFFnpUthQQY/tT7oLRbvVcUgIoRaGpkwwo/ohdkHTbkrf2KWeb5JAqLpkSa0zyzZIDNSZWpE2T/4TQLcu3NqoJkxzxXyA9uCWxmgZIgeh7rNjCptG4aKGTvckCjDjiFyafuYgERqHoKy02gxFuaSWI6jDySR2QglNKE5C/jfxgpZSf/df5DbahOSIPKqFPAAAAAElFTkSuQmCC",
    "Saradomin brew(4)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACsklEQVR4Xt2X7UtTURzHPf+J/0BgoVSSTFIMa1GmLsSnbRcXPsxcCrGx7U0YRZKhaWZUPpA9mmVhKVuYaOnulm+iF9H/8m2/czzX3bO9Ce7OoB98343dzz6/7znclZX9H8NgT0mHYfnxRcxNnMX9m3UYDdeUGorhz24Av7/18KwttWJp+nwpoZgF82vLwOrCJSxOuTlQRXm5bijRGQn086sfK3NNfH0l6BNDKHQNw8Mjlp39hJf36cl4I7q9Bgzjik4ohnAkkpehoRD6BwYRCPSivcOrc3UM0WiUJxaLZRNHPB7H4OAQ+vqC6O420NzSphcoF0QmmAXyGwF4LnfgnLtJJxANw/r6OhKJJJJJkYHsujo6fVapCUYrUDgcsRnq7w+iq8tfChga0SMViE6Y5lXJYdjP2A3Ryny+HrR62nUeeRoGM+XFj8x1Wz6s1mJhvhr3xit1WmL4vnMmCzBiy/ZWA9Y+1uL50ilMTVZh7M5RXZYY0qaRB7Sz3YCNz3V4+aIGsw+P67LEkNptRCZ9VYCkQ1bIWjJZj+U3LjyaPaHL0kF3ckBkCHRrswHv37kw//QkZqaLbonsuJExBzgAt6QAyR49W6zWYYnsdHIQNQLIzYE+rZ3mxS6yJWEnbQbyYGRSuxd4sRMbdXj9qsayNDlRWQxLWTt7Hr4uKyrQXgsv9uaXeqy8dRXTkrh36KjbgBQwApbFpguSLNElKS3NPKhyyhLjvz5j9h6kAFQ2Akj0iIqtWro7dswpSwdltoAKg5mpNg4kL0g6/qql27cc6xIrAGSHImhZbLogcy3RzT16o8Iy5Igl6pBIwEouHF2YstiyRwRD64pFjzgJI0e8BdKDVTgJJIstYeTpKgKMGPGF8j+Xas3gQPIVRAVxHCZ3Dh9wCKdGC0ihyX2wGvWz/zp/AX48rdGRADpKAAAAAElFTkSuQmCC",
    "Super restore(4)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACw0lEQVR4Xt2X709SURzGPX9EL9pyarHGNJoCCjIQApRMNCiEDM2URjLJ8Eo6fjRbzVWrtbbW1vJF5dYvnXMK2Wr1tz31PXfnernUizY4bH23Z2zAOB8+5zmXS1vb/zEMtWnpMNzLu5BL23E9fgbh4ZOthmL49imOww+TPE/WfVjNOloJxTSYg61LeFT2YiXTz4F6OztlQ6mdEUB7b6PYKHj49rWgTwyJKzNIJmc1OzubE3iw5satlBWBwHmEQuMyoRjmU+m6xBPTiEYTGAtH4Rnyy9w6hsXsEk9Wl8l4Ehcjk/D7Q7DZ1HJLA8rnV7UoSh65nILLsSmMhMJwOt0wm3tkAtEwbG/volr9gkqliv2DCt+uIW9AKzXBSAVaWFhEoVBEuVzmoe3ynRtuBQwNw/KygmKxxFMqlbT+SN4qMQw/g3e4oUJRDW1ZMDiKgQGXzCNPw7DrXcSPQL4mD3tjUMwjuNoh+YRtOufwPbBSA/PaMYvHfTGsdV/AXJcbsXarLEsMe95sHRBBPrMmUOwZQ+qUR5YlhjfOeXz1KxxIH3r+hT2JdcsEMiafLEtqd4wwAuhl/zTun40gZw7KsMTwbjCFiu82N2S0RK+9clzTik2WZroGm2mJ4bMno8HoQ0Bbrhu82E/74rzYTbak2tn3LtXBiBCQKPZdy3izLTF8dN/EoS+nxQj03pXmQKLYZCl7OsAtJToaehepXneozHogIxgBi2Jv9Eb58ddbirfbGmWJ8W9PZaYYgUSoX6LYdIFsoiWGnd+LCaC/gRGQvth6S2mTl1uKnOhrnCUjkBGKoEWxn9un+PEnS/nuEDc0etyiGWqIJfq5oNBJE9HDUcdEsUWPCIau2J5jpkbCiFHvAmlhI5wAEsUWMKI3TYBRR/1A8Z+r1ho9EhDB0EXRCNJwGP0cLXAEZ4wUkD+NfmFjjO/91/kFunYPah8m3l0AAAAASUVORK5CYII=",
    "Dragon bones": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADFklEQVR4Xu1W70uTYRT1+RP80HeR/oB9MCRJkiQxyTDJDM0wM4M0KyIzrcxM08wchmaaaTZr5mpMhuYPFF1OmaySwKCPQn/HiXPf3vb6zo2pY/ODBy7sZe+ee+65595nCQn72EdMoKDFnoDC95UVTIx93CukFJbn5zHndv8jFFe1FFwjIxjo6pIYGxzEpMMB79xcPMgp2F714smD+7hSWoo716vlc8+zdrx72QOnzSaqsZ2byW0VUYHC1bIynDqRjTO5uTiXny/Ebl+rQmNtLTqamtBvtcL+ul/8xbb+WlvDn40N/PD5MO10yvcsJIrEzJUGgsQe19Whs7kZfZ2doqZzeBjv+/ukvVSyuqJC2jtu/yDkokLKkpQEhpHM2uoqFr5MYvTNgChlJNb7vANtDxvQUFODksJCLE5NCdEXra3yW+2sXUMbfZ9nEUuzs1I1zU0lZsfHN5GlMiTD9vJZJ97+qNHgt11BSVIS4MRx0hxDQ1I12xSoOkDodE6O+I7PnplpaSNV1N+NQKVgjxi/o4mb79XLlLFStoVJSFB/X1eQ6lEVvQgSZPA3AfJhoR32bdkLv3cJq189cnAwQS2YkL7QE9KwVIzT9ra7W3zU/bRNvMUCjEFvBRccBK0lWwXH2Tg5TESFqBjHmWvh8oUSXCwqQnFBAc7m5cmayM7MxLH0dBxJTcXRtDQcz8jAyawsaeXdmzf+q2VmYoCSpJSWreFIMwkP4eHmYAJzsAAa3qxoqIjAS0qqZwWcEFbGilixOYwHc2uzZVOfP4l3VhYWNiUMF2YGJigZ16rychyyWKRqtsBcmTkZnznWNPiMyyVTRR9q7+4KCrcqK3EwOVl6v1Xy0NUp8Ro9Nz8xIddI+DsuYoRSIRIouEftcofpKv30+/F7fV3uNv0vDN/ZFqntEzFCiZeYmO3jZxJgSzmlvD700d/Z+TuCkt1EAtaWFtnQXBH0Ju82DkrMCfFK4a6iJ/m3havicEqKTC4/x5wQF+il88VC5kBiosnQEe2gaELJ1uam1smYJzSGZHTETY3QiKMa+4gt/gJ4WFToXQ3smQAAAABJRU5ErkJggg==",
    "Ranarr seed": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABrElEQVR4Xu2WvUsDMRjGm7FDhw4HUhBu6OAgIqJDQSgICgXp4OLi4OAi+P+vkScvT5NL3qRnr+YE+0Dolebjd+/Hk04mJ530Z2VsOkaTsRermb1az+zdZu6eF8vpWFDG3jzOHchqKwPfl7cCdN22NaHMDmL92tiHt8Z93r80LlrN+aR2lIwDeHo/s5sPGXjGAOQIUTIO4vlz4QahEClECWmsDAQZu/0SIEIRiDCVgSAPRaARYaiuB1WCKZseIarBtJdTZ3gV2nmf5Xu/gekRTJ87SAKA9sy/vXhN6DF0Yqw5IpS3fDprCORrQLyGQJrHpPWyL+KJUsvHMy5FPR0CBSDMx9wSDKOnR1yVbvm8qfXDzA5I/13mYI/wxk8jriq1fBwWpyJexRTkYLCelyvrrDcQQEJ3JVD+QErb3Ec8rDPUJVLXO20xUBlGoqNv7gs/7EbWZXnfjrrdkF8kHYkD+NZdKIl4eOP3KwFFhCjBAIT1wf85WifGES83wEGS2kBNMA0Y+TTE/qPNGSRp9dAeAIi05dIQR1ybM1DeIg6ui+Pr11Pxc8VpGBXmX+kb6Lnna64X4N8AAAAASUVORK5CYII=",
    "Snapdragon seed": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABrElEQVR4Xu2WvUsDMRjGm7FDhw4HUhBu6OAgIqJDQSgICgXp4OLi4OAi+P+vkScvT5NL3qRnr+YE+0Dolebjd+/Hk04mJ530Z2VsOkaTsRermb1az+zdZu6eF8vpWFDG3jzOHchqKwPfl7cCdN22NaHMDmL92tiHt8Z93r80LlrN+aR2lIwDeHo/s5sPGXjGAOQIUTIO4vlz4QahEClECWmsDAQZu/0SIEIRiDCVgSAPRaARYaiuB1WCKZseIarBtJdTZ3gV2nmf5Xu/gekRTJ87SAKA9sy/vXhN6DF0Yqw5IpS3fDprCORrQLyGQJrHpPWyL+KJUsvHMy5FPR0CBSDMx9wSDKOnR1yVbvm8qfXDzA5I/13mYI/wxk8jriq1fBwWpyJexRTkYLCelyvrrDcQQEJ3JVD+QErb3Ec8rDPUJVLXO20xUBlGoqNv7gs/7EbWZXnfjrrdkF8kHYkD+NZdKIl4eOP3KwFFhCjBAIT1wf85WifGES83wEGS2kBNMA0Y+TTE/qPNGSRp9dAeAIi05dIQR1ybM1DeIg6ui+Pr11Pxc8VpGBXmX+kb6Lnna64X4N8AAAAASUVORK5CYII=",
    "Nature rune": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADXklEQVR4Xu2V4UpUURSFPY/Qj/77CD2CIElIgklmaIaZGaaZmZlpZmaaZqaYmmmmmZamNTjImCUTBfVau1nnui57TrfxzDgFQRv2Hxnv+fjWuucWFPyfvI+Rg/evTHBYbGVF4mtvJbHxTnY2N+VTLCaft7ZkLx6XL4mEfNvd/dNgwcPXXy7I5tKSxJaXZWt1VbbX10Ko3Q/vLZiG+r63l2+w4GGLU1Oy8nxW1l7My8biYgCVsgQomAIYlrZcqB/JZD6gjEw/GpG58fEQ6M38nLUUQqVM2d2HywSF5x0rLMwVysj44GAa0OtnM6ElLMA0HAFhLBOUe5LHGBm515cGtDA5Ka+mpy0U9sjo0QMX3XKhcrBkpLejQx7e7U0Dmp+Y+OVAvYxT/w22ECHK7kK5p2aYAGjwTo+MDQzIzONRuy4AAGENUWJpz/0dYkS3EGFye9tCZWHJSFtTk/S0t4dAE0ND8nR4OO0QAM4+GbPmaA9wBNS/hTn0DKVHhLirCOWeHjFGWhsb5fb1NnnQ3S2j9/stFFYfAkBEiSUcAQHnxgt7eAkYIXr19eOOD5SRloYGuXWtVfq7umyPsCi4PgDdwtIeAaPi1fYsVMoU4/OIzsiV+nq5ebVF+jo7LRRMIT4sD4E52iNYVLTaGoAQHzoFS4jOC+jS+Vq50dxsi00oDcbDYE1HCjANo+PktYHSo08oOd48L6AL1dVhsQGlwQBFMMZJY1FRaigawtWALqHgXkBnKyrCHqHcmcC0LYLpGDUUgfgtxG3uBXS6rEwu19XZHhFKgwGKYASiKR2jC4UewQ6BUG4voJMlJXLxXI3tEaA0mGuLQOwW30gXikD8/lmgVGweQBgjNZWVtkdYgkXZIpCOLwoKlgCEUtMSYbyAaAmXpAulbRHCtaWhaAlA/MSgS552OEGXcCdpKNcWIVxbGoqfH7xpvI+ysMMxcqK42EaHN45Qri1CaFv6ztKW+LbhTsrSDsfIqdJSGx2gNBihCOG+hYSCJR0bupSDHY6R40VF9l7CNYD4oqCi3kJAudERLEc7nAAKphAfPiuZIiRUlC086xB29AQPQqdcMHd/B6hBDgkTTPCQAAxXwpnycguGra2qst8/QCJabTCPVqJHg/lsXq1kGn3QQev+7z87PwGSoxBEpObWFwAAAABJRU5ErkJggg==",
    "Law rune": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADaElEQVR4Xu2VcS+VYRjGvV+jv6xP0EewWdYsmyxppEnSRJIkkSQRSUxIJBJF1BkzUqbVVp+jb3J3fs/rPnvcHcfrOLW1dW/XXjae57frut77zcr6PxmfQPbXX5nwstjcnKwuvJX1pXeysbwsn2Ix+byyIlurq/JlfV2+bW7+abDw8MWXU7I8MyOx2VlZmZ+XtcWFBNTmh/cOzIf6vrWVabDwsOmREZl7Pi4LLyZlaXo6hIq7BBROAYbULQv1Y3s7E1CBjD7qk4nBwQTQm8kJ51ICKu6U0w5cKijOO5adnS5UIIPd3buAXj8bS7iEAPPhFBDHUkHZmyJMIH33OnYBTQ0Py6vRUQelYLiFfEDgEG7RLQuVhkuBtDc1ycO77buAJoeGHNSRoz8jCTjcIkLKbqHsrSkmBOq+0yYDXV0y9rjfiUvGnwz8dnEq4R4x4hYRbq+tOagDuBRIQ02NtDU2JoCGenrkaW+vuwC37KUq3EN0jWg1XqJ1bsVLT4TsKoWytyeZQOqrq+X29QZ50Noq/fc7HRQiPi5WQOB8QNzTaBWQpwLSMY2QXn39uBEFKpC6qiq5da1eOltaXI8QBwPHU+EQcAoEnB8vcDy1e8hBxZ3S+CJEF8iVykq5ebVOOpqbHRROcTAR+nDqngL50SLAtHMKRXx0CpeILhLQpfPlcqO21hVboTgUMKD4GTDWgrqG9nJM3QKITtEnSs6bFwnoQmlpothAIb3El8YJGL/7PUsmdYg3jy5R8EhAZ4uKEj2i3IDZw5E6ZmO0f2eB9FvINo8EdLqgQC5XVLgeKZQ9PBkUTtkYrXSrA0S5IwGdzMuTi+fKXI+AUjD/YL9ftvTJoOgTxfY/L8QWAYgJpKy42PUIKZi6pTEqlH+xhdJe8fYBRKnVJYWJBKQusSQtlN8tW3gbIVD69gGkS5IuRXRHJ+wSO8mHsm75MHtB6eeHnaT76ADu6ARyIjfXRccbp1DWLQuDtFdA+S7pXmInHdAdnUBO5ee76IDywYCyIMmgcMmPjS6l4Y5OIMdzctxeYg0Qn4Xy30JbdhudgqXpjk4IhVPEx2clVYQK5YOpW5x1CHf8CQ+iUxbMai9AH+SQMOGEh4RgrIQzhYUODJWXlLjvH5BE6zuYQVeSjw8WRRl1JdX4F+0n+7//7PwCkvMnD/mabHoAAAAASUVORK5CYII=",
    "Blood rune": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADUUlEQVR4Xu3V8UpTcRQHcH+P0AMEPkKPIEgSkmCSGZphZoZpZmZmmpnZTFu2Yc7MZTObNXM1NsZsNYyCeq3Tvr/ruZz9uLv+NlcQdOD8uXs/fM+5Z3V1/6vmpejw/ivlvCwVj1Mm8Z5yux9oL5mkL6kUfU2nqZDJ0Ldcjn7k838a5jx85/UGJTc3KbW1RentbcruJFxU/tNHDZOon4VCrWHOw2IrKxR/uUaJV1HajcUcVDEloJAUYGhOy0T92t+vBUpR5MkirYdCLuhddF2n5KKKSek+wPmh8LwT9fXVohSFAoES0NsXq25KaMAkjoFIzA9lvsmiFC0+mCkBbSwv05tIRKMYhrTQEggcGmlht0xUFSkpmh4bo8f3p0tA0XBYo9BICzgGBo8fc4ESh7QwQiy7iTLf6lMOKHBvipbm5mj1aVD32rMl3YxjIHAAlUsPY0RaGOF+NqtRFaSkaGRggKZGR11QeH6eni8s6EZaEggcGqBy6QGn0youPUaIW8Uo8+0epWi4v5/u3hyhR5OTFHw4q1FojA84BgKHBgZdLj0GYow8QuzV9897NihFQ319dOfGMM1OTOg9QmPBgWMgcAxkENorPbl7GlVMisdnMTpF13p76fb1IZoZH9coJIXxob1wElRurIzC+LBTSAmjswJdudhNtwYH9WIzSsJKUhMYbjlSiQIIO4V9wpLjy7MCXersdBcbKAkDSsJMjB+KE8KXh13CgluBzre1uXuE5faDmRA/FIP4vxDX3Ap0tqWFrvb06D1ilIQBxTATIZuXnlHYIz6cAGG5rUCnm5ro8oUuvUdASZhMywSYLU8Fg+TfC8ZmAUIp6mpv13uEZpiZlgnwakYhJYCw1JwSY6xAnBKOpIlimPlys/k8cEoA8ZHELlmmw+XsEm6SREmYCZAtbxb//eBL43tUQTpcik41NurR4YtjlJmWCUHLmyVT4q8NN6nCdLgUnWlu1qMDSsIYZWLQ8jToWyXGhl2qIh0uRScbGvRdwhnA+LxQXl8hUPKAyqSqTIfLQSEpjA9/K34jZJSEcVp41hHSkeU8CDtlwswuB5SQI2Kcch7iwHASzrW2ahi6u6ND//8BidHKBGuYindJmE3XNBW/ki86rM3f/rP1G17HIp9Jxz24AAAAAElFTkSuQmCC",
    "Death rune": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADaUlEQVR4Xu2Wfy+VcRjGPS/BH/3vJfS3zbIsk6XJkkaaJE0kSRJJEpHEhEQiHUXUGTNSptVW78PW+7g71/fpenaf755zfB2ntrbu7dr8mOf+7Lqu5z4yMv5P2seTvfVXxl8WjURkbfGtbCy/k82VFfkUjcrn1VXZXluTLxsb8m1r60+D+Q9fejkjK3NzEp2fl9WFBVlfWgygtj68N2Aa6vv2drrB/IfNjo1J5PmkLL6YluXZWR8q5hKg4BTAILplQ/3Y2UkHlCfjjwZkang4AHozPWVcCqBiThn9hksGhecdzspKFcqT4d7eOKDXzyYClyCA/dzdDURAOJYMyt7kMJ4M3OuKA5oZHZVX4+MGimAahoJzENxCt2yoFFzypLOlRR7e7YwDmh4ZMVAQ3MLyQ5mZAQi+5vd0D24hQpTdhrK3JhkfqPdOhwz19MjE40GjySdDRoDTEGFAELoGIUa4hQh31tcN1D5c8qSprk46mpsDoJG+Pnna328EtwAXBmSL8SJa41as9IgQt4pQ9vaQ8aSxtlZuX2+SB+3tMni/20BBiA9wkL08TIwWAhxiZITo1dePmy5QnjTU1Mita43S3dZmegSh4IAjoL1cO8V4Ea3dPQMVc4rxOUTnyZXqarl5tcEAQXAK8UGJ4AjEWBktwQiF+NApuITonIAuna+UG/X1pthdra1GicBspxir7htfBgChU+gTSo43zwmosqws6BHKDTDCEYxwBKNrumvaLQLx0qNLKLgT0NmSEhMbegRpMO1GmGOJOganEBtg+FmIa+4EdKqwMIgNXdJg9iINBij7d3QLLgGIBxNAKLcT0In8fBMb7hGkwRJBhYluMT4A6f8WEJsDEMaTM8XFxiV0KQxqLzAdIVwiEO4RoQjjBASXKkpLTZdsKFe3AKVd0h/QgHJ0h+N36UJ5uTmUhCKYvTyZ+Oah2ITahzscT47l5gbREQqyF0KJ3KJLjI1Q+3SHEx8dBDB7qX0e+HP99mmoFNzheHI0J0dOFxWZ24T4LldVxUEhPrvwvFn6kGqoFN3heHIkO1sK8vJMp2wwxkihYzYcwACFZx3AHT3+g9Cpk8cLTK9wpy6eqwiEnmlI7RqgNMgBYfzxH+KDoVeIEWAQOkZAgLFrAEujK+GjwVyUVleSjV60l+y//WfnFzHOyhcQXcloAAAAAElFTkSuQmCC",
    "Cannonball": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACMUlEQVR4Xu2WaUoDQRBG7Zt4BO8giIKICC4oiooL7vu+L3HXBDVuUUQU8ZYlr0LFtpkgZCb5YwqKdGaY1Ouvvq5JTU01qvG/w8nfWbFw0tzQID3t7TI+OCgrM9OyNDUps2NjMtzXK11tbdLS1FgpKCcdra1aGJCtxUXZmJ/XXJubVajR/n6FBbrMUE4Guru16N7qqhyur2uy3lleLsAtTExUAsrJ5NCQFj3f39M83d2R4+0tzdTmRgHOoGhnb2enNNbXJw3ltAAqAJFOpSRzdKSflwcHBTgDBA5wfFUGKKctYtcUA+Tm5ESyZ6earEM40odCWdqXEJRTs1LAQO4uzuXh6rKQfA/hSDaAqph/emQkKSin3qCAgTyl05q5TEZerq/1k+8+HGAoxbPmKb91dbW1pUA53SGtiAJ5vb2Vt2xWkzXXuA8UaoUq4Sc7dSUDYdRiIO/3d/Lx+CCfuSddc93UMqhQJYZmLCCkp0AUiMGQrH0oa1+oEvMpESAK+SA+jCX3rX0GFarEqYsFxA75cYoUg/l6zv1SCSgzu6lE6zmtBlMyELvjRykQBQSMZaiSQXEoaBnzLIY6Fj9tM/+EHgqB/BNHy2gXrYrpHz+czpbwqIdwoal5hlbhHQYjL+aEgIi8UhQyMB8uysx4h1Zx3Hk+pn+iIu8pitlMMjh/KAKOb3jdcNR9kARhLJwWslcJYDYM/VcGs4dWlUGVqAj/OxfPCsDkw2/BXxk+W41q/Nv4BhRRpPXhz24mAAAAAElFTkSuQmCC",
    "Zulrah's scales": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACgElEQVR4Xt3Wz0sUYRjA8Z3jHOYwh4W9CCIRHkIkdg8rClJseFgWNtot3BBEIwSjJA8FhqUt/ZA2BIWFDSRIQYhCCIRAhP6xt57n8Zl532feZdyZKaUHvied2Q/v7Dyay/1/4yjqUoyjWrOumpzInaEuHEYgRl0CGIG2er5a71B/AcY3kslxVLNBp6ODoLWtzGCOerG6ai16UxNkQ3HVqmu5PnYctTl3TzWnJqy1Gg1xUwIxRoKWn3kYY8aHh7Hw+tghUHt+Vu2P5I32dnYQVJmeDh4BYCTobddXj9c8zAaSnxgzBDq6ksckSKIkaGPbV6UKpaNGixmBGMUYDkDQ4oKLtXf9IAYxKnMQdPKqEGnhfgsr3xwzUDqISwgijAQd16Og4ugIYtw7TQxQXOVuPlJmIMBwEoSY+UXlPlhShdMuYsbKV/uWGqRjdBRi/iAQo4GCfvVUbmiIOnsbGZMYVJ283jfv9ZsQI0DXvn+0YvoXO/ICS8WS8jsrYdtPEcLpoOcrT9Tu+3fYTHE80rlQfLThERMk+C7ooF4bs4EYw6uC95mOGfAxmpAIqLsRgHQUg+TukpufQfJT+4yjZm651hAEmIQgeGFq5dLgoEfLHrZ3lMc+faPcWt1IohgEb6QE8TpJBTr4aYJu3A6TOEgH6enLNhXo8KRgoBjDfyrqddrCEsUQ3mGpQUsPXQPEwb8XnA6Cup0PqjZVimAkKjVIPyVIR9lAkMToqAQgGHpsX08J9PlHCNJRDOKdJTe7rcQgOCUdJFHwnTJBdJ0sc9CXY8JAL9e9SPLmvIHDTRxFhj879xAIkgBGDHJziYz7fcsQKA6R8OZJx37M/xhhzkUgfgOks4LFUjV2vAAAAABJRU5ErkJggg==",
    "Amulet of glory(6)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACZklEQVR4Xu2X709SYRTHPS97cV/wgs3xyj+hv8maG1lmSkqzZmX4Y2aQTSJJV0RlRd7BnIayHJDJYCmsjVitjdF64dafYPvG8+C94L1Wz1Pey9b6bmewcc9zP/f8uOfQ0fFf/64IvzZbRViLxfB6ZQVbqRTy2QyK+Tw+lEqI3gvhisdjJxhh9cVzqJEInoTnkVx+iVw6jS/VKr7v7+NrrYbsxjoWZmcxMjBgBxhhXVWRWHrKP82patjnSoXDhgMBXLrQbyUY8VSx1LDvJ7u6jjQNYDeX4+md9/sxfL7vuKGI1wtLSfDmtA5kvEpTK5gajcLpcPzWR1KEne23pugYrzoswsdyWdJHWIRMMokz3d0ST0ooFQq8yMV9hEV/8KSE7c1NhG7NWAM0OjQkeTAh/WoNdyYnJf2ERPq7RfxgQiqRwMzYmKSfkEhvXfGDib+Ppq6OSvoJiXDx3FmJgwmFN1k8uhvUfcT8hEXod7ulgFh0AuPjEj5SIvT19Age3ojO0v2wVdFhIjxbXBAG2ojHrWp3TYRycVfvGOOvTR2Mi/pGYGF0mIivGGzSX/N6D6DMxqLIXqAP5+asjI4m4ssYu+lgby88gwoujyi4cV2B3+fjQ5etHBZ2llGEb3t7mJ5y4HagYRO+BhSrFzaz5MfLX4nwOOpE5IEDoaADbmWCm3dY4SliW6StMHHVqUO0GkudVkM2wTDVl/vVo4GYnT51wo4ibhVhK9tpAmk124Hel1wmiLYC1aouFHd+HiWbgZjqf3E+NaAy6U5eU8uxZte1AYipvrhXXHhXaEK1ocOMOjwuNJDjhvkBAE5Tf0mNtcYAAAAASUVORK5CYII=",
    "Ring of dueling(8)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACPUlEQVR4Xu2VMUgcQRSGb0qLLaYYWAYWrrLWwsI2vZAiTRBSWAgRhANFEhASSEKMB4dBDoQDFUUQFUWRiAnHkUCCIJKICCLaCCEEJBDSCBYv/m+Y3XEPbLzZU7gffpa7W3a+e/97b3O5llq6cxJk3DRZAOO2pwG7SWCCZCVMXFTsrsedsTOGqgcKhiXbBerI57OCEqRWdeJlHQNZqIyAkp4pj78jta7ZsqzYQUHG9giUQGzNqtjVhZAWJxU96umJgVwoT0CCPs6rOgPmy1JIO2shbU4rmilKejsSkCyp2B6ABK1XzGHvX0h6PRTQ1BtJy1dV+LpiYPY2QzrY1lRbDKkyJv0D4fDyKwNjgVanFIP8+BDS4SdNR1VNp581f+/GC5gGAgmuwlzJRPFsIAFC/7ggP3c0nX+PuEq41wMMJLhPEANgeh+2MRDiw8EuyL/DiC5P8lwx3O8hKkhwLKjQ4JMEyI0k7bNvmntuuN/e11AJ+rVrYkBDA8iNIm38hukrPr9+b/qpt5C4iqGdjmvmX48O3vTSFLwCUM2XhYAedOd8AEGC+wPRYYIwbaVR0yNp211U6Aso0h6B0Ky/9yKeKsSHzYxK4HBc8RmrYWFCMWw62vQTGyBBfw8i7idMFiYJvYKK4IoVgOphHXiOy0rQxXFEf/YjHnNAYTPvboRcMYChOmhmTKPn6liZXkJ0FgivDewp21t2V3mujivTvJg6xIYtjrjQS25zZ1CdRHbfpF2/kzJWGqApEC21dB/0H50zODzRPyKxAAAAAElFTkSuQmCC",
    "Teak logs": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADFklEQVR4Xu2W20tUURSHPf+JTz33GhSFQ2IRUdEFMqPEilQmMy9JRiENhpkyZIYYJWKUICImhVjJEBEREV0xX/pTVn5rWsd9tmdmqpkaIRf8mDNnz9nr2+t2pqJiwzZs3Vkga1UWyzpvqt0qHQ0JudJcI33teyV96UA5oAJpq6+S7sZq6W3dI+N9x+Re6qiMXjsSAm2urCwWyg97bg1fPSQztxtkKn1SJgdPyMOB4zLRX6dgJQIKZLBrv4a8J7lLT04EknXb9Jr7Iz2H1SkQwMzeOaXievpWvd5nvQRAgbyduiAfZjvl89MeWbjfKE9Gz6gzHFkE+OQ791l3ZWAGlNiy6Wc0/8gCWc4My0hHter5WJPM3z0bgUI4nBs5rWsIcBPfWTMgCryIKAUhDMpMJEMonJgAxDlraHG8OSIDIvUGUxKgrwt96gCnbhQMAmDTywfnQhFNOo16KyI6WBRo6cWgOnAd5xKARJJ0UmcUfqplt9ORv2WrbewCGdSrRy0hmJ8eIkZE6CxaHRBqh5lE/Yz11oap873msECHmMmF+baYDiGsXkzUCQVOrZCeocsH1TGfQHEPGNbdIVkALNATLj27qacj1JyUTkO0vls/nLq/c59u7jrmWRzbOHC70ODdCOaBCuT15HkZbtsp6ZaE3GjeoQ+y6Zf5lG6oUVvZBDGV/dPHQfjFT1daNA0oR6EH8v3NhKbHB3o33abpwmGcbErHzSN3HLCHFbulLy8QqRm9WCNDrQkZSFaFJ+Y+UXo/075mGpsKgdg4sMKnJAoCMWuIEGkzICD8eeQWt03kOBCDAYTuRKwZEOnOC/RprlthrI4Acl8f2vorRe8DuSBxUQGE+kSkFZBfmNzZlymzhpQBdL1xu75c/VmUq/3josIna6TV5hAziX8MeaJjlu000vTxcZcWK+nw51GuwWggXFutEGVaPNveUeWJjmtrH3SBkP8KMQCiYK8Lfw9z7sv3HmvRB6J/Q5YzQ5E6slcFbVwIwPdThK1CkTKrHz8VfxEgzrJQpIbZVCYI3+Kj4f/qn9q6Afnv7QdOvyi1k7vU0wAAAABJRU5ErkJggg==",
    "Grimy ranarr weed": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAB7UlEQVR4Xu2WTWvCQBCGk2MO3nsRSpHioYiUilQsQkolhfSQS1oQigcvHoT+hP7zbcbx1Z3ZDZoPWygOPMiucfdxdnc2QXCJS1CExm3rvl+L0PRHwU6AuSnaxB9IsQzoTV3OIHX453JwV6Yf+2lRKjTdAacfE0JsULRtMPldEjnQ88Pr6zakwr0IBh+mPIEWsr/XtCqkZYiHLBIyWkDTqpBP5r6QtKE+YvPR2YI2aCgkN7GWGeeuEPV9LVjGhvqJmkIsoJcIEo+LyEyXUmiccNsnhP6aQlzM7A2sswKhrRSJgBIhep6ATAUh92inT5Egi11mq8hMUp6UOCakZy0JWe51hig7ycQvRkLxulyIMlkxO6G56gamexuYfB4J9P7RS0YyEEKfj4rZoeDs9IolIyBEtUWcosQVIpkzCAUB/2B3wkaHYidO0g7aM1oI2dJUXC43bDGfEMmA1Xtk1ovThPQ8FYJl7Ap7TAhSPhoKheLusaWwqSEzy6TQ5rNTKlNTSF4PZUKAhMC8EHpdMi+bzp4G2XGvClsoS1xQAuKcgRCk3prtH1cofeaJ87QcFEOCBOgz++ZiiBPIe7JSHF6+tJTOlG/pbCnUKByMGntIXhX61fNUIVvAlqgog5CD1UEL1JCQoQergx7zEv8+fgDT+CPTqE8QewAAAABJRU5ErkJggg==",
    "Yew logs": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAB9ElEQVR4Xu2WvUsDQRDF98otbQUhWIiIhBAMEklAYpMiTZo0giiCIGIlWAiCIP7do7Pnu5ud273sJYcJmIFXJLnc/PbNx50xu9jF1kVGVW0k8uTdQ0ODY0OjU0OTnqHpwG4CKvMg5mNLs2EuAPU6nXWhtO1xcdLFpXUgWi0B5Un4tCw+OTsgXeDTc0IGiakloB+YE+s061q6viqlE0LyGnkdgA72zK+bK0VG876lr+ex0+00nCwEogUgdnQNl7ICBkAaSgq/a0kgwLQCxGXTUDp5SOwg99ma7nDEgVLEwLJ/eAjKiWwU5RhLIBb3lE4cg8DuwXS6Ce0Y6u43auysWGSsFBiUT4NAuBf2kVySS8AymhwZl9hbavy5Xx197KZYYrmbZM9p+BqofO98PF7Q2/2QXu/OixsCRp48BhJbB7Gy1jR6RoszS59PI3p/8IGwID3nlEIQGqYxEBYhA7FLSIKS6bKlKNRzyUCckIFQNrgTa26dvA5kZSAumQSSj49UIA0h5Xpy6C/KKJDrk37e2Fy2l5tBY4c0gLwOI4+3hhp3EOUTHk3MqyAGpKH09ygNKx9vXzXuyKj+UQKxYg5ICH0PJNfS2YPh/8F/DcGiBEwxiQpCJ05OnhYllIRZBqHv0nLkUHBlQxA6wm7oq/40tgbk38c3NzR5UleITQgAAAAASUVORK5CYII=",
    "Magic logs": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADBUlEQVR4Xu2WXWsTYRCFu/+kFyJBpARpKRFFqCipWKhSq7RGYj+xaW1CbIjRUL+CVgoVJQiKiCCKXoigv8AbQcFiCTHGtDTqP/AiF7kYPbOZzbtvPtukxIsMHJJuk91nz8ycTUdHu9r135ZBRbWsihBDA13kGe6hae/+nYKqdlITYrB/L0NcnDxI4cAJVnB2gP/n7Oys8v0tl0Hv3obL3KkJMjrUTZd8fRQNnaJ87hW/Qk0GUmegvC77j9DNyJkSXQsPMxiA/DPHmwFksO2hucO0EvPS8q1zrKXrHlY+955uL47aFIuOWAIUHFqYH2wGkEE3wv2UTH+n1WSC7t0ZYygBE6hKAhyArgRPWkC+CXfB1W2VQZnsJqXWM+R4FmcgHUqVfhwOoWUAwoBjvsShbbpk0MbPLO15+YiBHtwdL4GC5JgqOOQbO8Crfn6kl4FODzobb1n29y8LCHMEKBVM/laFLcPcAWjirIuB4E5TgNih108YaPfjFYovT/JF8SrChkEADkwfsmDgDoCkXQhI+3bWXcUv/djcsID8U90UvNBjAaggcAUwKoi0StwBUO7PC34vbtUBZvCJRaHZXoYADFQJBK6oILpwvJxqPE5MGGxVIp2iD58+8toKUCUQVerNiPBZSJIbW5fPveEoqJHeBl0NHqX0xro1yACSkNNbhc+qWlxw24QMkzySkEQMCFRdQNGAy5obQAkMLoC7VS+mKhY5VhIHaoDqUACq4/lm8MzIZgkQWvEtk6YviTXaFV9id9Q11zNIlQ61ZSDMC9JZgHASzAza6Hj+kBxP79vyqBaUDoRnn7StLqC58X081Ny2AlBk3mWDxIBKHtUSBhkClLQNQIgDd5/DgqkAhDLbhi1bSyVZU56uYh79cwmzJKGoA5QTnAIQWg8Qc81N1YCRMp2a8TpZuCvevAKQJLYquKAfE+EGykHUCSNl/wGGoYZLiAJA6hcVqTkFNQhhL/sJDP5N9PnrKrunB6VIv5GGIaqX2Ur1eVYNYAdB1Go5QGm1HKBdhfoL8we2iN3VW0YAAAAASUVORK5CYII=",
    "Air rune": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADdElEQVR4Xu2WgUpUQRiFnTfxEXoEQZKQBJPM0AwzM0wzMzPTzMw0zczF1EwzzbQ0rUURzRKjoN5D6D3+9vy3c/kdrut13YKgHw7cXdiZj3POzN2cnP+T9XFysP7KBJsl5+dlbfGtbCy/k82VFfmUTMrn1VXZXluTLxsb8m1r60+DBYsvvZyWldlZSc7NyerCgqwvLYZQWx/eK5iF+r69nW2wYLGZ0VGZfz4hiy+mZHlmJoBKuQQoOAUwiG75UD92drIB5WTs0YBMDg+HQG+mJtWlECrllOo3XDoorHcsNzdTKCfDvb17gF4/Gw9dggBm4QgIx9JB+TvFGCcD97r2AE2PjMirsTGFIhjcggD3c3dXBTgIbqFbPlQGLjnpbGmRh3c79wBNJRIKBcEtwBGQMBTdg1uIEGX3ofxd00wA1HunQ4Z6emT88aBq4smQinDcHHA+EET3ECPcQoQ76+sKdQiXnDTV1UlHc3MIlOjrk6f9/Sq4RUC7uQW0YrTqVqr0iBB3FaH83SPGSWNtrdy+3iQP2ttl8H63QkHchICA43d0zweiEC1iZITo1dePm3GgnDTU1Mita43S3damPYJQcLsBugXZ7+ieDwOxewqVcorxxYjOyZXqarl5tUG6WlsVCk4hPshuQvf4Oco5igcC8aFTcAnRxQK6dL5SbtTXa7EJhUUJ5kPxGY4BCvKBeG2gU+gTSo6TFwvoQnl5WGxAQVwYUBA/I04+w62oKCH0iw7h5KFLKHgsoLMlJWGPUG6AcWHrGGQdg1t+jBS6RSC+C3GbxwI6XVQkl6uqtEeEsosDCsKzdQvFh6Kg0Cv0iLc6gFDuWEAnCwrk4rkK7RGgLBhjtJv5Efon0gLxBlegVGwxgDBOKkpLtUcQwXy3WHoL5peewukDEEpNlwgTC4gu4ZK0UNxgv25ZtyiePgDxHYguxXSHE3QJd5KFsm7ZTX23KMTH1w9OGu+jQ7jDcXIiP1+jw4kjVDq3/G4hPpac/xpw2nAnHdIdjpNThYUaHaAsmHXK7xbE+FByAvEmz8AdjpPjeXl6L+EaQHxRUFGnEPGx5Dx5BMvQHU4ABacQH14r+0VooSwY3cJaR3DHTrAQOuWD+doP0IIcESaYYJEADFfCmeJiBYMqy8r0/QdIRGsdzKIr0WPB4iirrqQbu9FB8n/7z84vGAHrextMi0gAAAAASUVORK5CYII=",
    "Feather": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABk0lEQVR4Xs3W4W6CMBAAYPom/trvPbRmbnMjGIJDCQSZSDBDlyx7nZvXo1IqbMliDy65SAjKl7v2rONYDQFNDhqESN9WkG3WsE+SIVECdlEERZpKCCai8P79ZMKNEhKi5zYMIQmCIUDUpjLbXtK5cyD2fdh4HjdIwPfpCF9VJV98eM8kJlouYe26sFosOEECPg8HOJUlHIsCqn0OH/lOgpodRhgGEL1MIbAyqlXh6wsEz08tkPntGwetF1y4rWsN4z/OwZvNwJ1Oa5i1aAC4rfM4linbdE4dg8nQMtreCoNzR84aDaOn5ZbRmujCmAtZXz+WQIJaU38qDA4/VR2zQharQwgFMauDs8YEWaxOG4OJ91R1dIyelqpzjUGIjulLq6AujFkd1arl/EGmJRAGHSn+g7EG0teM2lUmiA1zmcTn6z5MF8j8pRtF0y69On9hLIIwqDJ4Rv5tIjNhMAQdQ40/TxPBhFEhWhhsD9PRoisI09cq5spg1KCO9gyH0UDmE8wxKtA1ZnQg8wnmGB0Ig3dr/wCuyvwyXKfBQwAAAABJRU5ErkJggg==",
    "Dragon defender": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACm0lEQVR42u3X32tSYRgHcN9Khgd/7OjczOZmbY3oIroTZGMyGzjBsYnOoQVbs9k22yyHLKahQ1fBYN2Mfl00IlgRu9ldBEFddNc/0D/z7T3v8Zi/0l341rnowHPjQd/Ped7ned6jRvP/IpBDJZC4VYc5UfcvUTJk02bAPasBCxQjoaTPrjud4LBY6/tFuwFZGikKWqEQCaVgOgwiGHW52qSeIBYOY8smgzbLKG7ZGXe7W/y4vFVzMzMIBQLIl7PEKTvtQIQhZv1+3IpEsLy8zmArfGuHYGJsrMkCBNNTPswHgyzi8SQCgRCU73CpHenppUUnPR78nitySPeUiMWW4PfP1kA6jjncSOJtah3vHmzgKH2fbYsSElKJYHAePt8035rZX1rAbe9ETUZ+XrPjwCniMS3arXIXSVnxeCZ5YmRQLhKuW4TgS6mAH1dtsIoiCwnkco3yxxwk7jRZhOBTPlfBKCCHw8mrm2pR9ZjnAyacZB82gK6MjPwNkEZTjXlBMXsOEz0aTKzIX60lWI2VbkarJniz4JCpo4si3ji78WygG4XzRmT6DLhr1tUsLLW8EtHoYiU6jCJ4f8lSwRT7jUj2CrBrz1RtEcH3p7tsGCptXx0dLHSCiFlgmH2Kydv1WLUI6D1HGjrva2kHXq+vYSB2FFOgtaJgchSToJjLXWcbMErncR2IxX4TDgdlTKZPj0VRaLIFBN92i/i884h1HrdD9AnFHA9ZsHfBhBTFTBm7mpxNBB8zaV61Ugs6Ge5hmG16LNyow/xpTnHDZGlLvx4UkbYJGNdr2z45x0EoY15SzGqPALeg5X02tcZsU8zxsJn+XRF418TpQB+GzAiVMRxr4pSTmGJUkBUZs2bVqyErrV4xVHCpCqOG6xf40wBnxsoxwAAAAABJRU5ErkJggg==",
    "Super combat potion(4)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACRElEQVR4Xt2XTWsTQRzGM6c9LPQgtFBooR9AxIOlFCu11Qab2obU2rXZ1k3bBCkGsXnrbii59AVBKBS8iF/AgyBEPJhA8SB+rcednZ3ZVyLC7iz4h4eEZcn++M0zwyaX+z+GIJhMh+D6/BHOThbQPJrFYfl21lAEv7/v4ldfd/Lp6gnenS5mCUUEzM+vL/DxfR4X1gMH6O7MjGwo1hkOdPNFw4dLtnwZ9Ilg/6CGavWVsPPj87bo00ZxC5ubmkwogtf1N5FUKlXouoHn2zpWVzdkLh1Bo9FCs2mn1Uar1UG73YFhHGJn5yXW15/h4dKKXCDTtGBZXVjdrvPdNE3s7e2jVNLweKWAubn7MoHoEPT73zAYDDEcDu3PAfTdCgqFoig1hZEKdHzcQK/XEymXDaw9LWUBQ4cumxkBov2RvFR8CKY7agCILhnd8kvLeZlbng7BVF3FdDOYcU3BrTUFY4uKTEsEkwdRGHpNAC0rUOdzsizF26FAE7qC8SIDkmSJ2Zl6OwJoy7aUZ0ASLMXbEUCG6gGlb8m2U4u34wDVXCC3RxIs2UBHDCgOigL5i52yJddO3QMKhwPxYvPdlpKloJ24BIBoj3yWOFBCltydNcJOAMgtdsTSQmKWvDKPCjUogDQPKAVLf18uAeTbacKSr9zqvQQthQHC4UCiRz5LjiEXJqHXEnYgjkoAiB+QHOZOojB82FtgGCQAxc8iDuOeQSnAsGE/yP9zReGoJQ4TBkkcxj/eAzy4cKSAxI3/weGE7/3X+QOUuYMFmJekzAAAAABJRU5ErkJggg==",
    "Teleport to house": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACbklEQVR4Xu3X2S4EQRQGYP0arsQTuJMIIYQgCGKLLYxB7Pu+L2MbwyRCJF7DtTfxLEf+ktM5fXSPqu6+kajkj6Fr+eZU9fQoK/tviZpH6SSV5tHY4CCN9PebDPX20kB3t0lfZ6dJT3s7dbW1mnS0tJi0NjVRc0ODSWNdnUkKKI8mh4dpanTEz/T4mAle4xoCMKMBZjSwjASsvqYmCcrzF5YQZHZiwmQ+k/GD37m/xAGGCiZEebSYzfoLlIKUV376kTCuHGASha2rra52QXm0sbDggyREYiREh2G8rRKFs+WICgdFQdBPRsO4WozCmQKKQVUVFb+hgqAojIboyL6MwpnCQcfWxQLJbQqD6Grp69wH4/mw89bx4bYCrc7N+aAwjEZERfbFXLx1qJITiCskt8oVo8fI84QqxQaFTS7z8f4eiL4ux/DWAcSf3FagraXFwERRGH406GiQ3jocbmeQTXWwOFeGX2tQ2Hhn0M7KMi3PzEROKEEyYSA9Fm8UIPRzAvGdJifWIJtIEH4ChDvNGYSDbVOlUtFjYoP21lbNOUpaJV0dBIcaIDw+rEEH6+upVEm/mdigo83NklUqBZPXdXVig062t39USQP0wjpRffFpDRAesNags93dH1UCimFywTCI/psG4bZ3Al3u7weqBBQqhQAXBrQNg/C9yBqUPz8LVAkBjHFJgHiWAYSvINagt2KRbk9PTJWAQlAtJCmQv9YyxgKE5tHrQ8Ggro+PKHd4YIKtRFA9BGBGu4AdMdy+US/3eXq+u6WnmxsqXl2ZPOZyVLi4MFsLtAs8Joab/lc4eRJgvhtPkGb0Gn+ifQGaQg4dhjQTUAAAAABJRU5ErkJggg==",
    "Prayer potion": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC+ElEQVR4Xt2X/UtTURjHPf9CUCAoWkoMzeF7Kr41X5a4zJluyZKGlk6mss2h7KUswyyUBJPKhDQtk2mITgWhICIiIiLm/p9v95zrPd5d90uwnUEPfBncjZ0P3+f7PGdLS/s/iiBWKS2CB94quPpLYbdcgqnpQqqhCA43LDhY72KamajH2NDlVEIRDrO72oEnwTqMDpYxoMLsbNFQcmYUoO0VM6Z8Nax9KcgTgfVmD2y229ydzaU2PBqvxnBfMRoarsJovCYSiqC3r/+ULNZbMJutaDWZUVNrENk6AufQCNOQSl0WG663d8FgMKKkRA63MCCvd4zL4/HC5fLgRmc3mo0mVFRUQ6fLFwlEiyAU+oi9vX2Ew3vY2Q2zdtXWNfBQUxihQA6HEz6fH8FgkIm2q/5KUypgaBG43R74/QGmQCDA8yO4VUoRDES3mEM+vyzassbGFpSXV4kceVoEdZEV9EU3Y5T3Poic2UFkuDtFuiQd9GUO9mgoBqbw8zPkhSaQOz+MzPFupDvaRLlEYIi8ZUBqUciCnSnkvnQj614PMkY6RLhEUPR1Ht3RDfRIUgPR5wX709AtjyNrsleUS3J2KIxWRd+eQ384C92aHzkzDhEuSe58X0DH0Tp3KAZIek//aRZ5G/eRM+eUXfJakX6nNVkuEZRF3jCYeCr+8YIFO3/rIXIXXDg/fReZfluyXJLdaY2swRL9wHQK6OcrHuyLi6Ns/JPoEkHp79ccJp6Kfy2yYOsPnrJgq11Kd7Yn0iV5suio0/yodQroONh0QXKXJuyySwMJ+8FGWDu0MFpRB3mwpQXJXJKWZBJcIqiMLKPt6F1ccaA/SyfB3p5k4692iV4n53pbEueSFkQrOoE82OHHbPwVl+hOOmut5w4lxCWaIarmyCoXnTpF1EUe7OMcKffaGVNlImGUkn8F0oPptlYDUiD6TAk2u/Wl5ajsoCTAyCV/ofKfS75GFCj6SoOtXB1akITDqOvkgBM4rYSAxCv1wVppP/uv9RcGDVValy6FRgAAAABJRU5ErkJggg==",
    "Super restore": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACw0lEQVR4Xt2X709SURzGPX9EL9pyarHGNJoCCjIQApRMNCiEDM2URjLJ8Eo6fjRbzVWrtbbW1vJF5dYvnXMK2Wr1tz31PXfnernUizY4bH23Z2zAOB8+5zmXS1vb/zEMtWnpMNzLu5BL23E9fgbh4ZOthmL49imOww+TPE/WfVjNOloJxTSYg61LeFT2YiXTz4F6OztlQ6mdEUB7b6PYKHj49rWgTwyJKzNIJmc1OzubE3iw5satlBWBwHmEQuMyoRjmU+m6xBPTiEYTGAtH4Rnyy9w6hsXsEk9Wl8l4Ehcjk/D7Q7DZ1HJLA8rnV7UoSh65nILLsSmMhMJwOt0wm3tkAtEwbG/volr9gkqliv2DCt+uIW9AKzXBSAVaWFhEoVBEuVzmoe3ynRtuBQwNw/KygmKxxFMqlbT+SN4qMQw/g3e4oUJRDW1ZMDiKgQGXzCNPw7DrXcSPQL4mD3tjUMwjuNoh+YRtOufwPbBSA/PaMYvHfTGsdV/AXJcbsXarLEsMe95sHRBBPrMmUOwZQ+qUR5YlhjfOeXz1KxxIH3r+hT2JdcsEMiafLEtqd4wwAuhl/zTun40gZw7KsMTwbjCFiu82N2S0RK+9clzTik2WZroGm2mJ4bMno8HoQ0Bbrhu82E/74rzYTbak2tn3LtXBiBCQKPZdy3izLTF8dN/EoS+nxQj03pXmQKLYZCl7OsAtJToaehepXneozHogIxgBi2Jv9Eb58ddbirfbGmWJ8W9PZaYYgUSoX6LYdIFsoiWGnd+LCaC/gRGQvth6S2mTl1uKnOhrnCUjkBGKoEWxn9un+PEnS/nuEDc0etyiGWqIJfq5oNBJE9HDUcdEsUWPCIau2J5jpkbCiFHvAmlhI5wAEsUWMKI3TYBRR/1A8Z+r1ho9EhDB0EXRCNJwGP0cLXAEZ4wUkD+NfmFjjO/91/kFunYPah8m3l0AAAAASUVORK5CYII=",
    "Saradomin brew": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACsklEQVR4Xt2X7UtTURzHPf+J/0BgoVSSTFIMa1GmLsSnbRcXPsxcCrGx7U0YRZKhaWZUPpA9mmVhKVuYaOnulm+iF9H/8m2/czzX3bO9Ce7OoB98343dzz6/7znclZX9H8NgT0mHYfnxRcxNnMX9m3UYDdeUGorhz24Av7/18KwttWJp+nwpoZgF82vLwOrCJSxOuTlQRXm5bijRGQn086sfK3NNfH0l6BNDKHQNw8Mjlp39hJf36cl4I7q9Bgzjik4ohnAkkpehoRD6BwYRCPSivcOrc3UM0WiUJxaLZRNHPB7H4OAQ+vqC6O420NzSphcoF0QmmAXyGwF4LnfgnLtJJxANw/r6OhKJJJJJkYHsujo6fVapCUYrUDgcsRnq7w+iq8tfChga0SMViE6Y5lXJYdjP2A3Ryny+HrR62nUeeRoGM+XFj8x1Wz6s1mJhvhr3xit1WmL4vnMmCzBiy/ZWA9Y+1uL50ilMTVZh7M5RXZYY0qaRB7Sz3YCNz3V4+aIGsw+P67LEkNptRCZ9VYCkQ1bIWjJZj+U3LjyaPaHL0kF3ckBkCHRrswHv37kw//QkZqaLbonsuJExBzgAt6QAyR49W6zWYYnsdHIQNQLIzYE+rZ3mxS6yJWEnbQbyYGRSuxd4sRMbdXj9qsayNDlRWQxLWTt7Hr4uKyrQXgsv9uaXeqy8dRXTkrh36KjbgBQwApbFpguSLNElKS3NPKhyyhLjvz5j9h6kAFQ2Akj0iIqtWro7dswpSwdltoAKg5mpNg4kL0g6/qql27cc6xIrAGSHImhZbLogcy3RzT16o8Iy5Igl6pBIwEouHF2YstiyRwRD64pFjzgJI0e8BdKDVTgJJIstYeTpKgKMGPGF8j+Xas3gQPIVRAVxHCZ3Dh9wCKdGC0ihyX2wGvWz/zp/AX48rdGRADpKAAAAAElFTkSuQmCC",
    "Stamina potion": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACs0lEQVR4Xt2X/WtSURjHd/6MoGi9MGSrcG5OkzlNnc50meVLorWhtVc31IniS1sxRlERRETEiN5gxCKiaUF/3beeczl317P2Q6BH6IHvT1e8Hz/P9x6uAwP/xzB0pq/D8KDsQGFhHPOJCwhPn+03FMOvzwn83IvzPN1yo5K39ROK6TAHH2/gcdOFjWUrBzIPDqqG0jojgL69j2Kn5uTr60OfGJK3biOdntPtfNm9hu3qJNZyFni9MwgEZlVCMWRzC0eSSGYQjSYRCkfhnPKoXB3Dan6dJ29IPJFG5HocHk8AY2NauZUBlcsVPaVSGYVCCTdjKfgDYdjtkzCZRlQC0TDs739Fu/0DrVYb3w9afF1TLq9eaoJRCrS0tIparY5ms8lD63Jfme4HDA1DsVhCvd7gaTQaen8Ur0oMw95WmBuq1bXQyny+ICYmHCofeRqG12U/Pm2GOlJOWZENXUTEeV6lJYZHyy58uN8Js7PoRDVjw2LEjJhrCEH7GVWWGN5U/EeACLI5fxkr0VGkfCZVlhierLjwrnGVAxlDQJvZP68icQsy/mFVlrTuyDAUAt2+N4lScpz3KOEZQthxrpeWGJ7l3XhbC3JDsiW6Jnp0d/aSbslvPd0rSwyvNnw6jDFGoNodOy/2XHCkl5Y0O1RmGUbk+bpHL/ZabFS3ROXugSWGFwUvX5fIcUAPc1qxe2hJO3eozEYgGYyARbHpgCRLVG5xBMzYumaJ8V+/Ww3wyEAiL4vejmIbLRmPgC5Y0sosgI4DMwJRj+jxp0PSaMljOdU9SzKQDEXQokd0QApLVG7qkct8UjfUFUvUIQo9aSJGOLom94hgaF224RPdhBGjvQWSCRlOBqJ1EYx4unoAo432heI/V6c1AUQwVGQZpOswxjm8wSGcHCUgfxvjjeXIn/3X+Q3UkgpKDqFjDgAAAABJRU5ErkJggg==",
    "Divine super combat": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC1ElEQVR42uWXTWsTQRzGs6ccFkQKFQMt9AOIeFCKqNRGG5rENqSuic22Jm0TpDSITWLdBAmIr1DxDRGCeNCTB0GIeDAB8SB+rcdM19mdmZ3dTXZTLwYesmyGmd88/5eZRCL/z0fBP16MlfP3xUIOY4TymkjBq4eX8ODuedS3z2CrcEKysA10amYGoWG8d6fg97c1/OrqB3r3fBFP780J4xVUn9wfl0t+u1MsmJ9frqOzn8Cj5gVhvDlHSJfsnGAn4/PEfKZAPz7n8eaxGT5xHAsTAMiGICJ2U8tZuI3NCsrlm5Y73z/lrHxazmhYWclzm3K6M1L4/BxSsFO9xelFp4NSqQxdL+JaTkcyucxAiO4EqDw6kWg3fV+rNVCvD9S4g0ZjD52PH1AsbmF19QaWlq7i4vwC54roTsCccrfbMJpoNltotloHz4ZhYH19A9lsHpcXUpidPecSJtv59vu3Hv3LB8a5EwXd7lf0en30+/3Bdw/6WgmpVMZaRBYmVgRohJbgX/K7uzW0221LhUIR6StZz02wDhEYObxLUsvLnQ2b4QAi+eOVG9656VP2tOSpxA48vadyQCRkpOTn4wn4HTkjJrXcIbbKpqoqpuuMBnCT+Sgm0lEcmYsOsdiIjVLsHyxMbFOAGYi8Y4HUsxGHo6JrgY4RCsFO7HBHBIrbLlHx1RTqkOVDJ3OHAh3To5jM2EDHk0et/KMlLs/HEEAyd0SgiYTTJfEICnDQOs+zWEUOwwFpPBDNpRHKfPgTP/dSR+61qanbEqCiagKlbSgZUOi7MplMe2ZCyBTbdgdiXQp4H5J3VbLoUED5v3lEoQSgsVzytX13dyygigtQ3K0vhQDyckcKpNlQsrCFBvKCEYFopUldOj1Gl0j/seQCZJU+61KChwmd2K5QDBwHxObR4cH4QFVtKBmMenKsVeben0QoFkb8l3II7vD9yb4FyCVeW4LA/AHvC65v8gbviAAAAABJRU5ErkJggg==",
    "Battlestaves (Zaff)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABGElEQVR4Xu2WsQrCQAyGzXhDh75BH8HFwU1wKSKCi4IiFFREpCBSUMHBQXAQBAdBcBB80UgulGuLq02E/pDtDj7+P3dJrVapUqW/EyCXCgFGocHdyNMABRg2DM46Bo8RA9WDQBIKsNs0eJh4eJ6LAqU9AzhumxyMABDgsmfwuvItCEV1i30pdwAHLe6X997H19aVGFAydI5kSyAujuqyyDuSreKNHwtsVPd1HqZ4qiSxOxSVYL9kBfZVPTZK3KGPj6JS4I6L6pmIu+M+QAVRAcZ9z8IoeFVgp/dpquKJ82igJiZ3xGFot6HRIDy9U3ETU1RZd4qnShLvNTQaaFYJu8NR0U6sxh1a0qmRFbiTSnSv+a4URAWMtD5aNPEC+fhzagAAAABJRU5ErkJggg==",
    "NMZ herb boxes": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACZElEQVR4XuXWz6dUYRgH8DnLs3gXZ3E4DsNIriRjpJFyr64b15QpTeqWpjLlblpEi7tocRd3ES0u0a5dREREREQiIhKRSPS3PJ3v+77POc/7du6v8Z6G28N3Mb/O+Xje5z3ztlr7ryJyM9OK6Os4o2/Xq8wYFdGXa5kOYHjd63R0/G/+o4ro05WMPl+dGcafl4g+rmT0dpSWIP8XDZW5eTyIae2Yoo0TijYXEno2SOjD5aI7/dZfUP8KAauArCiKRzHFQ5sC1loqMh/T8zMprR9XOvf7VRrCRboD7y+ZZXl1Li1BjEKnHs4rHXROdwvp2sz53Zu6DIZBEvXibEpPlw3k8WKiUQ9OCowPQto206GKZRors1Q2jHpzwaBeDtMSA5iDkaga0B6H32I4AsVhFLr05HRCj06ZDmHJMEu1XZoOVGAmyqQG9e5iqpft9fmqS9hpEsXDjd1476gKCPJQWC6AGIUulTtP7DruDjB3urGLCQniAec4jwKx6zSq6MrdnqLVIw2C8ESWKWfKfz5ZEDCTww2C8J8l44BsdIfsMAMzPhQCtCpAAoU/UBk57NwlCQJmdDB2MUFANnzM4GwLKroCzPBAgyCcd2TK5WSQmB+AgBl0GgTJUyGyEwiYJR8zNYgjQN9v5GU0CO8zyJsfgIDpZ6FBIj9u5jqM2hZkMd3UhTAmCOjnJNdhWAmqGWgAgJlL3K7sEYPaGvTrlgFxHJA3P0AA01Yuxr/bLqs6TPkgmZ1AgTCmqotUuN+3cycaVDc//GQOhfGrDqe7txXIft4Ixi93MF2gD2kcU1fy5jOF/Ff1B8q4TSHNkI2QAAAAAElFTkSuQmCC",
    "Bonemeal and slime (Morytania)": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACs0lEQVR4Xu2X70tTYRTHfV720heBCMEIERGRiyhiTIwGUoIg0S/oB4vKEaYjyVwQpMZAKRgsVqEISy2CRvauP+8b3+dy3Nmz545276ZvPHBe7O7uns/9fs/zPGc9PedxHqceBtHZ9XALGry9PYK1hWHk5waRmx1A9upl3E2ncGvqUrfBTFNhft64M4pSsYhP29v4sLWJteUX+JqbxM7DwH7/YDrVDdUMivcDPM2EICxCMKpz+OUzvlUq2C+XLdRmoYBcNovrmQxKj8dReTZhofkSc2P9nYIy+PhoDEs3BjEb9NmHv7k5jOOjIxwfHKBWreLH3i52SyXsbLzDen7FQq0sLlql3t8btb9dmAiBghRVSxQG5Sfj9uF8W1rBAlSLvfK3VrNQVIpAtI1QBNE9Jn2VEMhYEPYFc+95Y/IabaE9VJH30l6CE4ZKrs4P2Rdgs6eHLia1zdhiLL6/NNWUvE4gKsj7BEjUIZBW9NpIX1KVjC1cXfanTyUBokoaiIuCPZgAKGzmw/wVbxLIVUlsEyDpIQFiYwep3ri2GXx/mfamQAkQVYqyTQOxsScHQqAYKhn8XE03pYYSlaJsc4Gksft7L7SrksGvV9NN6YJplXy2+YDY2ALUhkoGv1/PnKQPTKsUZZsPiI0dC+hPYcamBtNwAtXKNh8QGzsRkE4XrJVtUUA80zoGpFNUirLNBZLR5EyAtEJydJwJkLZMVpkG4j4UE4jRGkoD+XpIA8lQlxiIBVmYTewD8jW1rDICyWlPIA52AiQwbQPRChZkYRfOBXKXvQBxFuIK8wG5Ff8j6qe9C+buQbK6ZKfWw1wHgRjhgO6CRY0fepd2G1qeFcOuxgh/bE6mRhdGn2FysOr9xwVJBNMY4YMJ4bNJ5iCB6SJIPUQtSXeOJsypgLhRL6T/CHYW5B9Ufaeq+4hi4gAAAABJRU5ErkJggg==",
    "Bert's sand": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC9klEQVR4Xt2X70tTURjHPS974QtfCCIEItErCQkjFoqhKC6aSEwN/IGpLZY/mtOWupY/5nT+qBWZUEEEQQT9kU99nnH0epwSdO6kDnzZvXf3nudzn+f7nLPV1Pwfw8iJLn0Y6Y/2ydDAgIwND182lJHd9TV5OjmpII/Hx2RqdOSyoIxsra4GSmVkdnpa5hMJBatyCY3klpYUoKujQzpba1U9bXXyfG5WFmdm5MnEhEQjddWCMpJKJjU42ZiINqimYw16HVi+A/hBZ33Y2TKylsnIykJKA3M82lt/SoWXWdlYXlZvcT7cXa/AIYAZfXOMTEACURZX/e118qlUUiCyBpTNYHKg0ReU0Yln442SHb+qQfFN2/UrcpiPnxHA+IilAG8BajPIXK1NTX8LZdQPvCWTR1pqz0C4CgLxLLKl8wJEVpjQDXyeMPzI4KDEYzF9CYBsybwAMSEpdwNfJDKEgOPZ1JBHIEwMlBv0PFEyVm7MzXLw4tm8ZNNp/0CUDrkAruzKTWcCUkx16nLhDWju4U1JjbbJ4vgtTT9QGJZuw+R0HGppLosVG4DCXIdqdSqi3ecVCJjMo9s6eS4RkfXkHfVFJeVn2o/F/Yn4Ddnf3PALZDPkQhGULFCW/fRdebPUdQqGstnNGBgPQAyjRv1TMM75DhBW951XOV/ZscMoBMExK51zEZie/97vSoWCfMz1yuFKt38gAAhEQEpzlO3RDLDJEhwT258gXP+wtyuf1/oU5l2mCkAE4u0J+mUzKl+37sm3wn35vhNTcQ409+Er70D4h/JQFnzCWxOwEhCftlTcFwoQ3rE+wrwEsVkCyMKU5q8pJLDA8BvJc4cxzPFWgF9s2YJZIkM/9mJSTDbrtbfb29phHtcfd5Rb3+5NmPl1Pq+dRPD3xaIa+ehgX8+5bjMVGpD1EFmik84T2UKhAwX9QyDbZW6HcYyveCYE/wRHOQBmDnYXEPjn50G/frogIcGUR3nykz+JlVQVEHcEg1aSe/8/P34BMvK8xyR7yIkAAAAASUVORK5CYII=",
    "Tears of Guthix": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACAElEQVR4Xu3TXUsCQRQGYPefSBcRXsgSiigrLS6FiYpJbhLihfQBFX0ghRBB/3zinenMnp2dVTS1vdgDL+kMzTyes1so5JVXXmnlCHPlH8sRbuVAZAgVgdxiMQuovYFsIzDXHFEqFyVox2NzxPjSMy6wPStJULJLW0Eq0CRsMoACVeqHsTUOiu+pfa9+tI3ORSAThUtrnrqEQwiDDqkuKYzvlVI6t3Y5GjS99n9REajhl/Rn+h5d7EgIJQ7auFtx0GzSioFOgrJEIPiMEJoggV+W4VCMeAVKHRKPWjdBt9NA7uPy0+6x/NtquzL4bsO0A1efCwyBUkbo6F/Jw3Ecg2AfgPN+VSLCQUNm2Kvp9DtVnTRMCgiVRNEIcDEH3c/ONKYfNuQedREvAUJAxMSs6A6vOIqDOAZBV7rDmgRhHfsIcASkt47ewg1AqAiVBnq8acvuAHQx9mQISv9DZ9DbZ+LWAKFUizmIY57vOnpcwIwmTbmGriH0gNtwdPaK58dWqlP8DSLM60NXzJ96EgQMgm4hgNKDbuI4ZE0MleoSHYpwEHWHxgYgYuKQDbtiKycGwq8G5v1lIBZvF+Jq6idAHPWHES2rCEWjAOZzPhRfH6PULm1hRMtK/VICYQzAfC9C2SWO2lFXkqUOV5fx54OPZsddsReH2RB7g5iVCUReWagfODGMgbHJ6FwAAAAASUVORK5CYII=",
    "Magic birdhouse": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC/klEQVR4Xt2W34sSURTHZx598MGHhRCEQUQWWQYRRXYxFoSNLSwySqLdwECIJaIIeushiB6CIAiCIPpbT37Pme/MnXFSR103OvBlnMv13s89v+543v9tvmRHbtB8GdY9+QegAODLIPCkV/UkvHVjUAmIC9M88KRW3huUQbggeTAEagfBdUL5MjospUBcmHAO06yYXKjsKjs0XyZHaSDC9CKgPUFZiF4PyzLtJECAGURygQhFoB1CJbkCmKvTknpoNijJKCzJsOapCBWDRbnk5tOWUAYyqpfUC4TB+3nDND2ZP4NEhIvzaSkUtZb5chEl7tuzcgoGTY9AGLvo2zjkhjELxTAqMMaicK4B5cu4YxsDhDDwADZzgVBtCB2gMA5loRhGelBh1m8LlrSEoDRHoo2yQOOWhW7STUMxjOOGCVB5IVwJhBNjcSzKOOtJlwABmlDYfNI0aYhzcorhI8wSIFiScMlkX6Yt2+D93Va8KfIM47Oo6iDAwcM8VDZ0+euvME5MJluSA+jdnUN9AuigUomB8MQcnJzeQltA2ADkhmdx/cKWAL05a+oTMJQLhKQlAOC0L1WtqlbkSxEzIIQqD4iegtf2DvRqaED47XoIApDmSmAAgHPf3fzJ7lDAbAECvTxtxOUMADeHMMYWkQc0u93Q/+NQG0AZyPdnXfk17cdAWHRdIFYWCgEg2ouiHlQwfL58edKWz4/a8mkcyscHoXy4f6Q6b1nlsNIACiAmPTs6gC6PAxW/AjD2uFvT98JAV2G6nLE5IL9OOvLtaVcF7/247KkIxNbwsF1Nbv45DN839hBgfj7va7ig3y+OFY4iJLxIUFQgGidyhDD3wmrqQmUqFOxD8zutXU55CeLG9BI8xIpzBRhATU/qCsNrAuW/AQzNTsL+go2yHoKQV8gxCPkGsTWoV1L5sjEMzdcrgHAEhOfwnYTfeR6CVzi/F13ShNgChpa+f7gRLlF8N7mwCDPH3PlbemXRsou5YFlYF+hv/782Wzx5PtAN225C9Af7tkjurxi1NQAAAABJRU5ErkJggg==",
    "Empty birdhouse": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAAC0ElEQVR4Xu3W8WvTQBQH8PbH/eAP+SEwBoUySimjhDIqkrHSsRHJpI45qh1sSKVQcDIYKgOVobgJgiAIIogg/qcn3zte+vJyuaS6KogHXwYjaT65vHt3lcr/8e+OqnLnjw3zwH73hgqDdLprS0mywGuHmh+MQi8JUBQXDmk3TLLAuaHmht1NLxUOk7h5gGmocxjIQeTr7G15qbiA8+CGUVPFG3X9rE4df62jqkaxn4RQNpwLJnEcNb7bTjBhsFIM+vxiR+fT+Y76+Hxb58PTrVyYxOXBTg/XM5hW3SsGfb24rb68inQIRjjAkPdnffXutKeTByPcy0mYi1nxTQ05Qd9exzoSZps1wAh3+WRT583jDR38Lw8TNP1klQHjBP24uqO+v91NoThGzhIhLqahBpw/uqVjw/TXaxmIA0Mj3Semh4E62luzzgRHPHt4U50ddzWCQphBb1U1aks6JWYlO2Y3GNBo0FL7UUPPDGoFhY1VeDzwE8TJg46aDgM1udfWAWYUt3SvIQwHyWeWHFU1ud9OQJglAnHUeH9ZnRx1kmtpdVEjXBgInwqrByjfm4VA44MZiBqhrTP/FogeAhDqBiDUBAch+LS4dhg3k2a4cBCKGG+PIuYYfDYqfg4iBN8yrhUECED02WiLAQifFiBcR/Vji1zF8omOYW7gICxtDuKFzWtNbqQ8WIk8JVAGgqaIcBCWON4eKAmi6wbbqwmIb6q4l/epkjVltg5AqDvzrozeQiD+2QiE+iEQBbNK3RsBlLeCQlDe0YO2FNpOAAQU2wngVNBxD/uVgWARUEfnqw6Q2nJFpxCEt+VnIomTRw/MKLCYUfQpgtBpQLYAwlBpFGwjBsSTB5M429HDhpEQBwajqrsuYkPZMBLBD2Y2TEkIH7JPmOTNiETwYyth5KzIJxYO/hazH8kiEYmg8Cb4C7NSbkikC7pQSNGQyL8GWeT4CfLdXnoQ9tr8AAAAAElFTkSuQmCC",
    "Ranarr weed": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABuElEQVR4Xu3WsWrDMBAGYHn0kL2LIZRSMhQTSktoCQQCLS6kgxcvWTJ06dB36JurPp8vvjspJpKdFooPfoLtRPoiS7KNmWoqqMS6x/rcr1ViF4+mBWCu62PIH6AQQ7lZu7kAqvvnsnEXs9j6MyIqsVmOw08dEiyvj3mo87sidQLfX87nY6CSI4QaX+6wAw3i13VGBWkM5KFMBUYDdEYF+TD3NZIHzvVlIEhOYo1ZVS4IzvUlEoQAfYsI8bRP7fogQavC7dyXCBBuZnwC61EhUIMCCKXtFK77QpgAkLu0+ej4QJsPzPPOBegEjo7c7kNB208XQN+BBI5OYq8yY7PbGpJjml1Xgfgt8YE4QCdwdKBwdDiI9haxigoXBJgLgIzBH7QrzAPiExjmjAbRaOkE3i63OMwHAgxlU54P0v0EFGL4DtsHalBVB9MZCErEs4ejaFJrDOR1jzmFiQTJx8MpEEWD3g6Yl6/ZMQNGx31UaJAPBSuuuV1VByLU+7D54wdplA9EAQB8lt8zsQJxTgZV9/I1FEV7FC2MiDkkHxX61fNcEAdwRCCGSjYWEw2IQMjSjcVEtznVv68fmoMaeqBvcmgAAAAASUVORK5CYII=",
    "Snapdragon": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABz0lEQVR4Xu3Wv0vDQBQH8LsxQ3YXoVNxEClCEaRQUIQKdchgB4tQoUvBooiIgoOD4J995vX1Je/eXdvcJSpIHnwpTdO7T+9XqlRbbUFp476X136ttDk9U2sAptNVq/wBSpvhKCnSPVJOfgBV/nK7cW0uxkkRAhz23TSIwuHnHRHsMkuscMDxwA7c3+t0mkDpAkONn6zXiwRJhLy/MZDEQAYjP4Y+l2kUJDsDzNXEjgTwwP01QfYilhgfiK5vSiQIAXKKqNFhvpPOM3wlSDZ1O/clAmTvpm0gCEAg1zO3c7iPhzABIG329vFkrQqCjCYIkgCZwNGxj/tQEEQCfJiKIG0OckAvB/TzzuF5BMd/FRBhfCCODhwdKBwdDoLHAJ0vtHBpanwgDpCJACmFX0AYB/GdBKCbeWJuF9VBgdPlFofx0ZEgyN0yXWHGUxciQbKfgEIMP2UJ4wPNH1OzeEaYLzVB5SNCPpNoURMIMBx0/+qiak6XfTpvAlE4CDAP7xiYPkqN0UHMNpAPBaERevrAEOrtK20eJFESw/Pyma6OhOkSX2kX4poMqvLP1y7QLhSFNkbEGkJMXRAHcEQghspuLCYSEIGwSzYWE9lmW/++vgG7bR950NqTZQAAAABJRU5ErkJggg==",
    "Magic tree": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAADBUlEQVR4Xu2WXWsTYRCFu/+kFyJBpARpKRFFqCipWKhSq7RGYj+xaW1CbIjRUL+CVgoVJQiKiCCKXoigv8AbQcFiCTHGtDTqP/AiF7kYPbOZzbtvPtukxIsMHJJuk91nz8ycTUdHu9r135ZBRbWsihBDA13kGe6hae/+nYKqdlITYrB/L0NcnDxI4cAJVnB2gP/n7Oys8v0tl0Hv3obL3KkJMjrUTZd8fRQNnaJ87hW/Qk0GUmegvC77j9DNyJkSXQsPMxiA/DPHmwFksO2hucO0EvPS8q1zrKXrHlY+955uL47aFIuOWAIUHFqYH2wGkEE3wv2UTH+n1WSC7t0ZYygBE6hKAhyArgRPWkC+CXfB1W2VQZnsJqXWM+R4FmcgHUqVfhwOoWUAwoBjvsShbbpk0MbPLO15+YiBHtwdL4GC5JgqOOQbO8Crfn6kl4FODzobb1n29y8LCHMEKBVM/laFLcPcAWjirIuB4E5TgNih108YaPfjFYovT/JF8SrChkEADkwfsmDgDoCkXQhI+3bWXcUv/djcsID8U90UvNBjAaggcAUwKoi0StwBUO7PC34vbtUBZvCJRaHZXoYADFQJBK6oILpwvJxqPE5MGGxVIp2iD58+8toKUCUQVerNiPBZSJIbW5fPveEoqJHeBl0NHqX0xro1yACSkNNbhc+qWlxw24QMkzySkEQMCFRdQNGAy5obQAkMLoC7VS+mKhY5VhIHaoDqUACq4/lm8MzIZgkQWvEtk6YviTXaFV9id9Q11zNIlQ61ZSDMC9JZgHASzAza6Hj+kBxP79vyqBaUDoRnn7StLqC58X081Ny2AlBk3mWDxIBKHtUSBhkClLQNQIgDd5/DgqkAhDLbhi1bSyVZU56uYh79cwmzJKGoA5QTnAIQWg8Qc81N1YCRMp2a8TpZuCvevAKQJLYquKAfE+EGykHUCSNl/wGGoYZLiAJA6hcVqTkFNQhhL/sJDP5N9PnrKrunB6VIv5GGIaqX2Ur1eVYNYAdB1Go5QGm1HKBdhfoL8we2iN3VW0YAAAAASUVORK5CYII=",
    "Snape grass": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABTElEQVR4Xu2V0Q3CMAxE2wHYgREYgy82YQ7GBtHg5Hyx00Z1ioQ4yRJJW9/ruS3T9NdQzc9Uh8oznZ+nx20p+/gQWaYJEPcv5/NS+tpw1abL3v2aSoF6KYaK0wEYAMIaDKaB2Jz3EXgAlA1gQsgxTC8eyjHltYyuAgl/0A1ThsH1WJi3nJExHCUzYFz6G1PVB6gFEwiVmiKQN7psjL8VyO6xOTAAkY+ReQuIXTpVxsUwmBCetw62S3o8vFZwRmXQOKiSTpWWAZALElLXKqhusGKe/0ThziuIlfLBuoRvhgEoowBDHA+mIX3kWjm3G6q8Gbqx7FnmHlTpKn2532ZxOrBP5jwqb2x4g6Vfl7y7oVQMkFZxcl2qYZL4ueBRcYqckNd3h9pvnQWGUKXC1AbaWuFQXnmJ8H74+KQhF5rzfn3uYbIT4PWh+qr5T+sFXTbQ2Hm+QjAAAAAASUVORK5CYII=",
    "Palm tree": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABpUlEQVR4Xu2WsUvDUBDG80aHDh0CJRAIIUiHDh0KoiAKolCQgoIUpIiiIkqRgoOD4CAIDoIgCIKDf+rJ5XF5l2ueBPqSLPngoENpf3z33d3zvFatWqEULFdjUvB4Eqb1cBTC7TiA/WG3KSgFL7MoraephpofBnC+14PxqHYoBR/XCbxdxPB6FqdQz6dR5tJs14fJRm1QGgbr/TJOoQiIu3S85cPOoFM1lILPmyQrAkKXCGgxCeDqoAfTbT9t3SBaqwpKw3zd6cLP5JIE4m3DkHc7XhVQCr7n6xkQQdmAqG3oErVuGEUuoTQQr/+AqG3k0mbfaZ4U/NznYco4xIFGic6SI5c0EIdCIJ4hnLTGgMqEuggo9J2F2w5UNPY2IHLIgUsKfhd9KxA/HzYg2kcOYFBmwjiMBMLTQYsR9xAf+yRwCoQye0jmhwPh6ZBANPKO2kVSS1ta5qcMkPzVFWTumO2wYrskkAy0/NUVlb/0vF1yS9Mtw/xUCITKv4XQHQmEE4ZA5qHmPD9S+rUos8NhJEiFMCT5sM9XjSBG/E+LSn6/Vau69QeBQF3oyFmUBAAAAABJRU5ErkJggg==",
    "Coconut": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABpUlEQVR4Xu2WsUvDUBDG80aHDh0CJRAIIUiHDh0KoiAKolCQgoIUpIiiIkqRgoOD4CAIDoIgCIKDf+rJ5XF5l2ueBPqSLPngoENpf3z33d3zvFatWqEULFdjUvB4Eqb1cBTC7TiA/WG3KSgFL7MoraephpofBnC+14PxqHYoBR/XCbxdxPB6FqdQz6dR5tJs14fJRm1QGgbr/TJOoQiIu3S85cPOoFM1lILPmyQrAkKXCGgxCeDqoAfTbT9t3SBaqwpKw3zd6cLP5JIE4m3DkHc7XhVQCr7n6xkQQdmAqG3oErVuGEUuoTQQr/+AqG3k0mbfaZ4U/NznYco4xIFGic6SI5c0EIdCIJ4hnLTGgMqEuggo9J2F2w5UNPY2IHLIgUsKfhd9KxA/HzYg2kcOYFBmwjiMBMLTQYsR9xAf+yRwCoQye0jmhwPh6ZBANPKO2kVSS1ta5qcMkPzVFWTumO2wYrskkAy0/NUVlb/0vF1yS9Mtw/xUCITKv4XQHQmEE4ZA5qHmPD9S+rUos8NhJEiFMCT5sM9XjSBG/E+LSn6/Vau69QeBQF3oyFmUBAAAAABJRU5ErkJggg==",
    "Papaya fruit": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAACA0lEQVR4Xu2XwUdEURjF5y5b3MVdPN5y/oTWbSKiqMUsalGimM1QSpJiKCnFMAyVUoxSKY1RSmVIRESi/+jLudfn3ffMEL37LjWHa2YY837OOd/33uRyXf0fCYqOdwla3pS0uC5pvix9Qwk6vA70qZ0qKleUBurN531BCbpoBXRyb4DWqoqmSj0+XRJ08xZqqN3zgDZqimbmTGweXBJ09x5S6yukq+eQ9i8D2tpTVFyQ1DeQ8+FSBNR8Dem4GdDOgaLZFUmDIya2jF2KA6FH1brSkzY6njmQ0BAAevqMgNAjFLu0JKkwmVm5BTVeIpiHD/Mexcb4wyWUG9GNTTuHMmOOyQIMDmDwGZD1W+MSuoSdhPgmik6hjDtwBd3BK2AQGSbt7DHuEjY44nMEJfRFucjsDmAAyQsSLmFJYgWsbksdHQOlXHJTZHbHBgIoA2H8bSDbodSBEI8dlw2EuOAO39cAxJGh3Lybkr/6C8Ud4v7YccEdbGwAcYewuXkFwKEUXYrG/adAeCTBvW244CgyRMOxdYoLY185MmPPhe4fcgQEJ3hDtwNid7CHuNCO4oKEvjAigkud4sIO4kI7jItlYoMzNlAyLtzPeEs7istW1CWOyy4z3zbs5yIHcSVloJJAiAtAcCdDGJYpuQ0EGHTHAwzL/j8WPx5gjPjC7U7yu139eX0DI6mQ4t9H0gcAAAAASUVORK5CYII=",
    "Jangerberries": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAgCAYAAAB6kdqOAAABcklEQVR4Xu2VsUvDQBSHc2OGG25wLw7OLt0KQkGooEOHdqgUKmQpKIqIKBQUURQKhYKC4CAI/p3PvLy867s0dfIuDvfBo4SU5svv/Y4mSSQSkSigaZxSpJvSNCsmRHg65VixYHI1MqXQsC/EbGpBkCnQHA+EkBQNJbXbagGPI1RNML+m7wRFwWQkhHhCp0QoyMYkw0IyOfc6CLQu/JRCfNejTN1xJon+4WYhTyh4/d6CxaeBl3djxXpdtzduh7yhYDY3cPus4fJew/lMw/RKQ3ahiwfzulB4+WVg/iGlvaAKidMbkjg50zCYpDCekggKocTTm4GHpbHyHlenCgkUQJH9vC8HlTX9lmD11/4AZUXanQS2dxLx9qooNIpggiiC8qNslaC3lOSwjCw6ixwNVwl6PPLrfxXcm7sFrQtT3OulToLeZNahk3f9WNeZ4DKIsqeOyy47E1iGobJjX3BVDcswuUS7evIap7HebOZfyUQiOT82hlP5HimIcwAAAABJRU5ErkJggg=="
  }
};
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/data.js", error: String((e && e.message) || e) }); }

// ui_kits/android-app/kit.jsx
try { (() => {
const PT = {
  headline: {
    font: '700 24px/30px var(--font-serif)',
    color: 'var(--text-heading)',
    textShadow: 'var(--text-shadow-hard)',
    margin: 0
  },
  titleMd: {
    font: '700 17px/22px var(--font-serif)',
    color: 'var(--text-heading)',
    textShadow: 'var(--text-shadow-hard)'
  },
  titleSm: {
    font: '700 14px/18px var(--font-serif)',
    color: 'var(--text-heading)',
    textShadow: 'var(--text-shadow-hard)'
  },
  bodyLg: {
    font: '600 15px/22px var(--font-sans)',
    color: 'var(--text-body)'
  },
  bodyMd: {
    font: '400 13px/18px var(--font-sans)',
    color: 'var(--text-muted)'
  },
  bodySm: {
    font: '400 12px/16px var(--font-sans)',
    color: 'var(--text-muted)'
  },
  labelSm: {
    font: '500 11px/16px var(--font-sans)',
    color: 'var(--text-muted)'
  },
  yellowVal: {
    font: '800 17px/22px var(--font-serif)',
    color: 'var(--osrs-stack-yellow)',
    textShadow: 'var(--text-shadow-hard)'
  }
};
const rowBetween = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  gap: 8
};
function PhoneFrame({
  children
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      width: 412,
      height: 892,
      borderRadius: 36,
      padding: 10,
      background: '#0b0907',
      boxShadow: '0 0 0 1px #3a3128',
      flex: 'none'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: '100%',
      height: '100%',
      borderRadius: 28,
      overflow: 'hidden',
      background: 'var(--surface-page)',
      display: 'flex',
      flexDirection: 'column',
      position: 'relative'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      height: 28,
      flex: 'none',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '0 20px',
      font: '500 12px/16px var(--font-sans)',
      color: 'var(--text-muted)',
      background: 'var(--surface-panel-top)'
    }
  }, /*#__PURE__*/React.createElement("span", null, "9:41"), /*#__PURE__*/React.createElement("span", null, "\u25CF\u25CF\u25CF 87%")), /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      minHeight: 0,
      display: 'flex',
      flexDirection: 'column',
      position: 'relative'
    }
  }, children)));
}
function Scroll({
  children,
  pad = 16,
  gap = 12
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      minHeight: 0,
      overflowY: 'auto',
      padding: pad,
      display: 'flex',
      flexDirection: 'column',
      gap
    }
  }, children);
}
function Empty({
  children
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      flex: 1,
      display: 'grid',
      placeItems: 'center',
      padding: 24,
      textAlign: 'center',
      ...PT.bodyMd,
      whiteSpace: 'pre-line'
    }
  }, children);
}
Object.assign(window, {
  PT,
  rowBetween,
  PhoneFrame,
  Scroll,
  Empty
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/android-app/kit.jsx", error: String((e && e.message) || e) }); }

// ui_kits/setup-guide/DocsShell.jsx
try { (() => {
const DS = new Proxy({}, {
  get: (_, k) => window.PssstDesignSystem_7dc1e9[k]
});
const DT = {
  h1: {
    font: '700 32px/38px var(--font-serif)',
    color: 'var(--text-heading)',
    textShadow: 'var(--text-shadow-hard)',
    margin: 0
  },
  h2: {
    font: '700 24px/30px var(--font-serif)',
    color: 'var(--text-heading)',
    textShadow: 'var(--text-shadow-hard)',
    margin: 0
  },
  h3: {
    font: '700 17px/22px var(--font-serif)',
    color: 'var(--text-heading)',
    textShadow: 'var(--text-shadow-hard)',
    margin: 0
  },
  p: {
    font: '400 15px/22px var(--font-sans)',
    color: 'var(--text-body)',
    margin: 0,
    textWrap: 'pretty'
  },
  muted: {
    font: '400 13px/18px var(--font-sans)',
    color: 'var(--text-muted)',
    margin: 0
  }
};
function Badge({
  children,
  tone = 'accent'
}) {
  return /*#__PURE__*/React.createElement("span", {
    style: {
      font: '400 10px/14px var(--font-pixel)',
      letterSpacing: '.5px',
      padding: '3px 7px',
      borderRadius: 4,
      background: 'var(--surface-inset)',
      border: '1px solid',
      borderColor: 'var(--bevel-pressed)',
      color: tone === 'danger' ? 'var(--status-danger)' : 'var(--accent)',
      verticalAlign: 'middle'
    }
  }, children);
}
function Shot({
  n,
  children
}) {
  return /*#__PURE__*/React.createElement(DS.Slot, {
    padding: 0,
    style: {
      minHeight: 150,
      display: 'grid',
      placeItems: 'center',
      padding: 16,
      textAlign: 'center'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: 6
    }
  }, /*#__PURE__*/React.createElement(DS.DocIcon, {
    name: "image",
    size: 22
  }), /*#__PURE__*/React.createElement("span", {
    style: DT.muted
  }, "Screenshot ", n, ": ", children), /*#__PURE__*/React.createElement("span", {
    style: {
      font: '400 11px/14px var(--font-mono)',
      color: 'var(--text-muted)'
    }
  }, "real capture to come")));
}
function Section({
  id,
  title,
  children,
  badge
}) {
  return /*#__PURE__*/React.createElement("section", {
    id: id,
    "data-screen-label": title,
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 16,
      scrollMarginTop: 80
    }
  }, /*#__PURE__*/React.createElement("h2", {
    style: DT.h2
  }, title, " ", badge), children);
}
function Steps({
  children
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 14
    }
  }, children);
}
const S = p => /*#__PURE__*/React.createElement(DS.SettingChip, p);
const TOC = [['need', 'What you need'], ['install', 'Install Pssst Companion'], ['sync', 'Turn on sync'], ['paths', 'Choose how your phone reaches your PC'], ['app', 'Install the app and pair it'], ['check', 'Check that it works'], ['trouble', 'Troubleshooting'], ['privacy', 'What is sent, and where']];
function DocsShell({
  children,
  active,
  onNav,
  theme,
  onTheme
}) {
  return /*#__PURE__*/React.createElement("div", {
    "data-theme": theme,
    style: {
      minHeight: '100vh',
      background: 'var(--surface-page)',
      color: 'var(--text-body)'
    }
  }, /*#__PURE__*/React.createElement("header", {
    style: {
      position: 'sticky',
      top: 0,
      zIndex: 3,
      background: 'linear-gradient(180deg,var(--surface-panel-top),var(--surface-panel-bottom))',
      borderBottom: '1px solid var(--border-frame)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      maxWidth: 1120,
      margin: '0 auto',
      height: 60,
      padding: '0 24px',
      display: 'flex',
      alignItems: 'center',
      gap: 16
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: '800 24px/1 var(--font-serif)',
      color: 'var(--text-heading)',
      textShadow: 'var(--text-shadow-hard)'
    }
  }, "Pssst"), /*#__PURE__*/React.createElement("span", {
    style: DT.muted
  }, "Setup guide"), /*#__PURE__*/React.createElement("span", {
    style: {
      flex: 1
    }
  }), /*#__PURE__*/React.createElement("button", {
    onClick: onTheme,
    style: {
      border: '1px solid var(--border-frame)',
      background: 'transparent',
      borderRadius: 4,
      padding: '4px 10px',
      cursor: 'pointer',
      font: '500 12px/16px var(--font-sans)',
      color: 'var(--text-muted)'
    }
  }, theme === 'parchment' ? 'Dark' : 'Parchment'))), /*#__PURE__*/React.createElement("div", {
    style: {
      maxWidth: 1120,
      margin: '0 auto',
      padding: '32px 24px',
      display: 'grid',
      gridTemplateColumns: 'minmax(0,220px) minmax(0,1fr)',
      gap: 40
    }
  }, /*#__PURE__*/React.createElement("nav", {
    style: {
      position: 'sticky',
      top: 92,
      alignSelf: 'start',
      display: 'flex',
      flexDirection: 'column',
      gap: 2
    }
  }, TOC.map(([id, l], i) => /*#__PURE__*/React.createElement("a", {
    key: id,
    href: '#' + id,
    onClick: () => onNav(id),
    style: {
      display: 'flex',
      gap: 10,
      alignItems: 'center',
      padding: '7px 10px',
      borderRadius: 4,
      textDecoration: 'none',
      background: active === id ? 'var(--surface-inset)' : 'transparent',
      color: active === id ? 'var(--accent)' : 'var(--text-body)',
      font: (active === id ? '700' : '400') + ' 13px/18px var(--font-sans)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: '700 12px/1 var(--font-serif)',
      color: 'var(--text-muted)',
      width: 12
    }
  }, i + 1), l))), /*#__PURE__*/React.createElement("main", {
    style: {
      maxWidth: 720,
      minWidth: 0,
      display: 'flex',
      flexDirection: 'column',
      gap: 48
    }
  }, children, /*#__PURE__*/React.createElement(DS.FanFooter, null))));
}
Object.assign(window, {
  DS,
  DT,
  Badge,
  Shot,
  Section,
  Steps,
  S,
  TOC,
  DocsShell
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/setup-guide/DocsShell.jsx", error: String((e && e.message) || e) }); }

// ui_kits/setup-guide/GuideFinish.jsx
try { (() => {
function GuideFinish() {
  const {
    Step,
    Callout,
    DocTable,
    Panel
  } = DS;
  const list = items => /*#__PURE__*/React.createElement("ul", {
    style: {
      margin: 0,
      paddingLeft: 20,
      display: 'flex',
      flexDirection: 'column',
      gap: 4
    }
  }, items.map(i => /*#__PURE__*/React.createElement("li", {
    key: i,
    style: DT.p
  }, i)));
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Section, {
    id: "app",
    title: "5. Install the app and pair it"
  }, /*#__PURE__*/React.createElement(Steps, null, /*#__PURE__*/React.createElement(Step, {
    n: 1
  }, "Install Pssst [APK/Play link TBD]."), /*#__PURE__*/React.createElement(Step, {
    n: 2
  }, "Enter your ", /*#__PURE__*/React.createElement(S, null, "Server URL"), " (for example ", /*#__PURE__*/React.createElement(S, null, "http://192.168.1.20:8788"), ")."), /*#__PURE__*/React.createElement(Step, {
    n: 3
  }, "Paste your ", /*#__PURE__*/React.createElement(S, null, "Sync Token"), "."), /*#__PURE__*/React.createElement(Step, {
    n: 4
  }, "Leave ", /*#__PURE__*/React.createElement(S, null, "Remember this Sync Token on this device"), " on if you don't want to paste it again."), /*#__PURE__*/React.createElement(Step, {
    n: 5,
    detail: /*#__PURE__*/React.createElement(Shot, {
      n: 6
    }, "the app Setup screen, filled in")
  }, "Tap ", /*#__PURE__*/React.createElement(S, null, "Connect & Start Sync"), ".")), /*#__PURE__*/React.createElement(Callout, {
    kind: "warning",
    title: "127.0.0.1 means the phone"
  }, "Typing ", /*#__PURE__*/React.createElement(S, null, "127.0.0.1"), " or ", /*#__PURE__*/React.createElement(S, null, "localhost"), " in the app points at the phone itself, not your PC. The app warns you about this. Use your PC's address instead.")), /*#__PURE__*/React.createElement(Section, {
    id: "check",
    title: "6. Check that it works"
  }, /*#__PURE__*/React.createElement(Steps, null, /*#__PURE__*/React.createElement(Step, {
    n: 1,
    detail: /*#__PURE__*/React.createElement(Shot, {
      n: 7
    }, "the app dashboard after the first sync")
  }, "Log into the game. The dashboard fills in after the first sync."), /*#__PURE__*/React.createElement(Step, {
    n: 2,
    detail: /*#__PURE__*/React.createElement(Shot, {
      n: 8
    }, "the Timers screen")
  }, "Open ", /*#__PURE__*/React.createElement(S, null, "Timers"), ". Patches and birdhouses appear after you've seen them in game.")), /*#__PURE__*/React.createElement(Callout, {
    kind: "note",
    title: "Timers are estimates"
  }, "Farming and birdhouse timers use RuneLite's Time Tracking logic and can be up to one growth tick early or late. A patch appears or updates only after you've seen it in game. Birdhouses update on Fossil Island.")), /*#__PURE__*/React.createElement(Section, {
    id: "trouble",
    title: "7. Troubleshooting"
  }, /*#__PURE__*/React.createElement(DocTable, {
    columns: ['Symptom', 'Likely cause and fix'],
    rows: [['"Unauthorized"', 'Token mismatch. Re-copy it with no spaces. If you changed it, update the app (and the Worker secret).'], ["Can't connect / timeout", /*#__PURE__*/React.createElement("span", null, /*#__PURE__*/React.createElement(S, null, "This PC Only"), " is still on; Windows Firewall is blocking; the phone is on a different network or guest Wi-Fi; or the address is wrong (", /*#__PURE__*/React.createElement(S, null, "127.0.0.1"), " means the phone).")], ['Connected but "no state yet"', /*#__PURE__*/React.createElement("span", null, "Log into the game once after turning on ", /*#__PURE__*/React.createElement(S, null, "Enable Sync"), ".")], ['Worker returns 503', /*#__PURE__*/React.createElement("span", null, "The ", /*#__PURE__*/React.createElement(S, null, "ALLOWED_TOKEN_HASHES"), " secret isn't set.")], ['A farming patch is missing', 'Visit it once in game.'], ['Data looks old', 'The PC or RuneLite is closed (Local Server paths). The app keeps showing the last snapshot.'], ['Item values look old', 'They update the next time the plugin syncs. Until then the app uses prices bundled with it.']]
  })), /*#__PURE__*/React.createElement(Section, {
    id: "privacy",
    title: "8. What is sent, and where"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit,minmax(260px,1fr))',
      gap: 12
    }
  }, /*#__PURE__*/React.createElement(Panel, {
    padding: 16,
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 8
    }
  }, /*#__PURE__*/React.createElement("h3", {
    style: DT.h3
  }, "Sent"), list(['inventory, bank and potion storage (item ids and quantities)', "item values, from RuneLite's own price data (the app makes no price requests of its own)", 'skill levels and XP', 'your own GE offers', 'diary tiers, daily reset claims, Tears of Guthix, Miscellania approval and coffer, NMZ points', 'current Slayer assignment', 'last-seen state of each farming patch and birdhouse'])), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 12
    }
  }, /*#__PURE__*/React.createElement(Panel, {
    padding: 16,
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 8
    }
  }, /*#__PURE__*/React.createElement("h3", {
    style: DT.h3
  }, "Not sent"), /*#__PURE__*/React.createElement("p", {
    style: DT.p
  }, "Username, chat, location, friends, or anything about other players.")), /*#__PURE__*/React.createElement(Panel, {
    padding: 16,
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 8
    }
  }, /*#__PURE__*/React.createElement("h3", {
    style: DT.h3
  }, "Where it goes"), list(['only to your phone (Local Server) or to the server you set (Your Own Server)', 'nowhere else', 'no Pssst servers, no accounts'])))), /*#__PURE__*/React.createElement(Callout, {
    kind: "note"
  }, "The Sync Token and your data are never logged. If you change the Sync Token, re-enter it in the app. If you use a Worker, update its secret too.")));
}
window.GuideFinish = GuideFinish;
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/setup-guide/GuideFinish.jsx", error: String((e && e.message) || e) }); }

// ui_kits/setup-guide/GuideIntro.jsx
try { (() => {
function GuideIntro() {
  const {
    Step,
    Callout,
    Panel
  } = DS;
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 12
    }
  }, /*#__PURE__*/React.createElement("h1", {
    style: DT.h1
  }, "Setting up Pssst"), /*#__PURE__*/React.createElement("p", {
    style: DT.p
  }, "Pssst is a free, read-only companion for Old School RuneScape. A RuneLite plugin, ", /*#__PURE__*/React.createElement(S, null, "Pssst Companion"), ", makes a snapshot of what the game already shows you available to the Pssst app on your phone: bank, inventory and potion storage, skills, your Grand Exchange offers, farming and birdhouse timers, Slayer task, and daily and diary progress."), /*#__PURE__*/React.createElement("p", {
    style: DT.p
  }, "There is no Pssst account and no Pssst server. Data goes from your PC to your phone, directly or through a server you run yourself.")), /*#__PURE__*/React.createElement(Section, {
    id: "need",
    title: "1. What you need"
  }, /*#__PURE__*/React.createElement(Panel, {
    padding: 16,
    style: {
      display: 'flex',
      flexDirection: 'column',
      gap: 10
    }
  }, [['monitor', 'A Windows PC with RuneLite installed'], ['smartphone', 'An Android phone'], ['wifi', 'Both on the same Wi-Fi, or both on Tailscale (or your own server, for advanced users)']].map(([i, t]) => /*#__PURE__*/React.createElement("div", {
    key: i,
    style: {
      display: 'flex',
      gap: 12,
      alignItems: 'center'
    }
  }, /*#__PURE__*/React.createElement(DS.DocIcon, {
    name: i
  }), /*#__PURE__*/React.createElement("span", {
    style: DT.p
  }, t))))), /*#__PURE__*/React.createElement(Section, {
    id: "install",
    title: "2. Install Pssst Companion in RuneLite"
  }, /*#__PURE__*/React.createElement(Steps, null, /*#__PURE__*/React.createElement(Step, {
    n: 1
  }, "Install Pssst Companion from the Plugin Hub [link TBD]."), /*#__PURE__*/React.createElement(Step, {
    n: 2,
    detail: /*#__PURE__*/React.createElement(Shot, {
      n: 1
    }, "RuneLite \u2192 Configuration \u2192 Pssst Companion, all three sections visible")
  }, "Open ", /*#__PURE__*/React.createElement(S, null, "RuneLite \u2192 Configuration \u2192 Pssst Companion"), ". You'll see three sections: ", /*#__PURE__*/React.createElement(S, null, "Master Sync"), ", ", /*#__PURE__*/React.createElement(S, null, "Local Server"), " and ", /*#__PURE__*/React.createElement(S, null, "Your Own Server"), "."))), /*#__PURE__*/React.createElement(Section, {
    id: "sync",
    title: "3. Turn on sync and copy your Sync Token"
  }, /*#__PURE__*/React.createElement(Steps, null, /*#__PURE__*/React.createElement(Step, {
    n: 1,
    detail: /*#__PURE__*/React.createElement(Shot, {
      n: 2
    }, "the Enable Sync warning")
  }, "In ", /*#__PURE__*/React.createElement(S, null, "Master Sync"), ", turn on ", /*#__PURE__*/React.createElement(S, null, "Enable Sync"), ". Nothing leaves RuneLite until it's on. RuneLite shows a warning: \"This feature submits your IP address to a 3rd-party server not controlled or verified by RuneLite developers.\""), /*#__PURE__*/React.createElement(Step, {
    n: 2
  }, "Copy the ", /*#__PURE__*/React.createElement(S, null, "Sync Token"), ". It was generated the first time the plugin ran.")), /*#__PURE__*/React.createElement(Callout, {
    kind: "warning",
    title: "Keep your Sync Token private"
  }, "It's the password your phone uses to read your data.")));
}
window.GuideIntro = GuideIntro;
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/setup-guide/GuideIntro.jsx", error: String((e && e.message) || e) }); }

// ui_kits/setup-guide/GuidePaths.jsx
try { (() => {
function SameWifiSteps({
  addr = "http://<that address>:8788"
}) {
  const {
    Step,
    Callout,
    CodeBlock
  } = DS;
  return /*#__PURE__*/React.createElement(Steps, null, /*#__PURE__*/React.createElement(Step, {
    n: 1
  }, "Install Pssst Companion and turn on ", /*#__PURE__*/React.createElement(S, null, "Enable Sync"), "."), /*#__PURE__*/React.createElement(Step, {
    n: 2
  }, "Copy the ", /*#__PURE__*/React.createElement(S, null, "Sync Token"), "."), /*#__PURE__*/React.createElement(Step, {
    n: 3,
    detail: /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Callout, {
      kind: "warning",
      title: "Only on a network you trust"
    }, "Other devices on your network can then reach this PC on that port. They still need the Sync Token, and the connection is not encrypted."), /*#__PURE__*/React.createElement("div", {
      style: {
        height: 8
      }
    }), /*#__PURE__*/React.createElement(Shot, {
      n: 3
    }, "the This PC Only warning"))
  }, "Turn off ", /*#__PURE__*/React.createElement(S, null, "This PC Only"), " and accept the warning."), /*#__PURE__*/React.createElement(Step, {
    n: 4,
    detail: /*#__PURE__*/React.createElement(Shot, {
      n: 4
    }, "the Windows Firewall prompt")
  }, "If Windows Firewall asks about Java/RuneLite, allow it on ", /*#__PURE__*/React.createElement(S, null, "Private"), " networks."), /*#__PURE__*/React.createElement(Step, {
    n: 5,
    detail: /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(CodeBlock, {
      label: "Command Prompt"
    }, "ipconfig"), /*#__PURE__*/React.createElement("div", {
      style: {
        height: 8
      }
    }), /*#__PURE__*/React.createElement(Shot, {
      n: 5
    }, "ipconfig output with the IPv4 line highlighted"))
  }, "Find the PC's IPv4 address: run ", /*#__PURE__*/React.createElement(S, null, "ipconfig"), " and look under your active Wi-Fi or Ethernet adapter."), /*#__PURE__*/React.createElement(Step, {
    n: 6,
    detail: /*#__PURE__*/React.createElement(CodeBlock, {
      label: "App \u2192 Server URL",
      copyable: false
    }, addr)
  }, "In the app, enter the address, paste the token, and tap ", /*#__PURE__*/React.createElement(S, null, "Connect & Start Sync"), "."), /*#__PURE__*/React.createElement(Step, {
    n: 7
  }, "Log into the game. The app refreshes when opened and about every 15 minutes."));
}
function GuidePaths() {
  const {
    Panel,
    SegmentedTabs,
    ConnectionFlow,
    Callout,
    Step,
    CodeBlock,
    DocTable
  } = DS;
  const [p, setP] = React.useState('wifi');
  return /*#__PURE__*/React.createElement(Section, {
    id: "paths",
    title: "4. Choose how your phone reaches your PC"
  }, /*#__PURE__*/React.createElement(DocTable, {
    columns: ['', 'Same Wi-Fi', 'Tailscale / VPN', 'Your own server'],
    rows: [['Works when', 'Phone and PC are on the same network', 'Anywhere, while the PC and RuneLite are running', 'Anywhere, even with the PC off (shows the last snapshot)'], ['Difficulty', /*#__PURE__*/React.createElement(Badge, null, "EASY"), /*#__PURE__*/React.createElement(Badge, null, "MEDIUM"), /*#__PURE__*/React.createElement(Badge, {
      tone: "danger"
    }, "ADVANCED")]]
  }), /*#__PURE__*/React.createElement(SegmentedTabs, {
    value: p,
    onChange: setP,
    tabs: [{
      id: 'wifi',
      label: 'Same Wi-Fi'
    }, {
      id: 'vpn',
      label: 'Tailscale / VPN'
    }, {
      id: 'worker',
      label: 'Your own server'
    }]
  }), p === 'wifi' && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("h3", {
    style: DT.h3
  }, "Same Wi-Fi (Local Server)"), /*#__PURE__*/React.createElement(Panel, {
    padding: 16
  }, /*#__PURE__*/React.createElement(ConnectionFlow, {
    nodes: [{
      label: 'Your PC',
      icon: 'monitor',
      sub: ':8788'
    }, {
      label: 'Wi-Fi',
      icon: 'wifi'
    }, {
      label: 'Your phone',
      icon: 'smartphone'
    }]
  })), /*#__PURE__*/React.createElement(SameWifiSteps, {
    addr: "http://<PC's IPv4>:8788"
  })), p === 'vpn' && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("h3", {
    style: DT.h3
  }, "Tailscale or another VPN"), /*#__PURE__*/React.createElement(Panel, {
    padding: 16
  }, /*#__PURE__*/React.createElement(ConnectionFlow, {
    nodes: [{
      label: 'Your PC',
      icon: 'monitor',
      sub: '100.x.x.x'
    }, {
      label: 'Tailscale',
      icon: 'network'
    }, {
      label: 'Your phone',
      icon: 'smartphone'
    }]
  })), /*#__PURE__*/React.createElement("p", {
    style: DT.p
  }, "Install Tailscale on both devices and sign in to the same account. Then follow the Same Wi-Fi steps using the PC's 100.x Tailscale address."), /*#__PURE__*/React.createElement(CodeBlock, {
    label: "App \u2192 Server URL",
    copyable: false
  }, "http://<PC's Tailscale IP, 100.x.x.x>:8788")), p === 'worker' && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement("h3", {
    style: DT.h3
  }, "Your own server (Pssst Worker) ", /*#__PURE__*/React.createElement(Badge, {
    tone: "danger"
  }, "ADVANCED")), /*#__PURE__*/React.createElement(Panel, {
    padding: 16
  }, /*#__PURE__*/React.createElement(ConnectionFlow, {
    nodes: [{
      label: 'Your PC',
      icon: 'monitor'
    }, {
      label: 'Your Worker',
      icon: 'cloud',
      sub: 'Cloudflare'
    }, {
      label: 'Your phone',
      icon: 'smartphone'
    }]
  })), /*#__PURE__*/React.createElement(Steps, null, /*#__PURE__*/React.createElement(Step, {
    n: 1
  }, "Clone and deploy the Pssst Worker (see its README [link TBD])."), /*#__PURE__*/React.createElement(Step, {
    n: 2,
    detail: /*#__PURE__*/React.createElement(CodeBlock, null, 'npm run hash-token -- <your Sync Token>')
  }, "Hash your Sync Token."), /*#__PURE__*/React.createElement(Step, {
    n: 3,
    detail: /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(CodeBlock, null, "wrangler secret put ALLOWED_TOKEN_HASHES"), /*#__PURE__*/React.createElement("div", {
      style: {
        height: 8
      }
    }), /*#__PURE__*/React.createElement(Callout, {
      kind: "note"
    }, "Without this secret, the Worker refuses every request (HTTP 503) on purpose, so only your token can use it."))
  }, "Store the hash and paste it when asked."), /*#__PURE__*/React.createElement(Step, {
    n: 4
  }, "Deploy, then put the Worker URL in the plugin's ", /*#__PURE__*/React.createElement(S, null, "Server URL"), " and turn on ", /*#__PURE__*/React.createElement(S, null, "Send to My Server"), "."), /*#__PURE__*/React.createElement(Step, {
    n: 5
  }, "In the app, use the same Worker URL and Sync Token."))));
}
window.GuidePaths = GuidePaths;
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/setup-guide/GuidePaths.jsx", error: String((e && e.message) || e) }); }

__ds_ns.Button = __ds_scope.Button;

__ds_ns.Checkbox = __ds_scope.Checkbox;

__ds_ns.FilterChip = __ds_scope.FilterChip;

__ds_ns.SegmentedTabs = __ds_scope.SegmentedTabs;

__ds_ns.Switch = __ds_scope.Switch;

__ds_ns.TextField = __ds_scope.TextField;

__ds_ns.ItemSlot = __ds_scope.ItemSlot;

__ds_ns.ProgressBar = __ds_scope.ProgressBar;

__ds_ns.StatusChip = __ds_scope.StatusChip;

__ds_ns.Callout = __ds_scope.Callout;

__ds_ns.CodeBlock = __ds_scope.CodeBlock;

__ds_ns.ConnectionFlow = __ds_scope.ConnectionFlow;

__ds_ns.DocIcon = __ds_scope.DocIcon;

__ds_ns.DocTable = __ds_scope.DocTable;

__ds_ns.FAN_NOTICE = __ds_scope.FAN_NOTICE;

__ds_ns.FanFooter = __ds_scope.FanFooter;

__ds_ns.SettingChip = __ds_scope.SettingChip;

__ds_ns.Step = __ds_scope.Step;

__ds_ns.StepNumber = __ds_scope.StepNumber;

__ds_ns.AppIcon = __ds_scope.AppIcon;

__ds_ns.DEFAULT_TABS = __ds_scope.DEFAULT_TABS;

__ds_ns.BottomNav = __ds_scope.BottomNav;

__ds_ns.DrawerItem = __ds_scope.DrawerItem;

__ds_ns.TopBar = __ds_scope.TopBar;

__ds_ns.Panel = __ds_scope.Panel;

__ds_ns.Slot = __ds_scope.Slot;

})();
