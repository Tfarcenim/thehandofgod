package tfar.thehandofgod.entity.ai;

import net.minecraft.entity.ai.EntityAISwimming;
import tfar.thehandofgod.entity.EntityArchangel;

public class EntityAIArchangelSwimming extends EntityAISwimming {

    private final EntityArchangel archangel;
    private boolean obstructed;

    public EntityAIArchangelSwimming(EntityArchangel archangel) {
        super(archangel);
        this.archangel = archangel;
    }

    public boolean shouldContinueExecuting() {
        return this.shouldExecute() && !this.obstructed;
    }

    @Override
    public void startExecuting() {
        obstructed = false;
    }

    @Override
    public void updateTask() {
        if (archangel.getNavigator().noPath() && archangel.getAttackTarget() == null) {
            super.updateTask();
        }
    }
}
