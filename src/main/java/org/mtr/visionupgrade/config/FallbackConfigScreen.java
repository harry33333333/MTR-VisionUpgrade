package org.mtr.visionupgrade.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class FallbackConfigScreen extends Screen {
    private final Screen parent;

    public FallbackConfigScreen(Screen parent) {
        super(Text.translatable("mtr_visionupgrade.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        VisionUpgradeConfig config = VisionUpgradeConfig.get();
        int centerX = this.width / 2;
        int y = 50;

        // Button to toggle rail distance
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Rail View Distance: " + (config.railRenderDistanceChunks == 0 ? "Follow Vanilla" : config.railRenderDistanceChunks + " Chunks")),
                button -> {
                    config.railRenderDistanceChunks = (config.railRenderDistanceChunks + 4) % 36;
                    button.setMessage(Text.literal("Rail View Distance: " + (config.railRenderDistanceChunks == 0 ? "Follow Vanilla" : config.railRenderDistanceChunks + " Chunks")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(centerX - 120, y, 240, 20).build());

        y += 26;
        // Button to toggle vehicle distance
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Vehicle Distance: " + config.vehicleRenderDistance + " Blocks"),
                button -> {
                    config.vehicleRenderDistance += 32;
                    if (config.vehicleRenderDistance > 384) config.vehicleRenderDistance = 64;
                    button.setMessage(Text.literal("Vehicle Distance: " + config.vehicleRenderDistance + " Blocks"));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(centerX - 120, y, 240, 20).build());

        y += 26;
        // Button to toggle interior culling
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Interior Culling: " + (config.enableInteriorCulling ? "ON" : "OFF")),
                button -> {
                    config.enableInteriorCulling = !config.enableInteriorCulling;
                    button.setMessage(Text.literal("Interior Culling: " + (config.enableInteriorCulling ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(centerX - 120, y, 240, 20).build());

        y += 26;
        // Button to toggle frustum culling
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Frustum Culling: " + (config.enableFrustumCulling ? "ON" : "OFF")),
                button -> {
                    config.enableFrustumCulling = !config.enableFrustumCulling;
                    button.setMessage(Text.literal("Frustum Culling: " + (config.enableFrustumCulling ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(centerX - 120, y, 240, 20).build());

        // Done button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            VisionUpgradeConfig.save();
            this.client.setScreen(this.parent);
        }).dimensions(centerX - 60, this.height - 36, 120, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        VisionUpgradeConfig.save();
        this.client.setScreen(this.parent);
    }
}
