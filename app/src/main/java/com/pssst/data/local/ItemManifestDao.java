package com.pssst.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ItemManifestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemManifestEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemManifestEntity item);

    @Query("SELECT COUNT(*) FROM item_manifest")
    int count();

    @Query("SELECT * FROM item_manifest WHERE id = :id LIMIT 1")
    ItemManifestEntity getItemById(int id);

    @Query("SELECT * FROM item_manifest WHERE id IN (:ids)")
    List<ItemManifestEntity> getItemsByIds(List<Integer> ids);

    @Query("SELECT * FROM item_manifest WHERE name LIKE '%' || :query || '%' LIMIT :limit")
    List<ItemManifestEntity> searchItemsByName(String query, int limit);

    @Query("SELECT * FROM item_manifest")
    List<ItemManifestEntity> getAll();

    @Query("DELETE FROM item_manifest")
    void clearAll();
}
