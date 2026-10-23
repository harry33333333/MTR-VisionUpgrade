package org.mtr.visionupgrade.mixin;

import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.render.PositionAndRotation;
import org.mtr.mod.render.RenderVehicles;
import org.mtr.visionupgrade.config.VisionUpgradeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderVehicles.class, remap = false)
public abstract class RenderVehiclesMixin {

	@Inject(
		method = "renderPlayer(Lorg/mtr/mapping/holder/Entity;IDDLorg/mtr/mapping/holder/Vector3d;Lorg/mtr/mapping/holder/Vector3d;Ljava/lang/Double;Lorg/mtr/mod/render/PositionAndRotation;Lorg/mtr/mod/render/PositionAndRotation;Lorg/mtr/mapping/holder/Vector3d;)V",
		at = @At("HEAD"),
		cancellable = true,
		remap = false,
		require = 0
	)
	private static void mtr$cullDistantPassenger(
		Entity entity,
		int car,
		double x,
		double y,
		Vector3d vec1,
		Vector3d vec2,
		Double d,
		PositionAndRotation pr1,
		PositionAndRotation pr2,
		Vector3d cameraPosition,
		CallbackInfo ci
	) {
		VisionUpgradeConfig config = VisionUpgradeConfig.get();
		if (!config.enableInteriorCulling) {
			return;
		}
		PositionAndRotation pr = pr1 != null ? pr1 : pr2;
		if (pr != null && pr.position != null && cameraPosition != null) {
			double dx = pr.position.x - cameraPosition.getXMapped();
			double dy = pr.position.y - cameraPosition.getYMapped();
			double dz = pr.position.z - cameraPosition.getZMapped();
			double distSq = dx * dx + dy * dy + dz * dz;
			double interiorDist = config.interiorCullingDistance;
			if (distSq > interiorDist * interiorDist) {
				ci.cancel();
			}
		}
	}
}

