package org.mtr.visionupgrade.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.NativeImageBackedTexture;
import org.mtr.mod.client.DynamicTextureCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DynamicTextureCache.DynamicResource.class, remap = false)
public abstract class DynamicResourceMixin {

	@Inject(method = "<init>(Lorg/mtr/mapping/holder/Identifier;Lorg/mtr/mapping/holder/NativeImageBackedTexture;)V", at = @At("RETURN"), remap = false, require = 0)
	private void mtr$enableMipmaps(Identifier identifier, NativeImageBackedTexture texture, CallbackInfo ci) {
		if (texture != null) {
			try {
				final int glId = texture.getGlId();
				if (glId > 0) {
					GlStateManager._bindTexture(glId);
					GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				}
			} catch (Throwable ignored) {
			}
		}
	}
}
