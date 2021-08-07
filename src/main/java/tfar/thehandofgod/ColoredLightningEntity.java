package tfar.thehandofgod;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class ColoredLightningEntity extends EntityLightningBolt {

    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(ColoredLightningEntity.class, DataSerializers.VARINT);


    public ColoredLightningEntity(World worldIn, double x, double y, double z, boolean effectOnlyIn) {
        super(worldIn, x, y, z, effectOnlyIn);
    }

    public ColoredLightningEntity(World world) {
        super(world,0,0,0,false);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(COLOR, 0xffffff);
    }


    public int getColor()
    {
        return this.dataManager.get(COLOR);
    }

    private void setColor(int color) {
        this.dataManager.set(COLOR, color);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Color", 99))
        {
            this.setColor(compound.getInteger("Color"));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Color", this.getColor());
    }
}
