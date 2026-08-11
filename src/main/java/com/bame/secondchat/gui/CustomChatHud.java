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
                java.util.Set<com.bame.secondchat.data.SelectedLine> selected = activeTab.getSelectedLines();
                if (!selected.isEmpty()) {
                    java.util.List<com.bame.secondchat.data.SelectedLine> orderedSelection = new java.util.ArrayList<>();
                    for (ChatMessage msg : activeTab.getMessages()) {
                        java.util.List<net.minecraft.text.OrderedText> wrapped = client.textRenderer.wrapLines(msg.getRenderedMessage(), activeTab.getWidth() - 8);
                        for (int l = 0; l < wrapped.size(); l++) {
                            com.bame.secondchat.data.SelectedLine sl = new com.bame.secondchat.data.SelectedLine(msg, l);
                            if (selected.contains(sl)) {
                                orderedSelection.add(sl);
                            }
                        }
                    }
                    com.bame.secondchat.util.ClipboardImageUtil.copyMessagesToClipboard(orderedSelection, activeTab.getWidth());
                    activeTab.clearSelection();
                }
            }
            
            if (isCPressed && isCtrlPressed && !wasCPressed) {
                java.util.Set<com.bame.secondchat.data.SelectedLine> selected = activeTab.getSelectedLines();
                if (!selected.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (ChatMessage msg : activeTab.getMessages()) {
                        java.util.List<net.minecraft.text.OrderedText> wrapped = client.textRenderer.wrapLines(msg.getRenderedMessage(), activeTab.getWidth() - 8);
                        for (int l = 0; l < wrapped.size(); l++) {
                            com.bame.secondchat.data.SelectedLine sl = new com.bame.secondchat.data.SelectedLine(msg, l);
                            if (selected.contains(sl)) {
                                StringBuilder lineStr = new StringBuilder();
                                wrapped.get(l).accept((index, style, codePoint) -> {
                                    lineStr.append(new String(Character.toChars(codePoint)));
                                    return true;
                                });
                                sb.append(lineStr.toString()).append("\n");
                            }
                        }
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
        
        boolean isAllTab = "All".equals(activeTab.getName());
        boolean shouldFade = !chatOpen && isAllTab;

        if (shouldFade) {
            maxLines = Math.min(maxLines, 11);
        }

        int bg = 0x66000000;
        if (!shouldFade) {
            drawContext.fill(x, startY, x + width, startY + height, bg);
        }
        
        drawContext.enableScissor(x, startY, x + width, startY + height);
        
        ChatMessage hoveredMessage = null;
        int linesDrawn = 0;
        int currentTick = client.inGameHud.getTicks();
        
        boolean isRightClickHeld = org.lwjgl.glfw.GLFW.glfwGetMouseButton(client.getWindow().getHandle(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        
        int selectionColor = com.bame.secondchat.config.GlobalConfig.getInstance().parseSelectionColor();
        
        // Iterate backwards from newestVisibleIndex to oldest
        for (int i = newestVisibleIndex; i >= 0; i--) {
            if (linesDrawn >= maxLines) break;
            
            ChatMessage msg = messages.get(i);
            
            int age = currentTick - msg.getCreationTick();
            if (shouldFade && age > 200) {
                // Skip rendering this message if it's too old and we are fading
                continue;
            }
            
            List<net.minecraft.text.OrderedText> wrappedLines = client.textRenderer.wrapLines(msg.getRenderedMessage(), width - 8);
            
            double alpha = 1.0;
            if (shouldFade) {
                alpha = 1.0 - (age / 200.0);
                alpha = alpha * 10.0;
                alpha = Math.max(0.0, Math.min(1.0, alpha));
                alpha = alpha * alpha;
            }
            
            int alphaInt = (int)(255.0 * alpha);
            int color = (alphaInt << 24) | 0xFFFFFF;
            
            for (int l = wrappedLines.size() - 1; l >= 0; l--) {
                if (linesDrawn >= maxLines) break;
                
                int renderY = startY + height - ((linesDrawn + 1) * lineHeight);
                boolean isLineHovered = chatOpen && mouseX >= x && mouseX <= x + width && mouseY >= renderY && mouseY < renderY + lineHeight;
                
                if (isLineHovered) {
                    hoveredMessage = msg;
                    if (isRightClickHeld) {
                        activeTab.getSelectedLines().add(new com.bame.secondchat.data.SelectedLine(msg, l));
                    }
                }
                
                boolean isSelected = activeTab.isSelected(msg, l);
                
                if (isSelected) {
                    drawContext.fill(x, renderY, x + width, renderY + lineHeight, selectionColor);
                } else if (isLineHovered) {
                    drawContext.fill(x, renderY, x + width, renderY + lineHeight, 0x55AAAAAA);
                }
                
                if (shouldFade && alphaInt > 0) {
                    drawContext.fill(x, renderY, x + width, renderY + lineHeight, (int)(alphaInt * 0.5) << 24);
                }
                
                if (alphaInt > 0) {
                    drawContext.drawTextWithShadow(client.textRenderer, wrappedLines.get(l), x + 2, renderY + 2, color);
                }
                
                linesDrawn++;
            }
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
            try {
                String pattern = com.bame.secondchat.config.GlobalConfig.getInstance().timestampFormat;
                if (pattern == null || pattern.trim().isEmpty()) {
                    pattern = "dd.MM.yyyy HH:mm 'Uhr'";
                }
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(pattern);
                String dateString = sdf.format(new java.util.Date(hoveredMessage.getTimestamp()));
                
                // Add color if configured
                String colorPrefix = com.bame.secondchat.config.GlobalConfig.getInstance().timestampColor;
                net.minecraft.text.MutableText text = net.minecraft.text.Text.literal(dateString);
                if (colorPrefix != null && !colorPrefix.trim().isEmpty()) {
                    if (colorPrefix.startsWith("§")) {
                        text = net.minecraft.text.Text.literal(colorPrefix + dateString);
                    } else if (colorPrefix.startsWith("#")) {
                        try {
                            int colorInt = Integer.parseUnsignedInt(colorPrefix.substring(1), 16);
                            // If it's a 6-character hex, add full alpha
                            if (colorPrefix.length() == 7) {
                                colorInt = 0xFF000000 | colorInt;
                            }
                            text = text.fillStyle(net.minecraft.text.Style.EMPTY.withColor(colorInt & 0xFFFFFF));
                        } catch (Exception ignored) {
                        }
                    }
                }
                
                drawContext.drawTooltip(client.textRenderer, text, (int)mouseX, (int)mouseY);
            } catch (Exception e) {
                drawContext.drawTooltip(client.textRenderer, net.minecraft.text.Text.literal("Invalid Date Format"), (int)mouseX, (int)mouseY);
            }
        }
    }
}
