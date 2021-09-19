package tfar.thehandofgod;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
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
            if (playerIn.isSneaking() && HandOfGodConfig.area_kill) {
                int length = HandOfGodConfig.area_kill_range;
                Vec3d pos = playerIn.getPositionVector();
                List<Entity> entities = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, new AxisAlignedBB(pos.subtract(length, length, length), pos.add(length, length, length)));
                entities.forEach(HandOfGodItem::pk);
            }
        }

        if (HandOfGodConfig.raytrace_fluids) {

            ItemStack itemstack = playerIn.getHeldItem(handIn);
            RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
            ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemstack, raytraceresult);
            if (ret != null) return ret;

            if (raytraceresult == null) {
                return new ActionResult<>(EnumActionResult.PASS, itemstack);
            } else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(EnumActionResult.PASS, itemstack);
            } else {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                    return new ActionResult<>(EnumActionResult.FAIL, itemstack);
                } else {
                    if (!playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
                        return new ActionResult<>(EnumActionResult.FAIL, itemstack);
                    } else {
                        IBlockState iblockstate = worldIn.getBlockState(blockpos);

                        if (iblockstate.getPropertyKeys().contains(BlockLiquid.LEVEL) && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
                            worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 11);
                            playerIn.addStat(StatList.getObjectUseStats(this));
                            playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
                        } else {
                            return new ActionResult<>(EnumActionResult.FAIL, itemstack);
                        }
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote) {

        } else {
            if (HandOfGodConfig.infinite_energy) {
                if (entityIn instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entityIn;
                    for (ItemStack stack1 : player.inventory.mainInventory) {
                        if (stack1.hasCapability(CapabilityEnergy.ENERGY, null)) {
                            IEnergyStorage energyStorage = stack1.getCapability(CapabilityEnergy.ENERGY, null);
                            if (energyStorage != null) {
                                energyStorage.receiveEnergy(Integer.MAX_VALUE, false);
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
            tooltip.add("Owned by " + stack.getTagCompound().getString("owner_name"));
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (HandOfGodConfig.kill_facing) {
            List<Entity> coneEntities = Util.getEntitiesInCone(player);
            coneEntities.forEach(HandOfGodItem::pk);
        }
        return true;
    }

    public static void pk(Entity entity) {

        if (!HandOfGodConfig.kill_friendly && Util.isFriendlyMob(entity)) {
            return;
        }

        if (!HandOfGodConfig.kill_all && !(entity instanceof EntityLivingBase)) {
            return;
        }

        if (entity instanceof EntityPlayer) {

        } else {
            entity.setDead();
        }
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
        RayTraceResult ray = player.rayTrace(5, 0);
        if (ray != null) {
            //Breaks the Blocks
            if (!player.isSneaking()) {
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
        if (!this.tryHarvestBlock(world, targetPos, false, stack, player)) {
            return false;
        }

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
