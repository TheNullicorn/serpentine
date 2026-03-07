package me.nullicorn.hytale.serpent.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entity.system.UpdateLocationSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.component.Serpent;
import me.nullicorn.hytale.serpent.component.SerpentBone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * Runs each tick to remove {@link SerpentBone} entities that are not part of a valid {@link Serpent}.
 */
public final class SerpentBoneUnloadSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(
            new SystemDependency<>(Order.AFTER, UpdateLocationSystems.TickingSystem.class)
        );
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
            SerpentBone.getComponentType(),
            Query.not(Serpent.getComponentType())
        );
    }

    @Override
    public void tick(
        final float dt,
        final int index,
        @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final CommandBuffer<EntityStore> commandBuffer
    ) {
        final Ref<EntityStore> boneRef = archetypeChunk.getReferenceTo(index);
        final SerpentBone bone = archetypeChunk.getComponent(index, SerpentBone.getComponentType());
        assert bone != null;

        // Validate the `serpent` ref and the lower bound of `index`.
        if (bone.index >= 0 && bone.serpent != null && bone.serpent.isValid()) {
            final Serpent serpent = commandBuffer.getComponent(bone.serpent, Serpent.getComponentType());
            // Validate the upper bound of `index` and that the serpent contains the bone.
            if (serpent != null && bone.index < serpent.bones.length && boneRef.equals(serpent.bones[bone.index])) {
                // Bone has a valid relationship with the serpent. Don't remove.
                return;
            }
        }

        // Remove the stray bone.
        commandBuffer.removeEntity(boneRef, RemoveReason.UNLOAD);
    }
}
