package me.nullicorn.hytale.serpent.command;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.math.range.IntRange;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.asset.SerpentConfig;
import me.nullicorn.hytale.serpent.component.Serpent;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class SerpentAddCommand extends AbstractPlayerCommand {
    private final RequiredArg<SerpentConfig> serpentConfigArg =
        this.withRequiredArg(
            "config",
            "server.commands.serpent.add.config.desc",
            SerpentConfig.SINGLE_ARGUMENT_TYPE
        );

    private final OptionalArg<Integer> boneCountArg =
        this.withOptionalArg(
            "bones",
            "server.commands.serpent.add.bones.desc",
            ArgTypes.INTEGER
        );

    private final OptionalArg<Double> scaleArg =
        this.withOptionalArg(
            "scale",
            "server.commands.serpent.add.scale.desc",
            ArgTypes.DOUBLE
        );

    public SerpentAddCommand() {
        super("add", "server.commands.serpent.add.desc");
    }

    @Override
    protected void execute(
        @Nonnull final CommandContext context,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final PlayerRef playerRef,
        @Nonnull final World world
    ) {
        // TODO: Almost identical to SerpentMorphCommand. Move this shared code elsewhere.
        final SerpentConfig config = context.get(this.serpentConfigArg);
        final double scale = context.provided(this.scaleArg)
            ? context.get(this.scaleArg)
            : 1.0;

        if (scale <= 0) {
            context.sendMessage(Message.translation("server.commands.serpent.scale.mustBeGreaterThanZero"));
            return;
        }

        final int boneCount = Objects.requireNonNullElseGet(
            context.get(this.boneCountArg),
            () -> {
                final IntRange range = config.getBodyBoneCount();
                return RandomExtra.randomRange(range.getInclusiveMin(), range.getInclusiveMax());
            });
        if (boneCount < 0) {
            context.sendMessage(Message.translation("server.commands.serpent.bones.mustBeAtLeastZero"));
            return;
        }

        final Vector3d[] joints = new Vector3d[boneCount + 1];
        double distance = 0;
        for (int i = 0; i < joints.length; i++) {
            joints[i] = playerRef.getTransform().getPosition().clone().add(0, 0, -distance);
            if (i == 0) {
                distance += config.getHead().getLength() * scale;
            } else if (i < joints.length - 2) {
                distance += config.getBody().getLength() * scale;
            } else {
                distance += config.getTail().getLength() * scale;
            }
        }

        final Holder<EntityStore> holder = store.getRegistry().newHolder();
        holder.addComponent(Serpent.getComponentType(), new Serpent(joints, config, scale));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.addComponent(UUIDComponent.getComponentType(), UUIDComponent.randomUUID());
        store.addEntity(holder, AddReason.SPAWN);
    }
}
