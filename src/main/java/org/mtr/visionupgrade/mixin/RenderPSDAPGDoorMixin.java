package org.mtr.visionupgrade.mixin;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BooleanProperty;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.DirectionProperty;
import org.mtr.mapping.holder.EnumProperty;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.render.RenderPSDAPGDoor;
import org.mtr.visionupgrade.culling.BlockEntityRenderCulling;
import org.mtr.visionupgrade.render.DoorStateCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

	@Redirect(
		method = "render(Lorg/mtr/mod/block/BlockPSDAPGDoorBase$BlockEntityBase;FLorg/mtr/mapping/mapper/GraphicsHolder;II)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/mod/block/IBlock;getStatePropertySafe(Lorg/mtr/mapping/holder/World;Lorg/mtr/mapping/holder/BlockPos;Lorg/mtr/mapping/holder/DirectionProperty;)Lorg/mtr/mapping/holder/Direction;"),
		remap = false,
		require = 0
	)
	private Direction mtr$cacheFacing(World world, BlockPos pos, DirectionProperty property) {
		return DoorStateCache.getOrCreate(world, pos).facing;
	}

	@Redirect(
		method = "render(Lorg/mtr/mod/block/BlockPSDAPGDoorBase$BlockEntityBase;FLorg/mtr/mapping/mapper/GraphicsHolder;II)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/mod/block/IBlock;getStatePropertySafe(Lorg/mtr/mapping/holder/World;Lorg/mtr/mapping/holder/BlockPos;Lorg/mtr/mapping/holder/EnumProperty;)Ljava/lang/Enum;", ordinal = 0),
		remap = false,
		require = 0
	)
	private Enum<?> mtr$cacheSide(World world, BlockPos pos, EnumProperty<?> property) {
		return DoorStateCache.getOrCreate(world, pos).isRight ? IBlock.EnumSide.RIGHT : IBlock.EnumSide.LEFT;
	}

	@Redirect(
		method = "render(Lorg/mtr/mod/block/BlockPSDAPGDoorBase$BlockEntityBase;FLorg/mtr/mapping/mapper/GraphicsHolder;II)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/mod/block/IBlock;getStatePropertySafe(Lorg/mtr/mapping/holder/World;Lorg/mtr/mapping/holder/BlockPos;Lorg/mtr/mapping/holder/EnumProperty;)Ljava/lang/Enum;", ordinal = 1),
		remap = false,
		require = 0
	)
	private Enum<?> mtr$cacheHalf(World world, BlockPos pos, EnumProperty<?> property) {
		return DoorStateCache.getOrCreate(world, pos).isUpper ? IBlock.DoubleBlockHalf.UPPER : IBlock.DoubleBlockHalf.LOWER;
	}

	@Redirect(
		method = "render(Lorg/mtr/mod/block/BlockPSDAPGDoorBase$BlockEntityBase;FLorg/mtr/mapping/mapper/GraphicsHolder;II)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/mod/block/IBlock;getStatePropertySafe(Lorg/mtr/mapping/holder/World;Lorg/mtr/mapping/holder/BlockPos;Lorg/mtr/mapping/holder/BooleanProperty;)Z", ordinal = 0),
		remap = false,
		require = 0
	)
	private boolean mtr$cacheEnd(World world, BlockPos pos, BooleanProperty property) {
		return DoorStateCache.getOrCreate(world, pos).isEnd;
	}

	@Redirect(
		method = "render(Lorg/mtr/mod/block/BlockPSDAPGDoorBase$BlockEntityBase;FLorg/mtr/mapping/mapper/GraphicsHolder;II)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/mod/block/IBlock;getStatePropertySafe(Lorg/mtr/mapping/holder/World;Lorg/mtr/mapping/holder/BlockPos;Lorg/mtr/mapping/holder/BooleanProperty;)Z", ordinal = 1),
		remap = false,
		require = 0
	)
	private boolean mtr$cacheUnlocked(World world, BlockPos pos, BooleanProperty property) {
		return DoorStateCache.getOrCreate(world, pos).isUnlocked;
	}
}
