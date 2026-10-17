# Pssst Ecosystem Architectural Status Brief

The Pssst ecosystem architecture consists of a RuneLite companion plugin emitting player state snapshots, an Express mock server providing local sync endpoints, and a Kotlin/Compose Android client consuming state data by UUID.

---

## 1. RuneLite Plugin `StateDTO` JSON Schema

Emitted via HTTP POST using Gson serialization from `net.runelite.client.plugins.companion.StateDTO`:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "inventory": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "id": { "type": "integer" },
          "quantity": { "type": "integer" }
        },
        "required": ["id", "quantity"]
      }
    },
    "bank": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "id": { "type": "integer" },
          "quantity": { "type": "integer" }
        },
        "required": ["id", "quantity"]
      }
    },
    "varbits": {
      "type": "object",
      "additionalProperties": { "type": "integer" }
    },
    "slayer": {
      "type": "object",
      "properties": {
        "taskName": { "type": "string" },
        "amount": { "type": ["integer", "string"] }
      }
    }
  },
  "required": ["inventory", "bank", "varbits", "slayer"]
}
```

---

## 2. Mock Server Host/Port and Active Route Handlers

Located at `pssst-mock-server/server.js`:

- **Host & Port**: Listens on `0.0.0.0` with port default `8787` (`process.env.PORT || 8787`).
- **Route Handlers**:
  - `GET /health`: No auth. Returns status 200 `{ status: "ok", activeUsers: <count> }`.
  - `POST /sync`: Requires header `Authorization: Bearer <UUID>`. Accepts state payload (wrapping under `data` and setting `updated_at: Date.now()`). Returns status 200 `{ success: true }`.
  - `GET /state`: Requires header `Authorization: Bearer <UUID>`. Returns status 200 `{ data: {...}, updated_at: <timestamp> }` or 404 `{ error: "State not found for UUID" }`.

---

## 3. Android App Item ID Resolution

The Android app displays raw numbers:
- It does **not** bundle a static item mapping JSON.
- It does **not** query a remote Wiki API or external service.
- `InventoryScreen.kt` and `BankScreen.kt` render the raw ID formatted as `"#${item.id}"` alongside formatted quantities (`formattedQuantity`).

---

## 4. Cleartext HTTP Traffic in AndroidManifest.xml

No, `AndroidManifest.xml` does **not** enable cleartext HTTP traffic. Neither `android:usesCleartextTraffic="true"` nor an `android:networkSecurityConfig` declaration is present in the `<application>` tag, meaning Android 9+ (API 28+) will block plaintext `http://` traffic to a local mock server by default unless updated or using an HTTPS tunnel.

---

## 5. File Trees

### A. RuneLite Plugin & Cloudflare Backend (`L:\Dev\Plugins\PssstRunelite`)
```
L:\Dev\Plugins\PssstRunelite\
├── .gitignore
├── package.json
├── package-lock.json
├── README.md
├── schema.sql
├── tsconfig.json
├── wrangler.toml
└── src\
    ├── index.ts
    └── main\
        └── java\
            └── net\
                └── runelite\
                    └── client\
                        └── plugins\
                            └── companion\
                                ├── CompanionConfig.java
                                ├── CompanionPlugin.java
                                ├── NetworkDispatcher.java
                                └── StateDTO.java
```

### B. Mock Server (`L:\Dev\Mobile\PssstAndroid\pssst-mock-server`)
```
L:\Dev\Mobile\PssstAndroid\pssst-mock-server\
├── package.json
├── package-lock.json
├── server.js
└── test.js
```

### C. Android App (`L:\Dev\Mobile\PssstAndroid`)
```
L:\Dev\Mobile\PssstAndroid\
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
├── settings.gradle.kts
├── gradle\
│   ├── libs.versions.toml
│   └── wrapper\
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── app\
    ├── build.gradle.kts
    └── src\
        ├── androidTest\
        │   └── java\
        │       └── com\
        │           └── pssst\
        │               └── ui\
        │                   └── main\
        │                       └── MainScreenTest.kt
        ├── test\
        │   └── java\
        │       └── com\
        │           └── pssst\
        │               ├── ui\
        │               │   ├── main\
        │               │   │   └── MainViewModelFlowTest.kt
        │               │   └── setup\
        │               │       └── SetupFlowTest.kt
        │               └── worker\
        │                   └── SyncWorkerTest.kt
        └── main\
            ├── AndroidManifest.xml
            ├── java\
            │   └── com\
            │       └── pssst\
            │           ├── MainActivity.kt
            │           ├── Navigation.kt
            │           ├── NavigationKeys.kt
            │           ├── data\
            │           │   ├── local\
            │           │   │   ├── AppDatabase.java
            │           │   │   ├── PlayerStateDao.java
            │           │   │   ├── PlayerStateEntity.java
            │           │   │   └── UserPreferencesRepository.kt
            │           │   └── network\
            │           │       ├── StateApiService.kt
            │           │       └── model\
            │           │           └── StateDto.kt
            │           ├── notification\
            │           │   └── NotificationHelper.kt
            │           ├── theme\
            │           │   ├── Color.kt
            │           │   ├── Theme.kt
            │           │   └── Type.kt
            │           ├── ui\
            │           │   ├── main\
            │           │   │   ├── BankScreen.kt
            │           │   │   ├── DashboardScreen.kt
            │           │   │   ├── InventoryScreen.kt
            │           │   │   ├── MainUiState.kt
            │           │   │   ├── MainViewModel.kt
            │           │   └── TimersScreen.kt
            │           ├── setup\
            │           │   └── SetupScreen.kt
            │           ├── util\
            │           │   └── StateDiffAnalyzer.kt
            │           └── worker\
            │               ├── SyncScheduler.kt
            │               └── SyncWorker.kt
            └── res\
                ├── drawable\
                │   ├── ic_launcher_background.xml
                │   └── ic_launcher_foreground.xml
                ├── mipmap-anydpi-v26\
                │   ├── ic_launcher.xml
                │   └── ic_launcher_round.xml
                ├── mipmap-hdpi\
                │   ├── ic_launcher.webp
                │   └── ic_launcher_round.webp
                ├── mipmap-mdpi\
                │   ├── ic_launcher.webp
                │   └── ic_launcher_round.webp
                ├── mipmap-xhdpi\
                │   ├── ic_launcher.webp
                │   └── ic_launcher_round.webp
                ├── mipmap-xxhdpi\
                │   ├── ic_launcher.webp
                │   └── ic_launcher_round.webp
                ├── mipmap-xxxhdpi\
                │   ├── ic_launcher.webp
                │   └── ic_launcher_round.webp
                ├── values\
                │   ├── strings.xml
                │   └── themes.xml
                └── xml\
                    ├── backup_rules.xml
                    └── data_extraction_rules.xml
```
