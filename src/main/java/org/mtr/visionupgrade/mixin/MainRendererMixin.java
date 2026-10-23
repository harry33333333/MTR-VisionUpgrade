package org.mtr.visionupgrade.mixin;

import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
import org.mtr.visionupgrade.render.RenderCallbackPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(value = MainRenderer.class, remap = false)
public abstract class MainRendererMixin {

	@Unique private static final Identifier MTR$UNTEXTURED = new Identifier("");
	@Unique private static final QueuedRenderLayer[] MTR$RENDER_LAYERS = QueuedRenderLayer.values();

	/**
	 * @author MTR-VisionUpgrade
	 * @reason Reuse static empty untextured Identifier to avoid allocating a new Identifier on every scheduleRender call.
	 */
	@Overwrite
	public static void scheduleRender(QueuedRenderLayer queuedRenderLayer, BiConsumer<GraphicsHolder, Vector3d> callback) {
		MainRenderer.scheduleRender(MTR$UNTEXTURED, false, queuedRenderLayer, callback);
	}

	@Redirect(
		method = "scheduleRender(Lorg/mtr/mapping/holder/Identifier;ZLorg/mtr/mod/render/QueuedRenderLayer;Ljava/util/function/BiConsumer;)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectArrayMap;computeIfAbsent(Ljava/lang/Object;Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectFunction;)Ljava/lang/Object;"),
		remap = false,
		require = 0
	)
	private static Object mtr$acquirePooledList(Object2ObjectArrayMap<Identifier, ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>>> map, Object key, Object2ObjectFunction<?, ?> mappingFunction) {
		ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>> list = map.get(key);
		if (list == null) {
			list = RenderCallbackPool.INSTANCE.acquire();
			map.put((Identifier) key, list);
		}
		return list;
	}

	@Redirect(
		method = "render(Lorg/mtr/mapping/mapper/GraphicsHolder;Lorg/mtr/mapping/holder/Vector3d;)V",
		at = @At(value = "INVOKE", target = "Lorg/mtr/libraries/it/unimi/dsi/fastutil/objects/Object2ObjectArrayMap;clear()V", ordinal = 0),
		remap = false,
		require = 0
	)
	private static void mtr$recycleCurrentRenders(Object2ObjectArrayMap<Identifier, ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>>> map) {
		for (ObjectArrayList<BiConsumer<GraphicsHolder, Vector3d>> list : map.values()) {
			RenderCallbackPool.INSTANCE.recycle(list);
		}
		map.clear();
	}

	@Redirect(method = "render(Lorg/mtr/mapping/mapper/GraphicsHolder;Lorg/mtr/mapping/holder/Vector3d;)V", at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/QueuedRenderLayer;values()[Lorg/mtr/mod/render/QueuedRenderLayer;"), remap = false, require = 0)
	private static QueuedRenderLayer[] mtr$reuseRenderLayers() {
		return MTR$RENDER_LAYERS;
	}
}
