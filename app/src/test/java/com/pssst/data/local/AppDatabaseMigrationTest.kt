package com.pssst.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Opens a real v4 database (built from the exported schema in app/schemas), runs the migration, and lets Room
 * validate the result against the v5 schema. Without this a wrong migration would silently fall back to the
 * destructive wipe, which would throw away the cached player state.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java)

    @Test
    fun migrate4To5_keepsRowsAndAddsTheUnsetPriceTimestamp() {
        helper.createDatabase(DB_NAME, 4).apply {
            execSQL("INSERT INTO item_manifest (id, name, icon_base64, ge_price) VALUES (4151, 'Abyssal whip', NULL, 1500000)")
            execSQL(
                "INSERT INTO player_states (uuid, updated_at, raw_json, varbits_json, slayer_json, inventory_json, " +
                    "bank_json, last_synced_at) VALUES ('abc', 1, '{}', '{}', '{}', '[]', '[]', 2)"
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(DB_NAME, 5, true, AppDatabase.MIGRATION_4_5)

        migrated.query("SELECT name, ge_price, price_updated_at FROM item_manifest WHERE id = 4151").use { c ->
            assertEquals(1, c.count)
            c.moveToFirst()
            assertEquals("Abyssal whip", c.getString(0))
            assertEquals(1_500_000L, c.getLong(1))
            // An old baked price has no known age; the next reseed or refresh with a timestamp may replace it
            assertEquals(0L, c.getLong(2))
        }
        migrated.query("SELECT COUNT(*) FROM player_states WHERE uuid = 'abc'").use { c ->
            c.moveToFirst()
            assertEquals("cached player state must survive the migration", 1, c.getInt(0))
        }
    }

    private companion object {
        const val DB_NAME = "migration-test"
    }
}
