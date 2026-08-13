package com.bame.secondchat.mixin;

import com.bame.secondchat.data.ChatMessage;
import com.bame.secondchat.data.TabManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        if (message == null) return;
        
        int ticks = 0;
        if (MinecraftClient.getInstance().inGameHud != null) {
            ticks = MinecraftClient.getInstance().inGameHud.getTicks();
        }
        
        ChatMessage chatMessage = new ChatMessage(message, signature, indicator, ticks);
        String plainText = message.getString();
        
        // Verarbeite die Nachricht und prüfe, ob sie im Vanilla-Chat (All-Tab) versteckt werden soll
        boolean hideFromVanilla = TabManager.getInstance().processMessage(chatMessage, plainText);
        
        // Wenn die Nachricht versteckt werden soll, brechen wir das Hinzufügen zum Vanilla-Chat ab.
        if (hideFromVanilla) {
            ci.cancel();
        }
    }
}
