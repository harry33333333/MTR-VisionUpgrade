package org.mtr.visionupgrade.render;

import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.holder.World;
import org.mtr.mod.block.BlockPSDAPGDoorBase;
import org.mtr.mod.block.IBlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DoorStateCache {
	private static final Map<Long, CachedDoorState> CACHE = new ConcurrentHashMap<>();

	public static final class CachedDoorState {
		public final Direction facing;
		public final boolean isRight;
		public final boolean isUpper;
		public final boolean isEnd;
		public final boolean isUnlocked;

		public CachedDoorState(Direction facing, boolean isRight, boolean isUpper, boolean isEnd, boolean isUnlocked) {
			this.facing = facing;
			this.isRight = isRight;
			this.isUpper = isUpper;
			this.isEnd = isEnd;
			this.isUnlocked = isUnlocked;
		}
	}

	public static CachedDoorState getOrCreate(World world, BlockPos pos) {
		final long key = pos.asLong();
		CachedDoorState state = CACHE.get(key);
		if (state == null) {
			final Direction facing = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.FACING);
			final boolean isRight = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.SIDE) == IBlock.EnumSide.RIGHT;
			final boolean isUpper = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.HALF) == IBlock.DoubleBlockHalf.UPPER;
			final boolean isEnd = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.END);
			final boolean isUnlocked = IBlock.getStatePropertySafe(world, pos, BlockPSDAPGDoorBase.UNLOCKED);
			state = new CachedDoorState(facing, isRight, isUpper, isEnd, isUnlocked);
			if (CACHE.size() < 4096) {
				CACHE.put(key, state);
			}
		}
		return state;
	}

	public static void clear() {
		CACHE.clear();
	}
}
