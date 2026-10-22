package org.mtr.visionupgrade.mixin;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.render.RenderPSDAPGDoor;
import org.mtr.visionupgrade.culling.BlockEntityRenderCulling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderPSDAPGDoor.class, remap = false)
public abstract class RenderPSDAPGDoorMixin<T extends BlockPSDAPGDoorBase.BlockEntityBase> {

	@Inject(method = "render(Lorg/mtr/mod/block/BlockPSDAPGDoorBase$BlockEntityBase;FLorg/mtr/mapping/mapper/GraphicsHolder;II)V", at = @At(value = "INVOKE", target = "Lorg/mtr/mod/block/BlockPSDAPGDoorBase$BlockEntityBase;tick(F)V", shift = At.Shift.AFTER), cancellable = true, remap = false, require = 0)
	private void mtr$frustumCullDoor(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay, CallbackInfo ci) {
		final World world = entity.getWorld2();
		if (world == null) {
			return;
		}
		if (!org.mtr.visionupgrade.config.VisionUpgradeConfig.get().enableFrustumCulling) {
			return;
		}
		final BlockPos blockPos = entity.getPos2();
		if (!BlockEntityRenderCulling.isVisible(world, blockPos, 2)) {
			ci.cancel();
		}
	}
}
