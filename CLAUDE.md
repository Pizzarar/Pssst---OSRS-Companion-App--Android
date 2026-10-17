# Pssst — Android companion app

Companion to the RuneLite plugin + Worker at `L:\Dev\Plugins\PssstRunelite` (see its `CLAUDE.md`).

## Layout
- `app/` is a Kotlin/Compose app using Room, WorkManager, Retrofit, DataStore and Navigation3. Package `com.pssst`. Some data-layer classes are Java (`AppDatabase`, DAOs, entities).
  - `worker/SyncWorker` polls `GET /state` every 15 min and writes to Room. `SyncScheduler` and `BirdhouseReminderWorker` handle scheduling and reminders.
  - `data/network/StateApiService` builds the URL; the default base is `https://pssst.rosserlogic.tech/`. `data/network/model/StateDto.kt` is the payload.
  - `util/StateDiffAnalyzer`, `ui/main/MainViewModel` hold the farming/birdhouse/slayer logic. **Known wrong**; see the Phase 2 queue in the handoff.
  - `assets/items-complete.json` is the item manifest (5,083 items, stale `gePrice`), seeded into Room by `AssetDatabaseSeeder`.
- `pssst-mock-server/`: an Express stand-in for the Worker (`POST /sync`, `GET /state`, `/health`). It binds `0.0.0.0:8787` and persists to `state.json`.

## Commands (verified 2026-10-17)
| What | Command | Notes |
|---|---|---|
| Debug APK | `.\gradlew.bat :app:assembleDebug` | |
| Unit tests | `.\gradlew.bat :app:testDebugUnitTest` | 28 tests, all pass |
| Mock server | `cd pssst-mock-server; npm start` | Port 8787 clashes with `wrangler dev`'s default; don't run both |
| Mock server test | `cd pssst-mock-server; npm test` | Needs the server already running |

## Environment
- JDK: Temurin 17.0.20.1 at `C:/Users/aubro/.gradle/jdks/eclipse_adoptium-17-amd64-windows.2`, pinned by `org.gradle.java.home` in `gradle.properties`. The repo-local `.jdk17/` is the same version, unused, and gitignored. Gradle 9.6.0; JVM target 17.
- Android SDK: `C:\Users\aubro\AppData\Local\Android\Sdk` (from `local.properties`). It has platform `android-36`, build-tools `36.0.0`, platform-tools and the emulator. **No `cmdline-tools`**, so `sdkmanager` isn't available; install it from Android Studio → SDK Manager → SDK Tools if needed.
- compileSdk/targetSdk 36, minSdk 24.
- From the emulator, reach the host via `http://10.0.2.2:<port>`. Cleartext http is allowed globally (`res/xml/network_security_config.xml`) for LAN and Tailscale.

## Cross-repo contract
Payload: `{ inventory[], bank[], potion_storage[], skills[], ge_offers[], varbits{} }`, with items as `{id, quantity}`. The Worker, the mock server and the plugin's local server (port 8788) wrap it as `{ data, updated_at }`. `updated_at` is seconds from the Worker and ms from the plugin WS and the mock. `SyncWorker` normalizes to ms.
Change one side → update `StateDto.kt` **and** the plugin's `StateDTO.java` together, plus both servers if the envelope changes.

## Product rules (non-negotiable)
- Free. Read-only and observational. No "do X now" instructions and no AI gear advice.
- Never hotlink or scrape the OSRS Wiki. Price refreshes use the Wiki prices API with a descriptive User-Agent and caching.
- Third-party network features are off by default and carry an IP disclosure (plugin side: `ConfigItem.warning`).
- Never log the bank contents or the bearer token. HTTP logging stays at BASIC with `Authorization` redacted.

## Workflow
Feature branches named `claude/*`, one commit per logical change. Don't push or merge to `main` without asking.
