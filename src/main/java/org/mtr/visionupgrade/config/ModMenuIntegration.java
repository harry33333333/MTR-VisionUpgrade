package org.mtr.visionupgrade.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (FabricLoader.getInstance().isModLoaded("cloth-config") || FabricLoader.getInstance().isModLoaded("cloth-config2")) {
                try {
                    return ClothConfigScreenBuilder.createScreen(parent);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            return new FallbackConfigScreen(parent);
        };
    }
}
