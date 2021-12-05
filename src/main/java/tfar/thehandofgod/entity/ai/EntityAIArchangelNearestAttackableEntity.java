package tfar.thehandofgod.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import tfar.thehandofgod.entity.EntityArchangel;

import java.util.Collections;
import java.util.List;

public class EntityAIArchangelNearestAttackableEntity extends EntityAINearestAttackableTarget<EntityLivingBase> {

    public EntityAIArchangelNearestAttackableEntity(EntityCreature creature) {
        super(creature, EntityLivingBase.class, false);
    }

    public boolean shouldExecute() {
        List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(this.targetClass,
                getTargetableArea(getTargetDistance()), targetEntitySelector);
        list.removeIf(entity -> entity instanceof EntityArchangel);
        if (list.isEmpty()) {
            return false;
        } else {
            list.sort(this.sorter);
            targetEntity = list.get(0);
            return true;
        }
    }
}
