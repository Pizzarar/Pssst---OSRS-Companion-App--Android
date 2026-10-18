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
