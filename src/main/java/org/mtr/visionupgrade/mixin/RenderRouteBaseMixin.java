package org.mtr.visionupgrade.mixin;

import org.mtr.mapping.holder.Block;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.BlockPSDTop;
import org.mtr.mod.render.RenderRouteBase;
import org.mtr.visionupgrade.culling.BlockEntityRenderCulling;
import org.mtr.visionupgrade.culling.RouteMapSpanCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RenderRouteBase.class, remap = false)
public abstract class RenderRouteBaseMixin<T extends BlockPSDTop.BlockEntityBase> {

	@Shadow protected abstract boolean isLeft(BlockState state);
	@Shadow protected abstract boolean isRight(BlockState state);

	@Unique private final Object mtr$leftSpanKey = new Object();
	@Unique private final Object mtr$rightSpanKey = new Object();

	@Inject(method = "render(Lorg/mtr/mod/block/BlockPSDTop$BlockEntityBase;FLorg/mtr/mapping/mapper/GraphicsHolder;II)V", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
	private void mtr$frustumCullRouteMap(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay, CallbackInfo ci) {
		final World world = entity.getWorld2();
		if (world == null) {
			return;
		}
		final BlockPos blockPos = entity.getPos2();
		if (!BlockEntityRenderCulling.isVisible(world, blockPos, 1)) {
			ci.cancel();
		}
	}

	@Inject(method = "getTextureNumber(Lorg/mtr/mapping/holder/World;Lorg/mtr/mapping/holder/BlockPos;Lorg/mtr/mapping/holder/Direction;Z)I", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
	private void mtr$cachedTextureNumber(World world, BlockPos pos, Direction facing, boolean searchLeft, CallbackInfoReturnable<Integer> cir) {
		final RouteMapSpanCache spans = BlockEntityRenderCulling.routeSpans(world, searchLeft ? mtr$leftSpanKey : mtr$rightSpanKey);
		if (spans != null) {
			final Block thisBlock = world.getBlockState(pos).getBlock();
			final int dist = spans.distance(pos.data, (searchLeft ? facing.rotateYCounterclockwise() : facing.rotateYClockwise()).data, cursor -> {
				final BlockState state = world.getBlockState(new BlockPos(cursor));
				if (!state.getBlock().equals(thisBlock)) {
					return 0;
				}
				return 1 | ((searchLeft ? isLeft(state) : isRight(state)) ? 2 : 0) | ((searchLeft ? isRight(state) : isLeft(state)) ? 4 : 0);
			});
			cir.setReturnValue(dist);
		}
	}
}
