# Pssst — Android app UI kit

Click-through recreation of the Pssst Android app (Jetpack Compose source: `PssstAndroid/app/src/main/java/com/pssst/ui/`).

- **index.html** — Setup screen → Connect & Start Sync → dashboard. Add `?screen=overview|storage|exchange|progress|timers` to open directly on a tab.
- **SetupScreen.jsx** — `ui/setup/SetupScreen.kt` (Server URL, Sync Token, remember toggle, loopback warning).
- **Dashboard.jsx** — `ui/main/DashboardScreen.kt`: top bar, bottom nav, side drawer (connection status, item prices, notifications, jump links, Sync Now, Disconnect).
- **OverviewScreen.jsx** — wealth, Activity Status chips, Slayer task, Miscellania.
- **StorageScreen.jsx** — Bank (search, sort chips, 72px grid), Inventory (4 × 7), Potion Storage.
- **ExchangeScreen.jsx** — 8 GE slots.
- **ProgressScreen.jsx** — Skills (3-col grid) and Quests (search + filters).
- **TimersScreen.jsx** — Dailies, Birdhouses, Patches, Payments.
- **data.js** — sample state (fake numbers).

Item sprites are not included (they're game art, synced at runtime by the plugin); slots fall back to the item name, as the app does when no icon is available.
