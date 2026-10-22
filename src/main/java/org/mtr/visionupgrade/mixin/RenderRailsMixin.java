package org.mtr.visionupgrade.mixin;

import org.mtr.core.data.RailMath;
import org.mtr.mod.render.RenderRails;
import org.mtr.visionupgrade.render.RailGeometryCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderRails.class, remap = false)
public abstract class RenderRailsMixin {

	@Redirect(method = "renderWithinRenderDistance(Lorg/mtr/core/data/Rail;Lorg/mtr/mod/render/RenderRails$RenderRailWithBlockPos;DFF)V", at = @At(value = "INVOKE", target = "Lorg/mtr/core/data/RailMath;render(Lorg/mtr/core/data/RailMath$RenderRail;DFF)V"), remap = false, require = 0)
	private static void mtr$cachedRailRender(RailMath railMath, RailMath.RenderRail callback, double interval, float offsetRadius1, float offsetRadius2) {
		RailGeometryCache.INSTANCE.render(railMath, callback, interval, offsetRadius1, offsetRadius2);
	}
}
