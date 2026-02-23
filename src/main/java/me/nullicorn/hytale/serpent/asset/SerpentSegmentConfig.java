package me.nullicorn.hytale.serpent.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;

public final class SerpentSegmentConfig implements JsonAssetWithMap<String, DefaultAssetMap<String, SerpentSegmentConfig>> {
    public static final String PATH = "NPC/SerpentSegmentConfig";

    public static final AssetBuilderCodec<String, SerpentSegmentConfig> CODEC = AssetBuilderCodec.builder(
            SerpentSegmentConfig.class,
            SerpentSegmentConfig::new,
            Codec.STRING,
            (asset, s) -> asset.id = s,
            asset -> asset.id,
            (asset, data) -> asset.extraData = data,
            asset -> asset.extraData
        )
        .appendInherited(
            new KeyedCodec<>("Model", Codec.STRING, true),
            (serpentSegmentConfig, s) -> serpentSegmentConfig.modelAssetId = s,
            serpentSegmentConfig -> serpentSegmentConfig.modelAssetId,
            (serpentSegmentConfig, parent) -> serpentSegmentConfig.modelAssetId = parent.modelAssetId
        )
        .addValidator(Validators.nonNull())
        .addValidator(ModelAsset.VALIDATOR_CACHE.getValidator())
        .add()
        .appendInherited(
            new KeyedCodec<>("Length", Codec.DOUBLE, true),
            (serpentSegmentConfig, s) -> serpentSegmentConfig.length = s,
            serpentSegmentConfig -> serpentSegmentConfig.length,
            (serpentSegmentConfig, parent) -> serpentSegmentConfig.length = parent.length
        )
        .addValidator(Validators.nonNull())
        .addValidator(Validators.min(0.0d))
        .add()
        .afterDecode(serpentSegmentConfig -> serpentSegmentConfig.model = ModelAsset.getAssetMap().getAsset(serpentSegmentConfig.modelAssetId))
        .build();

    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(SerpentSegmentConfig.class, CODEC);

    public static final ValidatorCache<String> VALIDATOR_CACHE =
        new ValidatorCache<>(new AssetKeyValidator<>(SerpentSegmentConfig::getAssetStore));

    private static AssetStore<String, SerpentSegmentConfig, DefaultAssetMap<String, SerpentSegmentConfig>> ASSET_STORE;

    private String id;
    private AssetExtraInfo.Data extraData;
    private String modelAssetId;
    private double length;

    private ModelAsset model;

    public static AssetStore<String, SerpentSegmentConfig, DefaultAssetMap<String, SerpentSegmentConfig>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(SerpentSegmentConfig.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, SerpentSegmentConfig> getAssetMap() {
        return getAssetStore().getAssetMap();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public ModelAsset getModel() {
        return this.model;
    }

    public double getLength() {
        return this.length;
    }
}
