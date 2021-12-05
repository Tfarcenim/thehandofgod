package tfar.thehandofgod.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import tfar.thehandofgod.util.Util;

public class EntityAIArchangelAttack extends EntityAIAttackMelee {

    public EntityAIArchangelAttack(EntityCreature creature) {
        super(creature, 1.0, false);
    }

    public boolean shouldExecute() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null) {
            return false;
        } else if (Util.hasHand(target)) {
            attacker.setAttackTarget(null);
            return false;
        } else {
            attacker.dismountRidingEntity();
            attacker.setLocationAndAngles(target.posX, target.posY, target.posZ, target.rotationYaw,
                    target.rotationPitch);
            return true;
        }
    }

    public boolean shouldContinueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        } else if (!entitylivingbase.isEntityAlive()) {
            return false;
        } else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator()
                || Util.hasHand(entitylivingbase)) {
            return false;
        } else {
            return !this.attacker.getNavigator().noPath();
        }
    }

    @Override
    public void resetTask() {
        EntityLivingBase entity = this.attacker.getAttackTarget();
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()
                || Util.hasHand(entity)) {
            this.attacker.setAttackTarget(null);
        }
        this.attacker.getNavigator().clearPath();
    }

    @Override
    protected void checkAndPerformAttack(EntityLivingBase entity, double distance) {
        double d = this.getAttackReachSqr(entity);
        if (distance <= d && this.attackTick <= 0) {
            this.attackTick = 5;
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            this.attacker.attackEntityAsMob(entity);
        }
    }
}
