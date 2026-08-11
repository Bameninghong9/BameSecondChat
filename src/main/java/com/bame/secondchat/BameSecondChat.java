package com.bame.secondchat;

import com.bame.secondchat.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

import com.bame.secondchat.gui.TabHudRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class BameSecondChat implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.load();
        
        HudRenderCallback.EVENT.register(new TabHudRenderer());
    }
}
