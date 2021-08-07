package tfar.thehandofgod;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tfar.thehandofgod.util.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (HandOfGodConfig.infinite_energy) {
            if (!worldIn.isRemote) {
                if (entityIn instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)entityIn;
                    for (ItemStack stack1 :player.inventory.mainInventory) {
                        if (stack1.hasCapability(CapabilityEnergy.ENERGY,null)) {
                            IEnergyStorage energyStorage = stack1.getCapability(CapabilityEnergy.ENERGY,null);
                            if (energyStorage != null) {
                                energyStorage.receiveEnergy(Integer.MAX_VALUE,false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 2;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("owner")) {
            tooltip.add("Owned by "+stack.getTagCompound().getString("owner_name"));
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        List<Entity> coneEntities = Util.getEntitiesInCone(player);
        entity.setDead();
        coneEntities.forEach(HandOfGodItem::pk);
        return true;
    }

    public static void pk(Entity entity) {
        entity.setDead();
        if (HandOfGodConfig.judgement) {
            entity.world.addWeatherEffect(new ColoredLightningEntity(entity.world, entity.posX, entity.posY, entity.posZ, true));
        }
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        boolean toReturn = false;

        //Block hit
        //todo client only
        RayTraceResult ray = player.rayTrace(5,0);
        if (ray != null) {
            //Breaks the Blocks
            if (!player.isSneaking() ) {
                toReturn = this.breakBlocks(stack, HandOfGodConfig.break_radius, player.world, pos, ray.sideHit, player);
            }
        }
        return toReturn;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return true;
    }

    //borrowed from actually additions

    /**
     * Breaks Blocks in a certain Radius
     * Has to be called on both Server and Client
     *
     * @param stack  The Drill
     * @param radius The Radius to break Blocks in (0 means only 1 Block will be broken!)
     * @param world  The World
     * @param player The Player who breaks the Blocks
     */
    public boolean breakBlocks(ItemStack stack, int radius, World world, BlockPos targetPos, EnumFacing side, EntityPlayer player) {
        int xRange = radius;
        int yRange = radius;
        int zRange = radius;

        //Corrects Blocks to hit depending on Side of original Block hit
       /* if (side.getAxis() == EnumFacing.Axis.Y) {
            zRange = radius;
            yRange = 0;
        }
        if (side.getAxis() == EnumFacing.Axis.X) {
            xRange = 0;
            zRange = radius;
        }*/

        //Not defined later because main Block is getting broken below
        IBlockState state = world.getBlockState(targetPos);
        float mainHardness = state.getBlockHardness(world, targetPos);

        //Break Middle Block first
        if (!this.tryHarvestBlock(world, targetPos, false, stack, player)) { return false; }

        if (radius == 2 && side.getAxis() != EnumFacing.Axis.Y) {
            targetPos = targetPos.up();
            IBlockState theState = world.getBlockState(targetPos);
            if (theState.getBlockHardness(world, targetPos) <= mainHardness + 5.0F) {
                this.tryHarvestBlock(world, targetPos, true, stack, player);
            }
        }

        //Break Blocks around
        if (radius > 0 && mainHardness >= 0.2F) {
            for (int xPos = targetPos.getX() - xRange; xPos <= targetPos.getX() + xRange; xPos++) {
                for (int yPos = targetPos.getY() - yRange; yPos <= targetPos.getY() + yRange; yPos++) {
                    for (int zPos = targetPos.getZ() - zRange; zPos <= targetPos.getZ() + zRange; zPos++) {
                        if (!(targetPos.getX() == xPos && targetPos.getY() == yPos && targetPos.getZ() == zPos)) {
                            //Only break Blocks around that are (about) as hard or softer
                            BlockPos thePos = new BlockPos(xPos, yPos, zPos);
                            IBlockState theState = world.getBlockState(thePos);
                            if (theState.getBlockHardness(world, thePos) <= mainHardness + 5.0F) {
                                this.tryHarvestBlock(world, thePos, true, stack, player);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Tries to harvest a certain Block
     * Breaks the Block, drops Particles etc.
     * Has to be called on both Server and Client
     *
     * @param world   The World
     * @param isExtra If the Block is the Block that was looked at when breaking or an additional Block
     * @param stack   The Hand
     * @param player  The Player breaking the Blocks
     */
    private boolean tryHarvestBlock(World world, BlockPos pos, boolean isExtra, ItemStack stack, EntityPlayer player) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        float hardness = state.getBlockHardness(world, pos);
        boolean canHarvest = (ForgeHooks.canHarvestBlock(block, player, world, pos) || this.canHarvestBlock(state, stack))
                && (!isExtra || this.getDestroySpeed(stack, world.getBlockState(pos)) > 1.0F);
        if (hardness >= 0.0F && (!isExtra || canHarvest && !block.hasTileEntity(world.getBlockState(pos)))) {
            //Break the Block
            return Util.breakExtraBlock(stack, world, player, pos);
        }
        return false;
    }

    protected static final UUID REACH_DISTANCE_MODIFIER = UUID.fromString("fda5a5b8-04dd-4d77-ae58-aa07d33dee2b");

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multiMap = super.getItemAttributeModifiers(equipmentSlot);
        multiMap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_MODIFIER, "Weapon modifier", HandOfGodConfig.block_reach_distance, 0));
        return multiMap;
    }
}
