package org.mtr.visionupgrade.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.mtr.visionupgrade.render.iris.RenderOrderCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;

@Pseudo
@Mixin(targets = "net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource", remap = false)
public abstract class IrisBufferSourceMixin {
	@Unique
	private final RenderOrderCache mtr$renderOrderCache = new RenderOrderCache();

	@WrapOperation(method = "readyUp", at = @At(value = "INVOKE", target = "Lnet/irisshaders/batchedentityrendering/impl/ordering/RenderOrderManager;getRenderOrder()Ljava/util/List;"), require = 0)
	private List<?> mtr$reuseRenderOrder(@Coerce Object manager, Operation<List<?>> original) {
		if (!(manager instanceof IrisRenderOrderManagerAccessor)) {
			return original.call(manager);
		}
		final IrisRenderOrderManagerAccessor accessor = (IrisRenderOrderManagerAccessor) manager;
		return mtr$renderOrderCache.getOrCompute(accessor.mtr$getTypes().values(), () -> original.call(manager));
	}
}
