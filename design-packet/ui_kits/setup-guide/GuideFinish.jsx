function GuideFinish() {
  const { Step, Callout, DocTable, Panel } = DS;
  const list = (items) => <ul style={{ margin: 0, paddingLeft: 20, display: 'flex', flexDirection: 'column', gap: 4 }}>{items.map(i => <li key={i} style={DT.p}>{i}</li>)}</ul>;
  return <>
    <Section id="app" title="5. Install the app and pair it">
      <Steps>
        <Step n={1}>Install Pssst [APK/Play link TBD].</Step>
        <Step n={2}>Enter your <S>Server URL</S> (for example <S>http://192.168.1.20:8788</S>).</Step>
        <Step n={3}>Paste your <S>Sync Token</S>.</Step>
        <Step n={4}>Leave <S>Remember this Sync Token on this device</S> on if you don't want to paste it again.</Step>
        <Step n={5} detail={<Shot n={6}>the app Setup screen, filled in</Shot>}>Tap <S>Connect &amp; Start Sync</S>.</Step>
      </Steps>
      <Callout kind="warning" title="127.0.0.1 means the phone">Typing <S>127.0.0.1</S> or <S>localhost</S> in the app points at the phone itself, not your PC. The app warns you about this. Use your PC's address instead.</Callout>
    </Section>
    <Section id="check" title="6. Check that it works">
      <Steps>
        <Step n={1} detail={<Shot n={7}>the app dashboard after the first sync</Shot>}>Log into the game. The dashboard fills in after the first sync.</Step>
        <Step n={2} detail={<Shot n={8}>the Timers screen</Shot>}>Open <S>Timers</S>. Patches and birdhouses appear after you've seen them in game.</Step>
      </Steps>
      <Callout kind="note" title="Timers are estimates">Farming and birdhouse timers use RuneLite's Time Tracking logic and can be up to one growth tick early or late. A patch appears or updates only after you've seen it in game. Birdhouses update on Fossil Island.</Callout>
    </Section>
    <Section id="trouble" title="7. Troubleshooting">
      <DocTable columns={['Symptom', 'Likely cause and fix']} rows={[
        ['"Unauthorized"', 'Token mismatch. Re-copy it with no spaces. If you changed it, update the app (and the Worker secret).'],
        ["Can't connect / timeout", <span><S>This PC Only</S> is still on; Windows Firewall is blocking; the phone is on a different network or guest Wi-Fi; or the address is wrong (<S>127.0.0.1</S> means the phone).</span>],
        ['Connected but "no state yet"', <span>Log into the game once after turning on <S>Enable Sync</S>.</span>],
        ['Worker returns 503', <span>The <S>ALLOWED_TOKEN_HASHES</S> secret isn't set.</span>],
        ['A farming patch is missing', 'Visit it once in game.'],
        ['Data looks old', 'The PC or RuneLite is closed (Local Server paths). The app keeps showing the last snapshot.'],
        ['Item values look old', 'They update the next time the plugin syncs. Until then the app uses prices bundled with it.']]} />
    </Section>
    <Section id="privacy" title="8. What is sent, and where">
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(260px,1fr))', gap: 12 }}>
        <Panel padding={16} style={{ display: 'flex', flexDirection: 'column', gap: 8 }}><h3 style={DT.h3}>Sent</h3>{list(['inventory, bank and potion storage (item ids and quantities)', "item values, from RuneLite's own price data (the app makes no price requests of its own)", 'skill levels and XP', 'your own GE offers', 'diary tiers, daily reset claims, Tears of Guthix, Miscellania approval and coffer, NMZ points', 'current Slayer assignment', 'last-seen state of each farming patch and birdhouse'])}</Panel>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <Panel padding={16} style={{ display: 'flex', flexDirection: 'column', gap: 8 }}><h3 style={DT.h3}>Not sent</h3><p style={DT.p}>Username, chat, location, friends, or anything about other players.</p></Panel>
          <Panel padding={16} style={{ display: 'flex', flexDirection: 'column', gap: 8 }}><h3 style={DT.h3}>Where it goes</h3>{list(['only to your phone (Local Server) or to the server you set (Your Own Server)', 'nowhere else', 'no Pssst servers, no accounts'])}</Panel>
        </div>
      </div>
      <Callout kind="note">The Sync Token and your data are never logged. If you change the Sync Token, re-enter it in the app. If you use a Worker, update its secret too.</Callout>
    </Section>
  </>;
}
window.GuideFinish = GuideFinish;
