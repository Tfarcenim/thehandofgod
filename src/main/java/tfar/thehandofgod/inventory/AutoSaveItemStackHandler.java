package tfar.thehandofgod.inventory;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.ItemStackHandler;
import tfar.thehandofgod.world.saveddata.BackpackData;

public class AutoSaveItemStackHandler extends ItemStackHandler {

    private final World world;

    public AutoSaveItemStackHandler(int size, World world) {
        super(size);
        this.world = world;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        BackpackData.getDefaultInstance((WorldServer) world).markDirty();
    }
}
