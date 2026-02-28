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

public final class SerpentBoneConfig implements JsonAssetWithMap<String, DefaultAssetMap<String, SerpentBoneConfig>> {
    public static final String PATH = "NPC/SerpentBoneConfig";

    public static final AssetBuilderCodec<String, SerpentBoneConfig> CODEC = AssetBuilderCodec.builder(
            SerpentBoneConfig.class,
            SerpentBoneConfig::new,
            Codec.STRING,
            (asset, s) -> asset.id = s,
            asset -> asset.id,
            (asset, data) -> asset.extraData = data,
            asset -> asset.extraData
        )
        .appendInherited(
            new KeyedCodec<>("Model", Codec.STRING, true),
            (serpentBoneConfig, s) -> serpentBoneConfig.modelAssetId = s,
            serpentBoneConfig -> serpentBoneConfig.modelAssetId,
            (serpentBoneConfig, parent) -> serpentBoneConfig.modelAssetId = parent.modelAssetId
        )
        .addValidator(Validators.nonNull())
        .addValidator(ModelAsset.VALIDATOR_CACHE.getValidator())
        .add()
        .appendInherited(
            new KeyedCodec<>("Length", Codec.DOUBLE, true),
            (serpentBoneConfig, s) -> serpentBoneConfig.length = s,
            serpentBoneConfig -> serpentBoneConfig.length,
            (serpentBoneConfig, parent) -> serpentBoneConfig.length = parent.length
        )
        .addValidator(Validators.nonNull())
        .addValidator(Validators.min(0.0d))
        .add()
        .afterDecode(serpentBoneConfig -> serpentBoneConfig.model = ModelAsset.getAssetMap().getAsset(serpentBoneConfig.modelAssetId))
        .build();

    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(SerpentBoneConfig.class, CODEC);

    public static final ValidatorCache<String> VALIDATOR_CACHE =
        new ValidatorCache<>(new AssetKeyValidator<>(SerpentBoneConfig::getAssetStore));

    private static AssetStore<String, SerpentBoneConfig, DefaultAssetMap<String, SerpentBoneConfig>> ASSET_STORE;

    private String id;
    private AssetExtraInfo.Data extraData;
    private String modelAssetId;
    private double length;

    private ModelAsset model;

    public static AssetStore<String, SerpentBoneConfig, DefaultAssetMap<String, SerpentBoneConfig>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(SerpentBoneConfig.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, SerpentBoneConfig> getAssetMap() {
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
