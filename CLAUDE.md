# Pssst — Android companion app

Companion to the RuneLite plugin in `PssstRunelite` (see its `CLAUDE.md`). There is no server: the plugin pushes straight to this app.

## Layout
- `app/` is a Kotlin/Compose app using Room, WorkManager, Retrofit, DataStore and Navigation3. Package `com.pssst`. Some data-layer classes are Java (`AppDatabase`, DAOs, entities).
  - **The PC sends, the phone receives** (no server mode; the app makes no outbound requests). `receiver/PhoneReceiver` listens on port 8789 for `POST /sync` (Bearer token compared in constant time, home-network/Tailscale peers only, body <= 256 KB, JSON object only). `receiver/ReceiverHost` keeps it listening while the app is in the foreground (`MainActivity.onStart/onStop`) and for 75 s each time `worker/SyncWorker` runs (the 15-minute periodic job, app open, a new network via `NetworkSyncTrigger`). `data/SnapshotIngest` saves each snapshot (monotonic guard, completion alerts, reminders, Room row, prices, status). `util/PhoneAddresses` shows the address to type into RuneLite (setup screen, Overview card, drawer; on an emulator `127.0.0.1` after `adb forward tcp:8789 tcp:8789`).
  - Icons: only ~1,000 of 21,6k items are bundled. The receiver's 200 reply is `{"ok":true,"needIcons":[ids]}` (up to 100 ids the snapshot or the app itself shows that have no icon: `SnapshotIngest.missingIcons`, `util/IconWishlist`); the plugin draws them from the player's RuneLite and sends `icons: {id: {name, png}}` with a later POST, which `SnapshotIngest` stores (never over a bundled icon, never in the saved snapshot) before saving the snapshot. `ui/main/ItemIcon` shows initials until an icon exists. Body cap is 1 MB.
  - Top bar: pencil (edit lists on Overview, Dailies, Birdhouses, Patches), refresh icon (spins while a listen window runs), and `ReadyStrip` (patches, birdhouses, GE offers, Slayer kills left; `ReadyCounts`). Bank sort chips: Slot order / Total value (qty x price) / Price each / Quantity.
  - Address changes: `PhoneReceiver` reports the phone address each delivery arrived on; `receiver/ReceiverPrefs` keeps it, and `util/AddressChange` compares it with the current Wi-Fi address. When they differ the Overview shows a banner with the new address (not hideable), and `KeepAddressDialog` explains Tailscale, a router reservation, or a static IP (Android doesn't let apps set the address).
  - Editable lists: `ui/main/EditableList` (Edit list → up/down/Hide, Hidden → Show, Reset) on Dailies, Birdhouses, Patches and the Overview cards. `util/ListLayout` is the pure order/hidden logic; `data/local/ListLayouts` stores it per list in SharedPreferences.
  - Farming: `FarmingEntryDto` (from the plugin's `farming[]`) → `util/FarmingTiming` (done time, current stage) → `FarmingPatchUiModel` (status/progress are functions of `nowMs`; the UI supplies a ticking clock via `rememberNowMs`). `util/ReminderPlanner` + `worker/ReminderWorker` queue timed notifications (one per growing patch at its estimated finish; one for the birdhouses at the last seeded house's ready time), keyed by (key, finish time) via work tags.
  - Birdhouses: `BirdhouseEntryDto` -> `BirdHouseUiModel` (status is a function of `nowMs`). Slayer: `SlayerEntryDto` -> `SlayerUiModel`; completion alert in `util/StateDiffAnalyzer` (amount going from >0 to 0). Daily/kingdom logic in `util/StateDiffAnalyzer` and `ui/main/MainViewModel` is still **unverified**; see the Phase 2 queue in the handoff.
  - Item manifest: `assets/items-complete.json` is `{version, generatedAt, items[]}` (21k+ ids: every name incl. noted and untradeable, a price snapshot for tradeable ones, ~1k icons). `AssetDatabaseSeeder` applies it on first run and whenever `version` rises, never replacing a fresher price (`price_updated_at`). Room is at v5 with schemas exported to `app/schemas/` and a tested migration (`AppDatabaseMigrationTest`). Noted inventory items resolve through the plugin's `baseId`.
  - Prices: the plugin sends `prices{id: gp}` from RuneLite's `ItemManager` (held and offered items; noted items under their unnoted id). `SyncWorker` stores them in the manifest (`ItemManifestDao.applyPrices`, stamped with the snapshot time); the bundled snapshot is only the fallback. The app makes **no** outbound requests at all (`GuardrailsTest` fails if a third-party host appears in shipped code).
  - Sync triggers: app open (`MainActivity.onResume`), a new validated network while the process is alive (`NetworkSyncTrigger`/`NetworkChangeGate` from `PssstApplication`, at most once a minute), and the 15-minute periodic job. `syncNow` requires a network and waits for one. With the process dead, the periodic job is the catch-up.
  - Timers and notifications run from the saved snapshot: `SyncScheduler.replanFromCache` queues the reminders from Room on app open and when notifications are switched on, with no network. A sync is not needed to schedule them.
  - Staleness: the top bar shows "Game state from X ago" from the payload's `observedAt` (when the game last fed the snapshot; falls back to `updated_at` for older plugins) and the drawer shows "Checked X ago" (when this phone last fetched). `SyncWorker` ignores a snapshot whose `observedAt` is older than the saved one and stamps prices with `observedAt`.
- `pssst-mock-server/`: a leftover Express test server from before phone push (not used by the app) (`POST /sync`, `GET /state`, `/health`). It binds `0.0.0.0:8787` and persists to `state.json`.

## Commands (verified 2026-10-17)
| What | Command | Notes |
|---|---|---|
| Debug APK | `.\gradlew.bat :app:assembleDebug` | |
| Unit tests | `.\gradlew.bat :app:testDebugUnitTest` | 100 tests, all pass |
| Mock server | `cd pssst-mock-server; npm start` | Leftover; the app doesn't use it |
| Regenerate item manifest | `cd pssst-mock-server; $env:PSSST_CONTACT='https://github.com/<you>/<repo>'; npm run manifest` | 3 GETs (RuneLite names, Wiki mapping + latest); refuses to run without `PSSST_CONTACT`. Icons are carried over, not downloaded. Test: `npm run test:manifest` |
| Mock server test | `cd pssst-mock-server; npm test` | Needs the server already running |

## Environment
- JDK: Temurin 17, set in user-level `~/.gradle/gradle.properties` (`org.gradle.java.home=...`). It is deliberately **not** in either repo's `gradle.properties`: it is machine-specific, and the Plugin Hub builds with its own JDK. A fresh clone needs that line (or `JAVA_HOME` pointing at a JDK 17). Gradle 9.6.0; JVM target 17.
- Android SDK: Path from `local.properties` (`sdk.dir`) or `ANDROID_HOME` / `ANDROID_SDK_ROOT`. Platform `android-36`, build-tools `36.0.0`, platform-tools and the emulator.
- compileSdk/targetSdk 36, minSdk 24.
- From the emulator, reach the host via `http://10.0.2.2:<port>`; for the plugin to reach the emulator's receiver run `adb forward tcp:8789 tcp:8789` and use Phone Address `127.0.0.1`. Cleartext http is allowed globally (`res/xml/network_security_config.xml`) for LAN and Tailscale.

## Cross-repo contract
Payload: `{ observedAt, bankObservedAt, inventory[], bank[]?, potion_storage[], skills[], ge_offers[], varbits{}, varps{}, prices{}, slayer{}, farming[], birdhouses[] }`, with items as `{id, quantity}`. `farming[]` entries are `{regionId, regionName, patchName, patchType, varbitId, value, observedAt, cropName, itemId, cropState, stage, stages, tickMinutes}`: `regionId + varbitId` identify a patch (the varbit alone is a per-region slot), `observedAt` is epoch **ms** and only moves when `value` changes, crop fields are absent for unknown values, and `tickMinutes` is 0 when nothing is advancing. ETA = `floor(observedAt / tick) * tick + (stages - 1 - stage) * tick`, ignoring the farm tick offset (so up to one tick off). `prices{}` maps item id (the unnoted id for noted items) to its GE price in gp, from RuneLite's ItemManager. `varps{}` holds raw varp values (varps and varbits have separate id spaces, so they never share a map); today only NMZ reward points (1060). `slayer` is `{taskName, taskLocation, amount, initialAmount, points, streak}`: `amount` is kills remaining (0 with no names = no task), names are resolved by the plugin from the game's DB tables (as RuneLite's SlayerPlugin does), so no task table exists on Android. Older payloads with a loose `slayer` map using the same field names still parse. Items are `{id, quantity}`; inventory entries for noted items also carry `baseId` (the unnoted id that has the name and price). `birdhouses[]` entries are `{space, spaceName, varpId, value, observedAt, state, houseType, houseItemId, readyAt}`: `state` is SEEDED/BUILT/EMPTY/UNKNOWN per RuneLite's `BirdHouseState` (a non-zero multiple of 3 is seeded, any other non-zero value is built, `(value-1)/3` is the house type), `observedAt` only moves when `value` changes, and `readyAt` (epoch ms) is 50 minutes after the seeded value was first observed (0 when not seeded). The birdhouse varps (1626-1629) are no longer in `varbits{}`; they only hold real values on Fossil Island. The plugin POSTs the bare payload to the phone (`POST /sync`, port 8789); the phone stamps it with its arrival time (ms). The leftover mock server still speaks `{ data, updated_at }`; the app does not use it. `bank` and `potion_storage` are absent when the player turned off Include Bank: the Storage tab then says bank sharing is off. Auth is `Authorization: Bearer <sync token>` only.
`observedAt` / `bankObservedAt` (epoch ms) are set by the plugin: when the game last fed the snapshot, and when the bank was last read from the open bank (carried over across a RuneLite restart). They are separate from `updated_at`, which is when the snapshot was served, so a snapshot the PC saved earlier arrives with a fresh `updated_at` and its original `observedAt`.
Change one side → update `StateDto.kt` **and** the plugin's `StateDTO.java` together.

## Product rules (non-negotiable)
- Free. Read-only and observational. No "do X now" instructions and no AI gear advice.
- Never hotlink or scrape the OSRS Wiki. Price refreshes use the Wiki prices API with a descriptive User-Agent and caching.
- Third-party network features are off by default and carry an IP disclosure (plugin side: `ConfigItem.warning`).
- Never log the bank contents or the bearer token. HTTP logging stays at BASIC with `Authorization` redacted.

## Guardrails
Read `GUIDELINES.md` in the plugin repo (`PssstRunelite`) before touching networking, prices, assets or game logic. It holds the "not Mystix" rules, the five pre-merge questions, and the list of open deviations.

## Roadmap
Open work and plans: see the plugin repo (`PssstRunelite`).

## Workflow
One commit per logical change. Don't push or merge to `main` without asking.
