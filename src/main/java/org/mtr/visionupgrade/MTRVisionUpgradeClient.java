package org.mtr.visionupgrade;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mtr.visionupgrade.config.VisionUpgradeConfig;
import org.mtr.visionupgrade.culling.BlockEntityRenderCulling;
import org.mtr.visionupgrade.culling.InteriorCullingState;
import org.mtr.visionupgrade.render.RailGeometryCache;
import org.mtr.visionupgrade.render.RenderCallbackPool;
import org.mtr.visionupgrade.render.iris.RenderOrderCache;

public final class MTRVisionUpgradeClient implements ClientModInitializer {
	public static final String MOD_ID = "mtr_visionupgrade";
	public static final Logger LOGGER = LogManager.getLogger("MTR-VisionUpgrade");

	private static Object lastWorldInstance = null;

	@Override
	public void onInitializeClient() {
		LOGGER.info("[MTR-VisionUpgrade] Initializing client rendering and performance optimization suite...");
		VisionUpgradeConfig.load();
		BlockEntityRenderCulling.init();

		// Register lifecycle event listeners for disconnects and world joins
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			LOGGER.info("[MTR-VisionUpgrade] Disconnected from server/world, purging all client render caches...");
			lastWorldInstance = null;
			clearAllCaches();
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			LOGGER.info("[MTR-VisionUpgrade] Joined server/world, resetting client render caches...");
			lastWorldInstance = null;
			clearAllCaches();
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			LOGGER.info("[MTR-VisionUpgrade] Client stopping, releasing render caches...");
			lastWorldInstance = null;
			clearAllCaches();
		});

		// Detect client dimension switch (Overworld <-> Nether <-> End)
		WorldRenderEvents.START.register(context -> {
			final Object currentWorld = context.world();
			if (currentWorld != null && currentWorld != lastWorldInstance) {
				if (lastWorldInstance != null) {
					LOGGER.info("[MTR-VisionUpgrade] Dimension/world change detected, purging geometry & render caches...");
					clearAllCaches();
				}
				lastWorldInstance = currentWorld;
			}
		});

		LOGGER.info("[MTR-VisionUpgrade] Client frustum culling, buffer reuse, geometry cache, and shader sort optimizations active!");
	}

	public static void clearAllCaches() {
		try {
			RailGeometryCache.INSTANCE.clear();
			RenderCallbackPool.INSTANCE.clear();
			RenderOrderCache.clearAll();
			BlockEntityRenderCulling.clear();
			InteriorCullingState.reset();
			LOGGER.info("[MTR-VisionUpgrade] All client render caches successfully cleared.");
		} catch (Throwable t) {
			LOGGER.error("[MTR-VisionUpgrade] Failed to clear client caches cleanly", t);
		}
	}
}
