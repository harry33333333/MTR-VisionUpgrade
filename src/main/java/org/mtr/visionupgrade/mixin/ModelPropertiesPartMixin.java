package org.mtr.visionupgrade.mixin;

import org.mtr.core.serializer.ReaderBase;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mod.data.VehicleExtension;
import org.mtr.mod.generated.resource.ModelPropertiesPartSchema;
import org.mtr.mod.render.StoredMatrixTransformations;
import org.mtr.mod.resource.ModelPropertiesPart;
import org.mtr.mod.resource.RenderStage;
import org.mtr.visionupgrade.culling.InteriorCullingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelPropertiesPart.class, remap = false)
public abstract class ModelPropertiesPartMixin extends ModelPropertiesPartSchema {

	protected ModelPropertiesPartMixin(ReaderBase readerBase) {
		super(readerBase);
	}

	@Inject(
		method = "render(Lorg/mtr/mapping/holder/Identifier;Lorg/mtr/mod/render/StoredMatrixTransformations;Lorg/mtr/mod/data/VehicleExtension;I[IILorg/mtr/libraries/it/unimi/dsi/fastutil/objects/ObjectArrayList;Z)V",
		at = @At("HEAD"),
		cancellable = true,
		remap = false,
		require = 0
	)
	private void mtr$cullInteriorPart(
		Identifier texture,
		StoredMatrixTransformations storedMatrixTransformations,
		VehicleExtension vehicleExtension,
		int carNumber,
		int[] carTypes,
		int light,
		org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList<?> doorwayPositions,
		boolean doorsClosed,
		CallbackInfo ci
	) {
		if (InteriorCullingState.isCullingInterior()) {
			if (this.renderStage == RenderStage.INTERIOR || this.renderStage == RenderStage.INTERIOR_TRANSLUCENT) {
				ci.cancel();
			}
		}
	}
}
