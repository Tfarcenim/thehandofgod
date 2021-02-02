package tfar.thehandofgod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class HandOfGodItem extends Item {

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                int length = 64;
                Vec3d pos = playerIn.getPositionVector();
                List<Entity> entities = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn,new AxisAlignedBB(pos.subtract(length,length,length),pos.add(length,length,length)));
                entities.forEach(Entity::setDead);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        List<Entity> coneEntities = Util.getEntitiesInCone(player);
        entity.setDead();
        coneEntities.forEach(Entity::setDead);
        return true;
    }
}
