package org.mtr.visionupgrade.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfigScreenBuilder {
    public static Screen createScreen(Screen parent) {
        VisionUpgradeConfig config = VisionUpgradeConfig.get();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("mtr_visionupgrade.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Category 1: Distances
        ConfigCategory distancesCategory = builder.getOrCreateCategory(Text.translatable("mtr_visionupgrade.config.category.distances"));

        distancesCategory.addEntry(entryBuilder.startIntSlider(
                Text.translatable("mtr_visionupgrade.config.rail_render_distance"),
                config.railRenderDistanceChunks,
                0,
                32
        )
                .setDefaultValue(8)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.rail_render_distance.tooltip"))
                .setSaveConsumer(val -> config.railRenderDistanceChunks = val)
                .build());

        distancesCategory.addEntry(entryBuilder.startIntSlider(
                Text.translatable("mtr_visionupgrade.config.vehicle_render_distance"),
                config.vehicleRenderDistance,
                32,
                512
        )
                .setDefaultValue(192)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.vehicle_render_distance.tooltip"))
                .setSaveConsumer(val -> config.vehicleRenderDistance = val)
                .build());

        // Category 2: Culling & Optimizations
        ConfigCategory cullingCategory = builder.getOrCreateCategory(Text.translatable("mtr_visionupgrade.config.category.culling"));

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_interior_culling"),
                config.enableInteriorCulling
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_interior_culling.tooltip"))
                .setSaveConsumer(val -> config.enableInteriorCulling = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startIntSlider(
                Text.translatable("mtr_visionupgrade.config.interior_culling_distance"),
                config.interiorCullingDistance,
                16,
                128
        )
                .setDefaultValue(48)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.interior_culling_distance.tooltip"))
                .setSaveConsumer(val -> config.interiorCullingDistance = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_frustum_culling"),
                config.enableFrustumCulling
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_frustum_culling.tooltip"))
                .setSaveConsumer(val -> config.enableFrustumCulling = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_safepoint_fix"),
                config.enableSafepointFix
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_safepoint_fix.tooltip"))
                .setSaveConsumer(val -> config.enableSafepointFix = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_iris_optimization"),
                config.enableIrisOptimization
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_iris_optimization.tooltip"))
                .setSaveConsumer(val -> config.enableIrisOptimization = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_parallel_vehicles"),
                config.enableParallelVehicles
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_parallel_vehicles.tooltip"))
                .setSaveConsumer(val -> config.enableParallelVehicles = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_rail_adaptive_lod"),
                config.enableRailAdaptiveLOD
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_rail_adaptive_lod.tooltip"))
                .setSaveConsumer(val -> config.enableRailAdaptiveLOD = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_dynamic_mipmap"),
                config.enableDynamicMipmap
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_dynamic_mipmap.tooltip"))
                .setSaveConsumer(val -> config.enableDynamicMipmap = val)
                .build());

        cullingCategory.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("mtr_visionupgrade.config.enable_backface_culling"),
                config.enableBackFaceCulling
        )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("mtr_visionupgrade.config.enable_backface_culling.tooltip"))
                .setSaveConsumer(val -> config.enableBackFaceCulling = val)
                .build());

        builder.setSavingRunnable(VisionUpgradeConfig::save);
        return builder.build();
    }
}
