package com.bame.secondchat.gui;

import com.bame.secondchat.data.ChatMessage;
import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.TabManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.List;

public class CustomChatHud {
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        ChatTab activeTab = TabManager.getInstance().getActiveTab();
        if (activeTab == null) return;

        List<ChatMessage> messages = activeTab.getMessages();
        if (messages.isEmpty()) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        boolean chatOpen = client.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;
        
        int x = activeTab.getX();
        int y = activeTab.getY();
        int width = activeTab.getWidth();
        int height = activeTab.getHeight();
        
        int startY = y + 18;
        
        int scrollLines = (int) activeTab.getScrollOffset();
        int newestVisibleIndex = messages.size() - 1 - scrollLines;
        if (newestVisibleIndex >= messages.size()) {
            newestVisibleIndex = messages.size() - 1;
        }
        if (newestVisibleIndex < 0) newestVisibleIndex = 0;
        
        int lineHeight = 12;
        int maxLines = height / lineHeight;
        
        int bg = 0x66000000;
        drawContext.fill(x, startY, x + width, startY + height, bg);
        
        drawContext.enableScissor(x, startY, x + width, startY + height);
        
        int linesDrawn = 0;
        // Iterate backwards from newestVisibleIndex to oldest
        for (int i = newestVisibleIndex; i >= 0; i--) {
            if (linesDrawn >= maxLines) break;
            
            ChatMessage msg = messages.get(i);
            
            // Render newest at the bottom of the box
            int renderY = startY + height - ((linesDrawn + 1) * lineHeight);
            
            int color = 0xFFFFFFFF;
            drawContext.drawText(client.textRenderer, msg.getMessage(), x + 2, renderY + 2, color, true);
            
            linesDrawn++;
        }
        
        // Render Scrollbar
        if (chatOpen && messages.size() > maxLines) {
            int scrollbarWidth = 4;
            drawContext.fill(x + width - scrollbarWidth, startY, x + width, startY + height, 0xAA000000);
            
            int maxScroll = messages.size() - maxLines;
            if (maxScroll < 0) maxScroll = 0;
            
            double scrollFraction = maxScroll > 0 ? 1.0 - ((double)scrollLines / maxScroll) : 1.0;
            
            int thumbHeight = Math.max(10, (int)((double)maxLines / messages.size() * height));
            int thumbY = startY + (int)(scrollFraction * (height - thumbHeight));
            
            drawContext.fill(x + width - scrollbarWidth, thumbY, x + width, thumbY + thumbHeight, 0xFFAAAAAA);
        }
        
        drawContext.disableScissor();
    }
}
