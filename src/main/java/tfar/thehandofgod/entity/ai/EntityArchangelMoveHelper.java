package tfar.thehandofgod.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;
import tfar.thehandofgod.entity.EntityArchangel;

public class EntityArchangelMoveHelper extends EntityMoveHelper {

    private final EntityArchangel archangel;

    public EntityArchangelMoveHelper(EntityArchangel archangel) {
        super(archangel);
        this.archangel = archangel;
    }

    @Override
    public void onUpdateMoveHelper() {
        EntityLivingBase target = archangel.getAttackTarget();
        if (target != null && target.isInWater() && archangel.isInWater()) {
            if (action != Action.MOVE_TO || archangel.getNavigator().noPath()) {
                archangel.setAIMoveSpeed(0.0F);
                return;
            }
            double dx = posX - archangel.posX;
            double dy = posY - archangel.posY;
            double dz = posZ - archangel.posZ;
            double d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            dy = dy / d;
            float f = (float) (MathHelper.atan2(dz, dx) * 180 / Math.PI) - 90.0F;
            archangel.rotationYaw = limitAngle(archangel.rotationYaw, f, 90.0F);
            archangel.renderYawOffset = archangel.rotationYaw;
            float f1 = (float) (speed
                    * archangel.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
            archangel.setAIMoveSpeed(archangel.getAIMoveSpeed() + (f1 - archangel.getAIMoveSpeed()) * 0.125F);
            archangel.motionY += archangel.getAIMoveSpeed() * dy * 0.4;
            archangel.motionX += archangel.getAIMoveSpeed() * dx * 0.02;
            archangel.motionZ += archangel.getAIMoveSpeed() * dz * 0.02;
        } else {
            super.onUpdateMoveHelper();
        }
    }
}
