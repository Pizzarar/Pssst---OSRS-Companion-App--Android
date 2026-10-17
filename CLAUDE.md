# Pssst — Android companion app

Companion to the RuneLite plugin at `L:\Dev\Plugins\PssstRunelite` and the optional cloud relay at `L:\Dev\Plugins\PssstWorker` (see their `CLAUDE.md` files).

## Layout
- `app/` is a Kotlin/Compose app using Room, WorkManager, Retrofit, DataStore and Navigation3. Package `com.pssst`. Some data-layer classes are Java (`AppDatabase`, DAOs, entities).
  - `worker/SyncWorker` polls `GET /state` every 15 min and writes to Room. `SyncScheduler` schedules the sync and the timed reminders.
  - `data/network/StateApiService` builds the URL; the default base is `https://pssst.rosserlogic.tech/`. `data/network/model/StateDto.kt` is the payload.
  - Farming: `FarmingEntryDto` (from the plugin's `farming[]`) → `util/FarmingTiming` (done time, current stage) → `FarmingPatchUiModel` (status/progress are functions of `nowMs`; the UI supplies a ticking clock via `rememberNowMs`). `util/ReminderPlanner` + `worker/ReminderWorker` queue timed notifications (one per growing patch at its estimated finish; one for the birdhouses at the last seeded house's ready time), keyed by (key, finish time) via work tags.
  - Birdhouses: `BirdhouseEntryDto` -> `BirdHouseUiModel` (status is a function of `nowMs`). Slayer: `SlayerEntryDto` -> `SlayerUiModel`; completion alert in `util/StateDiffAnalyzer` (amount going from >0 to 0). Daily/kingdom logic in `util/StateDiffAnalyzer` and `ui/main/MainViewModel` is still **unverified**; see the Phase 2 queue in the handoff.
  - `assets/items-complete.json` is the item manifest (5,083 items, stale `gePrice`), seeded into Room by `AssetDatabaseSeeder`.
- `pssst-mock-server/`: an Express stand-in for the Worker (`POST /sync`, `GET /state`, `/health`). It binds `0.0.0.0:8787` and persists to `state.json`.

## Commands (verified 2026-10-17)
| What | Command | Notes |
|---|---|---|
| Debug APK | `.\gradlew.bat :app:assembleDebug` | |
| Unit tests | `.\gradlew.bat :app:testDebugUnitTest` | 39 tests, all pass |
| Mock server | `cd pssst-mock-server; npm start` | Port 8787 clashes with `wrangler dev`'s default (`PssstWorker`); don't run both |
| Mock server test | `cd pssst-mock-server; npm test` | Needs the server already running |

## Environment
- JDK: Temurin 17.0.20.1 at `C:/Users/aubro/.gradle/jdks/eclipse_adoptium-17-amd64-windows.2`, set once in the user-level `~/.gradle/gradle.properties` (`org.gradle.java.home=...`). It is deliberately **not** in either repo's `gradle.properties`: it is machine-specific, and the Plugin Hub builds with its own JDK. A fresh clone needs that line (or `JAVA_HOME` pointing at a JDK 17). The repo-local `.jdk17/` is the same version, unused, and gitignored. Gradle 9.6.0; JVM target 17.
- Android SDK: `C:\Users\aubro\AppData\Local\Android\Sdk` (from `local.properties`). It has platform `android-36`, build-tools `36.0.0`, platform-tools and the emulator. **No `cmdline-tools`**, so `sdkmanager` isn't available; install it from Android Studio → SDK Manager → SDK Tools if needed.
- compileSdk/targetSdk 36, minSdk 24.
- From the emulator, reach the host via `http://10.0.2.2:<port>`. Cleartext http is allowed globally (`res/xml/network_security_config.xml`) for LAN and Tailscale.

## Cross-repo contract
Payload: `{ inventory[], bank[], potion_storage[], skills[], ge_offers[], varbits{}, varps{}, slayer{}, farming[], birdhouses[] }`, with items as `{id, quantity}`. `farming[]` entries are `{regionId, regionName, patchName, patchType, varbitId, value, observedAt, cropName, itemId, cropState, stage, stages, tickMinutes}`: `regionId + varbitId` identify a patch (the varbit alone is a per-region slot), `observedAt` is epoch **ms** and only moves when `value` changes, crop fields are absent for unknown values, and `tickMinutes` is 0 when nothing is advancing. ETA = `floor(observedAt / tick) * tick + (stages - 1 - stage) * tick`, ignoring the farm tick offset (so up to one tick off). `varps{}` holds raw varp values (varps and varbits have separate id spaces, so they never share a map); today only NMZ reward points (1060). `slayer` is `{taskName, taskLocation, amount, initialAmount, points, streak}`: `amount` is kills remaining (0 with no names = no task), names are resolved by the plugin from the game's DB tables (as RuneLite's SlayerPlugin does), so no task table exists on Android. Older payloads with a loose `slayer` map using the same field names still parse. `birdhouses[]` entries are `{space, spaceName, varpId, value, observedAt, state, houseType, houseItemId, readyAt}`: `state` is SEEDED/BUILT/EMPTY/UNKNOWN per RuneLite's `BirdHouseState` (a non-zero multiple of 3 is seeded, any other non-zero value is built, `(value-1)/3` is the house type), `observedAt` only moves when `value` changes, and `readyAt` (epoch ms) is 50 minutes after the seeded value was first observed (0 when not seeded). The birdhouse varps (1626-1629) are no longer in `varbits{}`; they only hold real values on Fossil Island. The Worker, the mock server and the plugin's local server (port 8788) wrap it as `{ data, updated_at }`. `updated_at` is seconds from the Worker and ms from the plugin's local server and the mock. `SyncWorker` normalizes to ms. Auth is `Authorization: Bearer <sync token>` only. **Open item:** `DEFAULT_BASE_URL` (`https://pssst.rosserlogic.tech/`) does not resolve (NXDOMAIN); deploy the Worker there or clear the default.
Change one side → update `StateDto.kt` **and** the plugin's `StateDTO.java` together, plus both servers if the envelope changes.

## Product rules (non-negotiable)
- Free. Read-only and observational. No "do X now" instructions and no AI gear advice.
- Never hotlink or scrape the OSRS Wiki. Price refreshes use the Wiki prices API with a descriptive User-Agent and caching.
- Third-party network features are off by default and carry an IP disclosure (plugin side: `ConfigItem.warning`).
- Never log the bank contents or the bearer token. HTTP logging stays at BASIC with `Authorization` redacted.

## Workflow
Feature branches named `claude/*`, one commit per logical change. Don't push or merge to `main` without asking.
