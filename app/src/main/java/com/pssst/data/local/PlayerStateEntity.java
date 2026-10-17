package com.pssst.data.local;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "player_states")
public class PlayerStateEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    @NonNull
    @ColumnInfo(name = "raw_json")
    private String rawJson;

    @NonNull
    @ColumnInfo(name = "varbits_json")
    private String varbitsJson;

    @NonNull
    @ColumnInfo(name = "slayer_json")
    private String slayerJson;

    @NonNull
    @ColumnInfo(name = "inventory_json")
    private String inventoryJson;

    @NonNull
    @ColumnInfo(name = "bank_json")
    private String bankJson;

    @ColumnInfo(name = "last_synced_at")
    private long lastSyncedAt;

    @NonNull
    @ColumnInfo(name = "ge_offers_json", defaultValue = "'[]'")
    private String geOffersJson = "[]";

    @NonNull
    @ColumnInfo(name = "skills_json", defaultValue = "'[]'")
    private String skillsJson = "[]";

    @NonNull
    @ColumnInfo(name = "potion_storage_json", defaultValue = "'[]'")
    private String potionStorageJson = "[]";

    @NonNull
    @ColumnInfo(name = "quests_json", defaultValue = "'{}'")
    private String questsJson = "{}";

    public PlayerStateEntity(
            @NonNull String uuid,
            long updatedAt,
            @NonNull String rawJson,
            @NonNull String varbitsJson,
            @NonNull String slayerJson,
            @NonNull String inventoryJson,
            @NonNull String bankJson,
            long lastSyncedAt,
            @NonNull String geOffersJson,
            @NonNull String skillsJson,
            @NonNull String potionStorageJson,
            @NonNull String questsJson
    ) {
        this.uuid = uuid;
        this.updatedAt = updatedAt;
        this.rawJson = rawJson;
        this.varbitsJson = varbitsJson;
        this.slayerJson = slayerJson;
        this.inventoryJson = inventoryJson;
        this.bankJson = bankJson;
        this.lastSyncedAt = lastSyncedAt;
        this.geOffersJson = geOffersJson != null ? geOffersJson : "[]";
        this.skillsJson = skillsJson != null ? skillsJson : "[]";
        this.potionStorageJson = potionStorageJson != null ? potionStorageJson : "[]";
        this.questsJson = questsJson != null ? questsJson : "{}";
    }

    @androidx.room.Ignore
    public PlayerStateEntity(
            @NonNull String uuid,
            long updatedAt,
            @NonNull String rawJson,
            @NonNull String varbitsJson,
            @NonNull String slayerJson,
            @NonNull String inventoryJson,
            @NonNull String bankJson,
            long lastSyncedAt
    ) {
        this(uuid, updatedAt, rawJson, varbitsJson, slayerJson, inventoryJson, bankJson, lastSyncedAt, "[]", "[]", "[]", "{}");
    }

    @NonNull
    public String getUuid() { return uuid; }
    public void setUuid(@NonNull String uuid) { this.uuid = uuid; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    @NonNull
    public String getRawJson() { return rawJson; }
    public void setRawJson(@NonNull String rawJson) { this.rawJson = rawJson; }

    @NonNull
    public String getVarbitsJson() { return varbitsJson; }
    public void setVarbitsJson(@NonNull String varbitsJson) { this.varbitsJson = varbitsJson; }

    @NonNull
    public String getSlayerJson() { return slayerJson; }
    public void setSlayerJson(@NonNull String slayerJson) { this.slayerJson = slayerJson; }

    @NonNull
    public String getInventoryJson() { return inventoryJson; }
    public void setInventoryJson(@NonNull String inventoryJson) { this.inventoryJson = inventoryJson; }

    @NonNull
    public String getBankJson() { return bankJson; }
    public void setBankJson(@NonNull String bankJson) { this.bankJson = bankJson; }

    public long getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(long lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }

    @NonNull
    public String getGeOffersJson() { return geOffersJson; }
    public void setGeOffersJson(@NonNull String geOffersJson) { this.geOffersJson = geOffersJson; }

    @NonNull
    public String getSkillsJson() { return skillsJson; }
    public void setSkillsJson(@NonNull String skillsJson) { this.skillsJson = skillsJson; }

    @NonNull
    public String getPotionStorageJson() { return potionStorageJson; }
    public void setPotionStorageJson(@NonNull String potionStorageJson) { this.potionStorageJson = potionStorageJson; }

    @NonNull
    public String getQuestsJson() { return questsJson; }
    public void setQuestsJson(@NonNull String questsJson) { this.questsJson = questsJson; }
}
