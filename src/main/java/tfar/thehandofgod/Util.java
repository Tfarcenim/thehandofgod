package tfar.thehandofgod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<Entity> getEntitiesInCone(EntityLivingBase player) {
        List<Entity> entities = player.world.getEntities(Entity.class,entity -> entity != player);
        List<Entity> coneEntities = new ArrayList<>();
        Vec3d coneTip = player.getPositionVector();
        Vec3d direction = player.getLookVec();
        Vec3d basementCenter = coneTip.add(direction .scale( HandOfGodConfig.cone_length));
        Vec3d axisVect = coneTip.subtract(basementCenter);
        for (Entity entity : entities) {
            Vec3d entityPos = entity.getPositionVector();
            Vec3d apexToXVec = coneTip.subtract(entityPos);

            boolean isInInfiniteCone = apexToXVec.dotProduct(axisVect)
                    /(apexToXVec.length()*axisVect.length())
                    >
                    // We can safely compare cos() of angles
                    // between vectors instead of bare angles.
                    Math.cos(Math.toRadians(HandOfGodConfig.cone_angle / 2));
            if (isInInfiniteCone) {

                // X is contained in cone only if projection of apexToXVect to axis
                // is shorter than axis.
                // We'll use dotProd() to figure projection length.
                boolean isUnderRoundCap = apexToXVec.dotProduct(axisVect)
                        /axisVect.length()
                        <
                        axisVect.length();
                if (isUnderRoundCap) {
                    coneEntities.add(entity);
                }

                coneEntities.add(entity);
            }
        }
        return coneEntities;
    }

    private static final ItemStack HAND = new ItemStack(ModItems.HAND_OF_GOD);

    public static boolean hasHand(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            return player.inventory.hasItemStack(HAND);
        }
        return false;
    }
}
