package tfar.thehandofgod.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import tfar.thehandofgod.TheHandOfGod;
import tfar.thehandofgod.menu.ConfigurePotionContainer;

public class ConfigurePotionScreen extends GuiContainer {

    private final IInventory playerInventory;
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation(TheHandOfGod.MODID,"textures/gui/enchantments.png");

    public ConfigurePotionScreen(ConfigurePotionContainer container) {
        super(container);
        this.playerInventory = container.playerInventory;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
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
        this.fontRenderer.drawString(/*this.handler.getDisplayName().getUnformattedText()*/"Configure Potions", 8, 6, 0x404040);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }
}
