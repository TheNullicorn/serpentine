package me.nullicorn.hytale.serpent.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.SerpentPlugin;

public final class SerpentBoneAutoApplyModel implements Component<EntityStore> {
    private static final SerpentBoneAutoApplyModel INSTANCE = new SerpentBoneAutoApplyModel();

    public static SerpentBoneAutoApplyModel get() {
        return INSTANCE;
    }

    public static ComponentType<EntityStore, SerpentBoneAutoApplyModel> getComponentType() {
        return SerpentPlugin.get().getSerpentBoneAutoApplyModelComponentType();
    }

    private SerpentBoneAutoApplyModel() {
    }

    @Override
    public Component<EntityStore> clone() {
        return get();
    }
}