function SameWifiSteps({ addr = "http://<that address>:8788" }) {
  const { Step, Callout, CodeBlock } = DS;
  return <Steps>
    <Step n={1}>Install Pssst Companion and turn on <S>Enable Sync</S>.</Step>
    <Step n={2}>Copy the <S>Sync Token</S>.</Step>
    <Step n={3} detail={<><Callout kind="warning" title="Only on a network you trust">Other devices on your network can then reach this PC on that port. They still need the Sync Token, and the connection is not encrypted.</Callout><div style={{ height: 8 }} /><Shot n={3}>the This PC Only warning</Shot></>}>Turn off <S>This PC Only</S> and accept the warning.</Step>
    <Step n={4} detail={<Shot n={4}>the Windows Firewall prompt</Shot>}>If Windows Firewall asks about Java/RuneLite, allow it on <S>Private</S> networks.</Step>
    <Step n={5} detail={<><CodeBlock label="Command Prompt">ipconfig</CodeBlock><div style={{ height: 8 }} /><Shot n={5}>ipconfig output with the IPv4 line highlighted</Shot></>}>Find the PC's IPv4 address: run <S>ipconfig</S> and look under your active Wi-Fi or Ethernet adapter.</Step>
    <Step n={6} detail={<CodeBlock label="App → Server URL" copyable={false}>{addr}</CodeBlock>}>In the app, enter the address, paste the token, and tap <S>Connect &amp; Start Sync</S>.</Step>
    <Step n={7}>Log into the game. The app refreshes when opened and about every 15 minutes.</Step>
  </Steps>;
}
function GuidePaths() {
  const { Panel, SegmentedTabs, ConnectionFlow, Callout, Step, CodeBlock, DocTable } = DS;
  const [p, setP] = React.useState('wifi');
  return <Section id="paths" title="4. Choose how your phone reaches your PC">
    <DocTable columns={['', 'Same Wi-Fi', 'Tailscale / VPN', 'Your own server']} rows={[
      ['Works when', 'Phone and PC are on the same network', 'Anywhere, while the PC and RuneLite are running', 'Anywhere, even with the PC off (shows the last snapshot)'],
      ['Difficulty', <Badge>EASY</Badge>, <Badge>MEDIUM</Badge>, <Badge tone="danger">ADVANCED</Badge>]]} />
    <SegmentedTabs value={p} onChange={setP} tabs={[{ id: 'wifi', label: 'Same Wi-Fi' }, { id: 'vpn', label: 'Tailscale / VPN' }, { id: 'worker', label: 'Your own server' }]} />
    {p === 'wifi' && <>
      <h3 style={DT.h3}>Same Wi-Fi (Local Server)</h3>
      <Panel padding={16}><ConnectionFlow nodes={[{ label: 'Your PC', icon: 'monitor', sub: ':8788' }, { label: 'Wi-Fi', icon: 'wifi' }, { label: 'Your phone', icon: 'smartphone' }]} /></Panel>
      <SameWifiSteps addr={"http://<PC's IPv4>:8788"} />
    </>}
    {p === 'vpn' && <>
      <h3 style={DT.h3}>Tailscale or another VPN</h3>
      <Panel padding={16}><ConnectionFlow nodes={[{ label: 'Your PC', icon: 'monitor', sub: '100.x.x.x' }, { label: 'Tailscale', icon: 'network' }, { label: 'Your phone', icon: 'smartphone' }]} /></Panel>
      <p style={DT.p}>Install Tailscale on both devices and sign in to the same account. Then follow the Same Wi-Fi steps using the PC's 100.x Tailscale address.</p>
      <CodeBlock label="App → Server URL" copyable={false}>{"http://<PC's Tailscale IP, 100.x.x.x>:8788"}</CodeBlock>
    </>}
    {p === 'worker' && <>
      <h3 style={DT.h3}>Your own server (Pssst Worker) <Badge tone="danger">ADVANCED</Badge></h3>
      <Panel padding={16}><ConnectionFlow nodes={[{ label: 'Your PC', icon: 'monitor' }, { label: 'Your Worker', icon: 'cloud', sub: 'Cloudflare' }, { label: 'Your phone', icon: 'smartphone' }]} /></Panel>
      <Steps>
        <Step n={1}>Clone and deploy the Pssst Worker (see its README [link TBD]).</Step>
        <Step n={2} detail={<CodeBlock>{'npm run hash-token -- <your Sync Token>'}</CodeBlock>}>Hash your Sync Token.</Step>
        <Step n={3} detail={<><CodeBlock>wrangler secret put ALLOWED_TOKEN_HASHES</CodeBlock><div style={{ height: 8 }} /><Callout kind="note">Without this secret, the Worker refuses every request (HTTP 503) on purpose, so only your token can use it.</Callout></>}>Store the hash and paste it when asked.</Step>
        <Step n={4}>Deploy, then put the Worker URL in the plugin's <S>Server URL</S> and turn on <S>Send to My Server</S>.</Step>
        <Step n={5}>In the app, use the same Worker URL and Sync Token.</Step>
      </Steps>
    </>}
  </Section>;
}
window.GuidePaths = GuidePaths;
