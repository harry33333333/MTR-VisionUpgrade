package org.mtr.visionupgrade.mixin;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mod.render.MainRenderer;
import org.mtr.mod.render.QueuedRenderLayer;
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

	@Redirect(method = "render(Lorg/mtr/mapping/mapper/GraphicsHolder;Lorg/mtr/mapping/holder/Vector3d;)V", at = @At(value = "INVOKE", target = "Lorg/mtr/mod/render/QueuedRenderLayer;values()[Lorg/mtr/mod/render/QueuedRenderLayer;"), remap = false, require = 0)
	private static QueuedRenderLayer[] mtr$reuseRenderLayers() {
		return MTR$RENDER_LAYERS;
	}
}
