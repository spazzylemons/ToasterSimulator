package me.spazzylemons.toastersimulator.client.render;

import me.spazzylemons.toastersimulator.client.model.OBJLoader;
import me.spazzylemons.toastersimulator.client.model.geometry.QuadModel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelSavestate {
    private final PlayerModel<AbstractClientPlayerEntity> model;
    private final ModelRenderer head;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer body;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final boolean hatVisible;
    private final boolean leftSleeveVisible;
    private final boolean rightSleeveVisible;
    private final boolean jacketVisible;
    private final boolean leftPantsVisible;
    private final boolean rightPantsVisible;

    public ModelSavestate(PlayerModel<AbstractClientPlayerEntity> model) {
        this.model = model;
        head = model.head;
        leftArm = model.leftArm;
        rightArm = model.rightArm;
        body = model.body;
        leftLeg = model.leftLeg;
        rightLeg = model.rightLeg;
        hatVisible = model.hat.visible;
        leftSleeveVisible = model.leftSleeve.visible;
        rightSleeveVisible = model.rightSleeve.visible;
        jacketVisible = model.jacket.visible;
        leftPantsVisible = model.leftPants.visible;
        rightPantsVisible = model.rightPants.visible;
    }

    public void applyCustom() {
        model.head = new QuadModelRenderer(model, headModel);
        model.leftArm = new QuadModelRenderer(model, leftArmModel);
        model.rightArm = new QuadModelRenderer(model, rightArmModel);
        model.body = new QuadModelRenderer(model, bodyModel);
        model.leftLeg = new QuadModelRenderer(model, leftLegModel);
        model.rightLeg = new QuadModelRenderer(model, rightLegModel);
        model.hat.visible = false;
        model.leftSleeve.visible = false;
        model.rightSleeve.visible = false;
        model.jacket.visible = false;
        model.leftPants.visible = false;
        model.rightPants.visible = false;
    }

    public void restore() {
        model.head = head;
        model.leftArm = leftArm;
        model.rightArm = rightArm;
        model.body = body;
        model.leftLeg = leftLeg;
        model.rightLeg = rightLeg;
        model.hat.visible = hatVisible;
        model.leftSleeve.visible = leftSleeveVisible;
        model.rightSleeve.visible = rightSleeveVisible;
        model.jacket.visible = jacketVisible;
        model.leftPants.visible = leftPantsVisible;
        model.rightPants.visible = rightPantsVisible;
    }

    // temporary?
    private static final QuadModel headModel = OBJLoader.loadOrEmpty("head.obj");
    private static final QuadModel leftArmModel = OBJLoader.loadOrEmpty("left_arm.obj");
    private static final QuadModel rightArmModel = OBJLoader.loadOrEmpty("right_arm.obj");
    private static final QuadModel bodyModel = OBJLoader.loadOrEmpty("body.obj");
    private static final QuadModel leftLegModel = OBJLoader.loadOrEmpty("left_leg.obj");
    private static final QuadModel rightLegModel = OBJLoader.loadOrEmpty("right_leg.obj");
}
