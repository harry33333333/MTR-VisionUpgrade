package org.mtr.visionupgrade.render;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.mod.data.VehicleExtension;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.function.Consumer;

public final class VehicleParallelManager {
	private static final int PARALLELISM = Math.max(2, Math.min(8, Runtime.getRuntime().availableProcessors() / 2));
	private static final ForkJoinPool POOL = new ForkJoinPool(
		PARALLELISM,
		pool -> {
			final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
			worker.setName("MTR-Vehicle-Worker-" + worker.getPoolIndex());
			worker.setDaemon(true);
			worker.setPriority(Thread.NORM_PRIORITY - 1);
			return worker;
		},
		(thread, throwable) -> {
		},
		false
	);

	public static void prepareVehicles(ObjectArraySet<VehicleExtension> vehicles, Consumer<VehicleExtension> consumer) {
		if (vehicles == null || vehicles.isEmpty()) {
			return;
		}
		if (!org.mtr.visionupgrade.config.VisionUpgradeConfig.get().enableParallelVehicles || vehicles.size() <= 1) {
			vehicles.forEach(consumer);
			return;
		}

		try {
			POOL.submit(() -> vehicles.parallelStream().forEach(consumer)).get();
		} catch (Throwable t) {
			vehicles.forEach(consumer);
		}
	}
}
