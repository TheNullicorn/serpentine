package me.nullicorn.hytale.serpent.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.component.Serpent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A system that adds a {@link NetworkId} component to each newly added {@link Serpent} entity.
 */
public final class SerpentNetworkIdSystem extends HolderSystem<EntityStore> {
    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Serpent.getComponentType();
    }

    @Override
    public void onEntityAdd(
        @Nonnull final Holder<EntityStore> holder,
        @Nonnull final AddReason reason,
        @Nonnull final Store<EntityStore> store
    ) {
        holder.putComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
    }

    @Override
    public void onEntityRemoved(
        @Nonnull final Holder<EntityStore> holder,
        @Nonnull final RemoveReason reason,
        @Nonnull final Store<EntityStore> store
    ) {
        // N/A
    }
}
