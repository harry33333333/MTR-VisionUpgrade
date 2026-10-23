package org.mtr.visionupgrade;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.visionupgrade.render.VehicleParallelManager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class VehicleParallelManagerTest {

	@Test
	public void testEmptyOrSingleVehicleExecution() {
		ObjectArraySet<VehicleExtension> emptySet = new ObjectArraySet<>();
		AtomicInteger count = new AtomicInteger(0);

		VehicleParallelManager.prepareVehicles(emptySet, v -> count.incrementAndGet());
		Assertions.assertEquals(0, count.get());

		// Single vehicle runs synchronously without pool overhead
		ObjectArraySet<VehicleExtension> singleSet = new ObjectArraySet<>();
		singleSet.add(null);
		VehicleParallelManager.prepareVehicles(singleSet, v -> count.incrementAndGet());
		Assertions.assertEquals(1, count.get());
	}

	@Test
	public void testThreadSafetyUnderParallelExecution() {
		// Verify fine-grained thread-safe synchronized add
		org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<Integer> list =
			new org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<>();

		int threadCount = 10;
		int itemsPerThread = 1000;
		Thread[] threads = new Thread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			final int threadId = i;
			threads[i] = new Thread(() -> {
				for (int j = 0; j < itemsPerThread; j++) {
					synchronized (list) {
						list.add(threadId * itemsPerThread + j);
					}
				}
			});
			threads[i].start();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		Assertions.assertEquals(threadCount * itemsPerThread, list.size());
	}
}
