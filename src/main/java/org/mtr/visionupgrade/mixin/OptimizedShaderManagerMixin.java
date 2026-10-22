package org.mtr.visionupgrade.mixin;

import org.mtr.visionupgrade.render.ShaderGenerationTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Pseudo
@Mixin(targets = "org.mtr.mapping.render.shader.ShaderManager", remap = false)
public abstract class OptimizedShaderManagerMixin {

	@Shadow @Final private Map<String, ?> shaders;
	@Unique private long mtr$loadedGeneration = Long.MIN_VALUE;

	@Inject(method = "reloadShaders", at = @At("HEAD"), cancellable = true)
	private void mtr$reuseShaders(CallbackInfo ci) {
		if (mtr$loadedGeneration == ShaderGenerationTracker.GENERATION && shaders.size() == 3) {
			ci.cancel();
		}
	}

	@Inject(method = "reloadShaders", at = @At("RETURN"))
	private void mtr$recordGeneration(CallbackInfo ci) {
		mtr$loadedGeneration = ShaderGenerationTracker.GENERATION;
	}
}
