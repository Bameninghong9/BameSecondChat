package com.bame.secondchat.gui;

import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.TabManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.List;

public class TabHudRenderer implements HudRenderCallback {

    private final CustomChatHud customChatHud = new CustomChatHud();

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;
        
        handleDragging(client);
        
        ChatTab activeTab = TabManager.getInstance().getActiveTab();
        if (activeTab != null) {
            customChatHud.render(drawContext, tickCounter);
        }
        
        renderTabBar(drawContext);
    }

    private void handleDragging(MinecraftClient client) {
        if (DragState.isDraggingTab && DragState.draggedTab != null) {
            long windowHandle = client.getWindow().getHandle();
            if (org.lwjgl.glfw.GLFW.glfwGetMouseButton(windowHandle, org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1) == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                double mouseX = client.mouse.getX() * (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getWidth();
                double mouseY = client.mouse.getY() * (double)client.getWindow().getScaledHeight() / (double)client.getWindow().getHeight();
                
                int dx = (int)(mouseX - DragState.startMouseX);
                int dy = (int)(mouseY - DragState.startMouseY);
                
                DragState.draggedTab.setX(DragState.startTabX + dx);
                DragState.draggedTab.setY(DragState.startTabY + dy);
            } else {
                DragState.isDraggingTab = false;
                DragState.draggedTab = null;
                com.bame.secondchat.config.ModConfig.save();
            }
        } else if (DragState.isResizing && DragState.draggedTab != null) {
            long windowHandle = client.getWindow().getHandle();
            if (org.lwjgl.glfw.GLFW.glfwGetMouseButton(windowHandle, org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1) == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                double mouseX = client.mouse.getX() * (double)client.getWindow().getScaledWidth() / (double)client.getWindow().getWidth();
                double mouseY = client.mouse.getY() * (double)client.getWindow().getScaledHeight() / (double)client.getWindow().getHeight();
                
                int dx = (int)(mouseX - DragState.startMouseX);
                int dy = (int)(mouseY - DragState.startMouseY);
                
                int newWidth = Math.max(100, DragState.startTabWidth + dx);
                int newHeight = Math.max(50, DragState.startTabHeight + dy);
                
                DragState.draggedTab.setWidth(newWidth);
                DragState.draggedTab.setHeight(newHeight);
            } else {
                DragState.isResizing = false;
                DragState.draggedTab = null;
                com.bame.secondchat.config.ModConfig.save();
            }
        }
    }

    private void renderTabBar(DrawContext drawContext) {
        List<ChatTab> tabs = TabManager.getInstance().getTabs();
        ChatTab activeTab = TabManager.getInstance().getActiveTab();

        MinecraftClient client = MinecraftClient.getInstance();

        for (ChatTab tab : tabs) {
            String name = tab.getName();
            int width = client.textRenderer.getWidth(name) + 12;
            
            // Extra width for unread badge if needed
            if (tab.getUnreadCount() > 0) {
                width += client.textRenderer.getWidth(String.valueOf(tab.getUnreadCount())) + 6;
            }
            
            int height = 14;
            
            int xOffset = tab.getX();
            int yOffset = tab.getY();
            
            boolean isActive = (tab == activeTab);
            
            // Aktiver Tab Hintergrund: Helles Grau/Blau mit hoher Deckkraft. Inaktiv: Schwarz transparent
            int bgColor = isActive ? 0xCC202040 : 0x88000000;
            drawContext.fill(xOffset, yOffset, xOffset + width, yOffset + height, bgColor);
            
            if (isActive) {
                // Zeichne einen feinen Rand um den aktiven Tab
                int borderColor = 0xFF5555FF;
                drawContext.fill(xOffset, yOffset, xOffset + width, yOffset + 1, borderColor); // Top
                drawContext.fill(xOffset, yOffset + height - 1, xOffset + width, yOffset + height, borderColor); // Bottom
                drawContext.fill(xOffset, yOffset, xOffset + 1, yOffset + height, borderColor); // Left
                drawContext.fill(xOffset + width - 1, yOffset, xOffset + width, yOffset + height, borderColor); // Right
            }
            
            // Textfarbe: Weiß für aktiv, grauer für inaktiv
            int textColor = isActive ? 0xFFFFFFFF : 0xFFAAAAAA;
            drawContext.drawText(client.textRenderer, name, xOffset + 6, yOffset + 3, textColor, true);
            
            // Draw unread badge
            if (tab.getUnreadCount() > 0) {
                String badge = String.valueOf(tab.getUnreadCount());
                int badgeWidth = client.textRenderer.getWidth(badge);
                int badgeX = xOffset + width - badgeWidth - 4;
                drawContext.fill(badgeX - 2, yOffset + 2, badgeX + badgeWidth + 2, yOffset + 12, 0xFFFF3333); // Red background
                drawContext.drawText(client.textRenderer, badge, badgeX, yOffset + 3, 0xFFFFFFFF, true);
            }
        }
        
        // Render "+" button next to the "All" tab
        ChatTab allTab = TabManager.getInstance().getAllTab();
        int allWidth = client.textRenderer.getWidth("All") + 12;
        if (allTab.getUnreadCount() > 0) {
            allWidth += client.textRenderer.getWidth(String.valueOf(allTab.getUnreadCount())) + 6;
        }
        int plusX = allTab.getX() + allWidth + 2;
        int plusY = allTab.getY();
        
        int plusWidth = client.textRenderer.getWidth("+") + 12;
        drawContext.fill(plusX, plusY, plusX + plusWidth, plusY + 14, 0x88005500); // Greenish background
        drawContext.drawText(client.textRenderer, "+", plusX + 6, plusY + 3, 0xFFFFFFFF, true);
        
        // Render resize handle for active tab if custom chat is open
        if (activeTab != null && client.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen) {
            int chatX = activeTab.getX();
            int chatY = activeTab.getY() + 18;
            int chatWidth = activeTab.getWidth();
            int chatHeight = activeTab.getHeight();
            
            // Draw small triangle in bottom right
            drawContext.fill(chatX + chatWidth - 8, chatY + chatHeight - 2, chatX + chatWidth, chatY + chatHeight, 0xAAFFFFFF);
            drawContext.fill(chatX + chatWidth - 2, chatY + chatHeight - 8, chatX + chatWidth, chatY + chatHeight, 0xAAFFFFFF);
            drawContext.fill(chatX + chatWidth - 6, chatY + chatHeight - 4, chatX + chatWidth, chatY + chatHeight, 0xAAFFFFFF);
            drawContext.fill(chatX + chatWidth - 4, chatY + chatHeight - 6, chatX + chatWidth, chatY + chatHeight, 0xAAFFFFFF);
        }
    }
}
