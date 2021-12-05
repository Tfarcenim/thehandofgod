package tfar.thehandofgod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import tfar.thehandofgod.HandOfGodConfig;
import tfar.thehandofgod.entity.ai.*;

import javax.annotation.Nullable;

public class EntityArchangel extends EntityCreature implements IEntityArchangel {

    public boolean dimChanging;
    private boolean summoned;

    public EntityArchangel(World worldIn) {
        super(worldIn);
        setSize(1.8F, 4.5F);
        moveHelper = new EntityArchangelMoveHelper(this);
        setPathPriority(PathNodeType.WATER, 0);
        summoned = false;
        dimChanging = false;
    }

    @Override
    public boolean isSummoned() {
        return summoned;
    }

    @Override
    public void setDispersed(boolean value) {
        summoned = value;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAIArchangelAttack(this));
        tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1, 4));
        tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0f));
        tasks.addTask(6, new EntityAIArchangelSwimming(this));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(2, new EntityAIArchangelNearestAttackablePlayer(this));
        targetTasks.addTask(2, new EntityAIArchangelNearestAttackableEntity(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(HandOfGodConfig.archangelSpeed);
        getEntityAttribute(SWIM_SPEED).setBaseValue(HandOfGodConfig.archangelSpeed * 15);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(128);
    }

    @Override
    public void onEntityUpdate() {
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(HandOfGodConfig.archangelSpeed);
        getEntityAttribute(SWIM_SPEED).setBaseValue(HandOfGodConfig.archangelSpeed * 15);
        super.onEntityUpdate();
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return Short.MAX_VALUE;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }

    @Override
    protected void outOfWorld() {
        dismountRidingEntity();
        setLocationAndAngles(posX, 256, posZ, rotationYaw, rotationPitch);
    }

    @Override
    @Nullable
   // @Optional.Method(modid = IceAndFire.MODID)
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability,
                               @Nullable net.minecraft.util.EnumFacing facing) {
        T result = super.getCapability(capability, facing);
        /*
        if (result instanceof IEntityDataCapability) {
            IEntityData<?> data = ((IEntityDataCapability) result).getData("Ice And Fire - Stone Property Tracker");
            if (data != null && data instanceof StoneEntityProperties) {
                ((StoneEntityProperties) data).isStone = false;
            }
        }*/
        return result;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    protected void handleJumpWater() {
        this.motionY += 0.04;
    }

    @Override
    protected void handleJumpLava() {
        this.motionY += 0.04;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (isServerWorld() && isInWater() && getAttackTarget() != null && getAttackTarget().isInWater()) {
            moveRelative(strafe, vertical, forward, 0.01F);
            move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            motionX *= 0.9F;
            motionY *= 0.9F;
            motionZ *= 0.9F;
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
      //  ItemLoader.handOfGod.leftClickEntity(this, entity);
        return true;
    }

    @Override
    public Entity changeDimension(int dimensionIn, ITeleporter teleporter) {
        summoned = true;
        return super.changeDimension(dimensionIn, teleporter);
    }

    @Override
    public void onRemovedFromWorld() {
        if (!summoned && !world.isRemote) {
            EntityArchangel archangel = new EntityArchangel(world);
            archangel.copyLocationAndAnglesFrom(this);
            world.spawnEntity(archangel);
            summoned = true;
        }
        super.onRemovedFromWorld();
    }
}
