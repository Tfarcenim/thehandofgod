package tfar.thehandofgod.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import tfar.thehandofgod.client.SmallButton;
import tfar.thehandofgod.inventory.ItemStackHandlerManager;
import tfar.thehandofgod.menu.BackpackContainer;
import tfar.thehandofgod.network.C2SPagePacket;
import tfar.thehandofgod.network.PacketHandler;
import tfar.thehandofgod.world.saveddata.BackpackData;

import java.io.IOException;

public class BackpackScreen extends GuiContainer {

    /**
     * The ResourceLocation containing the chest GUI texture.
     */
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final IInventory playerInventory;
    /**
     * Window height is calculated with these values; the more rows, the higher
     */
    private final int inventoryRows;

    public BackpackScreen(BackpackContainer container) {
        super(container);
        this.playerInventory = container.playerInventory;
        this.allowUserInput = false;
        int i = 222;
        int j = 114;
        this.inventoryRows = 6;
        this.ySize = 114 + this.inventoryRows * 18;
    }

    private static final int RIGHT = 0;
    private static final int LEFT = 1;

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new SmallButton(LEFT,this.guiLeft + 64,this.guiTop + 127,10,10,"<"));
        buttonList.add(new SmallButton(RIGHT,this.guiLeft + 162,this.guiTop + 127,10,10,">"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == RIGHT) {
            PacketHandler.INSTANCE.sendToServer(new C2SPagePacket(backpackContainer().getPage() + 1));
        }
        if (button.id == LEFT) {
            PacketHandler.INSTANCE.sendToServer(new C2SPagePacket(backpackContainer().getPage() - 1));
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(/*this.handler.getDisplayName().getUnformattedText()*/"Backpack", 8, 6, 0x404040);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);

        int x = this.xSize /2 - 10;

        this.fontRenderer.drawString(backpackContainer().getPage() + "/"+ ItemStackHandlerManager.MAX_PAGES,x,6,0x404040);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }

    public BackpackContainer backpackContainer() {
        return (BackpackContainer)inventorySlots;
    }
}
