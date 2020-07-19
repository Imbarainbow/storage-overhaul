package de.maxhenkel.storage.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AdvancedShulkerboxScreen extends ScreenBase<AdvancedShulkerboxContainer> {

    public static final ResourceLocation DEFAULT_IMAGE = new ResourceLocation("textures/gui/container/shulker_box.png");

    private PlayerInventory playerInventory;

    public AdvancedShulkerboxScreen(AdvancedShulkerboxContainer shulkerboxContainer, PlayerInventory playerInventory, ITextComponent name) {
        super(DEFAULT_IMAGE, shulkerboxContainer, playerInventory, name);

        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
        field_230712_o_.func_238421_b_(matrixStack, field_230704_d_.getString(), 8F, 6F, FONT_COLOR);
        field_230712_o_.func_238421_b_(matrixStack, playerInventory.getDisplayName().getString(), 8F, (float) (ySize - 96 + 3), FONT_COLOR);
    }

}