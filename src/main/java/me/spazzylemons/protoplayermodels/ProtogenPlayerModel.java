package me.spazzylemons.protoplayermodels;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.spazzylemons.protoplayermodels.model.OBJLoader;
import me.spazzylemons.protoplayermodels.model.geometry.QuadModel;
import me.spazzylemons.protoplayermodels.render.QuadModelRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class ProtogenPlayerModel<T extends LivingEntity> extends PlayerModel<T> {
    private static final Field cubesField =
            ObfuscationReflectionHelper.findField(ModelRenderer.class, "cubes");

    public ProtogenPlayerModel(float scale) {
        super(scale, false);
        replace(head, hat, headModel);
        replace(leftArm, leftSleeve, leftArmModel);
        replace(rightArm, rightSleeve, rightArmModel);
        replace(body, jacket, bodyModel);
        replace(leftLeg, leftPants, leftLegModel);
        replace(rightLeg, rightPants, rightLegModel);
    }

    private void replace(ModelRenderer inner, ModelRenderer outer, QuadModel quadModel) {
        try {
            // empty the inner renderer
            ((ObjectList<?>) cubesField.get(inner)).clear();
            // empty the outer renderer
            ((ObjectList<?>) cubesField.get(outer)).clear();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            ProtoPlayerModels.err("Could not clear a part of the player model, things might look a little weird");
        }
        // create custom model renderer, add to inner renderer
        inner.addChild(new QuadModelRenderer(this, quadModel));
    }

    // temporary?
    private static final QuadModel headModel = OBJLoader.loadOrEmpty("head.obj");
    private static final QuadModel leftArmModel = OBJLoader.loadOrEmpty("left_arm.obj");
    private static final QuadModel rightArmModel = OBJLoader.loadOrEmpty("right_arm.obj");
    private static final QuadModel bodyModel = OBJLoader.loadOrEmpty("body.obj");
    private static final QuadModel leftLegModel = OBJLoader.loadOrEmpty("left_leg.obj");
    private static final QuadModel rightLegModel = OBJLoader.loadOrEmpty("right_leg.obj");
}
