package com.dronez.block.workshop;

import com.dronez.DronezMod;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class WorkshopScreen extends ContainerScreen<WorkshopContainer> {
    private final ResourceLocation ASSEMBLE_GUI = new ResourceLocation(DronezMod.MODID, "textures/gui/workshop_assemble.png");
    private final ResourceLocation AI_GUI = new ResourceLocation(DronezMod.MODID, "textures/gui/workshop_ai.png");
    private WorkshopDisplayType displayType;
    private Button switchViewButton;

    public WorkshopScreen(WorkshopContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        displayType = WorkshopDisplayType.ASSEMBLE;
    }

    @Override
    protected void init() {
        super.init();
        addButton(switchViewButton = new Button(guiLeft - 56, guiTop, 56, 20, getButtonTitle(), button -> {
            displayType = displayType == WorkshopDisplayType.ASSEMBLE ? WorkshopDisplayType.AI : WorkshopDisplayType.ASSEMBLE;
            switchViewButton.setMessage(getButtonTitle());
            container.setDisplayType(displayType);
        }));
        container.setDisplayType(displayType);
    }

    /**
     * Returns the name of the opposite display type
     * @return the title of the button for a certain display type
     */
    @Nonnull
    private String getButtonTitle() {
        switch (displayType) {
            case ASSEMBLE:
                return "AI Lab";
            case AI:
                return "Assemble";
            default:
                throw new IllegalStateException("displayType shouldn't be null");
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //this.font.drawString("Workshop", 8.0F, 6.0F, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (displayType == WorkshopDisplayType.ASSEMBLE) {
            getMinecraft().getTextureManager().bindTexture(ASSEMBLE_GUI);
        } else {
            getMinecraft().getTextureManager().bindTexture(AI_GUI);
        }
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
