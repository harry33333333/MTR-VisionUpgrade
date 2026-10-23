package org.mtr.visionupgrade;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mtr.mapping.holder.Direction;
import org.mtr.visionupgrade.render.DoorStateCache;

public class DoorStateCacheTest {

	@Test
	public void testDoorStateStructureAndClear() {
		DoorStateCache.CachedDoorState state = new DoorStateCache.CachedDoorState(
			null, true, false, true, false
		);

		Assertions.assertNull(state.facing);
		Assertions.assertTrue(state.isRight);
		Assertions.assertFalse(state.isUpper);
		Assertions.assertTrue(state.isEnd);
		Assertions.assertFalse(state.isUnlocked);

		// Test clear does not throw
		DoorStateCache.clear();
	}
}
