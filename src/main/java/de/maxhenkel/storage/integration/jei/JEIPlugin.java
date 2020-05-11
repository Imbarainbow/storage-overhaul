package de.maxhenkel.storage.integration.jei;

import de.maxhenkel.storage.Main;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;

@mezz.jei.api.JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Main.MODID, "storage_overhaul");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

    }
}
