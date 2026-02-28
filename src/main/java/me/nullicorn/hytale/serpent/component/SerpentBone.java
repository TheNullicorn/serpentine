package me.nullicorn.hytale.serpent.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.SerpentPlugin;

public final class SerpentBone implements Component<EntityStore> {
    public Ref<EntityStore> serpent;
    public int index;

    public static ComponentType<EntityStore, SerpentBone> getComponentType() {
        return SerpentPlugin.get().getSerpentBoneComponentType();
    }

    public SerpentBone(final Ref<EntityStore> serpent, final int index) {
        this.serpent = serpent;
        this.index = index;
    }

    public SerpentBone() {
    }

    @Override
    public Component<EntityStore> clone() {
        return new SerpentBone(this.serpent, this.index);
    }
}
