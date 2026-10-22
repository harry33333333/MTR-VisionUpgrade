package org.mtr.visionupgrade.mixin;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.client.VehicleRidingMovement;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.DynamicVehicleModel;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.mtr.visionupgrade.config.VisionUpgradeConfig;
import org.mtr.visionupgrade.culling.InteriorCullingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DynamicVehicleModel.class, remap = false)
public abstract class DynamicVehicleModelMixin {

	@Inject(
		method = "render(Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;I[IILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Z)V",
		at = @At("HEAD"),
		cancellable = true,
		remap = false,
		require = 0
	)
	private void mtr$onRenderDynamicModel(
		StoredMatrixTransformations storedMatrixTransformations,
		VehicleExtension vehicleExtension,
		int carNumber,
		int[] carTypes,
		int light,
		ObjectArrayList<?> doorwayPositions,
		boolean doorsClosed,
		CallbackInfo ci
	) {
		if (storedMatrixTransformations instanceof StoredMatrixTransformationsAccessor acc) {
			double x = acc.mtr$getInitialTranslateX();
			double y = acc.mtr$getInitialTranslateY();
			double z = acc.mtr$getInitialTranslateZ();
			if (x != 0 || z != 0) {
				boolean isRiding = VehicleRidingMovement.isRiding(vehicleExtension.getId());
				if (!isRiding) {
					try {
						Vector3d cameraPos = MinecraftClient.getInstance().getGameRendererMapped().getCamera().getPos();
						double dx = x - cameraPos.getXMapped();
						double dy = y - cameraPos.getYMapped();
						double dz = z - cameraPos.getZMapped();
						double distSq = dx * dx + dy * dy + dz * dz;

						VisionUpgradeConfig config = VisionUpgradeConfig.get();
						double maxDist = config.vehicleRenderDistance;
						if (distSq > maxDist * maxDist) {
							ci.cancel();
							return;
						}

						double interiorDist = config.interiorCullingDistance;
						if (config.enableInteriorCulling && distSq > interiorDist * interiorDist) {
							InteriorCullingState.setCullingInterior(true);
							return;
						}
					} catch (Throwable ignored) {
					}
				}
			}
		}
		InteriorCullingState.setCullingInterior(false);
	}

	@Inject(
		method = "render(Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;I[IILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Z)V",
		at = @At("RETURN"),
		remap = false,
		require = 0
	)
	private void mtr$afterRenderDynamicModel(
		StoredMatrixTransformations storedMatrixTransformations,
		VehicleExtension vehicleExtension,
		int carNumber,
		int[] carTypes,
		int light,
		ObjectArrayList<?> doorwayPositions,
		boolean doorsClosed,
		CallbackInfo ci
	) {
		InteriorCullingState.reset();
	}
}
