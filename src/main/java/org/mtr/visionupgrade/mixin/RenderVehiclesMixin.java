package org.mtr.visionupgrade.mixin;

import org.mtr.core.data.VehicleExtraData;
import org.mtr.core.tool.Vector;
import org.mtr.libraries.com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import org.mtr.libraries.com.logisticscraft.occlusionculling.util.Vec3d;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectBooleanImmutablePair;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.mtr.mapping.holder.ClientPlayerEntity;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.client.VehicleRidingMovement;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.render.PositionAndRotation;
import org.mtr.mod.render.RenderVehicles;
import org.mtr.visionupgrade.config.VisionUpgradeConfig;
import org.mtr.visionupgrade.culling.InteriorCullingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@Mixin(value = RenderVehicles.class, remap = false)
public abstract class RenderVehiclesMixin {

	@Redirect(
		method = "lambda$render$3(Lorg/mtr/mod/data/VehicleExtension;ILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectObjectImmutablePair;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/OcclusionCullingInstance;)Ljava/lang/Runnable;",
		at = @At(
			value = "INVOKE",
			target = "Lorg/mtr/libraries/com/logisticscraft/occlusionculling/OcclusionCullingInstance;isAABBVisible(Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;)Z"
		),
		remap = false,
		require = 0
	)
	private static boolean mtr$cullDistantVehicleOcclusion(
		OcclusionCullingInstance instance,
		Vec3d minVec,
		Vec3d maxVec,
		Vec3d cameraVec
	) {
		VisionUpgradeConfig config = VisionUpgradeConfig.get();
		double maxDist = config.vehicleRenderDistance;
		double maxDistSq = maxDist * maxDist;

		double cx = (minVec.x + maxVec.x) * 0.5;
		double cy = (minVec.y + maxVec.y) * 0.5;
		double cz = (minVec.z + maxVec.z) * 0.5;

		double dx = cx - cameraVec.x;
		double dy = cy - cameraVec.y;
		double dz = cz - cameraVec.z;
		if (dx * dx + dy * dy + dz * dz > maxDistSq) {
			return false;
		}
		return instance.isAABBVisible(minVec, maxVec, cameraVec);
	}

	@Inject(
		method = "lambda$render$14(Lorg/mtr/mod/data/VehicleExtension;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectObjectImmutablePair;Lorg/mtr/mapping/holder/Vector3d;Ljava/lang/Double;Lorg/mtr/mod/render/PositionAndRotation;Lorg/mtr/mapping/holder/Vector3d;ILorg/mtr/mapping/holder/ClientPlayerEntity;IZLorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lorg/mtr/mapping/holder/MinecraftClient;Lorg/mtr/mapping/holder/ClientWorld;JLorg/mtr/mod/render/RenderVehicles$PreviousGangwayMovementPositions;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectBooleanImmutablePair;)V",
		at = @At("HEAD"),
		cancellable = true,
		remap = false,
		require = 0
	)
	private static void mtr$beforeRenderCar(
		VehicleExtension vehicle,
		ObjectObjectImmutablePair carPair,
		Vector3d playerPos,
		Double yawDiff,
		PositionAndRotation positionAndRotation,
		Vector3d cameraPos,
		int carNumber,
		ClientPlayerEntity player,
		int ridingCar,
		boolean isSpectator,
		ObjectArrayList<?> bogiePositionsList,
		ObjectArrayList<?> carPositionsList,
		MinecraftClient minecraftClient,
		ClientWorld clientWorld,
		long millisElapsed,
		Object previousGangwayMovementPositions,
		ObjectBooleanImmutablePair vehicleResourcePair,
		CallbackInfo ci
	) {
		boolean isRiding = VehicleRidingMovement.isRiding(vehicle.getId());
		Object rightObj = carPair.right();
		if (rightObj instanceof ObjectObjectImmutablePair subPair) {
			Object posRotObj = subPair.right();
			if (posRotObj instanceof PositionAndRotation carPosRot) {
				Vector carPos = carPosRot.position;
				double dx = carPos.x - cameraPos.getXMapped();
				double dy = carPos.y - cameraPos.getYMapped();
				double dz = carPos.z - cameraPos.getZMapped();
				double distSq = dx * dx + dy * dy + dz * dz;

				VisionUpgradeConfig config = VisionUpgradeConfig.get();
				double maxVehicleDist = config.vehicleRenderDistance;
				if (!isRiding && distSq > maxVehicleDist * maxVehicleDist) {
					ci.cancel();
					return;
				}

				double interiorDist = config.interiorCullingDistance;
				if (!isRiding && config.enableInteriorCulling && distSq > interiorDist * interiorDist) {
					InteriorCullingState.setCullingInterior(true);
					return;
				}
			}
		}
		InteriorCullingState.setCullingInterior(false);
	}

	@Inject(
		method = "lambda$render$14(Lorg/mtr/mod/data/VehicleExtension;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectObjectImmutablePair;Lorg/mtr/mapping/holder/Vector3d;Ljava/lang/Double;Lorg/mtr/mod/render/PositionAndRotation;Lorg/mtr/mapping/holder/Vector3d;ILorg/mtr/mapping/holder/ClientPlayerEntity;IZLorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lorg/mtr/mapping/holder/MinecraftClient;Lorg/mtr/mapping/holder/ClientWorld;JLorg/mtr/mod/render/RenderVehicles$PreviousGangwayMovementPositions;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectBooleanImmutablePair;)V",
		at = @At("RETURN"),
		remap = false,
		require = 0
	)
	private static void mtr$afterRenderCar(
		VehicleExtension vehicle,
		ObjectObjectImmutablePair carPair,
		Vector3d playerPos,
		Double yawDiff,
		PositionAndRotation positionAndRotation,
		Vector3d cameraPos,
		int carNumber,
		ClientPlayerEntity player,
		int ridingCar,
		boolean isSpectator,
		ObjectArrayList<?> bogiePositionsList,
		ObjectArrayList<?> carPositionsList,
		MinecraftClient minecraftClient,
		ClientWorld clientWorld,
		long millisElapsed,
		Object previousGangwayMovementPositions,
		ObjectBooleanImmutablePair vehicleResourcePair,
		CallbackInfo ci
	) {
		InteriorCullingState.reset();
	}

	@Redirect(
		method = "lambda$render$14(Lorg/mtr/mod/data/VehicleExtension;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectObjectImmutablePair;Lorg/mtr/mapping/holder/Vector3d;Ljava/lang/Double;Lorg/mtr/mod/render/PositionAndRotation;Lorg/mtr/mapping/holder/Vector3d;ILorg/mtr/mapping/holder/ClientPlayerEntity;IZLorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Lorg/mtr/mapping/holder/MinecraftClient;Lorg/mtr/mapping/holder/ClientWorld;JLorg/mtr/mod/render/RenderVehicles$PreviousGangwayMovementPositions;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectBooleanImmutablePair;)V",
		at = @At(
			value = "INVOKE",
			target = "Lorg/mtr/core/data/VehicleExtraData;iterateRidingEntities(Ljava/util/function/Consumer;)V"
		),
		remap = false,
		require = 0
	)
	private static void mtr$cullDistantPassengers(VehicleExtraData data, Consumer<?> consumer) {
		if (InteriorCullingState.isCullingInterior()) {
			return; // Skip rendering passengers inside distant cars!
		}
		data.iterateRidingEntities((Consumer) consumer);
	}
}
