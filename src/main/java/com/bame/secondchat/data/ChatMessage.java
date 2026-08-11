package com.bame.secondchat.data;

import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import org.jetbrains.annotations.Nullable;

public class ChatMessage {
    private final Text message;
    @Nullable
    private final MessageSignatureData signature;
    @Nullable
    private final MessageIndicator indicator;
    private final int creationTick;

    public ChatMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, int creationTick) {
        this.message = message;
        this.signature = signature;
        this.indicator = indicator;
        this.creationTick = creationTick;
    }

    public Text getMessage() {
        return message;
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
}
