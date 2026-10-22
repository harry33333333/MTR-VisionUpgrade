package org.mtr.visionupgrade.mixin;

import org.mtr.core.data.RailMath;
import org.mtr.libraries.com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import org.mtr.libraries.com.logisticscraft.occlusionculling.util.Vec3d;
import org.mtr.mapping.mapper.MinecraftClientHelper;
import org.mtr.mod.render.RenderRails;
import org.mtr.visionupgrade.config.VisionUpgradeConfig;
import org.mtr.visionupgrade.render.RailGeometryCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderRails.class, remap = false)
public abstract class RenderRailsMixin {

	@Redirect(
		method = "renderWithinRenderDistance(Lorg/mtr/core/data/Rail;Lorg/mtr/mod/render/RenderRails$RenderRailWithBlockPos;DFF)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/core/data/RailMath;render(Lorg/mtr/core/data/RailMath$RenderRail;DFF)V"),
		remap = false,
		require = 0
	)
	private static void mtr$cachedRailRender(RailMath railMath, RailMath.RenderRail callback, double interval, float offsetRadius1, float offsetRadius2) {
		RailGeometryCache.INSTANCE.render(railMath, callback, interval, offsetRadius1, offsetRadius2);
	}

	@Redirect(
		method = "renderWithinRenderDistance(Lorg/mtr/core/data/Rail;Lorg/mtr/mod/render/RenderRails$RenderRailWithBlockPos;DFF)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/mapping/mapper/MinecraftClientHelper;getRenderDistance()I"),
		remap = false,
		require = 0
	)
	private static int mtr$redirectRailRenderDistance() {
		int vanillaChunks = MinecraftClientHelper.getRenderDistance();
		return VisionUpgradeConfig.get().getEffectiveRailRenderDistanceChunks(vanillaChunks);
	}

	@Redirect(
		method = "lambda$render$1(Lorg/mtr/mod/client/MinecraftClientData$RailWrapper;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/OcclusionCullingInstance;)Ljava/lang/Runnable;",
		at = @At(
			value = "INVOKE",
			target = "Lorg/mtr/libraries/com/logisticscraft/occlusionculling/OcclusionCullingInstance;isAABBVisible(Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;Lorg/mtr/libraries/com/logisticscraft/occlusionculling/util/Vec3d;)Z"
		),
		remap = false,
		require = 0
	)
	private static boolean mtr$cullDistantRailAABB(
		OcclusionCullingInstance instance,
		Vec3d startVector,
		Vec3d endVector,
		Vec3d cameraVector
	) {
		VisionUpgradeConfig config = VisionUpgradeConfig.get();
		int chunks = config.getEffectiveRailRenderDistanceChunks(MinecraftClientHelper.getRenderDistance());
		double maxDist = chunks * 16.0 + 32.0;
		double maxDistSq = maxDist * maxDist;

		double dx1 = startVector.x - cameraVector.x;
		double dy1 = startVector.y - cameraVector.y;
		double dz1 = startVector.z - cameraVector.z;
		double d1 = dx1 * dx1 + dy1 * dy1 + dz1 * dz1;

		double dx2 = endVector.x - cameraVector.x;
		double dy2 = endVector.y - cameraVector.y;
		double dz2 = endVector.z - cameraVector.z;
		double d2 = dx2 * dx2 + dy2 * dy2 + dz2 * dz2;

		if (d1 > maxDistSq && d2 > maxDistSq) {
			return false;
		}
		return instance.isAABBVisible(startVector, endVector, cameraVector);
	}
}
