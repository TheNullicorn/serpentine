package me.nullicorn.hytale.serpent.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import me.nullicorn.hytale.serpent.component.Serpent;
import me.nullicorn.hytale.serpent.component.SerpentSegment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SerpentTargetSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(
            new SystemDependency<>(Order.AFTER, SerpentAddRemoveSystem.class)
        );
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Serpent.getComponentType();
    }

    @Override
    public void tick(
        final float dt,
        final int index,
        @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final CommandBuffer<EntityStore> commandBuffer
    ) {
        final Serpent serpent = archetypeChunk.getComponent(index, Serpent.getComponentType());
        assert serpent != null;

        final List<Ref<EntityStore>> nearbyEntities = new ArrayList<>();
        commandBuffer
            .getResource(NPCPlugin.get().getNpcSpatialResource())
            .getSpatialStructure()
            .collect(serpent.joints[0].position, 50, nearbyEntities);

        Vector3d closestPos = null;
        double closestDistSq = Double.POSITIVE_INFINITY;
        for (final Ref<EntityStore> entityRef : nearbyEntities) {
            if (commandBuffer.getComponent(entityRef, SerpentSegment.getComponentType()) != null) {
                continue;
            }

            final TransformComponent transform = commandBuffer.getComponent(entityRef, TransformComponent.getComponentType());
            if (transform == null) {
                continue;
            }
            final double distSq = transform.getPosition().distanceSquaredTo(serpent.joints[0].position);
            if (distSq < closestDistSq) {
                closestPos = transform.getPosition();
                closestDistSq = distSq;
            }
        }

        if (closestPos == null) {
            return;
        }

        serpent.target = closestPos.clone();
    }
}
