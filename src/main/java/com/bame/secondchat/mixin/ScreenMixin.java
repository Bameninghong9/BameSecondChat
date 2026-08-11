package com.bame.secondchat.mixin;

import com.bame.secondchat.config.ModConfig;
import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.TabManager;
import com.bame.secondchat.gui.DragState;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Element.class)
public interface ScreenMixin {

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    default void onMouseDragged(Click click, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ChatScreen)) return;
        
        if (DragState.isDraggingTab) {
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "mouseReleased", at = @At("HEAD"))
    default void onMouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ChatScreen)) return;

        if (DragState.isDraggingTab) {
            DragState.isDraggingTab = false;
            ModConfig.save(); // Save new layout
        }
    }
}
