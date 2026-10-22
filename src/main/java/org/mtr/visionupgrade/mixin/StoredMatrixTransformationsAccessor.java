package org.mtr.visionupgrade.mixin;

import org.mtr.mod.render.StoredMatrixTransformations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = StoredMatrixTransformations.class, remap = false)
public interface StoredMatrixTransformationsAccessor {
	@Accessor("initialTranslateX")
	double mtr$getInitialTranslateX();

	@Accessor("initialTranslateY")
	double mtr$getInitialTranslateY();

	@Accessor("initialTranslateZ")
	double mtr$getInitialTranslateZ();
}
