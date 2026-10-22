package org.mtr.visionupgrade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = {
		"org.mtr.libraries.com.logisticscraft.occlusionculling.OcclusionCullingInstance",
		"com.logisticscraft.occlusionculling.OcclusionCullingInstance"
}, remap = false)
public abstract class OcclusionSafepointMixin {

	@Unique private int mtr$occlusionWork;

	@ModifyArg(method = {"isAABBVisible", "stepRay"}, at = @At(value = "INVOKE", target = "getCacheValue(III)I"), index = 0, require = 0)
	private int mtr$pollCacheScan(int x) {
		mtr$pollSafepoint();
		return x;
	}

	@ModifyArg(method = "isAABBVisible", at = @At(value = "INVOKE", target = "Ljava/util/BitSet;get(I)Z"), index = 0, require = 0)
	private int mtr$pollFaceScan(int index) {
		mtr$pollSafepoint();
		return index;
	}

	@Unique
	private void mtr$pollSafepoint() {
		if (org.mtr.visionupgrade.config.VisionUpgradeConfig.get().enableSafepointFix && (++mtr$occlusionWork & 0xFFFF) == 0) {
			Thread.yield();
		}
	}
}
