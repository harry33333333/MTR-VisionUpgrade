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
        final VisionUpgradeConfig config = VisionUpgradeConfig.get();
        final int centerX = this.width / 2;
        final int colWidth = 160;
        final int btnHeight = 20;
        final int leftX = centerX - colWidth - 5;
        final int rightX = centerX + 5;
        int y = 45;
        final int rowSpacing = 24;

        // Row 0: Rail Distance & Vehicle Distance
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Rail Dist: " + (config.railRenderDistanceChunks == 0 ? "Vanilla" : config.railRenderDistanceChunks + " Chunks")),
                button -> {
                    if (config.railRenderDistanceChunks == 0) config.railRenderDistanceChunks = 4;
                    else if (config.railRenderDistanceChunks >= 32) config.railRenderDistanceChunks = 0;
                    else config.railRenderDistanceChunks += 4;
                    button.setMessage(Text.literal("Rail Dist: " + (config.railRenderDistanceChunks == 0 ? "Vanilla" : config.railRenderDistanceChunks + " Chunks")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(leftX, y, colWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Train Dist: " + config.vehicleRenderDistance + "m"),
                button -> {
                    config.vehicleRenderDistance += 64;
                    if (config.vehicleRenderDistance > 384) config.vehicleRenderDistance = 64;
                    button.setMessage(Text.literal("Train Dist: " + config.vehicleRenderDistance + "m"));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(rightX, y, colWidth, btnHeight).build());

        // Row 1: Interior Distance & Interior Culling Toggle
        y += rowSpacing;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Interior Dist: " + config.interiorCullingDistance + "m"),
                button -> {
                    config.interiorCullingDistance += 16;
                    if (config.interiorCullingDistance > 96) config.interiorCullingDistance = 32;
                    button.setMessage(Text.literal("Interior Dist: " + config.interiorCullingDistance + "m"));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(leftX, y, colWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Interior Culling: " + (config.enableInteriorCulling ? "ON" : "OFF")),
                button -> {
                    config.enableInteriorCulling = !config.enableInteriorCulling;
                    button.setMessage(Text.literal("Interior Culling: " + (config.enableInteriorCulling ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(rightX, y, colWidth, btnHeight).build());

        // Row 2: Platform Door Frustum Culling & Safepoint GC Fix
        y += rowSpacing;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Door Frustum Cull: " + (config.enableFrustumCulling ? "ON" : "OFF")),
                button -> {
                    config.enableFrustumCulling = !config.enableFrustumCulling;
                    button.setMessage(Text.literal("Door Frustum Cull: " + (config.enableFrustumCulling ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(leftX, y, colWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Safepoint GC Fix: " + (config.enableSafepointFix ? "ON" : "OFF")),
                button -> {
                    config.enableSafepointFix = !config.enableSafepointFix;
                    button.setMessage(Text.literal("Safepoint GC Fix: " + (config.enableSafepointFix ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(rightX, y, colWidth, btnHeight).build());

        // Row 3: Iris Shaders Optimization & Multi-Core Vehicles
        y += rowSpacing;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Iris Shader Opt: " + (config.enableIrisOptimization ? "ON" : "OFF")),
                button -> {
                    config.enableIrisOptimization = !config.enableIrisOptimization;
                    button.setMessage(Text.literal("Iris Shader Opt: " + (config.enableIrisOptimization ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(leftX, y, colWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Multi-Core Train: " + (config.enableParallelVehicles ? "ON" : "OFF")),
                button -> {
                    config.enableParallelVehicles = !config.enableParallelVehicles;
                    button.setMessage(Text.literal("Multi-Core Train: " + (config.enableParallelVehicles ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(rightX, y, colWidth, btnHeight).build());

        // Row 4: Rail Adaptive Curve LOD & Dynamic Textures Mipmap
        y += rowSpacing;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Rail Curve LOD: " + (config.enableRailAdaptiveLOD ? "ON" : "OFF")),
                button -> {
                    config.enableRailAdaptiveLOD = !config.enableRailAdaptiveLOD;
                    button.setMessage(Text.literal("Rail Curve LOD: " + (config.enableRailAdaptiveLOD ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(leftX, y, colWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Texture Mipmap: " + (config.enableDynamicMipmap ? "ON" : "OFF")),
                button -> {
                    config.enableDynamicMipmap = !config.enableDynamicMipmap;
                    button.setMessage(Text.literal("Texture Mipmap: " + (config.enableDynamicMipmap ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(rightX, y, colWidth, btnHeight).build());

        // Row 5: Back-face Culling
        y += rowSpacing;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back-Face Culling: " + (config.enableBackFaceCulling ? "ON" : "OFF")),
                button -> {
                    config.enableBackFaceCulling = !config.enableBackFaceCulling;
                    button.setMessage(Text.literal("Back-Face Culling: " + (config.enableBackFaceCulling ? "ON" : "OFF")));
                    VisionUpgradeConfig.save();
                }
        ).dimensions(leftX, y, colWidth, btnHeight).build());

        // Reset to Defaults Button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset Defaults"),
                button -> {
                    config.railRenderDistanceChunks = 8;
                    config.vehicleRenderDistance = 192;
                    config.enableInteriorCulling = true;
                    config.interiorCullingDistance = 48;
                    config.enableFrustumCulling = true;
                    config.enableSafepointFix = true;
                    config.enableIrisOptimization = true;
                    config.enableParallelVehicles = true;
                    config.enableRailAdaptiveLOD = true;
                    config.enableDynamicMipmap = true;
                    config.enableBackFaceCulling = true;
                    VisionUpgradeConfig.save();
                    this.clearAndInit();
                }
        ).dimensions(rightX, y, colWidth, btnHeight).build());

        // Done button at bottom
        final int doneY = Math.max(y + rowSpacing + 4, this.height - 28);
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            VisionUpgradeConfig.save();
            this.client.setScreen(this.parent);
        }).dimensions(centerX - 60, doneY, 120, btnHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        VisionUpgradeConfig.save();
        this.client.setScreen(this.parent);
    }
}
