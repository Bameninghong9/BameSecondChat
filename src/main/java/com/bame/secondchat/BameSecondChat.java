package com.bame.secondchat;

import com.bame.secondchat.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

import com.bame.secondchat.gui.TabHudRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class BameSecondChat implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.load();
        com.bame.secondchat.config.GlobalConfig.load();
        
        HudRenderCallback.EVENT.register(new TabHudRenderer());
        
        net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("bamesecondchat")
                .executes(context -> {
                    net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
                    client.send(() -> client.setScreen(new com.bame.secondchat.gui.GlobalSettingsScreen(client.currentScreen)));
                    return 1;
                }));
        });
    }
}
