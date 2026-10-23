package org.mtr.visionupgrade.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mtr.visionupgrade.config.VisionUpgradeConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class VisionUpgradeMixinPlugin implements IMixinConfigPlugin {
	private static final Logger LOGGER = LogManager.getLogger("MTR-VisionUpgrade/MixinPlugin");
	private static final String IRIS_PACKAGE_PREFIX = "org.mtr.visionupgrade.mixin.Iris";

	@Override
	public void onLoad(String mixinPackage) {
		try {
			VisionUpgradeConfig.load();
		} catch (Throwable t) {
			LOGGER.warn("[MTR-VisionUpgrade] Early config loading failed in MixinPlugin, using defaults: {}", t.getMessage());
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.startsWith(IRIS_PACKAGE_PREFIX)) {
			final boolean irisLoaded = FabricLoader.getInstance().isModLoaded("iris");
			final boolean irisOptEnabled = VisionUpgradeConfig.get().enableIrisOptimization;
			if (!irisLoaded) {
				LOGGER.info("[MTR-VisionUpgrade] Iris mod not detected, skipping Iris mixin: {}", mixinClassName);
				return false;
			}
			if (!irisOptEnabled) {
				LOGGER.info("[MTR-VisionUpgrade] Iris optimization disabled in config, skipping: {}", mixinClassName);
				return false;
			}
			LOGGER.info("[MTR-VisionUpgrade] Applying Iris optimization mixin: {}", mixinClassName);
			return true;
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
