package org.mtr.visionupgrade;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mtr.visionupgrade.culling.BlockEntityRenderCulling;

public final class MTRVisionUpgradeClient implements ClientModInitializer {
	public static final String MOD_ID = "mtr_visionupgrade";
	public static final Logger LOGGER = LogManager.getLogger("MTR-VisionUpgrade");

	@Override
	public void onInitializeClient() {
		LOGGER.info("[MTR-VisionUpgrade] Initializing client rendering and performance optimization suite...");
		org.mtr.visionupgrade.config.VisionUpgradeConfig.load();
		BlockEntityRenderCulling.init();
		LOGGER.info("[MTR-VisionUpgrade] Client frustum culling, buffer reuse, geometry cache, and shader sort optimizations active!");
	}
}
