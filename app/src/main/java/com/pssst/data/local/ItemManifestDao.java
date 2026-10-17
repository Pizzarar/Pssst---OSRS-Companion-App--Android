package com.pssst.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.List;
import java.util.Map;

@Dao
public abstract class ItemManifestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<ItemManifestEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ItemManifestEntity item);

    @Query("SELECT COUNT(*) FROM item_manifest")
    public abstract int count();

    @Query("SELECT * FROM item_manifest WHERE id = :id LIMIT 1")
    public abstract ItemManifestEntity getItemById(int id);

    @Query("SELECT * FROM item_manifest WHERE id IN (:ids)")
    public abstract List<ItemManifestEntity> getItemsByIds(List<Integer> ids);

    @Query("SELECT * FROM item_manifest WHERE name LIKE '%' || :query || '%' LIMIT :limit")
    public abstract List<ItemManifestEntity> searchItemsByName(String query, int limit);

    @Query("SELECT * FROM item_manifest")
    public abstract List<ItemManifestEntity> getAll();

    @Query("DELETE FROM item_manifest")
    public abstract void clearAll();

    /** The newest price timestamp in the table, i.e. when prices were last refreshed or generated. 0 if none. */
    @Query("SELECT COALESCE(MAX(price_updated_at), 0) FROM item_manifest")
    public abstract long latestPriceTimestamp();

    // --- Reseeding from a newer bundled manifest ------------------------------------------------------------
    // Two statements instead of an upsert: SQLite's ON CONFLICT DO UPDATE needs Android 11, minSdk here is 24.

    @Query("INSERT OR IGNORE INTO item_manifest (id, name, icon_base64, ge_price, price_updated_at) "
        + "VALUES (:id, :name, :icon, :price, :pricedAt)")
    protected abstract void insertBakedIfAbsent(int id, String name, String icon, long price, long pricedAt);

    /** Takes the bundled name and icon, but takes the bundled price only if it is newer than the stored one. */
    @Query("UPDATE item_manifest SET name = :name, icon_base64 = COALESCE(:icon, icon_base64), "
        + "ge_price = CASE WHEN :pricedAt > price_updated_at THEN :price ELSE ge_price END, "
        + "price_updated_at = MAX(:pricedAt, price_updated_at) WHERE id = :id")
    protected abstract void applyBaked(int id, String name, String icon, long price, long pricedAt);

    @Transaction
    public void mergeBundled(List<ItemManifestEntity> items) {
        for (ItemManifestEntity item : items) {
            insertBakedIfAbsent(item.getId(), item.getName(), item.getIconBase64(), item.getGePrice(), item.getPriceUpdatedAt());
            applyBaked(item.getId(), item.getName(), item.getIconBase64(), item.getGePrice(), item.getPriceUpdatedAt());
        }
    }

    // --- Price refresh --------------------------------------------------------------------------------------

    @Query("UPDATE item_manifest SET ge_price = :price, price_updated_at = :at WHERE id = :id")
    protected abstract void setPrice(int id, long price, long at);

    /** Applies freshly fetched prices (item id to price); ids that are not in the manifest are ignored. */
    @Transaction
    public void applyPrices(Map<Integer, Long> prices, long at) {
        for (Map.Entry<Integer, Long> e : prices.entrySet()) {
            setPrice(e.getKey(), e.getValue(), at);
        }
    }
}
