package me.nullicorn.hytale.serpent.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.component.Serpent;
import me.nullicorn.hytale.serpent.solver.SerpentBoneSolver;
import me.nullicorn.hytale.serpent.solver.SerpentJointSolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Runs each tick to solve for the new positions of {@link Serpent#joints() joints} and {@link Serpent#bones() bones} in
 * each {@link Serpent} entity.
 */
public final class SerpentSolverSystem extends EntityTickingSystem<EntityStore> {
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
        final Ref<EntityStore> serpentRef = archetypeChunk.getReferenceTo(index);
        final Serpent serpent = archetypeChunk.getComponent(index, Serpent.getComponentType());
        assert serpent != null;

        final SerpentJointSolver jointSolver = serpent.jointSolver();
        if (jointSolver != null) {
            jointSolver.tick(serpent, serpentRef, dt, commandBuffer);
        }

        final SerpentBoneSolver boneSolver = serpent.boneSolver();
        if (boneSolver != null) {
            boneSolver.tick(serpent, serpentRef, dt, commandBuffer);
        }
    }
}
