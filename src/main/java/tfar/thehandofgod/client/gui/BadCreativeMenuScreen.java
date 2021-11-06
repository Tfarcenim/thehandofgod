package tfar.thehandofgod.client.gui;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.menu.BadCreativeMenu;

import java.io.IOException;
import java.util.Locale;

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
        badCreativeMenu.itemList.clear();
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
        this.updateSearch();

        this.currentScroll = 0.0F;
        badCreativeMenu.scrollTo(0.0F);
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
            ((BadCreativeMenu)this.inventorySlots).scrollTo(this.currentScroll);
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
                this.updateSearch();
            } else {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    private void updateSearch() {
        BadCreativeMenu guicontainercreative$containercreative = (BadCreativeMenu) this.inventorySlots;
        guicontainercreative$containercreative.itemList.clear();

        if (this.searchField.getText().isEmpty()) {
            for (Item item : Item.REGISTRY) {
                item.getSubItems(CreativeTabs.SEARCH, guicontainercreative$containercreative.itemList);
            }
        } else {
            guicontainercreative$containercreative.itemList.addAll(this.mc.getSearchTree(SearchTreeManager.ITEMS).search(this.searchField.getText().toLowerCase(Locale.ROOT)));
        }

        this.currentScroll = 0.0F;
        guicontainercreative$containercreative.scrollTo(0.0F);
    }


    /**
     * returns (if you are not on the inventoryTab) and (the flag isn't set) and (you have more than 1 page of items)
     */
    private boolean needsScrollBars()
    {
        return ((BadCreativeMenu)this.inventorySlots).canScroll();
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(/*this.handler.getDisplayName().getUnformattedText()*/"Pocket", 8, 6, 0x404040);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }

}
