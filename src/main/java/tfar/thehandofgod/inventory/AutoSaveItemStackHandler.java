package tfar.thehandofgod.inventory;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import tfar.thehandofgod.world.saveddata.BackpackData;

public class AutoSaveItemStackHandler extends BiggerItemStackHandler {

    private final World world;
    private int page;
    private final ItemStackHandlerManager manager;

    public AutoSaveItemStackHandler(int size, World world,int initialPage,ItemStackHandlerManager manager) {
        super(size);
        this.world = world;
        this.page = initialPage;
        this.manager = manager;
        this.stacks = manager.stacksStacks.get(page);
    }

    public void setPage(int page) {
        this.page = page;
        if (page >= manager.stacksStacks.size()) {
            manager.addPage();
        }
        this.stacks = manager.stacksStacks.get(page);
    }

    public int getPage() {
        return page;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        BackpackData.getDefaultInstance((WorldServer) world).markDirty();
    }
}
