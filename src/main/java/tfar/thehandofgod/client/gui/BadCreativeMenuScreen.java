package tfar.thehandofgod.client.gui;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import search.SearchHelper;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.menu.BadCreativeMenu;
import tfar.thehandofgod.network.C2SSendItemStackPacket;
import tfar.thehandofgod.network.PacketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BadCreativeMenuScreen extends GuiContainer {

    private final IInventory playerInventory;
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(TheHandOfGod.MODID, "textures/gui/heavenly_pocket.png");

    /**
     * Amount scrolled in Creative mode inventory (0 = top, 1 = bottom)
     */
    private float currentScroll;
    /**
     * True if the scrollbar is being dragged
     */
    private boolean isScrolling;
    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking;
    private GuiTextField searchField;

    private boolean clearSearch;
    private int topRow;
    private int totalItems;


    public BadCreativeMenuScreen(BadCreativeMenu container) {
        super(container);
        this.playerInventory = container.playerInventory;
        xSize += 18;
        ySize += 56;
    }

    @Override
    public void initGui() {
        super.initGui();
        BadCreativeMenu badCreativeMenu = (BadCreativeMenu)this.inventorySlots;
        this.dragSplittingSlots.clear();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.searchField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
        this.searchField.setMaxStringLength(50);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setTextColor(0xffffff);

        this.searchField.setCanLoseFocus(false);
        this.searchField.setFocused(true);
        this.searchField.setText("");
        //  this.searchField.width = tab.getSearchbarWidth();
        //   this.searchField.x = this.guiLeft + (82 /*default left*/ + 89 /*default width*/) - this.searchField.width;
        this.updateSearch(false);

        this.currentScroll = 0.0F;
       // badCreativeMenu.scrollTo(0.0F);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
        {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag)
        {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling)
        {
            this.currentScroll = ((float)(mouseY - l) - 7.5F) / ((float)(j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);


            topRow = (int) (Math.ceil(totalItems/9d - 6) * currentScroll);
            updateSearch(false);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);


        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.clearSearch) {
            this.clearSearch = false;
            this.searchField.setText("");
        }

        if (!this.checkHotbarKeys(keyCode)) {
            if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
                this.updateSearch(true);
            } else {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    private void updateSearch(boolean changedText) {

        if (changedText) {
            topRow = 0;
        }

        String text = searchField.getText();

        List<ItemStack> items = new ArrayList<>();

        if (text.isEmpty()) {
            NonNullList<ItemStack> nonNullList = NonNullList.create();
            for (Item item : Item.REGISTRY) {
                item.getSubItems(CreativeTabs.SEARCH, nonNullList);
            }
            items.addAll(nonNullList);
        } else {
            items = SearchHelper.search(text);
        }
        totalItems = items.size();
        //we only need to display 54 items so only send up to 54
        int skip = topRow * 9;

        items = items.subList(skip,Math.min(items.size(),BadCreativeMenu.SLOTS+skip));
        PacketHandler.INSTANCE.sendToServer(new C2SSendItemStackPacket(items));
    }

    private boolean needsScrollBars()
    {
        return totalItems > BadCreativeMenu.SLOTS;
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        if (needsScrollBars()) {
            int k = (int) (j + 18 + 95 * currentScroll);
            drawTexturedModalRect(i + 174, k, 244, 0, 12, 15);
         //   drawTexturedModalRect(i - 17, j + 68, 174, 18, 14, 111);
        }

        this.searchField.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(/*this.handler.getDisplayName().getUnformattedText()*/"Pocket", 8, 6, 0x404040);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (needsScrollBars()) {
            int scrollDelta = -Mouse.getEventDWheel();

            if (scrollDelta != 0) {

                if (scrollDelta > 0) {
                    scrollDelta = 1;
                }

                if (scrollDelta < 0) {
                    scrollDelta = -1;
                }
                topRow += scrollDelta;

                topRow = MathHelper.clamp(topRow,0,(int)Math.ceil(totalItems/9d - 6));

                updateSearch(false);
            }
        }
    }
}
