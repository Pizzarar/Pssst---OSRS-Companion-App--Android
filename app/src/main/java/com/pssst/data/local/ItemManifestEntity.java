package com.pssst.data.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "item_manifest")
public class ItemManifestEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @Nullable
    @ColumnInfo(name = "icon_base64")
    @SerializedName("iconBase64")
    private String iconBase64;

    @ColumnInfo(name = "ge_price", defaultValue = "0")
    @SerializedName("gePrice")
    private long gePrice;

    /**
     * When {@link #gePrice} was measured, in epoch ms. 0 means "never" (no price). The bundled manifest stamps its
     * generation time; a refresh stamps its fetch time. A reseed never replaces a price with an older one.
     */
    @ColumnInfo(name = "price_updated_at", defaultValue = "0")
    @SerializedName("pricedAt")
    private long priceUpdatedAt;

    public ItemManifestEntity(int id, @NonNull String name, @Nullable String iconBase64, long gePrice, long priceUpdatedAt) {
        this.id = id;
        this.name = name;
        this.iconBase64 = iconBase64;
        this.gePrice = gePrice;
        this.priceUpdatedAt = priceUpdatedAt;
    }

    @Ignore
    public ItemManifestEntity(int id, @NonNull String name, @Nullable String iconBase64, long gePrice) {
        this(id, name, iconBase64, gePrice, 0L);
    }

    @Ignore
    public ItemManifestEntity(int id, @NonNull String name, @Nullable String iconBase64) {
        this(id, name, iconBase64, 0L, 0L);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public String getIconBase64() {
        return iconBase64;
    }

    public void setIconBase64(@Nullable String iconBase64) {
        this.iconBase64 = iconBase64;
    }

    public long getGePrice() {
        return gePrice;
    }

    public void setGePrice(long gePrice) {
        this.gePrice = gePrice;
    }

    public long getPriceUpdatedAt() {
        return priceUpdatedAt;
    }

    public void setPriceUpdatedAt(long priceUpdatedAt) {
        this.priceUpdatedAt = priceUpdatedAt;
    }
}
