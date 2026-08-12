package com.bame.secondchat.mixin;

import com.bame.secondchat.util.FontTransformer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @ModifyVariable(
        method = "sendChatMessage(Ljava/lang/String;)V",
        at = @At("HEAD"),
        ordinal = 0,
        argsOnly = true
    )
    private String modifySendChatMessage(String content) {
        if (com.bame.secondchat.config.ModConfig.showFontDropdown && !content.startsWith("/")) {
            return FontTransformer.transform(content);
        }
        return content;
    }
}
