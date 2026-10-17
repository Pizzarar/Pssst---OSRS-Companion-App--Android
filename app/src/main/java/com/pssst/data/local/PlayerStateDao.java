package com.pssst.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PlayerStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(PlayerStateEntity entity);

    @Query("SELECT * FROM player_states WHERE uuid = :uuid LIMIT 1")
    PlayerStateEntity getStateByUuid(String uuid);

    @Query("SELECT * FROM player_states WHERE uuid = :uuid LIMIT 1")
    kotlinx.coroutines.flow.Flow<PlayerStateEntity> getStateFlow(String uuid);

    @Query("SELECT * FROM player_states ORDER BY updated_at DESC LIMIT 1")
    PlayerStateEntity getLatestState();

    // No LIMIT: MainViewModel picks the row matching the active UUID; LIMIT 1 made that a no-op
    @Query("SELECT * FROM player_states ORDER BY updated_at DESC")
    kotlinx.coroutines.flow.Flow<List<PlayerStateEntity>> getLatestStateFlow();

    @Query("SELECT * FROM player_states")
    List<PlayerStateEntity> getAllStates();

    @Query("DELETE FROM player_states WHERE uuid = :uuid")
    void deleteByUuid(String uuid);

    @Query("DELETE FROM player_states")
    void clearAll();
}
