package me.nullicorn.hytale.serpent.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.component.Serpent;
import me.nullicorn.hytale.serpent.component.SerpentSegment;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public final class SerpentSegmentTransformSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(
            new SystemDependency<>(Order.AFTER, SerpentSolverSystem.class)
        );
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
            SerpentSegment.getComponentType(),
            TransformComponent.getComponentType()
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
        // Get the segment's info.
        final SerpentSegment serpentSegment = archetypeChunk.getComponent(index, SerpentSegment.getComponentType());
        assert serpentSegment != null;
        if (serpentSegment.serpent == null || !serpentSegment.serpent.isValid()) {
            return;
        }

        // Get the segment's transform, which we'll be modifying below.
        final TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        assert transform != null;

        // Get the serpent that owns the segment.
        final Serpent serpent = commandBuffer.getComponent(serpentSegment.serpent, Serpent.getComponentType());
        if (serpent == null) {
            return;
        }

        final Vector3d direction = serpent.joints[serpentSegment.index].position.clone().subtract(serpent.joints[serpentSegment.index + 1].position).normalize();
        final Vector3f rotation = new Vector3f();

        rotation.setPitch((float) Math.asin(direction.y));
        rotation.setYaw((float) (Math.atan2(direction.x, direction.z) + Math.PI));
        rotation.setRoll((float) (Math.sin(serpent.twistTime + ((serpent.segments.length - serpentSegment.index - 1) * 0.1)) * Math.toRadians(10 + (double) serpentSegment.index / (serpent.segments.length - 1) * 50)));
        transform.setRotation(rotation);
        transform.setPosition(serpent.joints[serpentSegment.index + 1].position.clone().add(direction.clone().scale(serpent.lengths[serpentSegment.index] / 2.0)));
    }
}
