package org.mtr.visionupgrade.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Pseudo
@Mixin(targets = "org.mtr.mapping.render.batch.BatchManager", remap = false)
public abstract class OptimizedBatchManagerMixin {

	@Shadow @Final private Map<?, ?> translucentBatches;

	// drawAll(false) skips both drawing and clearing the translucent queue.
	@Inject(method = "drawAll", at = @At("RETURN"), require = 0)
	private void mtr$discardUndrawnTranslucent(CallbackInfo ci) {
		translucentBatches.clear();
	}

	@Inject(
		method = "queue(Lorg/mtr/mapping/render/object/VertexArray;Lorg/mtr/mapping/render/vertex/VertexAttributeState;)V",
		at = @At("HEAD"),
		cancellable = true,
		remap = false,
		require = 0
	)
	private void mtr$cullInteriorBatch(org.mtr.mapping.render.object.VertexArray vertexArray, org.mtr.mapping.render.vertex.VertexAttributeState vertexAttributeState, CallbackInfo ci) {
		if (org.mtr.visionupgrade.culling.InteriorCullingState.isCullingInterior()) {
			org.mtr.mapping.render.batch.MaterialProperties mat = vertexArray.materialProperties;
			if (mat != null) {
				org.mtr.mapping.mapper.OptimizedModel.ShaderType type = mat.shaderType;
				if (type == org.mtr.mapping.mapper.OptimizedModel.ShaderType.CUTOUT_BRIGHT || type == org.mtr.mapping.mapper.OptimizedModel.ShaderType.TRANSLUCENT_BRIGHT) {
					ci.cancel();
				}
			}
		}
	}
}
