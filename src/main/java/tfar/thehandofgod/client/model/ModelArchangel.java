package tfar.thehandofgod.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.client.util.obj.GroupObject;
import tfar.thehandofgod.client.util.obj.ObjModelManager;
import tfar.thehandofgod.client.util.obj.WavefrontObject;


public class ModelArchangel extends ModelBase {

    public static final ResourceLocation resourceDefaultModel = new ResourceLocation(TheHandOfGod.MODID,
            "models/entity/archangel.obj");

    private final RenderManager manager;
    private WavefrontObject archangel;
    private GroupObject head;
    private GroupObject la;
    private GroupObject ra;

    public ModelArchangel(RenderManager manager) {
        this.manager = manager;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        if (archangel == null) {
            refresh();
        }

        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.translate(0, -1.5, 0);
        la.rotateAngleX = MathHelper.cos((float) (limbSwing + Math.PI)) * limbSwingAmount * 1.5F;
        ra.rotateAngleX = MathHelper.cos(limbSwing) * limbSwingAmount * 1.5F;
        head.rotateAngleY = -netHeadYaw * 0.017453292F;
        head.rotateAngleX = headPitch * 0.017453292F;
        archangel.renderAll(manager);
        GlStateManager.popMatrix();
    }

    private void refresh() {
        this.archangel = ObjModelManager.getModel(resourceDefaultModel);
        for (GroupObject group : this.archangel.groupObjects) {
            if (group.name.equals("head")) {
                this.head = group;
            } else if (group.name.equals("la")) {
                this.la = group;
            } else if (group.name.equals("ra")) {
                this.ra = group;
            }
        }
    }
}