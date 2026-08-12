package com.bame.secondchat.mixin;

import com.bame.secondchat.config.ModConfig;
import com.bame.secondchat.util.FontTransformer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {

    @Shadow public abstract String getText();

    @ModifyVariable(
        method = "write", 
        at = @At("HEAD"), 
        ordinal = 0,
        argsOnly = true
    )
    private String modifyWriteText(String text) {
        if (ModConfig.showFontDropdown && MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
            String currentText = this.getText();
            // If the current text starts with '/', OR if we are typing a '/' as the first character
            if (currentText.startsWith("/") || (currentText.isEmpty() && text.startsWith("/"))) {
                return text;
            }
            return FontTransformer.transform(text);
        }
        return text;
    }
}
