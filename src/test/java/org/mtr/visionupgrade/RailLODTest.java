package org.mtr.visionupgrade;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RailLODTest {

	@Test
	public void testAdaptiveRailIntervalScaling() {
		// Continuous rail (e.g. interval = 0.1)
		double baseInterval = 0.1;

		// Near distance (< 48 blocks): dist = 20 blocks
		double distNear = 20.0;
		double distSqNear = distNear * distNear;
		double intervalNear = calculateAdaptiveInterval(baseInterval, distSqNear);
		Assertions.assertEquals(baseInterval, intervalNear, 1e-6, "Near rail must retain full precision");

		// Medium far distance (48 ~ 96 blocks): dist = 60 blocks
		double distMid = 60.0;
		double distSqMid = distMid * distMid;
		double intervalMid = calculateAdaptiveInterval(baseInterval, distSqMid);
		Assertions.assertEquals(0.25, intervalMid, 1e-6, "Mid distance continuous rail should merge segments 2.5x");

		// Far distance (> 96 blocks): dist = 120 blocks
		double distFar = 120.0;
		double distSqFar = distFar * distFar;
		double intervalFar = calculateAdaptiveInterval(baseInterval, distSqFar);
		Assertions.assertEquals(0.4, intervalFar, 1e-6, "Far distance continuous rail should merge segments 4x");

		// Sleeper interval (interval = 1.0) must never be altered!
		double sleeperInterval = 1.0;
		Assertions.assertEquals(sleeperInterval, calculateAdaptiveInterval(sleeperInterval, distSqFar), 1e-6, "Sleepers must never be altered");
	}

	private static double calculateAdaptiveInterval(double interval, double distSq) {
		if (interval >= 0.8) {
			return interval;
		}
		if (distSq > 96.0 * 96.0) {
			return Math.min(1.0, interval * 4.0);
		} else if (distSq > 48.0 * 48.0) {
			return Math.min(0.8, interval * 2.5);
		}
		return interval;
	}
}
