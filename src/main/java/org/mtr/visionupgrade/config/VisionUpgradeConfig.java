package org.mtr.visionupgrade.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class VisionUpgradeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static VisionUpgradeConfig INSTANCE = new VisionUpgradeConfig();

    private static File getConfigFile() {
        try {
            final FabricLoader loader = FabricLoader.getInstance();
            if (loader != null && loader.getConfigDir() != null) {
                return loader.getConfigDir().resolve("mtr_visionupgrade.json").toFile();
            }
        } catch (Throwable ignored) {
        }
        return new File("mtr_visionupgrade.json");
    }

    /**
     * Independent rail render distance in chunks.
     * 0 = follow vanilla client view distance.
     * > 0 = render rails up to (chunks * 16) blocks, even when on multiplayer servers.
     */
    public int railRenderDistanceChunks = 8;

    /**
     * Maximum vehicle/train render distance in blocks.
     * Train cars beyond this distance are completely culled.
     */
    public int vehicleRenderDistance = 192;

    /**
     * Whether distant interior culling is enabled.
     */
    public boolean enableInteriorCulling = true;

    /**
     * Distance in blocks beyond which car interior (seats, handrails, passengers) is culled.
     */
    public int interiorCullingDistance = 48;

    /**
     * Whether camera frustum culling is enabled for PSD / APG doors.
     */
    public boolean enableFrustumCulling = true;

    /**
     * Whether occlusion safepoint fix is enabled to prevent GC pauses.
     */
    public boolean enableSafepointFix = true;

    /**
     * Whether Iris shader topological sort optimization is enabled.
     */
    public boolean enableIrisOptimization = true;

    /**
     * Whether multi-core parallel vehicle preparation pipeline is enabled.
     */
    public boolean enableParallelVehicles = true;

    /**
     * Whether adaptive chordal LOD merging for distant continuous rails is enabled.
     */
    public boolean enableRailAdaptiveLOD = true;

    /**
     * Whether trilinear mipmap filtering is enabled for dynamic textures (text/PIDS).
     */
    public boolean enableDynamicMipmap = true;

    /**
     * Whether back-face culling is enabled for single-sided station route maps.
     */
    public boolean enableBackFaceCulling = true;

    public static VisionUpgradeConfig get() {
        return INSTANCE;
    }

    public static void load() {
        final File file = getConfigFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                INSTANCE = GSON.fromJson(reader, VisionUpgradeConfig.class);
                if (INSTANCE == null) {
                    INSTANCE = new VisionUpgradeConfig();
                }
            } catch (Exception e) {
                System.err.println("[MTR-VisionUpgrade] Failed to load config: " + e.getMessage());
                INSTANCE = new VisionUpgradeConfig();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            final File file = getConfigFile();
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            System.err.println("[MTR-VisionUpgrade] Failed to save config: " + e.getMessage());
        }
    }

    public int getEffectiveRailRenderDistanceChunks(int vanillaChunks) {
        if (railRenderDistanceChunks <= 0) {
            return vanillaChunks;
        }
        return railRenderDistanceChunks;
    }

    public int getEffectiveRailRenderDistanceBlocks(int vanillaChunks) {
        return getEffectiveRailRenderDistanceChunks(vanillaChunks) * 16;
    }
}
