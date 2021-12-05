package tfar.thehandofgod.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import tfar.thehandofgod.util.Util;

public class EntityAIArchangelNearestAttackablePlayer extends EntityAINearestAttackableTarget<EntityPlayer> {

    public EntityAIArchangelNearestAttackablePlayer(EntityCreature creature) {
        super(creature, EntityPlayer.class, false);
    }

    public boolean shouldExecute() {
        targetEntity = null;
        double min = Double.MAX_VALUE;
        for (EntityPlayer player : taskOwner.world.playerEntities) {
            if (!player.isSpectator() && !Util.hasHand(player)) {
                double d = player.getDistanceSq(taskOwner);
                if (d < min && d < getTargetDistance()) {
                    min = d;
                    targetEntity = player;
                }
            }
        }
        return targetEntity != null;
    }
}
