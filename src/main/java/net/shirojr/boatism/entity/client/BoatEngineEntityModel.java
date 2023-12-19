package net.shirojr.boatism.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.shirojr.boatism.entity.custom.BoatEngineEntity;

import java.util.ArrayList;
import java.util.List;

public class BoatEngineEntityModel<T extends BoatEngineEntity> extends CompositeEntityModel<T> {
    private final List<ModelPart> parts = new ArrayList<>();
    private final ModelPart root;
    private final ModelPart top;
    private final ModelPart rod;
    private final ModelPart propeller;


    public BoatEngineEntityModel(ModelPart base) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.root = base.getChild("root");
        this.top = root.getChild("top");
        this.rod = root.getChild("rod");
        this.propeller = rod.getChild("propeller");
        parts.addAll(List.of(this.root, this.top, this.rod, this.propeller));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 23.0F, -5.0F));
        ModelPartData top = root.addChild("top", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-3.0F, -7.0F, -4.0F, 6.0F, 3.0F, 9.0F, new Dilation(0.0F))
                .uv(11, 16)
                .cuboid(-1.0F, -4.0F, -4.0F, 2.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 14)
                .cuboid(-2.0F, -6.0F, -5.0F, 4.0F, 4.0F, 5.0F, new Dilation(0.0F))
                .uv(24, 18)
                .cuboid(-2.0F, -9.0F, 5.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 2.0F, 5.0F));
        ModelPartData rod = root.addChild("rod", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 2.0F, 5.0F));
        ModelPartData propeller = rod.addChild("propeller", ModelPartBuilder.create().uv(0, 10)
                .cuboid(0.0F, -3.0F, -1.0F, 0.0F, 6.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 3.0F, -2.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r1 = propeller.addChild("cube_r1", ModelPartBuilder.create().uv(0, 10)
                .cuboid(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 1.5708F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        float scaleFactor = 2.0f;
        matrices.push();
        //matrices.translate();
        matrices.scale(scaleFactor * 1.1f, scaleFactor, scaleFactor);
        matrices.pop();

        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return this.parts;
    }
}
