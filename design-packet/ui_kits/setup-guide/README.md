# Pssst — Setup guide UI kit

The user-facing setup guide (`docs/setup.md` outline in the plugin repo) rendered in the Pssst docs style from the art packet.

- **index.html** — single-page guide with sticky table of contents, connection-path switcher and a Dark / Parchment toggle.
- **DocsShell.jsx** — header, TOC sidebar, footer; shared helpers (`Section`, `Steps`, `Shot`, `Badge`).
- **GuideIntro.jsx** — sections 1–3.
- **GuidePaths.jsx** — section 4 (Same Wi-Fi, Tailscale / VPN, Your own server).
- **GuideFinish.jsx** — sections 5–8.

Screenshot slots are placeholders for the eight real captures listed in the handoff. Plugin Hub / Play links are deliberately "[link TBD]".
