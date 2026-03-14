package me.nullicorn.hytale.serpent.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.SerpentPlugin;

public final class SerpentBoneAutoApplyScale implements Component<EntityStore> {
    private static final SerpentBoneAutoApplyScale INSTANCE = new SerpentBoneAutoApplyScale();

    public static SerpentBoneAutoApplyScale get() {
        return INSTANCE;
    }

    public static ComponentType<EntityStore, SerpentBoneAutoApplyScale> getComponentType() {
        return SerpentPlugin.get().getSerpentBoneAutoApplyScaleComponentType();
    }

    private SerpentBoneAutoApplyScale() {
    }

    @Override
    public Component<EntityStore> clone() {
        return get();
    }
}
