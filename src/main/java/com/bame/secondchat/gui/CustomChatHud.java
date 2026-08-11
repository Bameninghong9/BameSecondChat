package com.bame.secondchat.gui;

import com.bame.secondchat.data.ChatMessage;
import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.TabManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.List;

public class CustomChatHud {
    
    private static boolean wasSPressed = false;
    private static boolean wasCPressed = false;

    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        ChatTab activeTab = TabManager.getInstance().getActiveTab();
        if (activeTab == null) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        boolean chatOpen = client.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;
        
        double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        
        if (chatOpen) {
            boolean isSPressed = org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), org.lwjgl.glfw.GLFW.GLFW_KEY_S) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            boolean isCPressed = org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), org.lwjgl.glfw.GLFW.GLFW_KEY_C) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            boolean isCtrlPressed = org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL) == org.lwjgl.glfw.GLFW.GLFW_PRESS || 
                                    org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            
            if (isSPressed && isCtrlPressed && !wasSPressed) {
                java.util.Set<ChatMessage> selected = activeTab.getSelectedMessages();
                if (!selected.isEmpty()) {
                    java.util.List<ChatMessage> orderedSelection = new java.util.ArrayList<>();
                    for (ChatMessage msg : activeTab.getMessages()) {
                        if (selected.contains(msg)) {
                            orderedSelection.add(msg);
                        }
                    }
                    com.bame.secondchat.util.ClipboardImageUtil.copyMessagesToClipboard(orderedSelection);
                    activeTab.clearSelection();
                }
            }
            
            if (isCPressed && isCtrlPressed && !wasCPressed) {
                java.util.Set<ChatMessage> selected = activeTab.getSelectedMessages();
                if (!selected.isEmpty()) {
                    java.util.List<ChatMessage> orderedSelection = new java.util.ArrayList<>();
                    for (ChatMessage msg : activeTab.getMessages()) {
                        if (selected.contains(msg)) {
                            orderedSelection.add(msg);
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    for (ChatMessage msg : orderedSelection) {
                        sb.append(msg.getMessage().getString()).append("\n");
                    }
                    client.keyboard.setClipboard(sb.toString().trim());
                    activeTab.clearSelection();
                }
            }
            
            wasSPressed = isSPressed;
            wasCPressed = isCPressed;
        }

        List<ChatMessage> messages = activeTab.getMessages();
        if (messages.isEmpty()) return;
        
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
        
        ChatMessage hoveredMessage = null;
        int linesDrawn = 0;
        // Iterate backwards from newestVisibleIndex to oldest
        for (int i = newestVisibleIndex; i >= 0; i--) {
            if (linesDrawn >= maxLines) break;
            
            ChatMessage msg = messages.get(i);
            
            // Render newest at the bottom of the box
            int renderY = startY + height - ((linesDrawn + 1) * lineHeight);
            
            boolean isHovered = chatOpen && (mouseX >= x && mouseX <= x + width && mouseY >= renderY && mouseY < renderY + lineHeight);
            if (isHovered) {
                hoveredMessage = msg;
            }
            
            if (activeTab.getSelectedMessages().contains(msg)) {
                drawContext.fill(x, renderY, x + width, renderY + lineHeight, 0x880000FF); // Semi-transparent blue highlight
            } else if (isHovered) {
                drawContext.fill(x, renderY, x + width, renderY + lineHeight, 0x55AAAAAA); // Grey hover highlight
            }
            
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
        
        if (hoveredMessage != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm 'Uhr'");
            String dateString = sdf.format(new java.util.Date(hoveredMessage.getTimestamp()));
            drawContext.drawTooltip(client.textRenderer, net.minecraft.text.Text.of(dateString), (int)mouseX, (int)mouseY);
        }
    }
}
