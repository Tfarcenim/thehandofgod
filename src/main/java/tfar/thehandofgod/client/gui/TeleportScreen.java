package tfar.thehandofgod.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.menu.TeleportContainer;
import tfar.thehandofgod.network.C2STeleportPacket;
import tfar.thehandofgod.network.PacketHandler;

import java.io.IOException;

public class TeleportScreen extends GuiContainer implements IContainerListener {

    private final IInventory playerInventory;
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation(TheHandOfGod.MODID, "textures/gui/teleport.png");

    private GuiTextField[] fields = new GuiTextField[4];

    private static final String[] labels = new String[]{"x:","y:","z:","dim:"};

    public TeleportScreen(TeleportContainer container) {
        super(container);
        this.playerInventory = container.playerInventory;
        ySize+=18;
    }

    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        addButton(new GuiButton(0, i + 10, j+ 25, 60, 20, "Teleport"));


        for (int k = 0; k < fields.length; k++) {
            GuiTextField field = new GuiTextField(0, this.fontRenderer, i + 62 + 38, j + 8 + k * 18, 60, 12);
            field.setTextColor(-1);
            field.setDisabledTextColour(-1);
            field.setEnableBackgroundDrawing(false);
            field.setMaxStringLength(9);
            fields[k] = field;
        }
        this.inventorySlots.removeListener(this);
        this.inventorySlots.addListener(this);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            String textX = fields[0].getText();
            String textY = fields[1].getText();
            String textZ = fields[2].getText();
            String dim = fields[3].getText();
             try {
                BlockPos pos = new BlockPos(Integer.parseInt(textX),Integer.parseInt(textY),Integer.parseInt(textZ));
                PacketHandler.INSTANCE.sendToServer(new C2STeleportPacket(pos,Integer.parseInt(dim)));
            } catch (NumberFormatException e) {
                 System.out.println("invalid number(s)");
             }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        for (GuiTextField field : fields) {
            drawRect(field.x - 2,field.y-2,field.x + field.width,field.y +field.height ,0xff000000);
            field.drawTextBox();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(/*this.handler.getDisplayName().getUnformattedText()*/"Teleport", 8, 6, 0x404040);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);

        for (int i = 0; i < labels.length;i++) {
            this.fontRenderer.drawString(labels[i],80,i * 18 + 12,0x404040);
        }

    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        this.inventorySlots.removeListener(this);
    }


    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if (keyCode == Keyboard.KEY_TAB) {

            return;
        }

        for (GuiTextField field : fields) {
            if (field.textboxKeyTyped(typedChar, keyCode)) {
                return;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiTextField field : fields) {
            field.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }


    @Override
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {

    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {

    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {

    }

    @Override
    public void sendAllWindowProperties(Container containerIn, IInventory inventory) {

    }
}
