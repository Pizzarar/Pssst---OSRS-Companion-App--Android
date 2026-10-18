function GuideIntro() {
  const { Step, Callout, Panel } = DS;
  return <>
    <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
      <h1 style={DT.h1}>Setting up Pssst</h1>
      <p style={DT.p}>Pssst is a free, read-only companion for Old School RuneScape. A RuneLite plugin, <S>Pssst Companion</S>, makes a snapshot of what the game already shows you available to the Pssst app on your phone: bank, inventory and potion storage, skills, your Grand Exchange offers, farming and birdhouse timers, Slayer task, and daily and diary progress.</p>
      <p style={DT.p}>There is no Pssst account and no Pssst server. Data goes from your PC to your phone, directly or through a server you run yourself.</p>
    </div>
    <Section id="need" title="1. What you need">
      <Panel padding={16} style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        {[['monitor', 'A Windows PC with RuneLite installed'], ['smartphone', 'An Android phone'], ['wifi', 'Both on the same Wi-Fi, or both on Tailscale (or your own server, for advanced users)']].map(([i, t]) => <div key={i} style={{ display: 'flex', gap: 12, alignItems: 'center' }}><DS.DocIcon name={i} /><span style={DT.p}>{t}</span></div>)}
      </Panel>
    </Section>
    <Section id="install" title="2. Install Pssst Companion in RuneLite">
      <Steps>
        <Step n={1}>Install Pssst Companion from the Plugin Hub [link TBD].</Step>
        <Step n={2} detail={<Shot n={1}>RuneLite → Configuration → Pssst Companion, all three sections visible</Shot>}>Open <S>RuneLite → Configuration → Pssst Companion</S>. You'll see three sections: <S>Master Sync</S>, <S>Local Server</S> and <S>Your Own Server</S>.</Step>
      </Steps>
    </Section>
    <Section id="sync" title="3. Turn on sync and copy your Sync Token">
      <Steps>
        <Step n={1} detail={<Shot n={2}>the Enable Sync warning</Shot>}>In <S>Master Sync</S>, turn on <S>Enable Sync</S>. Nothing leaves RuneLite until it's on. RuneLite shows a warning: "This feature submits your IP address to a 3rd-party server not controlled or verified by RuneLite developers."</Step>
        <Step n={2}>Copy the <S>Sync Token</S>. It was generated the first time the plugin ran.</Step>
      </Steps>
      <Callout kind="warning" title="Keep your Sync Token private">It's the password your phone uses to read your data.</Callout>
    </Section>
  </>;
}
window.GuideIntro = GuideIntro;
