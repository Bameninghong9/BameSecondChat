package com.bame.secondchat.data;

import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

public class ChatMessage {
    private final Text message;
    @Nullable
    private final MessageSignatureData signature;
    @Nullable
    private final MessageIndicator indicator;
    private final int creationTick;
    private final long timestamp;

    private int stackCount = 1;
    
    private Identifier playerSkin = null;
    private boolean skinChecked = false;

    public ChatMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, int creationTick) {
        this.message = message;
        this.signature = signature;
        this.indicator = indicator;
        this.creationTick = creationTick;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Text getMessage() {
        return message;
    }
    
    public Text getRenderedMessage() {
        if (stackCount > 1) {
            return net.minecraft.text.Text.empty().append(message).append(net.minecraft.text.Text.literal(" (" + stackCount + ")").formatted(net.minecraft.util.Formatting.GRAY));
        }
        return message;
    }
    
    public int getStackCount() {
        return stackCount;
    }
    
    public void incrementStackCount() {
        this.stackCount++;
    }

    @Nullable
    public MessageSignatureData getSignature() {
        return signature;
    }

    @Nullable
    public MessageIndicator getIndicator() {
        return indicator;
    }

    public int getCreationTick() {
        return creationTick;
    }
    
    public Identifier getPlayerSkin() {
        if (!skinChecked) {
            skinChecked = true;
            try {
                if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                    String plainText = message.getString();
                    // Strip formatting just in case
                    plainText = plainText.replaceAll("§[0-9a-fk-or]", "");
                    
                    String searchArea = plainText.length() > 25 ? plainText.substring(0, 25) : plainText;
                    
                    for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler().getPlayerList()) {
                        String name = entry.getProfile().name();
                        if (name != null && name.length() >= 3 && searchArea.contains(name)) {
                            this.playerSkin = entry.getSkinTextures().body().texturePath();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore errors related to skin fetching
            }
        }
        return playerSkin;
    }
}
