# Privacy Policy — Pssst

**Last updated:** October 2026

Pssst is a free, open-source companion app for Old School RuneScape that displays read-only game state snapshots sent from the user's own RuneLite client over their local network.

## 1. No Data Collection or External Transmission

- **Zero Outbound Requests:** The Pssst Android app makes no outbound internet requests. It contains no analytics SDKs, no crash-reporting services, no advertising frameworks, and no third-party tracking.
- **No Central Server or Accounts:** There is no Pssst server and no user account system. All communication occurs directly between the user's PC running RuneLite and the user's Android device over their own private network.

## 2. Data Stored Locally on Your Device

When you opt in to sync in the Pssst Companion RuneLite plugin and pair your phone using your locally generated Sync Token, the Android app stores the following data solely in its private on-device storage:

- **Sync Token & Local Network Address:** Stored in private app preferences (`DataStore` and `SharedPreferences`) to authenticate incoming snapshots from your PC. Both are excluded from Android cloud backups and device-to-device data extraction.
- **Character Snapshot Data:** Read-only game state (inventory, bank, potion storage, skills, quests, Grand Exchange offers, Slayer task, farming patches, birdhouses, and daily/kingdom progress flags) stored in a local on-device SQLite (`Room`) database so you can view your state and receive local timer notifications when away from your PC.
- **User Preferences:** Local UI preferences (such as goal tracker target, bank sort order, and hidden/reordered timer rows).

## 3. Data Deletion

You can delete all locally stored data at any time by clearing the app's storage or uninstalling the Pssst app from your Android device. Tapping **Disconnect / Change Token** in the app clears the active pairing token and stops all background sync work.

## 4. Permissions Used

- **Internet / Network State (`INTERNET`, `ACCESS_NETWORK_STATE`):** Used exclusively to listen for incoming snapshots from your PC on your local network (TCP port `8789`).
- **Notifications (`POST_NOTIFICATIONS`):** Used to post local device notifications when your farming patches, birdhouses, or Slayer tasks complete.

## 5. Contact

If you have questions about this Privacy Policy, please open an issue on GitHub:
https://github.com/Pizzarar/Pssst---OSRS-Companion-App--Android/issues
