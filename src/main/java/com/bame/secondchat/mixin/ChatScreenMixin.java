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

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(Click click, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        
        ChatTab activeTab = TabManager.getInstance().getActiveTab();
        List<ChatTab> tabs = TabManager.getInstance().getTabs();
        
        MinecraftClient client = MinecraftClient.getInstance();
        int screenHeight = client.getWindow().getScaledHeight();

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
                    if (activeTab == tab) {
                        TabManager.getInstance().setActiveTab(null);
                    } else {
                        TabManager.getInstance().setActiveTab(tab);
                    }
                    
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
        
        // 1.5 Check if clicking on bottom-right corner of active tab to resize
        if (activeTab != null && activeTab.getName() != null) {
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
        }
        
        // 1.7 Check if clicking inside the chat box to select a message
        if (activeTab != null && button == 1) { // Right click
            int chatX = activeTab.getX();
            int chatY = activeTab.getY() + 18;
            int chatWidth = activeTab.getWidth();
            int chatHeight = activeTab.getHeight();
            
            if (mouseX >= chatX && mouseX <= chatX + chatWidth &&
                mouseY >= chatY && mouseY <= chatY + chatHeight) {
                
                int lineHeight = 12;
                int maxLines = chatHeight / lineHeight;
                
                int relativeYFromBottom = (chatY + chatHeight) - (int)mouseY;
                int linesFromBottom = relativeYFromBottom / lineHeight;
                
                if (linesFromBottom >= 0 && linesFromBottom < maxLines) {
                    int scrollLines = (int) activeTab.getScrollOffset();
                    int newestVisibleIndex = activeTab.getMessages().size() - 1 - scrollLines;
                    if (newestVisibleIndex >= activeTab.getMessages().size()) newestVisibleIndex = activeTab.getMessages().size() - 1;
                    if (newestVisibleIndex < 0) newestVisibleIndex = 0;
                    
                    int clickedIndex = newestVisibleIndex - linesFromBottom;
                    if (clickedIndex >= 0 && clickedIndex <= newestVisibleIndex && clickedIndex < activeTab.getMessages().size()) {
                        com.bame.secondchat.data.ChatMessage clickedMsg = activeTab.getMessages().get(clickedIndex);
                        activeTab.toggleSelection(clickedMsg);
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
        
        // 2. Check if clicked on the "+" button (anchored to "All" tab)
        ChatTab allTab = TabManager.getInstance().getAllTab();
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
    }
    
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        ChatTab activeTab = TabManager.getInstance().getActiveTab();
        MinecraftClient client = MinecraftClient.getInstance();
        if (activeTab != null && activeTab.getName() != null) {
            int chatX = activeTab.getX();
            int chatY = activeTab.getY() + 18;
            int chatWidth = activeTab.getWidth();
            int chatHeight = activeTab.getHeight();
            
            if (mouseX >= chatX && mouseX <= chatX + chatWidth &&
                mouseY >= chatY && mouseY <= chatY + chatHeight) {
                
                double currentScroll = activeTab.getScrollOffset();
                currentScroll += verticalAmount; 
                
                if (currentScroll < 0) currentScroll = 0;
                
                activeTab.setScrollOffset(currentScroll);
                cir.setReturnValue(true);
            }
        }
    }
}
