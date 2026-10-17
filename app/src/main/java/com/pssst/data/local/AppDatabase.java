package com.pssst.data.local;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {PlayerStateEntity.class, ItemManifestEntity.class}, version = 5, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlayerStateDao playerStateDao();
    public abstract ItemManifestDao itemManifestDao();

    /** Adds the timestamp that lets a refreshed price survive a reseed from a newer bundled manifest. */
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE item_manifest ADD COLUMN price_updated_at INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "pssst_database"
                    )
                    .addMigrations(MIGRATION_4_5)
                    // Safety net for a future version someone forgets to migrate. MigrationTest covers the real ones.
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
