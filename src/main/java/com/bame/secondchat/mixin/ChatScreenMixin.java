package com.bame.secondchat.mixin;

import com.bame.secondchat.config.ModConfig;
import com.bame.secondchat.data.ChatTab;
import com.bame.secondchat.data.TabManager;
import com.bame.secondchat.gui.DragState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends net.minecraft.client.gui.screen.Screen implements com.bame.secondchat.gui.EmojiPickerProvider {

    protected ChatScreenMixin(net.minecraft.text.Text title) {
        super(title);
    }
    
    private com.bame.secondchat.gui.FontDropdownWidget fontDropdownWidget;
    private com.bame.secondchat.gui.EmojiPickerWidget emojiPickerWidget;
    private boolean showEmojiPicker = false;

    @Override
    public boolean getShowEmojiPicker() {
        return this.showEmojiPicker;
    }

    @Override
    public com.bame.secondchat.gui.EmojiPickerWidget getEmojiPickerWidget() {
        return this.emojiPickerWidget;
    }

    @Shadow
    protected abstract boolean handleClickEvent(net.minecraft.text.Style style, boolean insert);

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        if (com.bame.secondchat.config.ModConfig.showFontDropdown) {
            if (this.fontDropdownWidget == null) {
                this.fontDropdownWidget = new com.bame.secondchat.gui.FontDropdownWidget(screenWidth - 110, 5, 100, 16);
            }
            this.fontDropdownWidget.render(context, mouseX, mouseY, delta);
        } else {
            this.fontDropdownWidget = null;
        }

        // Render Emoji Button
        int btnX = screenWidth - 18;
        int btnY = screenHeight - 16;
        
        if (com.bame.secondchat.config.ModConfig.showEmojiButton) {
            context.fill(btnX, btnY, btnX + 15, btnY + 15, 0x88000000); // 15x15 translucent bg
            if (this.showEmojiPicker) {
                context.fill(btnX - 1, btnY - 1, btnX + 16, btnY, 0xFFFFFFFF);
                context.fill(btnX - 1, btnY + 15, btnX + 16, btnY + 16, 0xFFFFFFFF);
                context.fill(btnX - 1, btnY, btnX, btnY + 15, 0xFFFFFFFF);
                context.fill(btnX + 15, btnY, btnX + 16, btnY + 15, 0xFFFFFFFF);
            }
            if (mouseX >= btnX && mouseX <= btnX + 16 && mouseY >= btnY && mouseY <= btnY + 16) {
                context.fill(btnX, btnY, btnX + 16, btnY + 16, 0x88000000);
            }
            net.minecraft.util.Identifier smileyId = net.minecraft.util.Identifier.of("bamesecondchat", "smiley");
            context.drawGuiTexture(net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, smileyId, btnX + 1, btnY + 1, 12, 12);
        }

        if (this.showEmojiPicker) {
            if (this.emojiPickerWidget == null) {
                this.emojiPickerWidget = new com.bame.secondchat.gui.EmojiPickerWidget(screenWidth - 124, screenHeight - 120, 116, 100);
            }
            this.emojiPickerWidget.render(context, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(net.minecraft.client.input.KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (this.showEmojiPicker && this.emojiPickerWidget != null) {
            if (this.emojiPickerWidget.keyPressed(keyInput)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput charInput) {
        if (this.showEmojiPicker && this.emojiPickerWidget != null) {
            if (this.emojiPickerWidget.charTyped(charInput)) {
                return true;
            }
        }
        return super.charTyped(charInput);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        
        int btnX = screenWidth - 18;
        int btnY = screenHeight - 16;
        if (com.bame.secondchat.config.ModConfig.showEmojiButton) {
            if (mouseX >= btnX && mouseX <= btnX + 16 && mouseY >= btnY && mouseY <= btnY + 16) {
                this.showEmojiPicker = !this.showEmojiPicker;
                cir.setReturnValue(true);
                return;
            }
        }

        if (this.showEmojiPicker && this.emojiPickerWidget != null) {
            if (this.emojiPickerWidget.mouseClicked(click, bl)) {
                cir.setReturnValue(true);
                return;
            }
        }
        
        if (this.fontDropdownWidget != null && com.bame.secondchat.config.ModConfig.showFontDropdown) {
            if (this.fontDropdownWidget.mouseClicked(click, bl)) {
                cir.setReturnValue(true);
                return;
            }
        }
        
        List<ChatTab> tabs = TabManager.getInstance().getTabs();
        ChatTab allTab = TabManager.getInstance().getAllTab();
        ChatTab activeCustomTab = TabManager.getInstance().getActiveCustomTab();
        boolean isAllTabOpen = TabManager.getInstance().isAllTabOpen();
        
        int hudX = TabManager.getInstance().getHudX();
        int hudY = TabManager.getInstance().getHudY();
        
        // 1. Check if clicked on a tab header
        for (ChatTab tab : tabs) {
            int width = client.textRenderer.getWidth(tab.getName()) + 12;
            int height = 14;
            int tabX = tab.getX();
            int tabY = tab.getY();
            
            if (mouseX >= tabX && mouseX <= tabX + width && mouseY >= tabY && mouseY <= tabY + height) {
                if (button == 0) { // Left click
                    TabManager.getInstance().setActiveTab(tab);
                    
                    // Start dragging this individual tab
                    DragState.isDraggingTab = true;
                    DragState.draggedTab = tab;
                    DragState.startMouseX = mouseX;
                    DragState.startMouseY = mouseY;
                    DragState.startTabX = tab.getX();
                    DragState.startTabY = tab.getY();
                    
                    ((ChatScreen)(Object)this).setDragging(true);
                } else if (button == 1) { // Right click
                    if (!tab.getName().equals("All")) {
                        client.setScreen(new com.bame.secondchat.gui.TabEditScreen((ChatScreen)(Object)this, tab, false));
                    }
                }
                
                cir.setReturnValue(true);
                return;
            }
        }
        
        // Let's create a list of open tabs to check clicks inside them.
        // We add activeCustomTab first, then allTab, so custom tab has priority if they overlap.
        java.util.List<ChatTab> openTabs = new java.util.ArrayList<>();
        if (activeCustomTab != null) openTabs.add(activeCustomTab);
        if (isAllTabOpen) openTabs.add(allTab);
        
        for (ChatTab activeTab : openTabs) {
            // 1.5 Check if clicking on bottom-right corner of active tab to resize
            int resizeBoxSize = 10;
            int chatX = activeTab.getX();
            int chatY = activeTab.getY() + 18;
            int chatWidth = activeTab.getWidth();
            int chatHeight = activeTab.getHeight();
            
            if (mouseX >= chatX + chatWidth - resizeBoxSize && mouseX <= chatX + chatWidth &&
                mouseY >= chatY + chatHeight - resizeBoxSize && mouseY <= chatY + chatHeight) {
                if (button == 0) {
                    DragState.isResizing = true;
                    DragState.draggedTab = activeTab;
                    DragState.startMouseX = mouseX;
                    DragState.startMouseY = mouseY;
                    DragState.startTabWidth = chatWidth;
                    DragState.startTabHeight = chatHeight;
                    cir.setReturnValue(true);
                    return;
                }
            }
            
            // 1.7 Check if clicking inside the chat box to select a message
            if (mouseX >= chatX && mouseX <= chatX + chatWidth &&
                mouseY >= chatY && mouseY <= chatY + chatHeight) {
                
                // Check if clicking on scrollbar (rightmost 4 pixels)
                if (button == 0 && mouseX >= chatX + chatWidth - 4) {
                    DragState.isDraggingScrollbar = true;
                    DragState.draggedTab = activeTab;
                    DragState.startScrollbarMouseY = (int)mouseY;
                    DragState.startScrollOffset = activeTab.getScrollOffset();
                    cir.setReturnValue(true);
                    return;
                }
                
                if (button == 0) { // Left click in chat bounds
                    activeTab.clearSelection();
                    
                    int lineHeight = 12;
                    int maxLines = chatHeight / lineHeight;
                    
                    int relativeYFromBottom = (chatY + chatHeight) - (int)mouseY;
                    int linesFromBottom = relativeYFromBottom / lineHeight;
                    
                    if (linesFromBottom >= 0 && linesFromBottom < maxLines) {
                        int scrollLines = (int) activeTab.getScrollOffset();
                        int targetVisibleLine = linesFromBottom; // 0 = bottom
                        
                        int currentVisibleLine = 0;
                        int skippedLines = 0;
                        
                        for (int i = activeTab.getMessages().size() - 1; i >= 0; i--) {
                            com.bame.secondchat.data.ChatMessage msg = activeTab.getMessages().get(i);
                            java.util.List<net.minecraft.text.OrderedText> wrapped = client.textRenderer.wrapLines(msg.getRenderedMessage(), chatWidth - 8);
                            
                            for (int l = wrapped.size() - 1; l >= 0; l--) {
                                if (skippedLines < scrollLines) {
                                    skippedLines++;
                                    continue;
                                }
                                
                                if (currentVisibleLine == targetVisibleLine) {
                                    final int targetX = (int)(mouseX - (chatX + 2));
                                    int[] currentX = {0};
                                    net.minecraft.text.Style[] foundStyle = {null};
                                    
                                    wrapped.get(l).accept((index, s, codePoint) -> {
                                        int charWidth = client.textRenderer.getWidth(String.valueOf((char) codePoint));
                                        if (targetX >= currentX[0] && targetX < currentX[0] + charWidth) {
                                            foundStyle[0] = s;
                                            return false;
                                        }
                                        currentX[0] += charWidth;
                                        return true;
                                    });
                                    
                                    net.minecraft.text.Style style = foundStyle[0];
                                    
                                    if (style != null && style.getClickEvent() != null) {
                                        boolean success = this.handleClickEvent(style, false);
                                        if (success) {
                                            cir.setReturnValue(true);
                                        }
                                    }
                                    
                                    cir.setReturnValue(true);
                                    return;
                                }
                                currentVisibleLine++;
                            }
                        }
                    }
                    
                    cir.setReturnValue(true);
                    return;
                }
                
                if (button == 1) { // Right click
                    int lineHeight = 12;
                    int maxLines = chatHeight / lineHeight;
                    
                    int relativeYFromBottom = (chatY + chatHeight) - (int)mouseY;
                    int linesFromBottom = relativeYFromBottom / lineHeight;
                    
                    if (linesFromBottom >= 0 && linesFromBottom < maxLines) {
                        int scrollLines = (int) activeTab.getScrollOffset();
                        int targetVisibleLine = linesFromBottom; // 0 = bottom
                        
                        int currentVisibleLine = 0;
                        int skippedLines = 0;
                        
                        for (int i = activeTab.getMessages().size() - 1; i >= 0; i--) {
                            com.bame.secondchat.data.ChatMessage msg = activeTab.getMessages().get(i);
                            java.util.List<net.minecraft.text.OrderedText> wrapped = client.textRenderer.wrapLines(msg.getRenderedMessage(), chatWidth - 8);
                            
                            for (int l = wrapped.size() - 1; l >= 0; l--) {
                                if (skippedLines < scrollLines) {
                                    skippedLines++;
                                    continue;
                                }
                                
                                if (currentVisibleLine == targetVisibleLine) {
                                    activeTab.toggleSelection(msg, l);
                                    cir.setReturnValue(true);
                                    return;
                                }
                                currentVisibleLine++;
                            }
                        }
                    }
                }
            }
        }
        
        // 2. Check if clicked on the "+" button (anchored to "All" tab)
        int allWidth = client.textRenderer.getWidth("All") + 12;
        if (allTab.getUnreadCount() > 0) {
            allWidth += client.textRenderer.getWidth(String.valueOf(allTab.getUnreadCount())) + 6;
        }
        int plusX = allTab.getX() + allWidth + 2;
        int plusY = allTab.getY();
        int plusWidth = client.textRenderer.getWidth("+") + 12;
        
        if (mouseX >= plusX && mouseX <= plusX + plusWidth && mouseY >= plusY && mouseY <= plusY + 14) {
            if (button == 0) {
                ChatTab newTab = new ChatTab("", false);
                newTab.setX(allTab.getX() + 20);
                newTab.setY(allTab.getY() + 20);
                client.setScreen(new com.bame.secondchat.gui.TabEditScreen((ChatScreen)(Object)this, newTab, true));
                cir.setReturnValue(true);
                return;
            }
        }
    }
    
    @Inject(method = "close", at = @At("HEAD"))
    private void onClose(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        com.bame.secondchat.config.ModConfig.save();
        for (ChatTab tab : TabManager.getInstance().getTabs()) {
            tab.setScrollOffset(0);
            tab.clearSelection();
        }
    }
    
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (this.showEmojiPicker && this.emojiPickerWidget != null) {
            if (this.emojiPickerWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                cir.setReturnValue(true);
                return;
            }
        }
        
        ChatTab allTab = TabManager.getInstance().getAllTab();
        ChatTab activeCustomTab = TabManager.getInstance().getActiveCustomTab();
        boolean isAllTabOpen = TabManager.getInstance().isAllTabOpen();
        
        java.util.List<ChatTab> openTabs = new java.util.ArrayList<>();
        if (activeCustomTab != null) openTabs.add(activeCustomTab);
        if (isAllTabOpen) openTabs.add(allTab);
        
        for (ChatTab activeTab : openTabs) {
            int chatX = activeTab.getX();
            int chatY = activeTab.getY() + 18;
            int chatWidth = activeTab.getWidth();
            int chatHeight = activeTab.getHeight();
            
            if (mouseX >= chatX && mouseX <= chatX + chatWidth &&
                mouseY >= chatY && mouseY <= chatY + chatHeight) {
                
                double currentScroll = activeTab.getScrollOffset();
                currentScroll += verticalAmount; 
                
                int maxScroll = activeTab.getMessages().size() - (chatHeight / 12);
                if (maxScroll < 0) maxScroll = 0;
                
                if (currentScroll > maxScroll) currentScroll = maxScroll;
                if (currentScroll < 0) currentScroll = 0;
                
                activeTab.setScrollOffset(currentScroll);
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
