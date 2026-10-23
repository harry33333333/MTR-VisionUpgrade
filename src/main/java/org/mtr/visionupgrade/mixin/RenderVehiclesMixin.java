package org.mtr.visionupgrade.mixin;

import org.mtr.libraries.com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import org.mtr.libraries.com.logisticscraft.occlusionculling.util.Vec3d;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mod.render.PositionAndRotation;
import org.mtr.mod.render.RenderVehicles;
import org.mtr.visionupgrade.config.VisionUpgradeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderVehicles.class, remap = false)
public abstract class RenderVehiclesMixin {

	@Redirect(
		method = "render(JLorg/mtr/mapping/holder/Vector3d;)V",
		at = @At(
			value = "INVOKE",
			target = "Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArraySet;forEach(Ljava/util/function/Consumer;)V"
		),
		remap = false,
		require = 0
	)
	private static void mtr$parallelVehiclePreparation(org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet<org.mtr.mod.data.VehicleExtension> vehicles, java.util.function.Consumer<org.mtr.mod.data.VehicleExtension> consumer) {
		org.mtr.visionupgrade.render.VehicleParallelManager.prepareVehicles(vehicles, consumer);
	}

	@Redirect(
		method = "lambda$render$15",
		at = @At(value = "INVOKE", target = "Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;add(Ljava/lang/Object;)Z"),
		remap = false,
		require = 0
	)
	private static boolean mtr$threadSafeAdd(org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList list, Object element) {
		synchronized (list) {
			return list.add(element);
		}
	}

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
