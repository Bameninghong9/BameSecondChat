package com.bame.secondchat.data;

import java.util.ArrayList;
import java.util.List;

public class TabManager {
    private static final TabManager INSTANCE = new TabManager();
    
    private final List<ChatTab> tabs = new ArrayList<>();
    private boolean isAllTabOpen = true;
    private final List<ChatTab> activeCustomTabs = new ArrayList<>();
    
    private long blockCaptureUntil = 0;
    private ChatTab blockCaptureTab = null;
    
    private final ChatTab allTab;

    // Global positions for the Custom Chat HUD
    private int hudX = -1;
    private int hudY = -1;

    private TabManager() {
        allTab = new ChatTab("All", false);
        tabs.add(allTab);
    }

    public static TabManager getInstance() {
        return INSTANCE;
    }

    public List<ChatTab> getTabs() {
        return tabs;
    }

    public void setActiveTab(ChatTab tab) {
        if (tab == allTab) {
            isAllTabOpen = !isAllTabOpen;
            if (isAllTabOpen) {
                allTab.resetUnread();
            }
        } else {
            if (activeCustomTabs.contains(tab)) {
                activeCustomTabs.remove(tab);
            } else {
                activeCustomTabs.add(tab);
                if (tab != null) {
                    tab.resetUnread();
                }
            }
        }
    }
    
    public boolean isAllTabOpen() {
        return isAllTabOpen;
    }
    
    public List<ChatTab> getActiveCustomTabs() {
        return activeCustomTabs;
    }

    public ChatTab getAllTab() {
        return allTab;
    }

    public void addTab(ChatTab tab) {
        if (!tabs.contains(tab)) {
            tabs.add(tab);
        }
    }

    public void removeTab(ChatTab tab) {
        if (tab != allTab) {
            tabs.remove(tab);
            activeCustomTabs.remove(tab);
        }
    }

    /**
     * Processes an incoming message and routes it to the matching tabs.
     * @param message The message object
     * @param plainText The plain text representation for filtering
     * @return true if the message should be hidden from the Vanilla chat (All-Tab).
     */
    public boolean processMessage(ChatMessage message, String plainText) {
        boolean hideFromVanilla = false;
        long now = System.currentTimeMillis();
        
        if (now < blockCaptureUntil && blockCaptureTab != null) {
            blockCaptureTab.addMessage(message);
            if (!activeCustomTabs.contains(blockCaptureTab)) {
                blockCaptureTab.incrementUnread();
            }
            if (blockCaptureTab.isHideFromAll()) {
                hideFromVanilla = true;
            }
            return hideFromVanilla;
        }
        
        boolean addToAll = true;

        for (ChatTab tab : tabs) {
            if (tab == allTab) continue;

            boolean matches = false;
            boolean startsBlock = false;
            for (FilterRule rule : tab.getRules()) {
                if (rule.matches(plainText)) {
                    matches = true;
                    if (rule instanceof CapturesBlockRule) {
                        startsBlock = true;
                    }
                    break;
                }
            }

            if (matches) {
                tab.addMessage(message);
                if (!activeCustomTabs.contains(tab)) {
                    tab.incrementUnread();
                }
                
                if (startsBlock) {
                    blockCaptureTab = tab;
                    blockCaptureUntil = now + 100; // Capture messages for the next 100ms
                }

                if (tab.isHideFromAll()) {
                    hideFromVanilla = true;
                    addToAll = false;
                }
            }
        }

        if (addToAll) {
            allTab.addMessage(message);
            if (!isAllTabOpen) {
                allTab.incrementUnread();
            }
        }

        return hideFromVanilla;
    }
    
    /**
     * Ersetzt die aktuellen Tabs durch geladene Tabs aus der Config.
     */
    public void loadTabs(List<ChatTab> loadedTabs) {
        tabs.clear();
        tabs.add(allTab);
        
        for (ChatTab tab : loadedTabs) {
            if (tab.getName().equalsIgnoreCase("All")) {
                allTab.setX(tab.getX());
                allTab.setY(tab.getY());
                allTab.setWidth(tab.getWidth());
                allTab.setHeight(tab.getHeight());
            } else {
                tabs.add(tab);
            }
        }
        
        isAllTabOpen = true;
        activeCustomTabs.clear();
    }

    public void updateTab(ChatTab tab, String newName, boolean hideFromAll, List<FilterRule> newRules) {
        tab.setName(newName);
        tab.setHideFromAll(hideFromAll);
        tab.getRules().clear();
        tab.getRules().addAll(newRules);
    }

    public int getHudX() {
        return hudX;
    }

    public void setHudX(int hudX) {
        this.hudX = hudX;
    }

    public int getHudY() {
        return hudY;
    }

    public void setHudY(int hudY) {
        this.hudY = hudY;
    }
}
