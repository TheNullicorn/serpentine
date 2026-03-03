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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.component.Serpent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public final class SerpentSolverSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(
            new SystemDependency<>(Order.AFTER, SerpentTargetSystem.class)
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

        if (serpent.joints == null || serpent.joints.length < 2 || serpent.target == null) {
            return;
        }

        if (serpent.joints[0].position.distanceTo(serpent.target) < 0.00001) {
            return;
        }
        serpent.joints[0].position.assign(serpent.target);
        serpent.path.addFirst(serpent.target.clone());

        // FIXME: When bones move backward (when the head backtracks) bones that reach the tail get compressed into its
        //        position. Implement some form of extrapolation on `path` so that the tail bone can go backward.

        int pathIndex = 0;
        double remainder = 0.0;

        for (int i = 1; i < serpent.joints.length; i++) {
            final double boneLength = serpent.getBoneConfig(i - 1).getLength();

            // Account for how far along the path segment the previous joint left off.
            double distLeft = remainder + boneLength;
            for (; pathIndex < serpent.path.size() - 1; pathIndex++) {
                final Vector3d thisPathNode = serpent.path.get(pathIndex);
                final Vector3d nextPathNode = serpent.path.get(pathIndex + 1);
                final Vector3d pathSegment = nextPathNode.clone().subtract(thisPathNode);
                final double pathSegmentLength = pathSegment.length();
                // See if the joint should be placed along this path segment.
                if (pathSegmentLength > distLeft) {
                    // Normalize `pathSegment`.
                    final Vector3d pathSegmentDirection = pathSegment.clone().scale(1 / pathSegmentLength);
                    // Interpolate the joint along the segment.
                    serpent.joints[i].position.assign(thisPathNode.clone().add(pathSegmentDirection.clone().scale(distLeft)));
                    // Save how far into the segment we left off so that the next joint can continue from there.
                    remainder = distLeft;
                    // Next joint!
                    break;
                }
                // Next path segment!
                distLeft -= pathSegmentLength;
            }
        }

        // Remove path nodes that the tail bone has gone past.
        if (pathIndex < serpent.path.size() - 2) {
            serpent.path.subList(pathIndex + 2, serpent.path.size()).clear();
        }
    }
}
