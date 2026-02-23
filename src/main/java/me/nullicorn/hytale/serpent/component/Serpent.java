package me.nullicorn.hytale.serpent.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.serpent.SerpentPlugin;
import me.nullicorn.hytale.serpent.asset.SerpentConfig;

public final class Serpent implements Component<EntityStore> {
    public static final String ID = "Serpent";
    public static final BuilderCodec<Serpent> CODEC = BuilderCodec.builder(Serpent.class, Serpent::new).build();

    public SerpentConfig config;
    public Vector3d target;
    public SerpentJoint[] joints;
    public double[] lengths;
    public Ref<EntityStore>[] segments;
    public double twistTime;
    public double headScale;
    public double tailScale;

    public static ComponentType<EntityStore, Serpent> getComponentType() {
        return SerpentPlugin.get().getSerpentComponentType();
    }

    public Serpent() { // TODO: Make private
    }

    public Serpent(final Vector3d[] joints, final SerpentConfig config, final double headScale, final double tailScale) {
        if (joints.length < 2) {
            throw new IllegalArgumentException("joints must have at least 2 elements");
        }
        this.config = config;
        this.headScale = headScale;
        this.tailScale = tailScale;
        this.joints = new SerpentJoint[joints.length];
        for (int i = 0; i < joints.length; i++) {
            final SerpentJoint joint = new SerpentJoint();
            joint.position = joints[i].clone();
            this.joints[i] = joint;
        }

        this.lengths = new double[this.joints.length - 1];
        for (int i = 0; i < this.lengths.length; i++) {
            this.lengths[i] = joints[i].distanceTo(joints[i + 1]);
        }
        this.target = joints[0].clone();
        //noinspection unchecked
        this.segments = new Ref[this.joints.length - 1];
    }

    @Override
    public Component<EntityStore> clone() {
        final Serpent clone = new Serpent();
        clone.config = this.config;
        clone.headScale = this.headScale;
        clone.tailScale = this.tailScale;
        // TODO!!!
        return clone;
    }
}
