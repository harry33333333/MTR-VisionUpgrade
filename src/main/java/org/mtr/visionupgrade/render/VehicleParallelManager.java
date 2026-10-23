package org.mtr.visionupgrade.render;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.mtr.mod.data.VehicleExtension;

import java.util.function.Consumer;

public final class VehicleParallelManager {

	public static void prepareVehicles(ObjectArraySet<VehicleExtension> vehicles, Consumer<VehicleExtension> consumer) {
		if (vehicles == null || vehicles.isEmpty() || consumer == null) {
			return;
		}
		// In OpenGL/LWJGL and MTR, vehicle rendering, shader compilation and sound triggers
		// must execute strictly on the Render thread where OpenGL context is active.
		vehicles.forEach(consumer);
	}
}

